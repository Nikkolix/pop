package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public class ActionEffectRef extends PredicateRef {
    private final int effectIndex;

    //constructor

    public ActionEffectRef(int effectIndex, int planStepIndex) {
        super(planStepIndex);
        this.effectIndex = effectIndex;
    }

    public ActionEffectRef(@NotNull ActionEffectRef actionEffectRef) {
        super(actionEffectRef);
        this.effectIndex = actionEffectRef.effectIndex;
    }

    public ActionEffectRef(@NotNull ActionEffect actionEffect) {
        super(actionEffect.getPlanStepIndex());
        this.effectIndex = actionEffect.getIndex();
    }

    //getter

    public int getEffectIndex() {
        return effectIndex;
    }

    //setter

    //public methods

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ActionEffectRef actionEffectRef)) {
            return false;
        }
        return this.effectIndex == actionEffectRef.effectIndex && this.getPlanStepIndex() == actionEffectRef.getPlanStepIndex();
    }

    @Override
    public String toString() {
        return "(" + getPlanStepIndex() + ", " + effectIndex + ")";
    }

    @Override
    public PlannerObject copy() {
        return new ActionEffectRef(this);
    }

    @Override
    public String toStringIdentifier() {
        return "[AER" + effectIndex + "/" + getPlanStepIndex() + "]";
    }
}
