package dev.omega.arcane.ast;

import dev.omega.arcane.Molang;
import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.omega.arcane.reference.MolangVariableStorage;
import dev.omega.arcane.reference.ReferenceType;
import java.util.ArrayList;
import java.util.List;

public record ReferenceAssignmentExpression(ReferenceType type, String value, MolangExpression assignmentExpression) implements MolangExpression {

    @Override
    public float evaluate() {
        return 0.0f;
    }

    @Override
    public MolangExpression bind(ExpressionBindingContext context, Object[] values) {
        switch (type) {
            case VARIABLE -> {
                // Get all binding values that are variable storages
                List<MolangVariableStorage> variableStorages = new ArrayList<>(values.length);
                for (Object value : values)
                    if (value instanceof MolangVariableStorage variableStorage)
                        variableStorages.add(variableStorage);

                if (variableStorages.isEmpty()) {
                    Molang.LOGGER.warning("Was not able to bind %s %s to an assignable value, no variable storages!".formatted(type.name().toLowerCase(), value));
                    return this;
                }

                // Prioritize writing to variable storage that already contains the value or whichever is first
                MolangVariableStorage target = variableStorages.getFirst();
                for (MolangVariableStorage variableStorage : variableStorages) {
                    if (variableStorage.has(value)) {
                        target = variableStorage;
                        break;
                    }
                }

                return target.createAssignmentExpression(value, assignmentExpression.bind(context, values));
            }
            case TEMP -> {
                MolangVariableStorage tempVariableStorage = context.getTempVariableStorage();
                return tempVariableStorage.createAssignmentExpression(value, assignmentExpression.bind(context, values));
            }
        }

        Molang.LOGGER.warning("Was not able to bind %s %s to an assignable value!".formatted(type.name().toLowerCase(), value));
        return this;
    }

}
