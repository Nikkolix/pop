package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

/**
 * an action contains all information of a STRIPS-ACT
 */
public class Operation extends PlannerObject {
    //attributes
    /**
     * name of this action
     */
    private final String name;

    /**
     * priority of this action
     */
    private final int priority;

    /**
     * list of the preconditions in the precondition of this action
     */
    private final PlannerList<OperationPrecondition> preconditions;

    /**
     * list of the effects in the precondition of this action
     */
    private final PlannerList<OperationEffect> effects;

    private int index;

    //constructor
    public Operation(@NotNull String name, int priority, @NotNull PlannerList<OperationPrecondition> preconditions, @NotNull PlannerList<OperationEffect> effects) {
        this.name = name;
        this.priority = priority;
        this.preconditions = new PlannerList<>(preconditions);
        for (int i = 0; i < this.preconditions.size();i=i+1) {
            this.preconditions.get(i).initIndex(i);
        }
        this.effects = new PlannerList<>(effects);
        for (int i = 0; i < this.effects.size();i=i+1) {
            this.effects.get(i).initIndex(i);
        }
        this.index = -1;
    }

    public Operation(@NotNull Operation operation) {
        this.name = operation.name;
        this.priority = operation.priority;
        this.preconditions = new PlannerList<>(operation.preconditions);
        this.effects = new PlannerList<>(operation.effects);
        this.index = operation.index;
    }

    //getter

    /**
     * get the name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * get the priority
     *
     * @return priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * get the precondition at position index
     *
     * @param index position of precondition to get
     * @return precondition at position index
     */
    public OperationPrecondition getPrecondition(int index) {
        return this.preconditions.get(index);
    }

    /**
     * get a copy of the preconditions
     *
     * @return copy of preconditions
     */
    public PlannerList<OperationPrecondition> getPreconditions() {
        return new PlannerList<>(preconditions);
    }

    /**
     * get the number of preconditions
     *
     * @return number of preconditions
     */
    public int getNumPreconditions() {
        return this.preconditions.size();
    }

    /**
     * get the effect at position index
     *
     * @param index position of effect to get
     * @return effect at position index
     */
    public OperationEffect getEffect(int index) {
        return this.effects.get(index);
    }

    /**
     * get a copy of the effects
     *
     * @return copy of effects
     */
    public PlannerList<OperationEffect> getEffects() {
        return new PlannerList<>(this.effects);
    }

    /**
     * get the number of effects
     *
     * @return number of effects
     */
    public int getNumEffects() {
        return this.effects.size();
    }

    public int getIndex() {
        return this.index;
    }

    //setter
    public void init(int index) {
        this.index = index;
    }

    //public methods

    /**
     * get all variables in this action
     *
     * @return list of all variables
     */
    public PlannerList<Variable> calcVariableSet() {
        PlannerList<Variable> variables = new PlannerList<>();
        for (Predicate precondition : this.preconditions) {
            for (Argument argument : precondition.getArguments()) {
                if (argument instanceof Variable) {
                    if (!variables.contains((Variable) argument)) {
                        variables.add((Variable) argument);
                    }
                }
            }
        }
        for (Predicate effect : this.effects) {
            for (Argument argument : effect.getArguments()) {
                if (argument instanceof Variable) {
                    if (!variables.contains((Variable) argument)) {
                        variables.add((Variable) argument);
                    }
                }
            }
        }
        return variables;
    }

    /**
     * get a string representation of this action
     *
     * @return string representation of this action
     */
    public String toString() {
        return this.toString(0);
    }

    /**
     * get a string representation of this action with a given indentation
     *
     * @param indentation to add to the representation
     * @return string representation of this action with a given indentation
     */
    public String toString(int indentation) {
        StringBuilder out = new StringBuilder("\t".repeat(indentation) + "PRIORITY: " + this.priority + "\r\n");
        out.append("\t".repeat(indentation)).append("ACT: ").append(this.name).append(this.calcVariableSet().toString("(", ")", ",")).append("\r\n");

        out.append("\t".repeat(indentation)).append("PRE: {");
        for (int i = 0; i < this.preconditions.size() - 1; i = i + 1) {
            out.append(this.preconditions.get(i).toString()).append(", ");
        }
        if (!this.preconditions.isEmpty()) {
            out.append(this.preconditions.get(preconditions.size() - 1).toString());
        }
        out.append("}\r\n");

        out.append("\t".repeat(indentation)).append("EFF: {");
        for (int i = 0; i < this.effects.size() - 1; i = i + 1) {
            out.append(this.effects.get(i).toString()).append(", ");
        }
        if (!this.effects.isEmpty()) {
            out.append(this.effects.get(this.effects.size() - 1).toString());
        }
        out.append("}\r\n");

        return out.toString();
    }

    /**
     * this action is equal to an Object if it is an Action, and the index, name, priority, the preconditions and effect, have the same values
     *
     * @param obj to check if equal
     * @return true / false
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Operation operation)) {
            return false;
        }
        if (this.index != operation.index || !(this.name.equals(operation.name)) || this.priority != operation.priority || this.effects.size() != operation.effects.size() || this.preconditions.size() != operation.preconditions.size()) {
            return false;
        }
        for (int i = 0; i < this.preconditions.size(); i = i + 1) {
            if (!this.preconditions.get(i).equals(operation.getPreconditions().get(i))) {
                return false;
            }
        }
        for (int i = 0; i < this.effects.size(); i = i + 1) {
            if (!this.effects.get(i).equals(operation.getEffects().get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public PlannerObject copy() {
        return new Operation(this);
    }

    @Override
    public String toStringIdentifier() {
        return this.name + this.preconditions.toStringIdentifier() + this.effects.toStringIdentifier() + this.index;
    }
}
