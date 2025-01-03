package dev.rosewood.roseparticles.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public enum ParticleBillboardFaceCameraMode {
    ROTATE_XYZ,
    ROTATE_Y,
    LOOKAT_XYZ,
    LOOKAT_Y,
    DIRECTION_X,
    DIRECTION_Y,
    DIRECTION_Z,
    EMITTER_TRANSFORM_XY,
    EMITTER_TRANSFORM_XZ,
    EMITTER_TRANSFORM_YZ;

    public static ParticleBillboardFaceCameraMode parse(JsonObject jsonObject, String property, ParticleBillboardFaceCameraMode defaultValue) {
        JsonElement element = jsonObject.get(property);
        if (element == null)
            return defaultValue;

        String string = element.getAsString();
        for (ParticleBillboardFaceCameraMode value : values())
            if (value.name().equalsIgnoreCase(string))
                return value;

        throw new IllegalArgumentException("Unknown ParticleBillboardFaceCameraMode type: " + string);
    }

}
