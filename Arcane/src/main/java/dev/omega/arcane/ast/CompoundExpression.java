package dev.omega.arcane.ast;

import dev.omega.arcane.reference.ExpressionBindingContext;
import org.jetbrains.annotations.Nullable;

public record CompoundExpression(MolangExpression expression1, MolangExpression expression2) implements MolangExpression {

    @Override
    public float evaluate() {
        this.expression1.evaluate();
        return this.expression2.evaluate();
    }

    @Override
    public MolangExpression simplify() {
        @Nullable MolangExpression simplified = this.simplifyConstantExpression(this.expression1.simplify(), this.expression2.simplify());
        return simplified != null ? simplified : new CompoundExpression(this.expression1.simplify(), this.expression2.simplify());
    }

    @Override
    public MolangExpression bind(ExpressionBindingContext context, Object... values) {
        return new CompoundExpression(
                this.expression1.bind(context, values),
                this.expression2.bind(context, values)
        );
    }

}
