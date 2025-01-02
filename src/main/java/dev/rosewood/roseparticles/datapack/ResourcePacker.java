package dev.rosewood.roseparticles.datapack;

import com.google.gson.FormattingStyle;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import dev.rosewood.rosegarden.RosePlugin;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ResourcePacker {

    private static final Gson GSON = new Gson();

    private ResourcePacker() {

    }

    public static byte[] pack(RosePlugin rosePlugin, File resourcePack) {
        try {
            resourcePack.mkdirs();

            File mcmeta = new File(resourcePack, "pack.mcmeta");
            if (!mcmeta.exists())
                writeMcMetaFile(mcmeta);

            File fontFolder = new File(resourcePack, "assets/" + rosePlugin.getName().toLowerCase() + "/font");
            fontFolder.mkdirs();

            File font = new File(fontFolder, "sprites.json");
            writeSpritesFile(rosePlugin, font);

            return compressDirectoryContentsToZip(resourcePack, new File(rosePlugin.getDataFolder(), ResourceServer.PACK_NAME));
        } catch (IOException e) {
            rosePlugin.getLogger().severe("Could not pack resources for resource pack zip");
            e.printStackTrace();
            return new byte[0];
        }
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

    private static void writeSpritesFile(RosePlugin rosePlugin, File file) throws IOException {
        try (JsonWriter jsonWriter = GSON.newJsonWriter(new FileWriter(file))) {
            jsonWriter.setFormattingStyle(FormattingStyle.PRETTY);
            jsonWriter.beginObject();
            jsonWriter.name("providers");
            jsonWriter.beginArray();
            jsonWriter.beginObject();
            jsonWriter.name("type");
            jsonWriter.value("bitmap");
            jsonWriter.name("file");
            jsonWriter.value(rosePlugin.getName().toLowerCase() + ":font/glow.png");
            jsonWriter.name("ascent");
            jsonWriter.value(4);
            jsonWriter.name("height");
            jsonWriter.value(8);
            jsonWriter.name("chars");
            jsonWriter.beginArray();
            jsonWriter.jsonValue("\"\\u0001\"");
            jsonWriter.endArray();
            jsonWriter.endObject();
            jsonWriter.endArray();
            jsonWriter.endObject();
        }
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
                            String pathString = rootPath.relativize(path).toString().replace("\\", "/");
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
