import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import java.awt.Color;

public class WeakLearnerVisualizer {

    public static void main(String[] args) {
        // Define colors
        Color LIGHT_BLUE = Color.decode("#7faac9");
        Color LIGHT_RED = Color.decode("#e5b4b8");
        Color BLUE = Color.decode("#005493");
        Color RED = Color.decode("#8d3138");

        // Read input file
        In datafile = new In(args[0]);
        int n = datafile.readInt(); // number of points
        int k = datafile.readInt(); // dimensions

        if (k != 2) {
            throw new IllegalArgumentException("Only 2D data is supported for visualization.");
        }

        int[][] input = new int[n][k];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < k; j++) {
                input[i][j] = datafile.readInt();
            }
        }

        int[] labels = new int[n];
        for (int i = 0; i < n; i++) {
            labels[i] = datafile.readInt();
        }

        double[] weights = new double[n];
        for (int i = 0; i < n; i++) {
            weights[i] = datafile.readDouble();
        }

        WeakLearner weakLearner = new WeakLearner(input, weights, labels);

        // Get decision stump parameters
        int dimension = weakLearner.dimensionPredictor(); // dp
        int value = weakLearner.valuePredictor();         // vp
        int sign = weakLearner.signPredictor();           // sp

        // Determine plot boundaries
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            minX = Math.min(minX, input[i][0]);
            maxX = Math.max(maxX, input[i][0]);
            minY = Math.min(minY, input[i][1]);
            maxY = Math.max(maxY, input[i][1]);
        }

        // Add a buffer of 1 unit
        minX -= 1;
        maxX += 1;
        minY -= 1;
        maxY += 1;

        // Compute accuracy
        double accuracy = 0.0;
        for (int i = 0; i < n; i++) {
            int predictedLabel = weakLearner.predict(input[i]);
            if (predictedLabel == labels[i]) {
                accuracy += weights[i];
            }
        }

        // Set up StdDraw canvas
        StdDraw.setCanvasSize(800, 800); // Larger canvas
        StdDraw.setXscale(minX - 1, maxX + 1); // Expand the range for axes arrows
        StdDraw.setYscale(minY - 1, maxY + 1);
        StdDraw.clear();

        // Draw shaded regions
        if (dimension == 0) { // Vertical split
            if (sign == 0) {
                StdDraw.setPenColor(LIGHT_BLUE);
                StdDraw.filledRectangle((minX + value) / 2.0, (minY + maxY) / 2.0, (value - minX) / 2.0, (maxY - minY) / 2.0);
                StdDraw.setPenColor(LIGHT_RED);
                StdDraw.filledRectangle((value + maxX) / 2.0, (minY + maxY) / 2.0, (maxX - value) / 2.0, (maxY - minY) / 2.0);
            } else {
                StdDraw.setPenColor(LIGHT_RED);
                StdDraw.filledRectangle((minX + value) / 2.0, (minY + maxY) / 2.0, (value - minX) / 2.0, (maxY - minY) / 2.0);
                StdDraw.setPenColor(LIGHT_BLUE);
                StdDraw.filledRectangle((value + maxX) / 2.0, (minY + maxY) / 2.0, (maxX - value) / 2.0, (maxY - minY) / 2.0);
            }
        } else if (dimension == 1) { // Horizontal split
            if (sign == 0) {
                StdDraw.setPenColor(LIGHT_BLUE);
                StdDraw.filledRectangle((minX + maxX) / 2.0, (minY + value) / 2.0, (maxX - minX) / 2.0, (value - minY) / 2.0);
                StdDraw.setPenColor(LIGHT_RED);
                StdDraw.filledRectangle((minX + maxX) / 2.0, (value + maxY) / 2.0, (maxX - minX) / 2.0, (maxY - value) / 2.0);
            } else {
                StdDraw.setPenColor(LIGHT_RED);
                StdDraw.filledRectangle((minX + maxX) / 2.0, (minY + value) / 2.0, (maxX - minX) / 2.0, (value - minY) / 2.0);
                StdDraw.setPenColor(LIGHT_BLUE);
                StdDraw.filledRectangle((minX + maxX) / 2.0, (value + maxY) / 2.0, (maxX - minX) / 2.0, (maxY - value) / 2.0);
            }
        }

        // Draw grid
        StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
        for (int x = minX; x <= maxX; x++) {
            StdDraw.line(x, minY, x, maxY);
        }
        for (int y = minY; y <= maxY; y++) {
            StdDraw.line(minX, y, maxX, y);
        }

        // Draw axes with arrowheads
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01); // Thicker axes
        double arrowSize = 0.3; // Size of arrowhead

        // x-axis
        StdDraw.line(0, 0, maxX + 0.5 - arrowSize, 0);
        StdDraw.filledPolygon(
            new double[]{maxX + 0.5, maxX + 0.5 - arrowSize, maxX + 0.5 - arrowSize},
            new double[]{0, arrowSize / 2, -arrowSize / 2}
        );

        // y-axis
        StdDraw.line(0, 0, 0, maxY + 0.5 - arrowSize);
        StdDraw.filledPolygon(
            new double[]{0, arrowSize / 2, -arrowSize / 2},
            new double[]{maxY + 0.5, maxY + 0.5 - arrowSize, maxY + 0.5 - arrowSize}
        );

        // Axis labels
        StdDraw.setFont(StdDraw.getFont().deriveFont(20f)); // Larger font size
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(maxX + 0.7, -0.3, "x");
        StdDraw.text(-0.3, maxY + 0.7, "y");

        // Draw decision stump line
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.005); // Thinner than axes
        if (dimension == 0) {
            StdDraw.line(value, minY - 0.2, value, maxY + 0.2); // Vertical line
        } else if (dimension == 1) {
            StdDraw.line(minX - 0.2, value, maxX + 0.2, value); // Horizontal line
        }

        // Draw the input points
        StdDraw.setPenRadius(0.01);
        for (int i = 0; i < n; i++) {
            if (labels[i] == 0) {
                StdDraw.setPenColor(BLUE);
                StdDraw.filledSquare(input[i][0], input[i][1], 0.1);
            } else if (labels[i] == 1) {
                StdDraw.setPenColor(RED);
                StdDraw.filledCircle(input[i][0], input[i][1], 0.1);
            }
        }

        // Display accuracy and predictor parameters at the bottom
        StdDraw.setFont(StdDraw.getFont().deriveFont(18f)); // Font size for text
        StdDraw.setPenColor(StdDraw.BLACK); // Black text
        double textHeight = minY - 0.3; // Adjusted text height for visibility
        StdDraw.text((minX + maxX) / 2.0, textHeight, String.format("Accuracy: %.2f", accuracy));
        StdDraw.text((minX + maxX) / 2.0, textHeight - 0.5, String.format("vp: %d, dp: %d, sp: %d", value, dimension, sign));

        StdDraw.show();
    }
}

