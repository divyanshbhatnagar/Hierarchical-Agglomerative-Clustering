Hierarchical Agglomerative Clustering using single link:

Hierarchical clustering algorithms are either top-down or bottom-up. Bottom-up algorithms, also
known as Agglomerative Clustering Algorithm, treats each object as a singleton cluster in the
beginning and then successively merge pairs of clusters, depending on the shortest distance
between them, until all clusters have been merged into a single cluster that contains all objects.
Bottom-up hierarchical clustering is therefore called hierarchical agglomerative clustering or
HAC .
The inter cluster distance is defined by these methods:
1) MIN : The distance between two clusters is represented by the distance between the
closest pair of data objects belonging to different clusters.
2) MAX: The distance between two clusters is represented by the distance between the
farthest pair of data objects belonging to different clusters.
3) GROUP AVERAGE: The distance between two clusters is represented by the average
distance between all pair of data objects belonging to different clusters.
4) DISTANCE BETWEEN CENTROIDS: The distance between two clusters is
represented by the distance between the center of the clusters.
In our case, we performed HAC using Single Link (MIN method).
The algorithm which we implemented is as follows:

● Assigned each gene id to a separate cluster.

● Initialized a distance matrix which stored all pair-wise distances between clusters
using Single Link distance metric.

● At each step combining two clusters that contain the closest pair of elements not yet
belonging to the same cluster as each other.

● The distance between the two clusters is determined by a single element pair, namely
those two elements( one in each cluster) that are closest to each other. The shortest of
these links that remains at any step causes the fusion of the two clusters whose
elements are involved.

● Calculated the distance values using Euclidean distance.

● Looked for the pair of clusters with the shortest distance.

● Removed the pair from the matrix and merged them.

● Evaluated all distances from this new cluster to all other clusters, and updated the
matrix.

● Repeated until desired number of clusters are formed..

Pros:
1. It doesn’t require the number of clusters to be predefined.
2. It is flexible. It is not just limited to Euclidean distances (several other distance metrics
can be used) and numerical data.
3. Gives better understanding of the clustered data as compared to Kmeans and DBSCAN.

Cons:
1. The use of different distance metrics used for measuring distances can lead to different
final results. It needs to be performed multiple times and the results need to be compared
in order to come to a conclusion.
2. This algorithm can be a bit time consuming as it requires several split and merges to take
place.
3. Sensitive to noise and outliers.
Output: This algorithm generates k lists (k clusters) of ids of data point, each belonging to one
of the clusters.
We implemented Jaccard coefficient to measure the results obtained by our algorithm w.r.t the
groundtruth values that are already provided.
