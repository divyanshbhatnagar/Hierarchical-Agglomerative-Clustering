import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class HierarchialClustering {

	public static ArrayList<ArrayList<Double>> input = new ArrayList<ArrayList<Double>>();
	public static ArrayList<ArrayList<Double>> distList = new ArrayList<ArrayList<Double>>();
	public static ConcurrentHashMap<Integer, ArrayList<Integer>> clusterList = new ConcurrentHashMap<Integer, ArrayList<Integer>>();
	public static int numberOfClusters = 0;
	public static ArrayList<ArrayList<Integer>> finalClusteredList = new ArrayList<ArrayList<Integer>>();
	public static HashMap<Integer, ArrayList<Integer>> groundTruthHm = new HashMap<Integer, ArrayList<Integer>>();
	public static int groundTruth[][];
	public static int clusterTruth[][];

	public static int CLUSTER_NO = 3;
	public static String FILE_NAME = "new_dataset_2.txt";
	public static String CSV_FILE_NAME = "csv_file.csv";
	public static int MAX_NO_ROWS;
	public static int MAX_NO_COL;

	public static void initClusterList() {
		for (int i = 0; i < input.size(); i++) {
			ArrayList<Integer> a = new ArrayList<Integer>();
			a.add(i);
			clusterList.put(i, a);
		}
	}

	public static void formClusterList(int c1, int c2) {

		if (clusterList.containsKey(c1)) {
			ArrayList<Integer> a = clusterList.get(c1);

			if (!a.contains(c2))
				a.add(c2);
			clusterList.put(c1, a);
		}
	}

	public static void initDistList() {
		for (int i = 0; i < input.size(); i++) {
			ArrayList<Double> a = new ArrayList<Double>(Collections.nCopies(input.size(), 0.0));
			distList.add(a);
		}
	}

	public static void calculateDistance() {
		for (int i = 0; i < input.size(); i++) {
			for (int j = 0; j < input.size(); j++) {

				if (input.get(i) != null && input.get(j) != null) {
					double d = euclidDist(input.get(i), input.get(j));
					distList.get(i).set(j, d);
					distList.get(j).set(i, d);
				}
			}

		}

	}

	public static double euclidDist(ArrayList<Double> a, ArrayList<Double> b) {
		double sum = 0;

		for (int i = 0; i < a.size(); i++) {
			double diff = a.get(i) - b.get(i);
			double sqr = Math.pow(diff, 2);
			sum = sum + sqr;
		}
		return Math.sqrt(sum);
	}

	public static void distanceCalAndUpdate() {

		double min = Double.MAX_VALUE;
		int rowIndex = 0;
		int colIndex = 0;
		int minColIndex = 0;
		int minRowIndex = 0;
		for (int i = 0; i < input.size(); i++) {
			if (distList.get(i) != null) {
				ArrayList<Double> a = distList.get(i);

				double tempMin = Double.MAX_VALUE;
				int j;
				for (j = i + 1; j < a.size(); j++) {
					if (a.get(j) != null && tempMin > a.get(j)) {
						tempMin = a.get(j);
						colIndex = j;
						rowIndex = i;
					}
				}

				if (min > tempMin) {
					min = tempMin;
					minColIndex = colIndex;
					minRowIndex = rowIndex;
				}
			}
		}

		if (min != Double.MAX_VALUE) {
			updateDistanceList(minRowIndex, minColIndex);
			int largerRow = Math.max(minRowIndex, minColIndex);
			distList.set(largerRow, null);

			for (int i = 0; i < input.size(); i++) {
				if (distList.get(i) != null) {
					distList.get(i).set(largerRow, null);
				}

			}

			updateClusterList(minRowIndex, minColIndex);
		} else
			return;
	}

	public static void updateDistanceList(int row, int col) {
		for (int i = 0; i < input.size(); i++) {
			if (distList.get(i) != null) {
				double a = distList.get(row).get(i);
				double b = distList.get(col).get(i);

				double min = Math.min(a, b);

				distList.get(i).set(row, min);
				distList.get(row).set(i, min);
				distList.get(i).set(col, min);
				distList.get(col).set(i, min);
			}
		}
	}

	public static void updateClusterList(int row, int col) {

		int largerRow, smallerRow;
		largerRow = Math.max(row, col);
		smallerRow = Math.min(row, col);
		formClusterList(smallerRow, largerRow);
		formClusterList(largerRow, smallerRow);
		// clusterList.remove(largerRow);

		ArrayList<Integer> larRowList = clusterList.get(largerRow);
		ArrayList<Integer> smallColList = clusterList.get(smallerRow);

		for (int i : smallColList) {
			formClusterList(i, largerRow);
			formClusterList(largerRow, i);
		}

		for (int i : larRowList) {
			formClusterList(i, smallerRow);
			formClusterList(smallerRow, i);
		}
		clusterList.remove(largerRow);
	}

	public static int findNumberOfClusters() {
		numberOfClusters = 0;
		for (int i = 0; i < distList.size(); i++) {
			if (distList.get(i) != null)
				numberOfClusters++;
		}
		return numberOfClusters;
	}

	public static void copyClusterstoList() throws InterruptedException {

		for (int i : clusterList.keySet()) {
			ArrayList<Integer> a = clusterList.get(i);
			Collections.sort(a);
			finalClusteredList.add(a);
		}
	}

	public static void calcJaccardCoef(int[][] groundTruth, int[][] clusterTruth) {

		double m11 = 0.0;
		double m10 = 0.0;
		double m01 = 0.0;
		double jaccard;

		for (int i = 0; i < MAX_NO_ROWS; i++) {
			for (int j = 0; j < MAX_NO_COL; j++) {
				if ((groundTruth[i][j] == 1) && (clusterTruth[i][j] == 1)) {
					m11++;
				}
				if ((groundTruth[i][j] == 0) && (clusterTruth[i][j] == 1)) {
					m01++;
				}
				if ((groundTruth[i][j] == 1) && (clusterTruth[i][j] == 0)) {
					m10++;
				}
			}
		}
		jaccard = m11 / (m11 + m10 + m01);
		System.out.println("Jaccard:: " + jaccard);
	}

	public static void createGroundTruth() throws NumberFormatException, IOException {

		BufferedReader br = new BufferedReader(
				new FileReader("C:/Users/tulik/Documents/study/Data Mining/project2/datasets/" + FILE_NAME));
		String line;

		while ((line = br.readLine()) != null) {
			String[] str = line.split("\t");

			if (!groundTruthHm.containsKey(Integer.parseInt(str[1]) - 1)) {
				ArrayList<Integer> a = new ArrayList<Integer>();
				a.add(Integer.parseInt(str[0]) - 1);
				groundTruthHm.put(Integer.parseInt(str[1]) - 1, a);
			} else {

				ArrayList<Integer> a = groundTruthHm.get(Integer.parseInt(str[1]) - 1);
				a.add(Integer.parseInt(str[0]) - 1);
				groundTruthHm.put(Integer.parseInt(str[1]) - 1, a);
			}

		}

		createGroundTruth(groundTruthHm, groundTruth);
		br.close();
	}

	public static void createGroundTruth(HashMap<Integer, ArrayList<Integer>> hm, int[][] truth) {
		// Building the GroundTruth Table
		for (int i : hm.keySet()) {
			for (int j = 0; j < hm.get(i).size(); j++) {
				for (int k = 0; k < hm.get(i).size(); k++) {
					int leftIndx = hm.get(i).get(j);
					int rightIndx = hm.get(i).get(k);
					truth[leftIndx][rightIndx] = 1;
					truth[rightIndx][leftIndx] = 1;
				}
			}

		}
	}

	public static void createGroundTruth(ArrayList<ArrayList<Integer>> al, int[][] truth) {

		// Building the ClusterTruth Table
		for (int i = 0; i < al.size(); i++) {
			for (int j = 0; j < al.get(i).size(); j++) {
				for (int k = 0; k < al.get(i).size(); k++) {
					int leftIndx = al.get(i).get(j);
					int rightIndx = al.get(i).get(k);
					truth[leftIndx][rightIndx] = 1;
					truth[rightIndx][leftIndx] = 1;

				}
			}
		}
	}

	public static void sortArrayList(ArrayList<ArrayList<Integer>> a) {
		for (ArrayList<Integer> j : a) {
			Collections.sort(j);
		}

		Collections.sort(a, new Comparator<ArrayList>() {
			public int compare(ArrayList a1, ArrayList a2) {
				return a1.size() - a2.size();
			}
		});

	}

	public static void writeToCsv(HashMap<Integer, ArrayList<Integer>> gIndices) throws IOException {

		FileWriter writer = new FileWriter(
				"C:/Users/tulik/Documents/study/Data Mining/project2/datasets/" + CSV_FILE_NAME);

		for (int i : gIndices.keySet()) {

			ArrayList<Integer> dataP = gIndices.get(i);

			for (int j : dataP) {
				ArrayList<Double> data = input.get(j);
				writer.append(String.valueOf(i));
				writer.append(",");
				for (double d : data) {
					writer.append(String.valueOf(d));
					writer.append(",");
				}
				writer.append("\n");

			}

		}
		writer.close();
	}

	public static void readFile() throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(
				new FileReader("C:/Users/tulik/Documents/study/Data Mining/project2/datasets/" + FILE_NAME));
		String line;
		while ((line = br.readLine()) != null) {
			String[] str = line.split("\t");
			ArrayList<Double> list = new ArrayList<Double>();

			for (int i = 2; i < str.length; i++) {
				list.add(Double.parseDouble(str[i]));
			}
			input.add(list);

			// Initial Cluster Formation
			initClusterList();
			formClusterList(Integer.parseInt(str[0]) - 1, Integer.parseInt(str[0]) - 1);
		}
		br.close();
	}

	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException {
		// TODO Auto-generated method stub

		readFile();
		MAX_NO_ROWS = input.size();
		MAX_NO_COL = input.size();

		groundTruth = new int[MAX_NO_ROWS][MAX_NO_COL];
		clusterTruth = new int[MAX_NO_ROWS][MAX_NO_COL];

		initDistList();
		calculateDistance();

		int i = 0;
		findNumberOfClusters();
		while (numberOfClusters > CLUSTER_NO) {
			distanceCalAndUpdate();
			findNumberOfClusters();
			i++;
		}
		System.out.println("NO OF ITERATIONS:   " + i);
		System.out.println("File: " + FILE_NAME + "    NUMBER OF CLUSTERS REQUIRED: " + CLUSTER_NO);
		copyClusterstoList();
		sortArrayList(finalClusteredList);

		createGroundTruth();
		createGroundTruth(finalClusteredList, clusterTruth);
		calcJaccardCoef(groundTruth, clusterTruth);

		// Converting the list to HashMap for the report
		HashMap<Integer, ArrayList<Integer>> hm = new HashMap<Integer, ArrayList<Integer>>();
		int index = 0;
		for (ArrayList<Integer> a : finalClusteredList) {
			hm.put(index, a);
			index++;
		}

		writeToCsv(hm);
		System.out.println("Clusters are: ");
		for (int k : hm.keySet()) {
			System.out.println("{" + k + "}" + " = " + hm.get(k));
		}

	}

}