package dev.rosewood.roseparticles.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.omega.arcane.parser.MolangParser;

public record MolangExpressionVector3(MolangExpression x,
                                      MolangExpression y,
                                      MolangExpression z) {

    public static MolangExpressionVector3 parse(JsonObject jsonObject, String property) throws MolangLexException, MolangParseException {
        JsonElement element = jsonObject.get(property);
        if (element == null || !element.isJsonArray())
            return empty();

        JsonArray jsonArray = element.getAsJsonArray();
        MolangExpression x = MolangParser.parse(jsonArray.get(0).getAsString());
        MolangExpression y = MolangParser.parse(jsonArray.get(1).getAsString());
        MolangExpression z = MolangParser.parse(jsonArray.get(2).getAsString());
        return new MolangExpressionVector3(x, y, z);
    }

    public static MolangExpressionVector3 parsePlaneNormal(JsonObject jsonObject, String property) throws MolangLexException, MolangParseException {
        JsonElement element = jsonObject.get(property);
        if (element == null || !element.isJsonArray())
            return defaultPlaneNormal();

        JsonArray jsonArray = element.getAsJsonArray();
        MolangExpression x = MolangParser.parse(jsonArray.get(0).getAsString());
        MolangExpression y = MolangParser.parse(jsonArray.get(1).getAsString());
        MolangExpression z = MolangParser.parse(jsonArray.get(2).getAsString());
        return new MolangExpressionVector3(x, y, z);
    }

    public static MolangExpressionVector3 empty() throws MolangLexException, MolangParseException {
        return new MolangExpressionVector3(
                MolangParser.parse("0"),
                MolangParser.parse("0"),
                MolangParser.parse("0")
        );
    }

    public static MolangExpressionVector3 defaultPlaneNormal() throws MolangLexException, MolangParseException {
        return new MolangExpressionVector3(
                MolangParser.parse("0"),
                MolangParser.parse("1"),
                MolangParser.parse("0")
        );
    }

}
