package learntree;

import java.util.ArrayList;

public class PixelThresholdCondition extends Condition {
	private static final long serialVersionUID = 1L;
	
	public int x, y;
	public int threshold;
	
	public PixelThresholdCondition(int x, int y, int threshold) {
		this.x = x;
		this.y = y;
		this.threshold = threshold;
	}
	
	public boolean ask(int[] example) {
		int val = example[1 + x + y*28];

		return val > threshold;
	}
	
	public static ArrayList<Condition> generateConditions(int threshold) {
		ArrayList<Condition> conds = new ArrayList<Condition>();
		
		for(int i = 0; i < 28; i++)
			for(int j = 0; j < 28; j++)
				conds.add(new PixelThresholdCondition(j, i, threshold));

		return conds;
	}
}
