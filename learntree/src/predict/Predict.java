package predict;

import java.util.ArrayList;

import learntree.CsvReader;
import learntree.Node;
import learntree.TreeReaderWriter;

public class Predict {
	
	// returns label (0-9)
	public static int predict(Node root, int[] example) {
		while(!root.isLeaf()) {
			if(root.condition.ask(example))
				root = root.right;
			else
				root = root.left;
		}
		
		return root.label;
	}
	
	//returns percentage
	public static double predict(Node root, int[][] examples) {
		int succeeded = 0;
		
		for(int i = 0; i < examples.length; i++)
			if(predict(root, examples[i]) == examples[i][0])
				succeeded++;
		
		return ((double)succeeded / (double)examples.length) * 100.0;
	}
	
	public static void main(String[] args) {
		args = new String[2];
		args[0] = "output_tree.tree";
		args[1] = "mnist_test.csv";
		
		if(args.length != 2) {
			System.err.println("predict <tree_filename> <testset_filename>");
			return;
		}
		
		Node root = null;
		try {
			root = TreeReaderWriter.read(args[0]);
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
			return;
		}
		
		if(root == null) {
			System.err.println("Could not load tree from file: \"" + args[0] + "\".");
			return;
		}
		
		ArrayList<Integer[]> csvData = CsvReader.readCsv(args[1]);
		if(csvData == null) {
			System.err.println("Could not load file \"" + args[1] + "\".");
			return;
		}
		
		// convert to primitive array
		int[][] testData = new int[csvData.size()][];
		for(int i = 0; i < testData.length; i++) {
			Integer[] arr = csvData.get(i);
			testData[i] = new int[arr.length];
			for(int j = 0; j < arr.length; j++) testData[i][j] = arr[j];
		}
		
		// test
		//for(int i = 0; i < testData.length; i++)
			//System.out.println(predict(root, testData[i]));
		
		// debug
		//double err = (100.0 - predict(root, testData));
		//System.out.println("error on test: " + err);
		double success = predict(root, testData);
		System.out.println("error on test: " + (100.0-success));
	}
}
