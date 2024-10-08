package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public abstract class ActionPredicate extends Predicate {
    //attributes

    private int planStepIndex;

    //constructor

    public ActionPredicate(@NotNull String name, boolean negation, @NotNull PlannerList<Argument> arguments) {
        super(name, negation, arguments);
        this.planStepIndex = -1;
    }

    public ActionPredicate(@NotNull ActionPredicate actionPredicate) {
        super(actionPredicate);
        this.planStepIndex = actionPredicate.planStepIndex;
    }

    public int getPlanStepIndex() {
        return this.planStepIndex;
    }

    //public methods

    public void initPlanStepIndex(int planStepIndex) {
        this.planStepIndex = planStepIndex;
        if (this instanceof ActionPrecondition actionPrecondition) {
            for (Argument argument : this.getArguments()) {
                argument.init(new ActionPreconditionRef(actionPrecondition));
            }
        } else if (this instanceof ActionEffect actionEffect) {
            for (Argument argument : this.getArguments()) {
                argument.init(new ActionEffectRef(actionEffect));
            }
        } else {
            throw new Error("Argument has invalid instance");
        }
    }
}
