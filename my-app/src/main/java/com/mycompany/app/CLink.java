package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

/**
 * a CLink is a causal Link from one predicate to another
 */
public class CLink extends PlannerObject {
    //attributes
    /**
     * Predicate which the causal link connects from
     */
    private final ActionEffectRef from;

    /**
     * Predicate which the causal link connects to
     */
    private final ActionPreconditionRef to;

    //constructor

    /**
     * create a new action with a copy of the given properties
     *
     * @param from Predicate which the causal link connects from
     * @param to   Predicate which the causal link connects to
     */
    public CLink(@NotNull ActionEffectRef from, @NotNull ActionPreconditionRef to) {
        this.from = new ActionEffectRef(from);
        this.to = new ActionPreconditionRef(to);
    }

    /**
     * create a copy of a given cLink
     *
     * @param cLink to copy
     */
    public CLink(@NotNull CLink cLink) {
        this.from = new ActionEffectRef(cLink.from);
        this.to = new ActionPreconditionRef(cLink.to);
    }

    //getter

    /**
     * get the Predicate which the causal link connects from
     *
     * @return Predicate which the causal link connects from
     */
    public ActionEffectRef getFrom() {
        return from;
    }

    /**
     * get the Predicate which the causal link connects to
     *
     * @return Predicate which the causal link connects to
     */
    public ActionPreconditionRef getTo() {
        return to;
    }

    //setter

    //public methods

    /**
     * get a string representation of this CLink
     *
     * @return string representation of this CLink
     */
    public String toString() {
        return this.from.toString() + " -CL-> " + this.to.toString();
    }

    @Override
    public PlannerObject copy() {
        return new CLink(this);
    }

    @Override
    public String toStringIdentifier() {
        return "[CL" + this.from.toStringIdentifier() + "->" + this.to.toStringIdentifier() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CLink cLink)) {
            return false;
        }
        return this.from.equals(cLink.from) && this.to.equals(cLink.to);
    }
}
