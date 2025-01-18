package dev.omega.arcane.parser;

import dev.omega.arcane.ast.CompoundExpression;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.omega.arcane.reference.ExpressionBindingContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MultiExpressionParserTests {

    private final ExpressionBindingContext context = ExpressionBindingContext.create();

    @Test
    public void Parse_Expression_Two_ReturnsCompoundExpression() throws MolangLexException, MolangParseException {
        MolangExpression expression = MolangParser.parse("5.0;2.0", MolangParser.FLAG_NONE);

        Assertions.assertInstanceOf(CompoundExpression.class, expression);
        Assertions.assertEquals(2.0F, expression.evaluate());
    }

    @Test
    public void Parse_Expression_Two_TrailingSemicolon_ReturnsCompoundExpression() throws MolangLexException, MolangParseException {
        MolangExpression expression = MolangParser.parse("5.0;2.0;", MolangParser.FLAG_NONE);

        Assertions.assertInstanceOf(CompoundExpression.class, expression);
        Assertions.assertEquals(2.0F, expression.evaluate());
    }

    @Test
    public void Parse_Expression_Three_ReturnsCompoundExpression() throws MolangLexException, MolangParseException {
        MolangExpression expression = MolangParser.parse("5.0;2.0;7.5", MolangParser.FLAG_NONE);

        Assertions.assertInstanceOf(CompoundExpression.class, expression);
        Assertions.assertEquals(7.5F, expression.evaluate());
    }

    @Test
    public void Parse_Expression_Three_TrailingSemicolon_ReturnsCompoundExpression() throws MolangLexException, MolangParseException {
        MolangExpression expression = MolangParser.parse("5.0;2.0;7.5;", MolangParser.FLAG_NONE);

        Assertions.assertInstanceOf(CompoundExpression.class, expression);
        Assertions.assertEquals(7.5F, expression.evaluate());
    }

}
