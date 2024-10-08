package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public class PlannerException extends Exception {
    //attributes

    //constructors
    public PlannerException(@NotNull String message) {
        super(message);
    }

    public PlannerException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
