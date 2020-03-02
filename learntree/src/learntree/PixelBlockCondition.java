package learntree;

import java.util.ArrayList;

public class PixelBlockCondition extends Condition {
    private static final long serialVersionUID = 1L;

    public int x, y; // top left pos of the block
    public int n; // block size
    public int numDark;
    public int threshold; // pixels with values bigger than this are considered dark

    public PixelBlockCondition(int x, int y, int n, int numDark, int threshold) {
        this.x = x;
        this.y = y;
        this.n = n;
        this.numDark = numDark;
        this.threshold = threshold;
    }

    public boolean ask(int[] example) {
        int count = 0;

        for(int i = y; i <= y+(n-1); i++)
            for(int j = x; j <= x+(n-1); j++) {
                int pos = 1 + j + i*28;

                if(example[pos] > this.threshold)
                    count++;

                if(count >= this.numDark)
                    return true;
            }

        return false;
    }

    public static ArrayList<Condition> generateConditions(int n, int numDark, int threshold) {
        ArrayList<Condition> conds = new ArrayList<Condition>();

        for(int i = 0; i < 28 - (n-1); i++)
            for(int j = 0; j < 28 - (n-1); j++)
                conds.add(new PixelBlockCondition(i, j, n, numDark, threshold));

        return conds;
    }
}
