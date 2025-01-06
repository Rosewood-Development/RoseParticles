package dev.omega.arcane.reference;

import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.ast.ObjectAwareExpression;
import dev.omega.arcane.ast.ReferenceAssignmentExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.omega.arcane.parser.MolangParser;
import dev.omega.arcane.util.DummyStorageEntityObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AssignmentReferenceTests {

    private final ExpressionBindingContext context = ExpressionBindingContext.create();

    @Test
    public void VariableReference_Parse_ReturnsExpression() throws MolangLexException, MolangParseException {
        Assertions.assertInstanceOf(ReferenceAssignmentExpression.class, MolangParser.parse("variable.age = 5"));
    }

    @Test
    public void VariableContextReference_Parse_Evaluates() throws MolangLexException, MolangParseException {
        DummyStorageEntityObject entity = new DummyStorageEntityObject(); // default age 37

        MolangExpression assignmentExpression = MolangParser.parse("variable.age = 5").bind(context, entity);
        Assertions.assertEquals(5, assignmentExpression.evaluate()); // assignment should return the value set, which is 5
        MolangExpression evaluationExpression = MolangParser.parse("variable.age").bind(context, entity);
        Assertions.assertEquals(5, entity.get("age")); // verify change reflects on the entity object
        Assertions.assertEquals(5, evaluationExpression.evaluate()); // verify change reflects to other things bound to the entity

        Assertions.assertInstanceOf(ObjectAwareExpression.class, assignmentExpression);
        Assertions.assertInstanceOf(ObjectAwareExpression.class, evaluationExpression);
    }

    @Test
    public void VariableContextReference_ParseWithSelfReference_Evaluates() throws MolangLexException, MolangParseException {
        DummyStorageEntityObject entity = new DummyStorageEntityObject();

        MolangParser.parse("variable.age = 5").bind(context, entity).evaluate();
        Assertions.assertEquals(5, entity.get("age")); // set original age to 5
        MolangExpression assignmentExpression = MolangParser.parse("variable.age = variable.age + 5").bind(context, entity); // set equal to itself plus 5
        Assertions.assertEquals(10, assignmentExpression.evaluate()); // assignment should return the value set, which is 10
        MolangExpression evaluationExpression = MolangParser.parse("variable.age").bind(context, entity);
        Assertions.assertEquals(10, entity.get("age")); // verify change reflects on the entity object
        Assertions.assertEquals(10, evaluationExpression.evaluate()); // verify change reflects to other things bound to the entity

        Assertions.assertInstanceOf(ObjectAwareExpression.class, assignmentExpression);
        Assertions.assertInstanceOf(ObjectAwareExpression.class, evaluationExpression);
    }
}
