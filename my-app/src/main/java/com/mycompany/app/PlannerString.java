package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public class PlannerString extends PlannerObject {
    private final String string;

    PlannerString(@NotNull PlannerString string) {
        this.string = string.string;
    }

    PlannerString(@NotNull String string) {
        this.string = string;
    }

    public boolean equals(@NotNull String string) {
        return this.string.equals(string);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlannerString plannerString)) {
            return false;
        }
        return plannerString.string.equals(this.string);
    }

    public String toString() {
        return this.string;
    }

    @Override
    public PlannerObject copy() {
        return new PlannerString(this);
    }

    @Override
    public String toStringIdentifier() {
        return this.string.replaceAll("\t", "\\\\t").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
    }
}
