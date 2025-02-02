package dev.omega.arcane.arithmetic;

import dev.omega.arcane.Molang;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TermChainedArithmeticTests {

    @Test
    public void Addition_Addition_Success() {
        Assertions.assertEquals(15.0f, Molang.evaluateUnchecked("5.0 + 5.0 + 5.0"));
    }

    @Test
    public void Addition_Subtraction_Success() {
        Assertions.assertEquals(5.0f, Molang.evaluateUnchecked("5.0 + 5.0 - 5.0"));
    }

    @Test
    public void Subtraction_Addition_Success() {
        Assertions.assertEquals(5.0f, Molang.evaluateUnchecked("5.0 - 5.0 + 5.0"));
    }

    @Test
    public void Subtraction_Subtraction_Success() {
        Assertions.assertEquals(-5.0f, Molang.evaluateUnchecked("5.0 - 5.0 - 5.0"));
    }

}
