package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

/**
 * a CLink is a temporal Link from one PlanStep to another PlanStep, before must be done first, than after can be done
 */
public class TLink extends PlannerObject {
    //attributes
    /**
     * PlanStep before must be done first
     */
    private final int beforePlanStepIndex;

    /**
     * PlanStep after can be done when before is done
     */
    private final int afterPlanStepIndex;

    //constructor

    /**
     * create a new TLink from a copy of a given PlanStep that must be executed before the after PlanStep can be performed
     *
     * @param beforePlanStepIndex PlanStep before must be done first
     * @param afterPlanStepIndex  PlanStep after can be done when before is done
     */
    public TLink(int beforePlanStepIndex, int afterPlanStepIndex) {
        this.beforePlanStepIndex = beforePlanStepIndex;
        this.afterPlanStepIndex = afterPlanStepIndex;
    }


    /**
     * create a copy of an existing tLink
     *
     * @param tLink to copy
     */
    public TLink(@NotNull TLink tLink) {
        this.beforePlanStepIndex = tLink.beforePlanStepIndex;
        this.afterPlanStepIndex = tLink.afterPlanStepIndex;
    }

    //getter

    /**
     * get before of this TLink
     *
     * @return before of this TLink
     */
    public int getBeforePlanStepIndex() {
        return beforePlanStepIndex;
    }

    /**
     * get after of this TLink
     *
     * @return after of this TLink
     */
    public int getAfterPlanStepIndex() {
        return afterPlanStepIndex;
    }

    //setter

    //public methods

    /**
     * create a String representation of this TLink
     *
     * @return String representation of this TLink
     */
    public String toString() {
        return this.beforePlanStepIndex + " -TL-> " + this.afterPlanStepIndex;
    }

    @Override
    public PlannerObject copy() {
        return new TLink(this);
    }

    @Override
    public String toStringIdentifier() {
        return "[TL" + this.beforePlanStepIndex + "->" + this.afterPlanStepIndex + "]";
    }
}
