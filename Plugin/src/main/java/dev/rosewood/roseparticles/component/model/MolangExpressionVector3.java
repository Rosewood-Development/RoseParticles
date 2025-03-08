package dev.rosewood.roseparticles.component.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.omega.arcane.ast.ConstantExpression;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.omega.arcane.parser.MolangParser;
import dev.omega.arcane.reference.ExpressionBindingContext;
import org.bukkit.util.Vector;

public record MolangExpressionVector3(MolangExpression x,
                                      MolangExpression y,
                                      MolangExpression z) {

    public MolangExpressionVector3(MolangExpression value) {
        this(value, value, value);
    }

    public MolangExpressionVector3 bind(ExpressionBindingContext context, Object... values) {
        return new MolangExpressionVector3(
                this.x.bind(context, values),
                this.y.bind(context, values),
                this.z.bind(context, values)
        );
    }

    public Vector evaluate() {
        return new Vector(this.x.evaluate(), this.y.evaluate(), this.z.evaluate());
    }

    public static MolangExpressionVector3 parse(JsonObject jsonObject, String property) throws MolangLexException, MolangParseException {
        JsonElement element = jsonObject.get(property);
        if (element == null)
            return empty();
        return parse(element);
    }

    public static MolangExpressionVector3 parse(JsonObject jsonObject, String property, Vector defaultValue) throws MolangLexException, MolangParseException {
        JsonElement element = jsonObject.get(property);
        if (element == null) {
            if (defaultValue == null) {
                return null;
            } else {
                return new MolangExpressionVector3(
                        new ConstantExpression((float) defaultValue.getX()),
                        new ConstantExpression((float) defaultValue.getY()),
                        new ConstantExpression((float) defaultValue.getZ())
                );
            }
        }
        return parse(element);
    }

    public static MolangExpressionVector3 parse(JsonElement jsonElement) throws MolangLexException, MolangParseException {
        if (!jsonElement.isJsonArray())
            return empty();

        JsonArray jsonArray = jsonElement.getAsJsonArray();
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

    public static MolangExpressionVector3 empty() {
        return new MolangExpressionVector3(
                new ConstantExpression(0),
                new ConstantExpression(0),
                new ConstantExpression(0)
        );
    }

    public static MolangExpressionVector3 defaultPlaneNormal() {
        return new MolangExpressionVector3(
                new ConstantExpression(0),
                new ConstantExpression(1),
                new ConstantExpression(0)
        );
    }

}
