package dev.rosewood.roseparticles.component.particle.appearance;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.util.JsonHelper;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ParticleAppearanceTintingComponent(Color color,
                                                 List<Color> gradientList,
                                                 Map<Float, Color> gradientMap,
                                                 MolangExpression gradientInterpolant) {

    public static ParticleAppearanceTintingComponent parse(JsonElement jsonElement) throws MolangLexException, MolangParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Color color = new Color(255, 255, 255, 255);
        List<Color> gradientList = null;
        Map<Float, Color> gradientMap = null;
        MolangExpression gradientInterpolant = null;

        JsonElement colorElement = jsonObject.get("color");
        if (colorElement.isJsonPrimitive() || colorElement.isJsonArray()) {
            color = JsonHelper.parseColor(colorElement);
        } else {
            JsonObject colorObject = colorElement.getAsJsonObject();
            JsonElement gradientElement = colorObject.get("gradient");
            if (gradientElement.isJsonArray()) {
                JsonArray gradientArray = gradientElement.getAsJsonArray();
                gradientList = new ArrayList<>(gradientArray.size());
                for (int i = 0; i < gradientArray.size(); i++)
                    gradientList.add(JsonHelper.parseColor(gradientArray.get(i)));
            } else {
                gradientMap = new HashMap<>();
                JsonObject gradientObject = gradientElement.getAsJsonObject();
                for (String key : gradientObject.keySet()) {
                    float value = Float.parseFloat(key);
                    Color gradientColor = JsonHelper.parseColor(gradientObject.get(key));
                    gradientMap.put(value, gradientColor);
                }
            }

            gradientInterpolant = JsonHelper.parseMolang(jsonObject, "interpolant");
        }

        return new ParticleAppearanceTintingComponent(color, gradientList, gradientMap, gradientInterpolant);
    }

}
