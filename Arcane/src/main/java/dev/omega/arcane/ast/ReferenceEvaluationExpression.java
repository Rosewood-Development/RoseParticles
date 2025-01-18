package dev.omega.arcane.ast;

import dev.omega.arcane.Molang;
import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.omega.arcane.reference.MolangVariableStorage;
import dev.omega.arcane.reference.ReferenceType;
import java.util.ArrayList;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ReferenceEvaluationExpression(ReferenceType type, String value) implements MolangExpression {

    @Override
    public float evaluate() {
        return 0.0f;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public MolangExpression bind(ExpressionBindingContext context, Object[] values) {
        MolangExpression expression = this;

        // If the context provides a way to bind this ReferenceEvaluationExpression to an Object value, try to simplify it down now~
        @Nullable List<ExpressionBindingContext.Binder<?>> evaluators = context.getEvaluators(type);
        if(evaluators != null) {
            for (ExpressionBindingContext.Binder binder : evaluators) {
                if(binder.getReferenceName().equals(value)) {
                    @Nullable Class<?> expectedClass = binder.getExpectedClass();

                    // Try to find the expected class the mapper wants from our Object[]
                    if(expectedClass != null) {
                        for (Object value : values) {
                            if(expectedClass.isAssignableFrom(value.getClass())) {
                                return binder.bind(value);
                            }
                        }
                    } else {
                        return binder.bind(null);
                    }
                }
            }
        }

        // Otherwise try to find the variable through the storages
        if (type == ReferenceType.VARIABLE) {
            // Get all binding values that are variable storages
            List<MolangVariableStorage> variableStorages = new ArrayList<>(values.length);
            for (Object value : values)
                if (value instanceof MolangVariableStorage variableStorage)
                    variableStorages.add(variableStorage);

            // Prioritize a storage that already contains the variable
            for (MolangVariableStorage variableStorage : variableStorages)
                if (variableStorage.has(value))
                    return variableStorage.createEvaluationExpression(value);

            // Fall back to the primary storage if available
            if (!variableStorages.isEmpty())
                return variableStorages.getFirst().createEvaluationExpression(value);
        } else if (type == ReferenceType.TEMP) {
            MolangVariableStorage tempVariableStorage = context.getTempVariableStorage();
            return tempVariableStorage.createEvaluationExpression(value);
        }

        Molang.LOGGER.warning("Was not able to bind %s %s to a value!".formatted(type.name().toLowerCase(), value));
        return this;
    }
}
