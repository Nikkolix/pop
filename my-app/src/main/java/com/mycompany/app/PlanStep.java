package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

/**
 * a PlanStep consists of an Action, a list of done Substitution and a state that is valid after the execution of the PlanStep
 */
public class PlanStep extends PlannerObject {
    //attributes
    /**
     * the action that is done in this PlanStep
     */
    private final Action action;

    /**
     * a list of the Substitutions performed in this PlanStep
     */
    private final PlannerList<Substitution> substitutions;

    private int index;

    //constructor

    /**
     * create a new PlanStep with a copy of a given Action and list of Substitutions
     *
     * @param action        to perform and copy into new PlanStep
     * @param substitutions to perform and copy into new PlanStep
     */
    public PlanStep(@NotNull Action action, @NotNull PlannerList<Substitution> substitutions) {
        this.index = -1;
        this.action = new Action(action);
        this.action.initPlanStep(this.index);
        this.substitutions = new PlannerList<>(substitutions);
    }

    /**
     * create a copy of a given PlanStep
     *
     * @param planStep to copy
     */
    public PlanStep(@NotNull PlanStep planStep) {
        this.index = planStep.index;
        this.action = new Action(planStep.action);
        this.substitutions = new PlannerList<>(planStep.substitutions);
    }

    //getter

    /**
     * get action of this PlanStep
     *
     * @return action
     */
    public Action getAction() {
        return action;
    }

    /**
     * get a copy of the substitutions of this PlanStep
     *
     * @return copy of the substitutions
     */
    public PlannerList<Substitution> getSubstitutions() {
        return substitutions;
    }

    public int getIndex() {
        return this.index;
    }

    //setter

    //public methods
    /**
     * add a copy of a substitution
     *
     * @param substitution to add
     */
    public void addSubstitution(@NotNull Substitution substitution) {
        substitutions.add(new Substitution(substitution));
    }

    public void initIndex(int index) {
        this.index = index;
        this.action.initPlanStep(index);
        for (int i = 0; i < this.action.getPreconditions().size(); i = i + 1) {
            this.action.getPrecondition(i).initPlanStepIndex(index);
            this.action.getPrecondition(i).initIndex(i);
            for (Argument argument : this.action.getPrecondition(i).getArguments()) {
                argument.init(new ActionPreconditionRef(i, index));
            }
        }
        for (int i = 0; i < this.action.getEffects().size(); i = i + 1) {
            this.action.getEffect(i).initPlanStepIndex(index);
            this.action.getEffect(i).initIndex(i);
            for (Argument argument : this.action.getEffect(i).getArguments()) {
                argument.init(new ActionEffectRef(i, index));
            }
        }
    }

    public void substitute(@NotNull Substitution substitution) {
        this.getAction().substitute(substitution);
    }

    public void substitute(PlannerList<@NotNull Substitution> substitutions) {
        this.getAction().substitute(substitutions);
    }


    /**
     * get a String representation of this PlanStep
     *
     * @return String representation of this PlanStep
     */
    public String toString() {
        return this.toString(0);
    }

    /**
     * get a string representation of this PlanStep with given indentation
     *
     * @param indentation of the string representation
     * @return string representation of this PlanStep
     * @throws IllegalArgumentException if (indentation less 0)
     */
    public String toString(int indentation) throws IllegalArgumentException {
        if (indentation < 0) {
            throw new IllegalArgumentException("indentation < 0");
        }
        StringBuilder out = new StringBuilder();
        out.append("\t".repeat(indentation)).append("ACTION: \r\n").append(this.action.toString(indentation + 1));
        out.append("\t".repeat(indentation)).append("SUBSTITUTIONS: {");
        for (int i = 0; i < this.substitutions.size() - 1; i = i + 1) {
            out.append(this.substitutions.get(i).toString()).append(", ");
        }
        if (!this.substitutions.isEmpty()) {
            out.append(this.substitutions.get(this.substitutions.size() - 1).toString());
        }
        out.append("}\r\n");
        return out.toString();
    }

    //todo comment
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlanStep step)) {
            return false;
        }
        if (step.index != this.index) {
            return false;
        }
        if (step.substitutions.size() != this.substitutions.size()) {
            return false;
        }
        for (int i = 0; i < step.substitutions.size(); i = i + 1) {
            if (!step.substitutions.get(i).equals(this.substitutions.get(i))) {
                return false;
            }
        }
        return this.getAction().equals(step.getAction());
    }


    @Override
    public PlannerObject copy() {
        return new PlanStep(this);
    }

    @Override
    public String toStringIdentifier() {
        return "[" + this.action.toStringIdentifier() + this.index + this.substitutions.toStringIdentifier() + "]";
    }
}
