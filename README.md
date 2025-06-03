# Fraud Detection with Machine Learning

This project implements a machine learning pipeline to detect fraudulent credit card transactions. It includes dimensionality reduction via clustering, classification using a weak learner (decision stump), and iterative refinement with AdaBoost.

## Overview

Given a set of transaction summaries and known labels (`clean` or `fraud`), this system builds a model to predict whether new transactions are fraudulent. Each transaction records spending at a set of physical locations (shops, restaurants, etc.). The pipeline consists of:

1. **Clustering**: Groups nearby map locations using Euclidean distance to reduce the dimensionality of transaction data.
2. **Weak Learning**: Trains a simple classifier (decision stump) to make predictions slightly better than random guessing.
3. **Boosting**: Iteratively refines predictions using AdaBoost, focusing on inputs that previous learners misclassified.

## File Descriptions

#### `Clustering.java`
- Implements k-means-style clustering.
- Input: `Point2D[]` (map locations), integer `k` (number of clusters).
- Output: A mapping from `m`-dimensional transaction summaries to `k`-dimensional cluster summaries.

#### `WeakLearner.java`
- Implements a **decision stump** classifier.
- Trained on reduced transaction data (`n x k`), labels (`0` or `1`), and sample weights.
- Outputs a binary predictor to classify new samples as `clean` or `fraud`.

#### `BoostingAlgorithm.java`
- Implements **AdaBoost** using decision stumps.
- Input: original transaction data (`n x m`), `Point2D[]` locations, integer `k` (number of clusters).
- Internally:
  1. Reduces dimensionality using `Clustering`.
  2. Trains weak learners iteratively.
  3. Adjusts sample weights based on misclassification.
  4. Aggregates predictions using majority vote.

## Data Format

- **Transaction Summary**: `int[]` of length `m`, representing spending at each location.
- **Label**: `0` (clean) or `1` (fraud).
- **Map Locations**: `Point2D[]` coordinate array.
- **Weights**: `double[]` of length `n`; initialized to `1/n` and updated each round.

## Prediction Process

To classify a new transaction:
1. Reduce its dimension using the `Clustering` object.
2. Get predictions from all trained weak learners.
3. Use majority voting to decide the final label.
   - In case of a tie, predict `0` (clean).

## Dependencies

- **Java 8 or later**
- **algs4.jar** from the [Princeton Algorithms library](https://algs4.cs.princeton.edu/code/)  
  - Required for the `Point2D` data type and utility classes.
  - Add it to your project’s classpath during compilation and execution:

    ```bash
    javac -cp .:algs4.jar *.java
    java -cp .:algs4.jar BoostingAlgorithm
    ```