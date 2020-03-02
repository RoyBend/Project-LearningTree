package learntree;

import java.util.*;

import predict.Predict;

public class LearnTree {
	public Node root;
	public int[][] data;
	public PriorityQueue<Node> leaves; // max heap based on leaf weighted IG
	public ArrayList<Condition> conditions;

	// fields for research
	public int internalNodes;
	public double entropy; // weighted entropy
	HashMap<Class<? extends Condition>, Integer> conditionsCounter;

	public static boolean dbg = true;

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		String fileName = "mnist_train.csv";
		String outputFileName = "output_tree.tree";
		System.out.println("debug mode [y/n]: ");
		String userInput = reader.next(); // Scans the next token of the input as an string.
		while (userInput.compareTo("y")!=0 && userInput.compareTo("n")!=0){
			System.out.println("Answer only in [y/n]");
			userInput = reader.next();
		}
		if(userInput.compareTo("y")==0){
			dbg=true;
		}
		else {
			dbg=false;
		}

		System.out.println("enter validation percentage: ");
		int validationPercentage = reader.nextInt(); // Scans the next token of the input as an int.
		while (validationPercentage<1 || validationPercentage > 99){
			System.out.println("illegal number , preferred between 10-20");
			validationPercentage = reader.nextInt();
		}

		System.out.println("enter max Tree height: ");
		int treePower = reader.nextInt(); // Scans the next token of the input as an int.
		while (treePower<1 || treePower > 15){
			System.out.println("illegal number , must be between 1-15");
			treePower = reader.nextInt();
		}

		//once finished
		reader.close();
		if(dbg)
			System.out.println("Reading data from " + fileName + "...");
		ArrayList<Integer[]> csvData = CsvReader.readCsv(fileName);
		if(csvData == null) {
			System.err.println("Could not load file \"" + fileName + "\".");
			return;
		}

		// shuffle data
		Collections.shuffle(csvData, new Random());

		int numValidationData =
				(int)((double)validationPercentage * (double) csvData.size() / 100.0);

		// copy data to primitive arrays, just for convenience
		int[][] data = new int[csvData.size() - numValidationData][];
		int[][] validationData = new int[numValidationData][];
		for(int i = 0; i < data.length; i++) {
			Integer[] arr = csvData.get(i);
			data[i] = new int[arr.length];
			for(int j = 0; j < arr.length; j++){
				data[i][j] = arr[j];
			}
		}
		for(int i = 0; i < validationData.length; i++) {
			Integer[] arr = csvData.get(data.length + i);
			validationData[i] = new int[arr.length];
			for(int j = 0; j < arr.length; j++) {
				validationData[i][j] = arr[j];
			}
		}

		// free csv data
		csvData.clear();
		csvData = null;

		if(dbg)
			System.out.println("Generating conditions...");
		Condition.data = data;
		Condition.validationData = validationData;
		ArrayList<Condition> conditions = Condition.generateConditions2();

		if(dbg)
			System.out.println("Building tree...");
		LearnTree tree = new LearnTree(data, conditions);
		double[] successRates = new double[treePower+1]; // remember success rate for each tree size

		System.out.println("Done building tree, first diverge");
		for(int L = 0; L <= treePower; L++) {
			int T = L > 0 ? (1 << (L-1)) : 1;
			for(int i = 0 ; i < T; i++)
			{
				tree.diverge();
			}
			successRates[L] = Predict.predict(tree.root, validationData);

			// for debugging
			if(dbg) {
				System.out.println("internal nodes: " + tree.internalNodes + " (L=" + L + ")");
				System.out.println("entropy: " + tree.entropy);
				System.out.println("success rate: " + successRates[L]);
				System.out.println("-----------------");
			}
		}

		tree = null; // free current tree

		// get the best tree size
		int L = largest(successRates);
		int T = (1 << L); // num of diverges

		// merge data and validationData
		int[][] allData = mergeArray(data, validationData);

		// free the objects
		data = null;
		validationData = null;

		if(dbg)
			System.out.println("Building new tree with validation + training");

