import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class BoostingAlgorithm {

    private Clustering clustering; // the clustering object to keep track of clusters
    private int[][] input; // the reduced-dimension input
    private int[] labels; // the given labels
    private double[] weights; // the current weights, which is updated each iteration
    private int n; // the number of data points
    private int m; // the number of locations

    // the weak learners from each iteration
    private ArrayList<WeakLearner> weakLearners;

    // create the clusters and initialize your data structures
    public BoostingAlgorithm(int[][] input, int[] labels, Point2D[] locations, int k) {

        // validate input
        validateNotNull(input);
        validateNotNullArray(input);
        validateNotNull(labels);
        validateNotNull(locations);
        validateNotNullArray(locations);

        n = input.length;
        m = input[0].length;

        if (locations.length != m || labels.length != n) {
            throw new IllegalArgumentException("Input arrays must be same size");
        }

        for (int[] datapoint : input) {
            if (datapoint.length != m) {
                throw new IllegalArgumentException(
                        "Every data point must have the same number of locations");
            }
        }

        if (k < 1 || k > m) {
            throw new IllegalArgumentException("k must be between 1 and m inclusive");
        }

        for (int label : labels) {
            if (label != 0 && label != 1) {
                throw new IllegalArgumentException("Labels must be 0 or 1");
            }
        }

        // create defensive copy of labels
        this.labels = new int[n];
        for (int i = 0; i < n; i++) {
            this.labels[i] = labels[i];
        }

        // m is number of locations. we reduce m to k
        clustering = new Clustering(locations, k);
        this.input = new int[n][k];
        for (int dataPoint = 0; dataPoint < n; dataPoint++) {
            this.input[dataPoint] = clustering.reduceDimensions(input[dataPoint]);
        }
        weakLearners = new ArrayList<WeakLearner>();

        // set weights
        weights = new double[n];
        for (int i = 0; i < n; i++) {
            weights[i] = 1.0 / n;
        }
    }

    // throw error if input is null
    private void validateNotNull(Object in) {
        if (in == null) {
            throw new IllegalArgumentException("Input is null");
        }
    }

    // throw error if any entry in inputted array is null
    private void validateNotNullArray(Object[] in) {
        validateNotNull(in);
        for (Object i : in) {
            validateNotNull(i);
        }
    }

    // return the current weight of the ith point
    public double weightOf(int i) {
        if (i < 0 || i > n - 1) {
            throw new IllegalArgumentException("Input is not within range");
        }
        return weights[i];
    }

    // apply one step of the boosting algorithm
    public void iterate() {
        // double weights of mislabeled points
        WeakLearner wl = new WeakLearner(input, weights, labels);
        weakLearners.add(wl);
        for (int i = 0; i < n; i++) {
            if (wl.predict(input[i]) != labels[i]) {
                weights[i] *= 2;
            }
        }

        // normalize weights
        double sum = 0.0;
        for (double weight : weights) {
            sum += weight;
        }
        for (int i = 0; i < n; i++) {
            weights[i] /= sum;
        }
    }

    // return the prediction of the learner for a new sample
    public int predict(int[] sample) {
        validateNotNull(sample);
        if (sample.length != m) {
            throw new IllegalArgumentException(
                    "Input array must have the same size as number of locations");
        }

        int[] reducedSample = clustering.reduceDimensions(sample);

        // number of votes for 1 minus number of votes for 0
        int netVote = 0;
        for (WeakLearner wl : weakLearners) {
            netVote += wl.predict(reducedSample) * 2 - 1;
        }
        if (netVote > 0) {
            return 1;
        }
        return 0;
    }

    // unit testing
    public static void main(String[] args) {
        // read in the terms from a file
        DataSet training = new DataSet(args[0]);
        DataSet testing = new DataSet(args[1]);
        int k = Integer.parseInt(args[2]);
        int times = Integer.parseInt(args[3]);

        // Stopwatch timer = new Stopwatch();

        int[][] trainingInput = training.getInput();
        int[][] testingInput = testing.getInput();
        int[] trainingLabels = training.getLabels();
        int[] testingLabels = testing.getLabels();
        Point2D[] trainingLocations = training.getLocations();

        // train the model
        BoostingAlgorithm model = new BoostingAlgorithm(trainingInput, trainingLabels,
                                                        trainingLocations, k);
        for (int t = 0; t < times; t++)
            model.iterate();

        // calculate the training data set accuracy
        double trainingAccuracy = 0;
        for (int i = 0; i < training.getN(); i++)
            if (model.predict(trainingInput[i]) == trainingLabels[i])
                trainingAccuracy += 1;
        trainingAccuracy /= training.getN();

        // calculate the test data set accuracy
        double testAccuracy = 0;
        for (int i = 0; i < testing.getN(); i++)
            if (model.predict(testingInput[i]) == testingLabels[i])
                testAccuracy += 1;
        testAccuracy /= testing.getN();

        // StdOut.println(
        //         k + "          " + times + "        " +
        //                 testAccuracy + "             " + timer.elapsedTime());
        StdOut.println("Training accuracy of model: " + trainingAccuracy);
        StdOut.println("Test accuracy of model: " + testAccuracy);
    }
}
