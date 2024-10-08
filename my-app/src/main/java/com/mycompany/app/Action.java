package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

/**
 * an action contains all information of a STRIPS-ACT
 */
public class Action extends PlannerObject {
    //attributes
    /**
     * name of this action
     */
    private final String name;

    /**
     * list of the preconditions in the precondition of this action
     */
    private final PlannerList<ActionPrecondition> preconditions;

    /**
     * list of the effects in the precondition of this action
     */
    private final PlannerList<ActionEffect> effects;

    private int planStepIndex;
    private int operationIndex;

    //constructor

    /**
     * create a new action with copies of given properties
     *
     * @param name          of the action
     * @param preconditions of the action
     * @param effects       of the action
     * @throws IllegalArgumentException if (name == null) or (preconditions == null) or (preconditions.contains(null)) or (effects == null) or (effects.contains(null))
     */
    public Action(@NotNull String name, @NotNull PlannerList<ActionPrecondition> preconditions, @NotNull PlannerList<ActionEffect> effects) {
        this.planStepIndex = -1;
        this.operationIndex = -1;
        this.name = name;
        this.preconditions = new PlannerList<>(preconditions);
        for (int i = 0; i < this.preconditions.size(); i++) {
            this.preconditions.get(i).initIndex(i);
            for (Argument argument : this.preconditions.get(i).getArguments()) {
                if (argument instanceof Variable variable) {
                    variable.init(new ActionPreconditionRef(this.preconditions.get(i)));
                } else {
                    argument.init(new ActionPreconditionRef(this.preconditions.get(i)));
                }
            }
        }
        this.effects = new PlannerList<>(effects);
        for (int i = 0; i < this.effects.size(); i++) {
            this.effects.get(i).initIndex(i);
            for (Argument argument : this.effects.get(i).getArguments()) {
                if (argument instanceof Variable variable) {
                    variable.init(new ActionEffectRef(this.effects.get(i)));
                } else {
                    argument.init(new ActionEffectRef(this.effects.get(i)));
                }
            }
        }
    }

    public Action(@NotNull Action action) throws IllegalArgumentException {
        this.name = action.name;
        this.preconditions = new PlannerList<>(action.preconditions);
        this.effects = new PlannerList<>(action.effects);
        this.planStepIndex = action.planStepIndex;
        this.operationIndex = -1;
    }

