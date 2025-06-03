import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class WeakLearner {

    private int dp; // the parameter dp for the model
    private int vp; // the parameter vp for the model
    private int sp; // the parameter sp for the model
    private int dimensions; // number of dimensions

    // train the weak learner
    public WeakLearner(int[][] input, double[] weights, int[] labels) {

        // validate input
        validateNotNull(input);
        validateNotNull(weights);
        validateNotNull(labels);
        validateNotNullArray(input);

        // number of inputs
        int n = input.length;
        if (weights.length != n || labels.length != n) {
            throw new IllegalArgumentException("Input arrays must be same size");
        }
        dimensions = input[0].length;
        for (int[] datapoint : input) {
            if (datapoint.length != dimensions) {
                throw new IllegalArgumentException(
                        "Every data point must have the same dimension");
            }
        }
        for (double weight : weights) {
            if (weight < 0) {
                throw new IllegalArgumentException("Weights must be non-negative");
            }
        }
        for (int label : labels) {
            if (label != 0 && label != 1) {
                throw new IllegalArgumentException("Labels must be 0 or 1");
            }
        }

        // a RB-BST that maps each dimension d coordinate to an ArrayList
        // of the indices of each point with that dimension d coordinate
        // we use such a map so that we can loop through the possible vp values
        // in a sorted order. this RB-BST is a simple way to do it without
        // sorting all three arrays
        RedBlackBST<Integer, ArrayList<Integer>> dCoordToIndex;


        double championSum = Double.NEGATIVE_INFINITY;
        int championVp = 0;
        int championDp = 0;
        int championSp = 0;

        // try each dimension
        for (int d = 0; d < dimensions; d++) {

            // load in values to dCoordToIndex
            dCoordToIndex = new RedBlackBST<>();
            for (int j = 0; j < n; j++) {
                ArrayList<Integer> current = dCoordToIndex.get(input[j][d]);
                if (current == null) {
                    current = new ArrayList<Integer>();
                }
                current.add(j);
                dCoordToIndex.put(input[j][d], current);
            }

            // try each sp value
            for (int s = 0; s < 2; s++) {
                double runningSum = 0;

                // compute for vp just smaller than the smallest dCoord of a point
                int v = dCoordToIndex.min() - 1;
                for (int i = 0; i < n; i++) {
                    runningSum += weight(input[i][d], v, s, labels[i], weights[i]);
                }

                // update champ
                if (runningSum > championSum) {
                    championSum = runningSum;
                    championVp = v;
                    championDp = d;
                    championSp = s;
                }

                // go through points from smallest to largest d-coordinate
                for (int j = 0; j < dCoordToIndex.size(); j++) {
                    v = dCoordToIndex.select(j);

                    ArrayList<Integer> indices = dCoordToIndex.get(v);

                    // adjust running sum at all points with same d-coordinate
                    for (int i : indices) {
                        runningSum += changeInWeight(
                                input[i][d], v, s, labels[i], weights[i]);
                    }

                    // update champ
                    if (runningSum > championSum) {
                        championSum = runningSum;
                        championVp = v;
                        championDp = d;
                        championSp = s;
                    }
                }
            }
        }

        dp = championDp;
        vp = championVp;
        sp = championSp;
    }


    // throw error if input is null
    private void validateNotNull(Object input) {
        if (input == null) {
            throw new IllegalArgumentException("Input is null");
        }
    }

    // throw error if any entry in inputted array is null
    private void validateNotNullArray(Object[] input) {
        validateNotNull(input);
        for (Object i : input) {
            validateNotNull(i);
        }
    }

    // return the change in weight that a point contributes to running sum
    // when it is now included within the v range vs when it wasn't
    private double changeInWeight(int dCoord, int v, int s, int label, double weight) {
        if (predictSpecific(dCoord, v, s) == label) {
            return weight;
        }
        return -weight;
    }

    // return the weight that a point adds to the sum given
    private double weight(int dCoord, int v, int s, int label, double weight) {
        if (predictSpecific(dCoord, v, s) == label) {
            return weight;
        }
        return 0;
    }

    // given d-coordinate, and the trial vp and sp of the point
    // determines the prediction of the point
    private int predictSpecific(int dCoord, int v, int s) {
        if (dCoord <= v) {
            return s;
        }
        return (1 - s);
    }

    // return the prediction of the learner for a new sample
    public int predict(int[] sample) {
        validateNotNull(sample);
        if (sample.length != dimensions) {
            throw new IllegalArgumentException("Input arrays must be same size");
        }
        return predictSpecific(sample[dp], vp, sp);
    }

    // return the dimension the learner uses to separate the data
    public int dimensionPredictor() {
        return dp;
    }

    // return the value the learner uses to separate the data
    public int valuePredictor() {
        return vp;
    }

    // return the sign the learner uses to separate the data
    public int signPredictor() {
        return sp;
    }

    // unit testing
    public static void main(String[] args) {

        // used the built-in unit testing, but also used this custom test in addition

        int[][] input = new int[][] {
                new int[] { 1, 2 },
                new int[] { 3, 4 },
                new int[] { 3, 3 },
                new int[] { 3, 2 }
        };
        WeakLearner wl = new WeakLearner(
                input,
                new double[] { 1.0, 0.5, 1.0, 1.0 },
                new int[] { 1, 0, 0, 0 }
        );
        StdOut.println(
                wl.valuePredictor() + " " +
                        wl.dimensionPredictor() + " " + wl.signPredictor());
    }
}
