package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public class UnequalActionPrecondition extends ActionPrecondition {
    public UnequalActionPrecondition(@NotNull PlannerList<Argument> arguments) {
        super("neq", false, arguments);
    }

    public UnequalActionPrecondition(@NotNull ActionPrecondition actionEffect) {
        super(actionEffect);
    }

    public UnequalActionPrecondition(@NotNull OperationPrecondition operationPrecondition) {
        super(operationPrecondition);
    }
}
