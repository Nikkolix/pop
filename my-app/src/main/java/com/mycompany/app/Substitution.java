package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

/**
 * a substitution contains of a source Variable to and a destination Argument to and works like a replacement
 */
public class Substitution extends PlannerObject {
    //attributes
    /**
     * the source of this Substitution
     */
    private final Variable from;

    /**
     * the destination of this Substitution
     */
    private final Argument to;

    //constructor

    /**
     * create a new Substitution from a Variable to an Argument
     *
     * @param from Variable to substitute
     * @param to   Argument that replaces
     */
    public Substitution(@NotNull Variable from, @NotNull Argument to) {
        this.from = new Variable(from);
        this.to = (Argument)to.copy();
    }

    /**
     * create a new copy of the existing given substitution
     *
     * @param substitution to copy
     */
    public Substitution(@NotNull Substitution substitution) {
        this.from = new Variable(substitution.from);
        this.to = (Argument)substitution.to.copy();
    }

    //getter

    /**
     * get from Variable of this Substitution
     *
     * @return from Variable of this Substitution
     */
    public Variable getFrom() {
        return from;
    }

    /**
     * get to Argument of this Substitution
     *
     * @return to Argument of this Substitution
     */
    public Argument getTo() {
        return to;
    }

    //setter

    //public methods

    /**
     * get String representation of this Substitution
     *
     * @return String representation of this Substitution
     */
    public String toString() {
        return this.from.toString() + "/" + this.to;
    }

    //todo comments
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Substitution substitution)) {
            return false;
        }
        return (substitution.from.equals(this.from) && substitution.to.equals(this.to));
    }

    @Override
    public PlannerObject copy() {
        return new Substitution(this);
    }

    public String toStringIdentifier() {
        return this.from.toStringIdentifier() + "/" + this.to.toStringIdentifier();
    }
}
