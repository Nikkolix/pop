package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

/**
 * a Literal is a constant kind of Argument that can't get substituted
 */
public class Literal extends Argument {
    //constructor

    /**
     * create a new Literal with a specified name
     *
     * @param name of the new Literal
     */
    public Literal(@NotNull String name) {
        super(name);
    }

    /**
     * create a copy of the given Literal
     *
     * @param literal to copy
     */
    public Literal(@NotNull Literal literal) {
        super(literal);
    }

    public Literal(@NotNull Literal literal, @NotNull Substitution substitution) {
        super(literal,substitution);
    }

    //public methods

    /**
     * checks if other can get substituted to match this
     *
     * @param other Argument to try substitute with
     * @return true/false
     */
    public boolean canSubstitute(@NotNull Argument other) {
        return other instanceof Variable || this.equals(other);
    }

    public boolean canSubstituteTo(@NotNull Argument other) {
        return this.equals(other);
    }

    /**
     * get a Substitution if other can get substituted to match this
     *
     * @param other Argument to try substitute with
     * @return ArrayList of Substitutions
     */
    public Substitution calcSubstitution(@NotNull Argument other) {
        if (other instanceof Variable v) {
            return new Substitution(v, this);
        }
        throw new IllegalArgumentException("can't substitute");
    }

    /**
     * nothing to substitute on a Literal, method needed for inheritance
     *
     * @param substitution to perform
     * @return this Argument
     */
    Argument substitute(@NotNull Substitution substitution) {
        return this;
    }

    /**
     * another Object is equal to this Literal if it is a Literal and the names are equal
     *
     * @param obj to check if equal
     * @return true/false
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Literal)) {
            return false;
        }
        return ((Literal) obj).getName().equals(this.getName());
    }

    @Override
    public PlannerObject copy() {
        return new Literal(this);
    }

    public String toStringIdentifier() {
        return "L" + this.getName();
    }
}
