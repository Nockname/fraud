import edu.princeton.cs.algs4.CC;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.KruskalMST;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Clustering {

    private int m; // number of locations
    private int k; // number of clusters
    // the CC object for the cluster graph
    private CC connectedComponents;

    // run the clustering algorithm and create the clusters
    public Clustering(Point2D[] locations, int k) {
        validateNotNullArray(locations);
        this.m = locations.length;
        validateRange(k, 1, m);
        this.k = k;

        EdgeWeightedGraph locationGraph = new EdgeWeightedGraph(m);

        // add edges to graph
        for (int i = 0; i < m; i++) {
            for (int j = i + 1; j < m; j++) {
                double dist = locations[i].distanceTo(locations[j]);
                locationGraph.addEdge(new Edge(i, j, dist));
            }
        }

        KruskalMST kMST = new KruskalMST(locationGraph);

        // note by implementation of KruskalMST, mstEdges is already sorted
        // in increasing weight
        Iterable<Edge> mstEdges = kMST.edges();

        // add m-k smallest-weight edges of mst to new graph
        EdgeWeightedGraph clusterGraph = new EdgeWeightedGraph(m);
        int i = 0;
        for (Edge edge : mstEdges) {
            if (i >= m - k) {
                break;
            }
            i++;
            clusterGraph.addEdge(edge);
        }

        this.connectedComponents = new CC(clusterGraph);
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

    // throw error if input is not within lower and upper bounds
    private void validateRange(int input, int lower, int upper) {
        if (input < lower || input > upper) {
            throw new IllegalArgumentException("Input is not within range");
        }
    }

    // return the cluster of the ith location
    public int clusterOf(int i) {
        validateRange(i, 0, m - 1);
        return connectedComponents.id(i);
    }

    // use the clusters to reduce the dimensions of an input
    public int[] reduceDimensions(int[] input) {
        validateNotNull(input);
        if (input.length != m) {
            throw new IllegalArgumentException("Input is not the right length");
        }

        // add each location in cluster to the cluster's total score
        int[] reduced = new int[k];
        for (int i = 0; i < m; i++) {
            reduced[clusterOf(i)] += input[i];
        }
        return reduced;
    }

    // determine if a point in centers has Euclidean distance ≥ 4 from
    // each previous point in the array.
    private static boolean isFarFromPreviousCenters(Point2D[] centers,
                                                    int i, Point2D point) {
        for (int j = 0; j < i; i++) {
            if (point.distanceSquaredTo(centers[j]) < 16) {
                return false;
            }
        }
        return true;
    }

    // unit testing (required)
    public static void main(String[] args) {
        int c = Integer.parseInt(args[0]);
        int p = Integer.parseInt(args[1]);

        // add all points to centers
        Point2D[] centers = new Point2D[c];
        for (int i = 0; i < c; i++) {
            // rejection sampling
            Point2D point = null;
            while (point == null || !isFarFromPreviousCenters(centers, i, point)) {
                point = new Point2D(StdRandom.uniformDouble(0, 1000),
                                    StdRandom.uniformDouble(0, 1000));
            }
            centers[i] = point;
        }

        Point2D[] locations = new Point2D[c * p];
        for (int i = 0; i < c; i++) {
            for (int j = 0; j < p; j++) {
                // rejection sampling
                Point2D point = null;
                while (point == null || point.distanceSquaredTo(centers[i]) > 1) {
                    point = new Point2D(
                            StdRandom.uniformDouble(centers[i].x() - 1,
                                                    centers[i].x() + 1),
                            StdRandom.uniformDouble(centers[i].y() - 1,
                                                    centers[i].y() + 1));
                }
                locations[i * p + j] = point;
            }
        }

        Clustering clustering = new Clustering(locations, c);

        for (int i = 0; i < c; i++) {
            for (int j = 1; j < p; j++) {
                if (clustering.clusterOf(i * p + j) != clustering.clusterOf(i * p)) {
                    StdOut.println(
                            "Error! Locations in same cluster have different values");
                }
            }
            for (int k = 0; k < i; k++) {
                if (clustering.clusterOf(k * p) == clustering.clusterOf(i * p)) {
                    StdOut.println(
                            "Error! Locations in different clusters have same value");
                }
            }
        }
    }
}
