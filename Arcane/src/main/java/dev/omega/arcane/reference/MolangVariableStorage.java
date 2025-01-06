package dev.omega.arcane.reference;

import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.ast.ObjectAwareExpression;

public interface MolangVariableStorage {

    /**
     * Returns the float value of the variable stored under the identifier, or 0 if nothing is stored
     *
     * @param identifier The identifier to get the value of
     * @return the identifier value, or 0 if nothing is stored
     */
    float get(String identifier);

    /**
     * Returns true if this storage contains a value for the identifier, false otherwise
     *
     * @param identifier The identifier to check for the value of
     * @return true if this storage contains a value for the identifier, false otherwise
     */
    boolean has(String identifier);

    /**
     * Sets the float value of the variable stored under the identifier.
     * Does nothing if {@link #readonly()} returns true.
     *
     * @param identifier The identifier to set the value of
     * @param value The identifier value to assign
     */
    default void set(String identifier, float value) {

    }

    /**
     * @return true if this variable storage is read-only, false if it can be written to
     */
    default boolean readonly() {
        return true;
    }

    default MolangExpression createEvaluationExpression(String identifier) {
        return new ObjectAwareExpression<>(this) {
            @Override
            public float evaluate() {
                return value.get(identifier);
            }
        };
    }

    default MolangExpression createAssignmentExpression(String identifier, MolangExpression assignmentValue) {
        return new ObjectAwareExpression<>(this) {
            @Override
            public float evaluate() {
                value.set(identifier, assignmentValue.evaluate());
                return value.get(identifier);
            }
        };
    }

}
