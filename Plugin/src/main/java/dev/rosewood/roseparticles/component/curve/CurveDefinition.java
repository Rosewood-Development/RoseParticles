package dev.rosewood.roseparticles.component.curve;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.rosewood.roseparticles.util.JsonHelper;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public record CurveDefinition(String name,
                              CurveType type,
                              MolangExpression input,
                              MolangExpression horizontalRange,
                              List<Float> nodes,
                              SortedMap<Float, ChainNode> bezierChainNodes) {

    public static CurveDefinition parse(String name, JsonObject jsonObject) throws MolangLexException, MolangParseException {
        CurveType type = CurveType.parse(jsonObject, "type", CurveType.LINEAR);
        MolangExpression input = JsonHelper.parseMolang(jsonObject, "input");
        MolangExpression horizontalRange = JsonHelper.parseMolang(jsonObject, "horizontal_range", 1.0F);
        JsonElement nodesElement = jsonObject.get("nodes");
        List<Float> nodes = JsonHelper.parseFloatList(nodesElement);
        SortedMap<Float, ChainNode> bezierChainNodes;
        if (nodesElement.isJsonObject()) {
            bezierChainNodes = new TreeMap<>();
            JsonObject nodesObject = nodesElement.getAsJsonObject();
            for (String key : nodesObject.keySet()) {
                float node = Float.parseFloat(key);
                JsonObject nodeObject = nodesObject.get(key).getAsJsonObject();
                float value = nodeObject.get("value").getAsFloat();
                float slope = nodeObject.get("slope").getAsFloat();
                bezierChainNodes.put(node, new ChainNode(value, slope));
            }
        } else {
            bezierChainNodes = Collections.emptySortedMap();
        }
        return new CurveDefinition(name, type, input, horizontalRange, nodes, bezierChainNodes);
    }

}
