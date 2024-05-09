package edu.boisestate.murelbench.domains;

import java.util.Optional;

class BotConstraint extends Constraint {
    public BotConstraint() {
        super(Optional.empty(), true);
    }

    @Override
    public String toString() {
        return "⟘";
    }

    @Override
    public boolean equals(Object o) {
        boolean equal = false;
        if (o != null && o instanceof Constraint) {
            equal = this.equals((Constraint) o);
        }
        return equal;
    }

    public boolean equals(Constraint c) {
        boolean equal = false;
        if (c instanceof BotConstraint || c.isBottom()) {
            equal = true;
        }
        return equal;
    }

}
