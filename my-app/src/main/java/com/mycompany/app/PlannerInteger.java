package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public class PlannerInteger extends PlannerObject {
    private final Integer integer;

    PlannerInteger(@NotNull PlannerInteger integer) {
        this.integer = integer.integer;
    }

    PlannerInteger(int integer) {
        this.integer = integer;
    }

    public Integer getInteger() {
        return this.integer;
    }

    public int getInt() {
        return this.integer;
    }

    public boolean equals(int integer) {
        return this.integer.equals(integer);
    }

    public boolean equals(@NotNull Integer integer) {
        return this.integer.equals(integer);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlannerInteger objInt)) {
            return false;
        }
        return objInt.getInt() == this.getInt();
    }

    @Override
    public PlannerObject copy() {
        return new PlannerInteger(this);
    }

    @Override
    public String toStringIdentifier() {
        return this.integer.toString();
    }
}
