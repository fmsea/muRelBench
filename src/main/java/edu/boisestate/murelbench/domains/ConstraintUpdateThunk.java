package edu.boisestate.murelbench.domains;

public class ConstraintUpdateThunk {
    public final int s;
    public final int sbar;
    public final int t;
    public final int tbar;
    public final Constraint c;

    private ConstraintUpdateThunk(int s, int t, Constraint c) {
        this.s = s;
        this.sbar = s ^ 1;
        this.t = t;
        this.tbar = t ^ 1;
        this.c = c;
    }

    public static ConstraintUpdateThunk of(int s, int t, Constraint c) {
        return new ConstraintUpdateThunk(s, t, c);
    }

    public static ConstraintUpdateThunk xor(ConstraintUpdateThunk c) {
        return new ConstraintUpdateThunk(c.sbar, c.tbar, c.c);
    }

    @Override
    public String toString() {
        return String.format("%d, %d = %s", this.s, this.t, this.c);
    }
}
