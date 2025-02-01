package dev.rosewood.roseparticles.datapack;

import dev.rosewood.roseparticles.component.model.Vector2;
import java.awt.Dimension;
import java.util.List;

public record StitchedTexture(Vector2 uv,
                              Vector2 uvSize,
                              Dimension dimension,
                              List<String> fileNames,
                              List<Character> symbols) { }
