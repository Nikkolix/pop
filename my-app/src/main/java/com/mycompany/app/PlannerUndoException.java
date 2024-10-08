package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public class PlannerUndoException extends PlannerException {
    //attributes
    private final UndoType undoType;

    //constructors
    public PlannerUndoException(@NotNull UndoType undoType, @NotNull String message) {
        super(message);
        this.undoType = undoType;
    }

    public PlannerUndoException(@NotNull UndoType undoType, @NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
        this.undoType = undoType;
    }

    //getter
    public UndoType getUndoType() {
        return this.undoType;
    }
}
