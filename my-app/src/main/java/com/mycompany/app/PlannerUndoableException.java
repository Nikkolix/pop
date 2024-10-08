package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public class PlannerUndoableException extends PlannerException {
    //attributes

    //constructors
    public PlannerUndoableException(@NotNull String message) {
        super(message);
    }

    public PlannerUndoableException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }

}