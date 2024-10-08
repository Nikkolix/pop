package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

/**
 * a Variable is an Argument with normally lower case name, that can be substituted
 */
public class Variable extends Argument {
    private int id;

    //constructor

    /**
     * create a new Variable with a specified name
     *
     * @param name of the new Variable
     */
    public Variable(@NotNull String name) {
        super(name);
        this.id = -1;
    }

    /**
     * create a copy of the given Variable
     *
     * @param variable to copy
     */
    public Variable(@NotNull Variable variable) {
        super(variable);
        this.id = variable.id;
    }

    public Variable(@NotNull Variable variable, @NotNull Substitution substitution) {
        super(variable,substitution);
        this.id = variable.id;
    }

    public int getId() {
        return this.id;
    }

    //public methods

    @Override
    public void init(@NotNull PredicateRef actionPredicate) {
        super.init(actionPredicate);
        this.id = actionPredicate.getPlanStepIndex();
    }

    /**
     * Variable this can be substituted to the other
     *
     * @param other Argument to try substitute with
     * @return true
     */
    public boolean canSubstitute(@NotNull Argument other) {
        return true;
    }

    public boolean canSubstituteTo(@NotNull Argument other) {
        return true;
    }

    /**
     * Variable this can be substituted to the other
     *
     * @param other Argument to try substitute with
     * @return Substitution
     */
    public Substitution calcSubstitution(@NotNull Argument other) {
        if (this.equals(other)) {
            return null;
        }
        return new Substitution(this, other);
    }

    /**
     * Perform the substitution on this Argument and return result.
     *
     * @param substitution Substitution to perform
     * @return Argument
     */
    Argument substitute(@NotNull Substitution substitution) {
        if (substitution.getFrom().equals(this) ) {
            Argument to = substitution.getTo();

            substitution.getFrom().setSubstitution(this.getSubstitution());

            if (to instanceof Literal l) {
                Literal literal = new Literal(l,substitution);
                literal.init(l.getActionPredicate());
                return literal;
            } else if (to instanceof Variable v) {
                Variable variable = new Variable(v,substitution);
                variable.init(v.getActionPredicate());
                return variable;
            } else {
                throw new Error("Argument has invalid subclass");
            }
        }
        return this;
    }

    /**
     * this Variable is equal to an Object if it is also a Variable and the names are equal
     *
     * @param obj to check if equal
     * @return true/false
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Variable variable)) {
            return false;
        }
        return variable.getName().equals(this.getName()) && variable.id == this.id;
    }

    @Override
    public PlannerObject copy() {
        return new Variable(this);
    }

    public String toStringIdentifier() {
        return "V" + this.getName();
    }


}
