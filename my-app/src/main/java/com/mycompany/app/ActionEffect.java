package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public class ActionEffect extends ActionPredicate {
    //constructors
    public ActionEffect(@NotNull String name, boolean negation, @NotNull PlannerList<Argument> arguments) {
        super(name, negation, arguments);
    }

    public ActionEffect(@NotNull ActionEffect actionEffect) {
        super(actionEffect);
    }

    public ActionEffect(@NotNull OperationEffect operationEffect) {
        super(operationEffect.getName(), operationEffect.isNegation(), new PlannerList<>(operationEffect.getArguments()));
    }

    //public methods

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ActionEffect actionEffect)) {
            return false;
        }
        if (actionEffect.getIndex() != this.getIndex() || actionEffect.getPlanStepIndex() != this.getPlanStepIndex() || actionEffect.isNegation() != this.isNegation() || !actionEffect.getName().equals(this.getName()) || actionEffect.getArguments().size() != this.getArguments().size()) {
            return false;
        }
        for (int i = 0; i < this.getArguments().size(); i = i + 1) {
            if (!this.getArguments().get(i).equals(actionEffect.getArguments().get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public PlannerObject copy() {
        return new ActionEffect(this);
    }

    @Override
    public String toStringIdentifier() {
        String out = "[AE";
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
