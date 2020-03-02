package learntree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public abstract class Condition implements Serializable {
	private static final long serialVersionUID = 1L;
	public static int[][] data;
	public static int[][] validationData;

	public static boolean[][] conditionsAnswers; // examples X conditions

	public int index;

	public abstract boolean ask(int[] example);
	
	public final static ArrayList<Condition> generateConditions1() {
		return PixelThresholdCondition.generateConditions(128);
	}
	
	public final static ArrayList<Condition> generateConditions2() {
		ArrayList<Condition> conds = new ArrayList<Condition>();

		// blocks errors:
		// n=3, numDark=4:
		// th = 20: 10.40
		// th = 25: 9.77
		// th = 15: 10.21

		//conds.addAll(PixelBlockCondition.generateConditions(3, 4, 25)); //9.77
		conds.addAll(PixelThresholdCondition.generateConditions(25)); // a better threshold (th = 25 : err = 7.63)
		//conds.addAll(DarkPixelsCondition.generateConditions(15));
		//conds.addAll(LineColumnCondition.generateConditions(8, 25));

		//if(p1 * p2 > 20) k = p1 * p2 / 4;
		//else if (p1 * p2 >= 10) k = p1 * p2 / 3;
		//else k = p1 * p2 / 3 + 1;

		//conds.addAll(PixelRectangleCondition.generateConditions(1,5,2,25));
		conds.addAll(PixelRectangleCondition.generateConditions(2,6,4,25));
		//conds.addAll(PixelRectangleCondition.generateConditions(4,7,7,25));
		//conds.addAll(PixelBlockCondition.generateConditions(3, 4, 25));
		//conds.addAll(PixelThresholdCondition.generateConditions(25)); // a better threshold
		//conds.addAll(PixelRectangleCondition.generateConditions(2,6,4,25));
		for(int k = 1; k <= 10; k++)
			conds.addAll(LineColumnCondition.generateConditions(k, 25));
		//conds.addAll(PixelBlockCondition.generateConditions(3, 4, 25));

		//error on test: 16.659999999999997

		if(LearnTree.dbg)
			System.out.println("Calculating conditions answers...");

		for(int i = 0; i < conds.size(); i++)
			conds.get(i).index = i;
		calcConditionsAnswers(conds);

		return conds;
	}

	public static void calcConditionsAnswers(ArrayList<Condition> conds) {
		int n = data.length + validationData.length;
		conditionsAnswers = new boolean[n][conds.size()];
		Node.exampleToLabel = new int[n];

		for(int i = 0; i < data.length; i++) {
			boolean[] answers = conditionsAnswers[i];
			for(int j = 0; j < conds.size(); j++)
				answers[j] = conds.get(j).ask(data[i]);
			Node.exampleToLabel[i] =  data[i][0];
		}

		for(int i = 0; i < validationData.length; i++) {
			boolean[] answers = conditionsAnswers[data.length + i];
			for(int j = 0; j < conds.size(); j++)
				answers[j] = conds.get(j).ask(validationData[i]);
			Node.exampleToLabel[data.length + i] = validationData[i][0];
		}
	}

	public boolean answersYesTo(int exampleIndex) {
		return conditionsAnswers[exampleIndex][this.index];
	}
}
