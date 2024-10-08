package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public class ActionPrecondition extends ActionPredicate {
    //constructors
    public ActionPrecondition(@NotNull String name, boolean negation, @NotNull PlannerList<Argument> arguments) {
        super(name, negation, arguments);
    }

    public ActionPrecondition(@NotNull ActionPrecondition actionEffect) {
        super(actionEffect);
    }

    public ActionPrecondition(@NotNull OperationPrecondition operationPrecondition) {
        super(operationPrecondition.getName(), operationPrecondition.isNegation(), new PlannerList<>(operationPrecondition.getArguments()));
        this.initPlanStepIndex(-1);
    }

    //public methods

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ActionPrecondition actionPrecondition)) {
            return false;
        }
        if (actionPrecondition.getIndex() != this.getIndex() || actionPrecondition.getPlanStepIndex() != this.getPlanStepIndex() || actionPrecondition.isNegation() != this.isNegation() || !actionPrecondition.getName().equals(this.getName()) || actionPrecondition.getArguments().size() != this.getArguments().size()) {
            return false;
        }
        for (int i = 0; i < this.getArguments().size(); i = i + 1) {
            if (!this.getArguments().get(i).equals(actionPrecondition.getArguments().get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public PlannerObject copy() {
        if (this instanceof UnequalActionPrecondition) {
            return new UnequalActionPrecondition(this);
        }
        return new ActionPrecondition(this);
    }

    @Override
    public String toStringIdentifier() {
        String out = "[AP";
        if (this.isNegation()) {
            out = out + "-";
        } else {
            out = out + "+";
        }
        out = out + this.getName();
        out = out + this.getArguments().toStringIdentifier();
        out = out + this.getIndex() + "/";
        out = out + this.getPlanStepIndex() + "]";
        return out;
    }
}
