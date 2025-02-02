package dev.omega.arcane.arithmetic;

import dev.omega.arcane.Molang;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MixedChainArithmeticTests {

    @Test
    public void AdditionSubtraction_LeftAssociativity() {
        // Left associativity: ((5 - 5) + 5) = 5
        Assertions.assertEquals(5.0f, Molang.evaluateUnchecked("5.0 - 5.0 + 5.0"));

        // ((10 - 5) + 3) = 8
        Assertions.assertEquals(8.0f, Molang.evaluateUnchecked("10.0 - 5.0 + 3.0"));
    }

    @Test
    public void MultiplicationDivision_LeftAssociativity() {
        // ((8 / 2) * 3) = 12
        Assertions.assertEquals(12.0f, Molang.evaluateUnchecked("8.0 / 2.0 * 3.0"));

        // ((16 / 4) / 2) * 3 = 6
        Assertions.assertEquals(6.0f, Molang.evaluateUnchecked("16.0 / 4.0 / 2.0 * 3.0"));
    }

    @Test
    public void MixedPrecedence_WithParentheses() {
        // (5 + (3 * 2)) - (4 / 2) = 9
        Assertions.assertEquals(9.0f, Molang.evaluateUnchecked("5.0 + 3.0 * 2.0 - 4.0 / 2.0"));

        // ((12 / 3) + (4 * 2)) - 5 = 7
        Assertions.assertEquals(7.0f, Molang.evaluateUnchecked("12.0 / 3.0 + 4.0 * 2.0 - 5.0"));
    }

    @Test
    public void ComplexMixedOperators() {
        // ((5 * 3) - (10 / 2)) + 4 = 14
        Assertions.assertEquals(14.0f, Molang.evaluateUnchecked("5.0 * 3.0 - 10.0 / 2.0 + 4.0"));

        // (20 - (10 / 2)) - 3 = 12
        Assertions.assertEquals(12.0f, Molang.evaluateUnchecked("20.0 - 10.0 / 2.0 - 3.0"));
    }

    @Test
    public void NestedPrecedenceOperations() {
        // 2 + (3 * 4) + 5 = 19
        Assertions.assertEquals(19.0f, Molang.evaluateUnchecked("2.0 + 3.0 * 4.0 + 5.0"));

        // 10 + ((2 * 3) / 2) = 13
        Assertions.assertEquals(13.0f, Molang.evaluateUnchecked("10.0 + 2.0 * 3.0 / 2.0"));
    }

    @Test
    public void DivisionEdgeCases() {
        // (8 / 2) / 4 = 1
        Assertions.assertEquals(1.0f, Molang.evaluateUnchecked("8.0 / 2.0 / 4.0"));

        // ((15 - 5) * 2) / (3 + 2) = 4
        Assertions.assertEquals(4.0f, Molang.evaluateUnchecked("(15.0 - 5.0) * 2.0 / (3.0 + 2.0)"));
    }

}
