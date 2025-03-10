package dev.rosewood.roseparticles.datapack;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.roseparticles.RoseParticles;
import dev.rosewood.roseparticles.component.ComponentType;
import dev.rosewood.roseparticles.component.model.Vector2;
import dev.rosewood.roseparticles.particle.config.ParticleFile;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;

public class TextureStitcher {

    private final Map<String, StitchedTexture> textureSymbolMappings;

    public TextureStitcher(RosePlugin rosePlugin, Collection<ParticleFile> particleFiles, File texturesDirectory, File packDirectory) {
        this.textureSymbolMappings = new LinkedHashMap<>();
        texturesDirectory.mkdirs();

        File packTexturesDirectory = new File(packDirectory, "assets/" + rosePlugin.getName().toLowerCase() + "/textures/particle");
        deleteDirectory(packTexturesDirectory);
        packTexturesDirectory.mkdirs();

        Logger log = RoseParticles.getInstance().getLogger();

        int textureId = 1;
        for (ParticleFile particleFile : particleFiles) {
            var description = particleFile.description();
            var renderParameters = description.renderParameters();
            if (renderParameters == null)
                continue;

            String identifier = description.identifier();
            String fileName = particleFile.file().getName();

            if (this.textureSymbolMappings.containsKey(identifier)) {
                log.warning(identifier + " - identifier is already taken by another particle file, ignoring this file: " + fileName);
                continue;
            }

            var appearanceBillboard = particleFile.getComponent(ComponentType.PARTICLE_APPEARANCE_BILLBOARD);
            if (appearanceBillboard == null) {
                log.warning(identifier + " is missing a minecraft:particle_appearance_billboard component, particles will not be visible");
                continue;
            }

            String textureFileName = renderParameters.texturePath();
            if (!textureFileName.startsWith("textures/")) {
                log.warning(identifier + " references a file texture outside of the textures directory: " + textureFileName);
                continue;
            }

            String texturePath = textureFileName.substring("textures/".length());

            File fileFolder = new File(texturesDirectory, texturePath).getParentFile();
            if (!fileFolder.exists()) {
                log.warning(identifier + " references a directory that doesn't exist: " + textureFileName);
                continue;
            }

            String textureName;
            int slashIndex = texturePath.lastIndexOf('/');
            if (slashIndex != -1) {
                textureName = texturePath.substring(slashIndex + 1);
            } else {
                textureName = texturePath;
            }

            var uv = appearanceBillboard.uv();
            if (uv == null) {
                // Full scale texture
                List<File> files = getFilesForName(textureName, fileFolder);
                if (files.isEmpty()) {
                    log.warning(identifier + " references a texture that doesn't exist: " + textureFileName);
                    continue;
                }

                List<String> fileNames = new ArrayList<>(files.size());
                List<Character> symbols = new ArrayList<>(files.size());
                Dimension dimension = null;
                for (File file : files) {
                    String newTextureName = "%d.png".formatted(textureId);
                    File newTexturePath = new File(packTexturesDirectory, newTextureName);
                    if (dimension == null) {
                        try (FileInputStream inputStream = new FileInputStream(file)) {
                            BufferedImage bufferedImage = ImageIO.read(inputStream);
                            dimension = new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight());
                            Path path = newTexturePath.toPath();
                            Files.copy(file.toPath(), path);
                        } catch (Exception e) {
                            RoseParticles.getInstance().getLogger().warning("Failed to cut image texture, texture will be missing: " + fileName);
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Files.copy(file.toPath(), newTexturePath.toPath());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    fileNames.add(newTextureName);
                    symbols.add((char) textureId);
                    textureId++;
                }

                this.textureSymbolMappings.put(identifier, new StitchedTexture(dimension, fileNames, symbols));
            } else {
                File file = new File(fileFolder, textureName + ".png");
                if (!file.exists()) {
                    log.warning(identifier + " references a texture that doesn't exist: " + textureFileName);
                    continue;
                }

                try (FileInputStream inputStream = new FileInputStream(file)) {
                    BufferedImage bufferedImage = ImageIO.read(inputStream);
                    Dimension dimension = new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight());

                    int textureWidth = uv.textureWidth();
                    int textureHeight = uv.textureHeight();

                    Vector2 scalar = new Vector2((float) dimension.width / textureWidth, (float) dimension.height / textureHeight);

                    var flipbook = uv.flipbook();
                    if (flipbook == null) {
                        Vector2 uvStart = uv.uv().multiply(scalar);
                        Vector2 uvSize = uv.uvSize().multiply(scalar);
                        BufferedImage texture = bufferedImage.getSubimage(Math.round(uvStart.x()), Math.round(uvStart.y()), Math.round(uvSize.x()), Math.round(uvSize.y()));
                        dimension = new Dimension(texture.getWidth(), texture.getHeight());
                        String newTextureName = "%s.png".formatted(textureId);
                        File newTexturePath = new File(packTexturesDirectory, newTextureName);
                        ImageIO.write(texture, "png", newTexturePath);
                        this.textureSymbolMappings.put(identifier, new StitchedTexture(dimension, List.of(newTextureName), List.of((char) textureId)));
                        textureId++;
                    } else {
                        Vector2 baseUV = flipbook.baseUV().multiply(scalar);
                        Vector2 sizeUV = flipbook.sizeUV().multiply(scalar);
                        Vector2 stepUV = flipbook.stepUV().multiply(scalar);

                        if (stepUV.x() == 0 && stepUV.y() == 0) {
                            RoseParticles.getInstance().getLogger().warning(identifier + " step_UV is set to [0, 0] which would lead to an infinite loop and thus is invalid");
                            continue;
                        }

                        List<String> fileNames = new ArrayList<>();
                        List<Character> symbols = new ArrayList<>();

                        // Cut textures until we go out of bounds
                        int x = Math.round(baseUV.x());
                        int y = Math.round(baseUV.y());

                        while (x >= 0 && y >= 0 && x < dimension.width && y < dimension.height
                                && x + sizeUV.x() >= 0 && y + sizeUV.y() >= 0 && x + sizeUV.x() <= dimension.width && y + sizeUV.y() <= dimension.height) {
                            BufferedImage texture = bufferedImage.getSubimage(x, y, Math.round(sizeUV.x()), Math.round(sizeUV.y()));
                            String newTextureName = "%s.png".formatted(textureId);
                            File newTexturePath = new File(packTexturesDirectory, newTextureName);
                            ImageIO.write(texture, "png", newTexturePath);
                            fileNames.add(newTextureName);
                            symbols.add((char) textureId);
                            textureId++;
                            x += Math.round(stepUV.x());
                            y += Math.round(stepUV.y());
                        }

                        dimension = new Dimension(Math.round(sizeUV.x()), Math.round(sizeUV.y()));
                        this.textureSymbolMappings.put(identifier, new StitchedTexture(dimension, fileNames, symbols));
                    }
                } catch (Exception e) {
                    RoseParticles.getInstance().getLogger().warning("Failed to cut image texture, texture will be missing: " + fileName);
                    e.printStackTrace();
                }
            }
        }
    }

