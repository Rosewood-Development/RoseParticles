package dev.omega.arcane.comparison;

import dev.omega.arcane.Molang;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ComparisonTests {

    @Test
    public void Equals_True() {
        Assertions.assertEquals(1.0, Molang.evaluateUnchecked("5 == 5"));
    }

    @Test
    public void Equals_False() {
        Assertions.assertEquals(0.0, Molang.evaluateUnchecked("5 == 10"));
    }

    @Test
    public void NotEquals_True() {
        Assertions.assertEquals(1.0, Molang.evaluateUnchecked("5.0 != 10.0"));
    }

    @Test
    public void NotEquals_False() {
        Assertions.assertEquals(0.0, Molang.evaluateUnchecked("5.0 != 5.0"));
    }

    @Test
    public void GreaterThan_True() {
        Assertions.assertEquals(1.0, Molang.evaluateUnchecked("10.0 > 5.0"));
    }

    @Test
    public void GreaterThan_False() {
        Assertions.assertEquals(0.0, Molang.evaluateUnchecked("5.0 > 10.0"));
    }

    @Test
    public void LessThan_True() {
        Assertions.assertEquals(1.0, Molang.evaluateUnchecked("5.0 < 10.0"));
    }

    @Test
    public void LessThan_False() {
        Assertions.assertEquals(0.0, Molang.evaluateUnchecked("10.0 < 5.0"));
    }

    @Test
    public void GreaterThanEquals_NotEqual_True() {
        Assertions.assertEquals(1.0, Molang.evaluateUnchecked("10.0 >= 5.0"));
    }

    @Test
    public void GreaterThanEquals_NotEqual_False() {
        Assertions.assertEquals(0.0, Molang.evaluateUnchecked("5.0 >= 10.0"));
    }

    @Test
    public void GreaterThanEquals_Equal_True() {
        Assertions.assertEquals(1.0, Molang.evaluateUnchecked("5.0 >= 5.0"));
    }

    @Test
    public void LessThanEquals_NotEqual_True() {
        Assertions.assertEquals(1.0, Molang.evaluateUnchecked("5.0 <= 10.0"));
    }

    @Test
    public void LessThanEquals_NotEqual_False() {
        Assertions.assertEquals(0.0, Molang.evaluateUnchecked("10.0 <= 5.0"));
    }

    @Test
    public void LessThanEquals_Equal_True() {
        Assertions.assertEquals(1.0, Molang.evaluateUnchecked("5.0 <= 5.0"));
    }

}
