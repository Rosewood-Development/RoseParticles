package dev.omega.arcane.reference;

import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.ast.ObjectAwareExpression;
import dev.omega.arcane.ast.ReferenceAssignmentExpression;
import dev.omega.arcane.ast.ReferenceEvaluationExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.omega.arcane.parser.MolangParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TempReferenceTests {

    private final ExpressionBindingContext context = ExpressionBindingContext.create();

    @Test
    public void VariableReference_Parse_ReturnsExpression() throws MolangLexException, MolangParseException {
        Assertions.assertInstanceOf(ReferenceEvaluationExpression.class, MolangParser.parse("temp.age"));
        Assertions.assertInstanceOf(ReferenceAssignmentExpression.class, MolangParser.parse("temp.age = 5"));
    }

    @Test
    public void VariableContextReference_Parse_Evaluates() throws MolangLexException, MolangParseException {
        MolangExpression checkExpression = MolangParser.parse("temp.age").bind(context);
        Assertions.assertEquals(0, checkExpression.evaluate()); // verify variables default to 0

        MolangExpression assignmentExpression = MolangParser.parse("temp.age = 5").bind(context);
        Assertions.assertEquals(5, assignmentExpression.evaluate()); // assignment should return the value set, which is 5
        Assertions.assertEquals(5, checkExpression.evaluate()); // evaluate the original expression again to ensure the new value is set

        Assertions.assertInstanceOf(ObjectAwareExpression.class, checkExpression);
        Assertions.assertInstanceOf(ObjectAwareExpression.class, assignmentExpression);
    }

}
