package dev.omega.arcane.arithmetic;

import dev.omega.arcane.Molang;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FactorChainedArithmeticTests {

    @Test
    public void Multiplication_Multiplication_Success() {
        Assertions.assertEquals(125.0f, Molang.evaluateUnchecked("5.0 * 5.0 * 5.0"));
    }

    @Test
    public void Multiplication_Division_Success() {
        Assertions.assertEquals(5.0f, Molang.evaluateUnchecked("5.0 * 5.0 / 5.0"));
    }

    @Test
    public void Division_Multiplication_Success() {
        Assertions.assertEquals(5.0f, Molang.evaluateUnchecked("5.0 / 5.0 * 5.0"));
    }

    @Test
    public void Division_Division_Success() {
        Assertions.assertEquals(0.2f, Molang.evaluateUnchecked("5.0 / 5.0 / 5.0"));
    }

}
