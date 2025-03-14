package dev.rosewood.roseparticles.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.ast.ConstantExpression;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.omega.arcane.parser.MolangParser;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Material;

public final class JsonHelper {

    public static boolean parseBoolean(JsonObject jsonObject, String property, boolean defaultValue) {
        JsonElement element = jsonObject.get(property);
        if (element == null)
            return defaultValue;
        return element.getAsBoolean();
    }

    public static float parseFloat(JsonObject jsonObject, String property, float defaultValue) {
        JsonElement element = jsonObject.get(property);
        if (element == null)
            return defaultValue;
        return element.getAsFloat();
    }

    public static int parseInt(JsonObject jsonObject, String property, int defaultValue) {
        JsonElement element = jsonObject.get(property);
        if (element == null)
            return defaultValue;
        return element.getAsInt();
    }

    public static List<String> parseStringList(JsonObject jsonObject, String property) {
        JsonElement element = jsonObject.get(property);
        if (element == null)
            return List.of();
        return parseStringList(element);
    }

    public static List<String> parseStringList(JsonElement element) {
        if (element.isJsonPrimitive())
            return List.of(element.getAsString());
        if (element.isJsonArray()) {
            JsonArray jsonArray = element.getAsJsonArray();
            List<String> strings = new ArrayList<>(jsonArray.size());
            for (int i = 0; i < jsonArray.size(); i++)
                strings.add(jsonArray.get(i).getAsString());
            return strings;
        }
        return List.of();
    }

    public static Map<Float, List<String>> parseTimeEventMap(JsonObject jsonObject, String property) {
        Map<Float, List<String>> map = new HashMap<>();
        JsonElement mapElement = jsonObject.get(property);
        if (mapElement == null || !mapElement.isJsonObject())
            return map;

        JsonObject mapObject = mapElement.getAsJsonObject();
        for (String key : mapObject.keySet()) {
            float time = Float.parseFloat(key);
            List<String> events = JsonHelper.parseStringList(mapObject, key);
            map.put(time, events);
        }

        return map;
    }

    public static Color parseColor(JsonElement element) {
        if (element.isJsonArray()) {
            JsonArray jsonArray = element.getAsJsonArray();
            if (jsonArray.size() == 3) {
                return new Color(jsonArray.get(0).getAsInt(), jsonArray.get(1).getAsInt(), jsonArray.get(2).getAsInt());
            } else if (jsonArray.size() == 4) {
                return new Color(jsonArray.get(0).getAsInt(), jsonArray.get(1).getAsInt(), jsonArray.get(2).getAsInt(), jsonArray.get(3).getAsInt());
            } else {
                throw new IllegalArgumentException("Invalid color array length, must be 3 or 4 but found " + jsonArray.size());
            }
        } else if (element.isJsonPrimitive()) {
            Color color = hexToColor(element.getAsString());
            if (color != null)
                return color;
        }
        return Color.WHITE;
    }

    public static Color hexToColor(String hex) {
        if (hex.startsWith("#"))
            hex = hex.substring(1);

        if (hex.length() == 6) {
            // #RRGGBB format
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            return new Color(r, g, b);
        } else if (hex.length() == 8) {
            // #AARRGGBB format
            int a = Integer.parseInt(hex.substring(0, 2), 16);
            int r = Integer.parseInt(hex.substring(2, 4), 16);
            int g = Integer.parseInt(hex.substring(4, 6), 16);
            int b = Integer.parseInt(hex.substring(6, 8), 16);
            return new Color(r, g, b, a);
        } else {
            return null;
        }
    }

    public static List<Material> parseBlockList(JsonElement element) {
        return parseStringList(element).stream()
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<Float> parseFloatList(JsonElement element) {
        return parseStringList(element).stream()
                .map(Float::parseFloat)
                .toList();
    }

    public static MolangExpression parseMolang(JsonObject jsonObject, String property) throws MolangLexException, MolangParseException {
        return parseMolang(jsonObject, property, 0.0F);
    }

    public static MolangExpression parseMolang(JsonObject jsonObject, String property, Float defaultValue) throws MolangLexException, MolangParseException {
        JsonElement element = jsonObject.get(property);
        if (element == null) {
            if (defaultValue == null) {
                return null;
            } else {
                return new ConstantExpression(defaultValue);
            }
        }
        String expression = element.getAsString();
        return MolangParser.parse(expression);
    }

}
