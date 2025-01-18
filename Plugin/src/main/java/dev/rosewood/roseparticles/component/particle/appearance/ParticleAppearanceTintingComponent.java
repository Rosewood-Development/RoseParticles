package dev.rosewood.roseparticles.component.particle.appearance;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.component.model.MolangExpressionColor;
import dev.rosewood.roseparticles.util.JsonHelper;
import java.awt.Color;
import java.util.NavigableMap;
import java.util.TreeMap;

public record ParticleAppearanceTintingComponent(MolangExpressionColor colorExpression,
                                                 NavigableMap<Float, Color> gradientMap,
                                                 MolangExpression gradientInterpolant) {

    public static ParticleAppearanceTintingComponent parse(JsonElement jsonElement) throws MolangLexException, MolangParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        MolangExpressionColor colorExpression = null;
        NavigableMap<Float, Color> gradientMap = null;
        MolangExpression gradientInterpolant = null;

        JsonElement colorElement = jsonObject.get("color");
        if (colorElement.isJsonPrimitive()) {
            colorExpression = new MolangExpressionColor(JsonHelper.parseColor(colorElement));
        } else if (colorElement.isJsonArray()) {
            colorExpression = MolangExpressionColor.parse(colorElement);
        } else {
            gradientMap = new TreeMap<>();
            JsonObject colorObject = colorElement.getAsJsonObject();
            JsonElement gradientElement = colorObject.get("gradient");
            if (gradientElement.isJsonArray()) {
                JsonArray gradientArray = gradientElement.getAsJsonArray();
                int count = gradientArray.size();
                float percentage = 1F / (count - 1);
                for (int i = 0; i < count; i++) {
                    float value = i == count - 1 ? 1F : i * percentage; // fix floating point error
                    gradientMap.put(value, JsonHelper.parseColor(gradientArray.get(i)));
                }
            } else {
                JsonObject gradientObject = gradientElement.getAsJsonObject();
                for (String key : gradientObject.keySet()) {
                    float value = Float.parseFloat(key);
                    Color gradientColor = JsonHelper.parseColor(gradientObject.get(key));
                    gradientMap.put(value, gradientColor);
                }
            }

            gradientInterpolant = JsonHelper.parseMolang(colorObject, "interpolant");
        }

        return new ParticleAppearanceTintingComponent(colorExpression, gradientMap, gradientInterpolant);
    }

}
