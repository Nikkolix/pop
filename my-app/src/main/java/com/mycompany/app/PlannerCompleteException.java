package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public class PlannerCompleteException extends PlannerException {
    //attributes

    //constructors
    public PlannerCompleteException(@NotNull String message) {
        super(message);
    }

    public PlannerCompleteException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }

}