    public Action(@NotNull Operation operation) throws IllegalArgumentException {
        this.name = operation.getName();
        this.preconditions = new PlannerList<>();
        for (int i = 0; i < operation.getNumPreconditions();i=i+1) {
            ActionPrecondition pre = new ActionPrecondition(operation.getPrecondition(i));
            if (operation.getPrecondition(i) instanceof UnequalOperationPrecondition unequalOperationPrecondition) {
                pre = new UnequalActionPrecondition(unequalOperationPrecondition);
            }
            pre.initIndex(i);
            for (Argument argument : pre.getArguments()) {
                if (argument instanceof Variable variable) {
                    variable.init(new ActionPreconditionRef(pre));
                }
            }
            this.preconditions.add(pre);
        }
        this.effects = new PlannerList<>();
        for (int i = 0; i < operation.getNumEffects();i=i+1) {
            ActionEffect eff = new ActionEffect(operation.getEffect(i));
            eff.initIndex(i);
            for (Argument argument : eff.getArguments()) {
                if (argument instanceof Variable variable) {
                    variable.init(new ActionEffectRef(eff));
                }
            }
            this.effects.add(eff);
        }
        this.planStepIndex = -1;
        this.operationIndex = operation.getIndex();
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
     * get the precondition at position index
     *
     * @param index position of precondition to get
     * @return precondition at position index
     * @throws IllegalArgumentException if (index less 0 || index greater equal this.preconditions.size())
     */
    public ActionPrecondition getPrecondition(int index) throws IllegalArgumentException {
        if (index < 0 || index >= this.preconditions.size()) {
            throw new IllegalArgumentException("index < 0 || index >= this.preconditions.size()");
        }
        return this.preconditions.get(index);
    }

    /**
     * get a copy of the preconditions
     *
     * @return copy of preconditions
     */
    public PlannerList<ActionPrecondition> getPreconditions() {
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
     * @throws IllegalArgumentException if (index less 0 || index greater equal this.effects.size())
     */

    public ActionEffect getEffect(int index) throws IllegalArgumentException {
        if (index < 0 | index >= this.effects.size()) {
            throw new IllegalArgumentException("index < 0 || index >= this.effects.size()");
        }
        return this.effects.get(index);
    }

    /**
     * get a copy of the effects
     *
     * @return copy of effects
     */
    public PlannerList<ActionEffect> getEffects() {
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

    public int getPlanStepIndex() {
        return this.planStepIndex;
    }

    public int getOperationIndex() {
        return this.operationIndex;
    }

    //setter

    //public methods

    public void initPlanStep(int planStepIndex) {
        this.planStepIndex = planStepIndex;
    }

    public void initOperation(int operationIndex) {
        this.operationIndex = operationIndex;
    }

    public PlannerList<ActionPredicate> getPredicates() {
        PlannerList<ActionPredicate> predicates = new PlannerList<>(this.preconditions);
        predicates.addAll(this.effects);
        return predicates;
    }

    /**
     * get all variables in this action
     *
     * @return list of all variables
     */
    public PlannerList<Variable> calcVariableSet() {
        PlannerList<Variable> variables = new PlannerList<>();
        for (ActionPredicate precondition : this.getPredicates()) {
            for (Argument argument : precondition.getArguments()) {
                if (argument instanceof Variable) {
                    if (!variables.contains((Variable) argument)) {
                        variables.add((Variable) argument);
                    }
                }
            }
        }
        return variables;
    }

    public void substitute(@NotNull Substitution substitution) {
        for (int i = 0; i < this.preconditions.size(); i = i + 1) {
            this.preconditions.get(i).substitute(substitution);
        }
        for (int i = 0; i < this.effects.size(); i = i + 1) {
            this.effects.get(i).substitute(substitution);
        }
    }

    public void substitute(@NotNull PlannerList<Substitution> substitutions) {
        for (int i = 0; i < this.preconditions.size(); i = i + 1) {
            this.preconditions.get(i).substitute(substitutions);
        }
        for (int i = 0; i < this.effects.size(); i = i + 1) {
            this.effects.get(i).substitute(substitutions);
        }
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
     * @throws IllegalArgumentException if (indentation less 0)
     */
    public String toString(int indentation) throws IllegalArgumentException {
        if (indentation < 0) {
            throw new IllegalArgumentException("indentation < 0");
        }
        StringBuilder out = new StringBuilder();
        out.append("\t".repeat(indentation)).append("ACT: ").append(this.name).append("(");
        PlannerList<Variable> variables = this.calcVariableSet();
        for (int i = 0; i < variables.size() - 1; i = i + 1) {
            out.append(variables.get(i).toString()).append(", ");
        }
        if (!variables.isEmpty()) {
            out.append(variables.get(variables.size() - 1).toString());
        }
        out.append(")\r\n");

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
     * this action is equal to an Object if it is an Action, and the id, name, priority, the preconditions and effect, have the same values
     *
     * @param obj to check if equal
     * @return true / false
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Action action)) {
            return false;
        }
        if (this.operationIndex != action.operationIndex || this.planStepIndex != action.planStepIndex || !(this.name.equals(action.name)) || this.effects.size() != action.effects.size() || this.preconditions.size() != action.preconditions.size()) {
            return false;
        }
        for (int i = 0; i < this.preconditions.size(); i = i + 1) {
            if (!this.preconditions.get(i).equals(action.getPreconditions().get(i))) {
                return false;
            }
        }
        for (int i = 0; i < this.effects.size(); i = i + 1) {
            if (!this.effects.get(i).equals(action.getEffects().get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public PlannerObject copy() {
        return new Action(this);
    }

    @Override
    public String toStringIdentifier() {
        return "[" + this.name + this.preconditions.toStringIdentifier() + this.effects.toStringIdentifier() + this.operationIndex + "/" + this.planStepIndex + "]";
    }
}
