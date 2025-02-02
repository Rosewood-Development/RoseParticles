package dev.rosewood.roseparticles.datapack;

import java.awt.Dimension;
import java.util.List;

public record StitchedTexture(Dimension dimension,
                              List<String> fileNames,
                              List<Character> symbols) { }
