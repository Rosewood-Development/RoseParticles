package dev.rosewood.roseparticles.particle.config;

import java.io.File;

public class ParticleDefinitionFile {

    private final File file;

    private ParticleDefinitionFile(File file) {
        this.file = file;
    }

    public ParticleDefinitionFile parse(File file) {
        return new ParticleDefinitionFile(file);
    }

}
