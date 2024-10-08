package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * an Argument is either a Literal or a Variable
 */
public abstract class Argument extends PlannerObject {
    //attributes
    private final String name;

    private PredicateRef actionPredicate;

    private Substitution substitution;

    //constructor
    public Argument(@NotNull String name) {
        this.name = name;
        this.actionPredicate = null;
        this.substitution = null;
    }

    public Argument(@NotNull Argument argument) {
        this.name = argument.name;
        if (argument.actionPredicate == null) {
            this.actionPredicate = null;
        } else {
            this.actionPredicate = (PredicateRef) argument.actionPredicate.copy();
        }
        if (argument.substitution != null) {
            this.substitution = new Substitution(argument.substitution);
        }
    }

    public Argument(@NotNull Argument argument, @NotNull Substitution substitution) {
        this.name = argument.name;
        if (argument.actionPredicate == null) {
            this.actionPredicate = null;
        } else {
            this.actionPredicate = (PredicateRef) argument.actionPredicate.copy();
        }
        this.substitution = new Substitution(substitution);
    }

    //getter

    public String getName() {
        return name;
    }

    public PredicateRef getActionPredicate() {
        return this.actionPredicate;
    }

    public Substitution getSubstitution() {
        return this.substitution;
    }

    //setter

    public void setSubstitution(Substitution substitution) {
        if (substitution == null) {
            this.substitution = null;
            return;
        }
        this.substitution = new Substitution(substitution);
    }

    //public methods

    public void init(PredicateRef actionPredicate) {
        this.actionPredicate = actionPredicate;
    }

    abstract public boolean canSubstitute(@NotNull Argument other);

    abstract public boolean canSubstituteTo(@NotNull Argument other);

    abstract Substitution calcSubstitution(@NotNull Argument other);

    abstract Argument substitute(@NotNull Substitution substitution);

    public String toString() {
        StringBuilder out = new StringBuilder();

        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();

        Substitution substitution = this.substitution;
        while (substitution != null) {
            names.add(substitution.getFrom().getName());
            ids.add(substitution.getFrom().getId());
            substitution = substitution.getFrom().getSubstitution();
        }
        for (int k = names.size()-1; k >= 0 ; k=k-1) {
            out.append(names.get(k));
            if (ids.get(k) != -1) {
                out.append("[").append(ids.get(k)).append("]");
            }
            out.append("/");
        }

        out.append(this.name);
        if (this instanceof Variable variable) {
            out.append("[").append(variable.getId()).append("]");
        }

        return out.toString();
    }

    @Override
    public abstract boolean equals(Object obj);
}
