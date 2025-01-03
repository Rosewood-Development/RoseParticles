package dev.rosewood.roseparticles.datapack;

import com.google.gson.FormattingStyle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import dev.rosewood.rosegarden.RosePlugin;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import org.bukkit.Bukkit;

public final class ResourcePacker {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private ResourcePacker() {

    }

    public static byte[] pack(RosePlugin rosePlugin, File resourcePack) {
        try {
            resourcePack.mkdirs();

            File mcmeta = new File(resourcePack, "pack.mcmeta");
            if (!mcmeta.exists())
                writeMcMetaFile(mcmeta);

            File texturesFolder = new File(resourcePack, "assets/" + rosePlugin.getName().toLowerCase() + "/textures/particle");
            deleteDirectory(texturesFolder);
            texturesFolder.mkdirs();

            Path rootTexturePath = new File(rosePlugin.getDataFolder(), "textures").toPath();
            List<Path> originalTexturePaths = getTextureFilePaths(rosePlugin);
            // TODO: Potentially split files based on particle file definition UVs, for now just copy everything
            List<Path> texturePaths = new ArrayList<>(originalTexturePaths.size());
            for (Path texturePath : originalTexturePaths) {
                String relativePath = rootTexturePath.relativize(texturePath).toString().replace('\\', '/');
                Path targetPath = new File(texturesFolder, relativePath).toPath();
                Files.copy(texturePath, targetPath);
                texturePaths.add(targetPath);
            }

            File fontFolder = new File(resourcePack, "assets/" + rosePlugin.getName().toLowerCase() + "/font");
            fontFolder.mkdirs();

            File font = new File(fontFolder, "sprites.json");
            writeSpritesFile(rosePlugin, font, texturesFolder, texturePaths);

            return compressDirectoryContentsToZip(resourcePack, new File(rosePlugin.getDataFolder(), ResourceServer.PACK_NAME));
        } catch (IOException e) {
            rosePlugin.getLogger().severe("Could not pack resources for resource pack zip");
            e.printStackTrace();
            return new byte[0];
        }
    }

    private static void deleteDirectory(File directory) {
        File[] contents = directory.listFiles();
        if (contents != null)
            for (File file : contents)
                deleteDirectory(file);
        directory.delete();
    }

    private static List<Path> getTextureFilePaths(RosePlugin rosePlugin) throws IOException {
        File texturesFolder = new File(rosePlugin.getDataFolder(), "textures");
        try (Stream<Path> stream = Files.walk(texturesFolder.toPath())) {
            return stream.filter(path -> !Files.isDirectory(path))
                    .filter(ResourcePacker::isSupportedImageType)
                    .toList();
        }
    }

    private static boolean isSupportedImageType(Path path) {
        String fileName = path.getFileName().toString();
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        return switch (extension) {
            case ".png" -> true;
            default -> false;
        };
    }

    private static void writeMcMetaFile(File file) throws IOException {
        try (JsonWriter jsonWriter = GSON.newJsonWriter(new FileWriter(file))) {
            jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
            jsonWriter.beginObject();
            jsonWriter.name("pack");
            jsonWriter.beginObject();
            jsonWriter.name("pack_format");
            jsonWriter.value(61); // 12 for 1.19.4
            jsonWriter.name("description");
            jsonWriter.value("RoseParticles sprite font pack");
            jsonWriter.endObject();
            jsonWriter.endObject();
        }
    }

    private static void writeSpritesFile(RosePlugin rosePlugin, File file, File texturesFolder, List<Path> textures) throws IOException {
        try (JsonWriter jsonWriter = GSON.newJsonWriter(new FileWriter(file))) {
            jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
            jsonWriter.beginObject();
            jsonWriter.name("providers");
            jsonWriter.beginArray();
            int id = 1;
            Path texturesFolderPath = texturesFolder.toPath();
            for (Path texturePath : textures) {
                try {
                    Dimension imageDimension = getImageDimension(texturePath.toFile());
                    String relativePath = texturesFolderPath.relativize(texturePath).toString().replace('\\', '/');
                    Bukkit.broadcastMessage("Loaded texture at " + relativePath + " with id " + id);
                    jsonWriter.beginObject();
                    jsonWriter.name("type");
                    jsonWriter.value("bitmap");
                    jsonWriter.name("file");
                    jsonWriter.value(rosePlugin.getName().toLowerCase() + ":particle/" + relativePath);
                    jsonWriter.name("ascent");
                    jsonWriter.value(imageDimension.height / 2);
                    jsonWriter.name("height");
                    jsonWriter.value(imageDimension.height);
                    jsonWriter.name("chars");
                    jsonWriter.beginArray();
                    jsonWriter.jsonValue("\"\\u%04x\"".formatted(id));
                    jsonWriter.endArray();
                    jsonWriter.endObject();
                    id++;
                } catch (Exception e) {
                    rosePlugin.getLogger().warning("Failed to read texture " + texturePath);
                    e.printStackTrace();
                }
            }
            jsonWriter.endArray();
            jsonWriter.endObject();
        }
    }

    /**
     * Gets image dimensions for given file.
     * <a href="https://stackoverflow.com/a/12164026">Source</a>
     * @param imgFile image file
     * @return dimensions of image
     * @throws IOException if the file is not a known image
     */
    private static Dimension getImageDimension(File imgFile) throws IOException {
        int pos = imgFile.getName().lastIndexOf(".");
        if (pos == -1)
            throw new IOException("No extension for file: " + imgFile.getAbsolutePath());
        String suffix = imgFile.getName().substring(pos + 1);
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
        while (iter.hasNext()) {
            ImageReader reader = iter.next();
            try {
                ImageInputStream stream = new FileImageInputStream(imgFile);
                reader.setInput(stream);
                int width = reader.getWidth(reader.getMinIndex());
                int height = reader.getHeight(reader.getMinIndex());
                return new Dimension(width, height);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                reader.dispose();
            }
        }

        throw new IOException("Not a known image file: " + imgFile.getAbsolutePath());
    }

    private static byte[] compressDirectoryContentsToZip(File root, File destination) throws IOException {
        Path zipFilePath = destination.toPath();
        if (destination.exists())
            Files.delete(zipFilePath);
        Files.createFile(destination.toPath());

        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
            Path rootPath = root.toPath();
            try (Stream<Path> stream = Files.walk(rootPath)) {
                stream.filter(path -> !Files.isDirectory(path))
                        .forEach(path -> {
                            String pathString = rootPath.relativize(path).toString().replace('\\', '/');
                            ZipEntry zipEntry = new ZipEntry(pathString);
                            zipEntry.setTime(0);
                            try {
                                zs.putNextEntry(zipEntry);
                                Files.copy(path, zs);
                                zs.closeEntry();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }
        }

        try (FileInputStream fis = new FileInputStream(destination)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] data = new byte[1024];
            int read;
            while ((read = fis.read(data)) != -1)
                digest.update(data, 0, read);
            return digest.digest();
        } catch (IOException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        return new byte[0];
    }

}
