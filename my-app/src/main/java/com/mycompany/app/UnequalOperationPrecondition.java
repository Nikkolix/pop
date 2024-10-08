package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public class UnequalOperationPrecondition extends OperationPrecondition {
    public UnequalOperationPrecondition(@NotNull PlannerList<Argument> arguments) {
        super("neq", false, arguments);
    }

    public UnequalOperationPrecondition(@NotNull OperationPrecondition operationPrecondition) {
        super(operationPrecondition);
    }
}
