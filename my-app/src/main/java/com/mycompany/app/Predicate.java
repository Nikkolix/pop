package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

/**
 * a predicate consists of multiple arguments, a name and can be negated
 */
public abstract class Predicate extends PlannerObject {
    //attributes

    private final String name;

    private final boolean negation;

    private final PlannerList<Argument> arguments;

    private int index;

    //constructor

    public Predicate(@NotNull String name, boolean negation, @NotNull PlannerList<@NotNull Argument> arguments) {
        this.name = name;
        this.negation = negation;
        this.arguments = new PlannerList<>(arguments);
        this.index = -1;
    }

    public Predicate(@NotNull Predicate predicate) {
        this.name = predicate.name;
        this.negation = predicate.negation;
        this.arguments = new PlannerList<>(predicate.arguments);
        this.index = predicate.index;
    }

    //getter

    public String getName() {
        return name;
    }

    public boolean isNegation() {
        return negation;
    }

    public Argument getArgument(int index) throws IllegalArgumentException {
        return this.arguments.get(index);
    }

    public PlannerList<Argument> getArguments() {
        return arguments;
    }

    public int getNumArguments() {
        return this.arguments.size();
    }

    public int getIndex() {
        return this.index;
    }

    //setter

    //public methods

    public void initIndex(int index) {
        this.index = index;
    }


    /**
     * Checks if this and other can get substituted to match. (negation is ignored)
     *
     * @param other Predicate to try substitute with
     * @return true/false
     */
    public boolean canSubstitute(@NotNull Predicate other, boolean checkNegation) {
        if (checkNegation && this.negation != other.negation) {
            return false;
        }
        if (this.arguments.size() != other.getNumArguments() || !other.name.equals(this.name)) {
            return false;
        }
        for (int i = 0; i < this.arguments.size(); i = i + 1) {
            if (!this.arguments.get(i).canSubstitute(other.getArgument(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean canSubstituteTo(@NotNull Predicate other) {
        if (this.arguments.size() != other.getNumArguments() || !other.name.equals(this.name)) {
            return false;
        }
        for (int i = 0; i < this.arguments.size(); i = i + 1) {
            if (!this.arguments.get(i).canSubstituteTo(other.getArgument(i))) {
                return false;
            }
        }
        return true;
    }

    public void substitute(@NotNull Substitution substitution) {
        for (int i = 0; i < this.getArguments().size(); i = i + 1) {
            this.arguments.set(i,this.arguments.get(i).substitute(substitution));
        }
    }

    public void substitute(@NotNull PlannerList<Substitution> substitutions) {
        for (Substitution substitution : substitutions) {
            for (int i = 0; i < this.getArguments().size(); i = i + 1) {
                this.arguments.set(i,this.arguments.get(i).substitute(substitution));
            }
        }
    }

    /**
     * Get a ArrayList of Substitutions if this and other can get substituted to match, otherwise return null. (negation is ignored)
     *
     * @param other Predicate to try substitute with
     * @return null/ArrayList of Substitutions
     */
    public PlannerList<Substitution> calcSubstitution(@NotNull Predicate other) {
        PlannerList<Substitution> substitutions = new PlannerList<>();
        if (this.arguments.size() != other.getNumArguments()) {
            return null;
        }
        for (int i = 0; i < this.arguments.size(); i = i + 1) {
            if (!this.arguments.get(i).canSubstitute(other.getArgument(i))) {
                return null; //todo
            }
            if (this.arguments.get(i).equals(other.getArgument(i))) {
                continue;
            }
            Substitution substitution = this.arguments.get(i).calcSubstitution(other.getArgument(i));
            if (substitution != null) {
                substitutions.add(substitution);
            }

        }
        return substitutions;
    }

    /**
     * get a string representation of this Predicate
     *
     * @return string representation of this Predicate
     */
    public String toString() {
        StringBuilder out = new StringBuilder();
        if (this.negation) {
            out.append("!");
        }
        out.append(this.name).append("(");
        for (int i = 0; i < this.arguments.size() - 1; i = i + 1) {
            out.append(this.arguments.get(i).toString()).append(", ");
        }
        if (!this.arguments.isEmpty()) {
            out.append(this.arguments.get(this.arguments.size() - 1).toString());
        }
        out.append(")");
        return out.toString();
    }

    @Override
    public abstract boolean equals(Object obj);

    public boolean equalsValue(Object obj) {
        if (!(obj instanceof Predicate predicate)) {
            return false;
        }
        if (!predicate.getName().equals(this.getName()) || predicate.getArguments().size() != this.getArguments().size()) {
            return false;
        }
        return predicate.arguments.equals(this.arguments);
    }

    public abstract String toStringIdentifier();
}
