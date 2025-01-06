package dev.rosewood.roseparticles.datapack;

import java.awt.Dimension;
import java.util.List;

public record StitchedTexture(String identifier,
                              Dimension dimension,
                              List<String> fileNames,
                              List<Character> symbols) { }
