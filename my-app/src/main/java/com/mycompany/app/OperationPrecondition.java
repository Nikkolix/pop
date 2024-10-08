package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public class OperationPrecondition extends Predicate {
    public OperationPrecondition(@NotNull String name, boolean negation, @NotNull PlannerList<Argument> arguments) {
        super(name, negation, arguments);
    }

    public OperationPrecondition(@NotNull OperationPrecondition operationPrecondition) {
        super(operationPrecondition);
    }

    /**
     * this Predicate is equal to another Object if it is also a Predicate, and the name, negation and arguments are the same
     *
     * @param obj to check if equal
     * @return true/false
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OperationPrecondition operationPrecondition)) {
            return false;
        }
        if (operationPrecondition.getIndex() != this.getIndex() || operationPrecondition.isNegation() != this.isNegation() || !operationPrecondition.getName().equals(this.getName()) || operationPrecondition.getArguments().size() != this.getArguments().size()) {
            return false;
        }
        for (int i = 0; i < this.getArguments().size(); i = i + 1) {
            if (!this.getArguments().get(i).equals(operationPrecondition.getArguments().get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public PlannerObject copy() {
        if (this instanceof UnequalOperationPrecondition) {
            return new UnequalOperationPrecondition(this);
        }
        return new OperationPrecondition(this);
    }

    @Override
    public String toStringIdentifier() {
        String out = "[OP";
        if (this.isNegation()) {
            out = out + "-";
        } else {
            out = out + "+";
        }
        out = out + this.getName();
        out = out + this.getArguments().toStringIdentifier();
        out = out + this.getIndex() + "]";
        return out;
    }
}
