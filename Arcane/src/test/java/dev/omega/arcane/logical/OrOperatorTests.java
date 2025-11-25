package dev.omega.arcane.logical;

import dev.omega.arcane.Molang;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Or (||)")
public class OrOperatorTests {

    @Test
    public void Or_FalseFalse_False() {
        Assertions.assertEquals(0.0, Molang.evaluateUnchecked("0.0 || 0.0"));
        Assertions.assertEquals(0.0, Molang.evaluateUnchecked("0 || 0"));
    }

    @Test
    public void Or_FalseTrue_True() {
        Assertions.assertEquals(1.0, Molang.evaluateUnchecked("0.0 || 1.0"));
        Assertions.assertEquals(1.0, Molang.evaluateUnchecked("1.0 || 0.0"));
    }

    @Test
    public void Or_TrueTrue_True() {
        Assertions.assertEquals(1.0, Molang.evaluateUnchecked("1.0 || 1.0"));
    }

    @Test
    public void Or_MissingPipe_ThrowsLexException() {
        Assertions.assertThrowsExactly(MolangLexException.class, () -> Molang.evaluate("0.0 | 0.0"));
    }

    @Test
    public void Or_MissingRightOperator_ThrowsLexException() {
        Assertions.assertThrowsExactly(MolangParseException.class, () -> Molang.evaluate("0.0 ||"));
    }

    @Test
    public void Or_MissingLeftOperator_ThrowsLexException() {
        Assertions.assertThrowsExactly(MolangParseException.class, () -> Molang.evaluate("|| 0.0"));
    }

    @Test
    public void Or_MathConstant_ReturnAnswer() {
        // sin(1.0) is != 0.0, so it is true
        Assertions.assertEquals(1.0f, Molang.evaluateUnchecked("math.sin(1.0) || 1.0"));
    }
}
