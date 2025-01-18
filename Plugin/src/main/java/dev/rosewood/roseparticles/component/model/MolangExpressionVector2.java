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

public record MolangExpressionVector2(MolangExpression x,
                                      MolangExpression y) {

    public MolangExpressionVector2 bind(ExpressionBindingContext context, Object... values) {
        return new MolangExpressionVector2(
                this.x.bind(context, values),
                this.y.bind(context, values)
        );
    }

    public Vector2 evaluate() {
        return new Vector2(this.x.evaluate(), this.y.evaluate());
    }

    public static MolangExpressionVector2 parse(JsonObject jsonObject, String property) throws MolangLexException, MolangParseException {
        JsonElement element = jsonObject.get(property);
        if (element == null || !element.isJsonArray())
            return empty();

        JsonArray jsonArray = element.getAsJsonArray();
        MolangExpression x = MolangParser.parse(jsonArray.get(0).getAsString());
        MolangExpression y = MolangParser.parse(jsonArray.get(1).getAsString());
        return new MolangExpressionVector2(x, y);
    }

    public static MolangExpressionVector2 empty() throws MolangLexException, MolangParseException {
        return new MolangExpressionVector2(
                new ConstantExpression(0),
                new ConstantExpression(0)
        );
    }

}
