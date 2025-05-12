package dev.fmsea.murelbench.domains;

import java.util.Optional;

class TopConstraint extends Constraint {
    public TopConstraint() {
        super(Optional.empty(), false);
    }

    @Override
    public String toString() {
        return "⟙";
    }
}
