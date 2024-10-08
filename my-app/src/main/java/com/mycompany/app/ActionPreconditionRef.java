package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public class ActionPreconditionRef extends PredicateRef {
    //attributes

    private final int preconditionIndex;

    //constructors

    public ActionPreconditionRef(int preconditionIndex, int planStepIndex) {
        super(planStepIndex);
        this.preconditionIndex = preconditionIndex;
    }

    public ActionPreconditionRef(@NotNull ActionPreconditionRef actionPreconditionRef) {
        super(actionPreconditionRef);
        this.preconditionIndex = actionPreconditionRef.preconditionIndex;
    }

    public ActionPreconditionRef(@NotNull ActionPrecondition actionPrecondition) {
        super(actionPrecondition.getPlanStepIndex());
        this.preconditionIndex = actionPrecondition.getIndex();
    }

    //getter

    public int getPreconditionIndex() {
        return this.preconditionIndex;
    }

    //setter

    //public methods

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ActionPreconditionRef actionPreconditionRef)) {
            return false;
        }
        return this.preconditionIndex == actionPreconditionRef.preconditionIndex && this.getPlanStepIndex() == actionPreconditionRef.getPlanStepIndex();
    }



    @Override
    public String toString() {
        return "(" + this.getPlanStepIndex() + ", " + preconditionIndex + ")";
    }

    @Override
    public PlannerObject copy() {
        return new ActionPreconditionRef(this);
    }

    @Override
    public String toStringIdentifier() {
        return "[APR" + preconditionIndex + "/" + this.getPlanStepIndex() + "]";
    }
}
