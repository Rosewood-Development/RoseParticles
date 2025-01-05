package dev.rosewood.roseparticles.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Random;

public final class ParticleUtils {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final Random RANDOM = new Random();

    private ParticleUtils() {

    }

}
