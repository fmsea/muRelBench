package edu.boisestate.murelbench.benchmarks.states;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import edu.boisestate.murelbench.domains.OctagonDifferenceBoundedMatrix;
import edu.boisestate.murelbench.domains.providers.OctagonDifferenceBoundedMatrixProvider;

@State(Scope.Thread)
public class SyntheticOctagon {
    @Param({"0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9"})
    public double density;

    @Param({"50", "100", "200"})
    public int N;

    public OctagonDifferenceBoundedMatrix matrix;

    @Setup
    public void prepare() {
        this.matrix = OctagonDifferenceBoundedMatrixProvider.sample(N, density);
        this.matrix.canonicalize();
    }
}
