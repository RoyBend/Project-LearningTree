package learntree;

import java.util.ArrayList;

public class PixelRectangleCondition extends Condition {
    private static final long serialVersionUID = 1L;

    public int x, y; // top left pos of the block
    public int width, height;
    public int numDark;
    public int threshold; // pixels with values bigger than this are considered dark

    public PixelRectangleCondition(int x, int y, int width, int height, int numDark, int threshold) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.numDark = numDark;
        this.threshold = threshold;
    }

    public boolean ask(int[] example) {
        int count = 0;

        for(int i = y; i <= y+(height-1); i++)
            for(int j = x; j <= x+(width-1); j++) {
                int pos = 1 + j + i*28;

                if(example[pos] > this.threshold)
                    count++;

                if(count >= this.numDark)
                    return true;
            }

        return false;
    }

    private static ArrayList<Condition> generateConditionsWidthHeight(int width, int height, int numDark, int threshold) {
        ArrayList<Condition> conds = new ArrayList<Condition>();

        for(int i = 0; i < 28 - (height-1); i++)
            for(int j = 0; j < 28 - (width-1); j++)
                conds.add(new PixelRectangleCondition(j, i, width, height, numDark, threshold));

        return conds;
    }

    public static ArrayList<Condition> generateConditions(int m, int n, int numDark, int threshold) {
        ArrayList<Condition> conds = new ArrayList<Condition>();

        conds.addAll(generateConditionsWidthHeight(m, n, numDark, threshold));
        conds.addAll(generateConditionsWidthHeight(n, m, numDark, threshold));

        return conds;
    }
}