		// create the final tree
		tree = new LearnTree(allData, conditions);
		for(int i = 0; i < T; i++)
			tree.diverge();

		// clear condition data before writing to disk
		Condition.data = null;
		Condition.validationData = null;
		Condition.conditionsAnswers = null;

		try {
			if(dbg)
				System.out.print("attempting to write " + outputFileName + "...");
			TreeReaderWriter.write(tree.root, outputFileName);
			if(dbg)
				System.out.println(" Done.");
		} catch(RuntimeException e) {
			System.err.println(e.getMessage());
		}

		if(dbg) {
			System.out.println("size: " + tree.internalNodes + " internal nodes");
			System.out.println("success on validation: " + successRates[L]);
			System.out.println("took " + (System.currentTimeMillis() - start) / 1000 + " sec.");
		}
		if(dbg) {
			System.out.println("\nConditions usage:");
			System.out.println(tree.getConditionsUsage());
		}
		Predict.main(null);
	}

	public LearnTree(int[][] data, ArrayList<Condition> conditions) {
		this.data = data;
		this.conditions = conditions;
		this.internalNodes = 0;
		this.conditionsCounter = new HashMap<>();
		this.leaves = new PriorityQueue<>(
					    (Node a, Node b) -> (b.weightedIG - a.weightedIG < 0 ? -1 : 1)
						);

		// set root (leaf) node
		this.root = new Node();
		this.root.samples = data.length;
		this.root.examples = new int[data.length];
		for(int i = 0; i < data.length; i++) {
			this.root.values[data[i][0]]++;
			this.root.examples[i] = i;
		}
		this.root.calcLabel();
		this.root.calcEntropy();
		this.root.calcCondition(data, conditions);
		
		this.leaves.add(root);
		this.entropy = this.root.entropy * (double)this.root.samples;
	}
	
	public void diverge() {
		Node leaf = leaves.peek();
		if(leaf == null)
			return;

		double leafWeightedEntropy = leaf.weightedEntropy; // save this before diverging
		if(!leaf.diverge(data, conditions)) // try to diverge (might fail for logical reasons)
			return;
		
		leaf = leaves.poll();
		entropy -= leafWeightedEntropy;

		//update research fields
		Class<? extends Condition> chosenCondition = leaf.condition.getClass();
		Integer conditionCount = conditionsCounter.get(chosenCondition);
		if(conditionCount == null)
			conditionCount = 0;
		conditionsCounter.put(chosenCondition, conditionCount+1);
		internalNodes++;
		entropy += (leaf.left.weightedEntropy + leaf.right.weightedEntropy);
		
		leaves.add(leaf.left);
		leaves.add(leaf.right);
	}
	
	public String visualize() {
		return TreeVisualizer.getCode(this.root);
	}

	public String getConditionsUsage() {
		StringBuilder sb = new StringBuilder();

		for(Class<? extends Condition> cond : conditionsCounter.keySet()) {
			String name = cond.getSimpleName();
			int count = conditionsCounter.get(cond);
			sb.append(name + " : " + count + " / " + this.internalNodes + "\n");
		}
		return sb.toString();
	}

	public static int largest(int[] arr)
    {
        int max = arr[0];
        int maxi = 0;
        for (int i = 1; i < arr.length; i++)
            if (arr[i] > max) {
                max = arr[i];
                maxi = i;
            }
        return maxi;
    }
	
	public static int largest(double[] arr) 
    {
		double max = arr[0];
        int maxi = 0;
        for (int i = 1; i < arr.length; i++) 
            if (arr[i] > max) {
                max = arr[i];
                maxi = i;
            }
        return maxi; 
    }

	private static int[][] mergeArray(int[][] data, int[][] validationData) {
		int[][] allData = new int[data.length + validationData.length][];
		for(int i = 0; i < data.length; i++)
			allData[i] = data[i]; // shallow copy is fine
		for(int i = 0; i < validationData.length; i++)
			allData[data.length + i] = validationData[i];
		return allData;
	}
}
