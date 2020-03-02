package learntree;

import java.util.ArrayList;

public class DarkPixelsCondition extends Condition {
	private static final long serialVersionUID = 1L;
	
	public int num;
	public int threshold; // pixels with this value or higher are considered "black"
	
	public DarkPixelsCondition(int num, int threshold) {
		this.num = num;
		this.threshold = threshold;
	}

	public boolean ask(int[] example) {
		int count = 0;

		for(int i = 1; i < example.length; i++) {
			if (example[i] >= threshold)
				count++;

			if (count >= this.num)
				return true;
		}

		return false;
	}
	
	
	public static ArrayList<Condition> generateConditions(int threshold) {
		ArrayList<Condition> conds = new ArrayList<Condition>();
		
		for(int i = 10; i <= 780; i+=10)
			conds.add(new DarkPixelsCondition(i, threshold));
		
		return conds;
	}	
}
