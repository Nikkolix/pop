package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

public class OperationEffect extends Predicate {
    public OperationEffect(@NotNull String name, boolean negation, @NotNull PlannerList<Argument> arguments) {
        super(name, negation, arguments);
    }

    public OperationEffect(@NotNull OperationEffect operationEffect) {
        super(operationEffect);
    }

    /**
     * this Predicate is equal to another Object if it is also a Predicate, and the name, negation and arguments are the same
     *
     * @param obj to check if equal
     * @return true/false
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OperationEffect operationEffect)) {
            return false;
        }
        if (operationEffect.getIndex() != this.getIndex() || operationEffect.isNegation() != this.isNegation() || !operationEffect.getName().equals(this.getName()) || operationEffect.getArguments().size() != this.getArguments().size()) {
            return false;
        }
        for (int i = 0; i < this.getArguments().size(); i = i + 1) {
            if (!this.getArguments().get(i).equals(operationEffect.getArguments().get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public PlannerObject copy() {
        return new OperationEffect(this);
    }

    public String toStringIdentifier() {
        String out = "[OE";
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
