package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public abstract class PredicateRef extends PlannerObject {
    private final int planStepIndex;

    public PredicateRef(int planStepIndex) {
        this.planStepIndex = planStepIndex;
    }

    public PredicateRef(@NotNull PredicateRef predicateRef) {
        this.planStepIndex = predicateRef.planStepIndex;
    }

    public int getPlanStepIndex() {
        return this.planStepIndex;
    }
}
