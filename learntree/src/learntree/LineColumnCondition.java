package learntree;

import java.util.ArrayList;

public class LineColumnCondition extends Condition {
    private static final long serialVersionUID = 1L;

    public boolean isLine;
    public int pos; // line/column x/y
    public int num; // number of black pixels
    public int threshold; // pixels with this value or higher are considered dark

    public LineColumnCondition(boolean isLine, int pos, int num, int threshold) {
        this.isLine = isLine;
        this.pos = pos;
        this.num = num;
        this.threshold = threshold;
    }

    public boolean ask(int[] example) {
        int count = 0;

        if(isLine) { // line
            for(int i = 0; i < 28; i++) {
                int index = 1 + i + pos * 28;
                if (example[index] > threshold)
                    count++;
                if(count >= this.num)
                    return true;
            }
            return false;
        }

        // column
        for(int i = 0; i < 28; i++) {
            int index = 1 + pos + i * 28;
            if (example[index] > threshold)
                count++;
            if(count >= this.num)
                return true;
        }
        return false;
    }


    public static ArrayList<Condition> generateConditions(int num, int threshold) {
        ArrayList<Condition> conds = new ArrayList<Condition>();

        for(int i = 0; i < 28; i++) {
            conds.add(new LineColumnCondition(true, i, num, threshold));
            conds.add(new LineColumnCondition(false, i, num, threshold));
        }

        return conds;
    }
}
