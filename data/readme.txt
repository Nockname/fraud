Programming Assignment 7: Fraud Detection

/* *****************************************************************************
 *  Describe how you implemented the Clustering constructor
 **************************************************************************** */

After validating the input, I made an EdgeWeightedGraph of all the locations,
where each two vertices were connected via an edge that's weight
was the Euclidean distance between the two locations.
Then I used the KruskalMST algorithm to give the edges in the MST from
smallest to largest weight. I then made a new graph with the same number
of vertices, and went through and added the m-k edges from MST of the previous graph
with the smallest weights. This created a graph with k connected parts. I lastly
created an instance of the CC data structure on this graph so that, in other methods,
it would be easily find which vertices were in the same connected qpart.

/* *****************************************************************************
 *  Describe how you implemented the WeakLearner constructor
 **************************************************************************** */

After validating the input, I create a RB-BST whose keys are, during each
iteration over each possible dimension d, the d-coordinates of a data point and
whose cooresponding value is an ArrayList of the indices of all points which have
said value as their d-coordinate. I use the ArrayLists to group together points
with the same d-coordinate, making it easier to handle the edge case of points having
the same d-coordinate. The reason I use a RB-BST is that, in order to achieve the
faster runtime for the constructor, I need to sort the indices of data points based
off their d-ccordinate value. Pairing these together as key value pairs and sorting
using a RB-BST seemed like an easy-to-implement solution, as opposed to sorting all
three inputed arrays (input, weights, labels), each time.

Next, I looped through each dimension, loading values into the RB-BST as described
above. Then I looped through each possible value of sp. Now, for each such sp and
dimension value, I calculate the sum of successes at a v value smaller than the
smallest dCoordinate of a point, and update the champion values if this sum is better.
Then I also do the same for v values at each point's dCoordinate, going from smallest
coordinate size to largest. To find the sum at a v coordinate
given the last one without
doing order n computations, I just take the change-in-weight that the new points
with that dCorrdiante contribute and add it to the previous sum. After each time,
we update the champion values if necessary.


/* *****************************************************************************
 *  Consider the large_training.txt and large_test.txt datasets.
 *  Run the boosting algorithm with different values of k and T (iterations),
 *  and calculate the test data set accuracy and plot them below.
 *
 *  (Note: if you implemented the constructor of WeakLearner in O(kn^2) time
 *  you should use the small_training.txt and small_test.txt datasets instead,
 *  otherwise this will take too long)
 **************************************************************************** */

      k          T         test accuracy       time (seconds)
   --------------------------------------------------------------------------
      5          1           0.68875             0.042
      5          10          0.83375             0.08
      5          100         0.86625             0.356
      5          1000        0.86875             0.90375
      5          10000       0.86875             28.247
      20         1000        0.97                6.361
      20         2000        0.97                12.312
      30         1600        0.9725              12.352
      50         900         0.9775              9.901
      50         1000        0.9775              10.606
      80         700         0.9775              11.062
      120          500        0.89125             9.812
      200        500         0.865               15.57

/* *****************************************************************************
 *  Find the values of k and T that maximize the test data set accuracy,
 *  while running under 10 second. Write them down (as well as the accuracy)
 *  and explain:
 *   1. Your strategy to find the optimal k, T.
 *   2. Why a small value of T leads to low test accuracy.
 *   3. Why a k that is too small or too big leads to low test accuracy.
 **************************************************************************** */

The optimal values I found for my computer were k = 50, T = 900, with
accuracy 0.9775. I found these values using the following strategy:
since it rarely hurts to have a larger T value, for each k,
we will find the accuracy at the T value such that the time is ten seconds.
Then we perform a binary-search-esq algorithm on the k values.
I did this and found k = 50, T = 900.

Small T values mean that we have few weak learners. Since the weak learners by
themselves are not too good at predicting (i.e. they only predict
for certain subsets of points), small T values give low accuracy. It is only when
there are many weak learners that more points are accounted for and there is
higher overall accuracy.

Small k values mean that too much information is being lost in the dimension
reduction, which reduces accuracy. On the other hand, if k is too large, then
the model overfits the given data, because it has too much flexibility. That means
it finds patterns that are quircks of the training data and don't exist in the
test data, thus reducing accuracy.

/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback
 *  on how much you learned from doing the assignment, and whether
 *  you enjoyed doing it.
 **************************************************************************** */

This was a fun assignment!
