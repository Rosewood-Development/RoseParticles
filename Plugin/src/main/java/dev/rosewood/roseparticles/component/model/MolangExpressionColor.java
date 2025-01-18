package dev.rosewood.roseparticles.component.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.omega.arcane.ast.ConstantExpression;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.omega.arcane.parser.MolangParser;
import dev.omega.arcane.reference.ExpressionBindingContext;
import java.awt.Color;

public record MolangExpressionColor(MolangExpression r,
                                    MolangExpression g,
                                    MolangExpression b,
                                    MolangExpression a) {

    public MolangExpressionColor(Color color) {
        this(
                new ConstantExpression(color.getRed()),
                new ConstantExpression(color.getGreen()),
                new ConstantExpression(color.getBlue()),
                new ConstantExpression(color.getAlpha())
        );
    }

    public MolangExpressionColor bind(ExpressionBindingContext context, Object... values) {
        return new MolangExpressionColor(
                this.r.bind(context, values),
                this.g.bind(context, values),
                this.b.bind(context, values),
                this.a.bind(context, values)
        );
    }

    public Color evaluate() {
        return new Color(this.r.evaluate(), this.g.evaluate(), this.b.evaluate(), this.a.evaluate());
    }

    public static MolangExpressionColor parse(JsonElement jsonElement) throws MolangLexException, MolangParseException {
        if (!jsonElement.isJsonArray())
            return empty();

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        MolangExpression r = MolangParser.parse(jsonArray.get(0).getAsString());
        MolangExpression g = MolangParser.parse(jsonArray.get(1).getAsString());
        MolangExpression b = MolangParser.parse(jsonArray.get(2).getAsString());
        MolangExpression a;
        if (jsonArray.size() > 3) {
            a = MolangParser.parse(jsonArray.get(3).getAsString());
        } else {
            a = new ConstantExpression(255);
        }
        return new MolangExpressionColor(r, g, b, a);
    }

    public static MolangExpressionColor empty() throws MolangLexException, MolangParseException {
        return new MolangExpressionColor(
                new ConstantExpression(0),
                new ConstantExpression(0),
                new ConstantExpression(0),
                new ConstantExpression(255)
        );
    }

}