    @Nullable
    public StitchedTexture getTexture(String identifier) {
        return this.textureSymbolMappings.get(identifier);
    }

    public Collection<StitchedTexture> getTextures() {
        return Collections.unmodifiableCollection(this.textureSymbolMappings.values());
    }

    private static void deleteDirectory(File directory) {
        File[] contents = directory.listFiles();
        if (contents != null)
            for (File file : contents)
                deleteDirectory(file);
        directory.delete();
    }

    private static List<File> getFilesForName(String targetName, File directory) {
        // Look for exact match
        File exactFile = new File(directory, targetName + ".png");
        if (exactFile.exists())
            return List.of(exactFile);

        // Look for animated textures split into individual frames and ID'd by _#
        File[] files = directory.listFiles();
        Pattern pattern = Pattern.compile(Pattern.quote(targetName) + "_(\\d+)\\.png");

        if (files == null)
            return List.of();

        Map<Integer, File> animationFiles = new HashMap<>();
        for (File file : files) {
            String fileName = file.getName();
            Matcher matcher = pattern.matcher(fileName);
            if (matcher.matches()) {
                int index = Integer.parseInt(matcher.group(1));
                animationFiles.put(index, file);
            }
        }

        return animationFiles.entrySet().stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(Map.Entry::getKey)))
                .map(Map.Entry::getValue)
                .toList();
    }

}
