package learntree;

import java.text.DecimalFormat;

// use http://viz-js.com

public class TreeVisualizer {
	private static final String INTERIOR_SHAPE = "record";
	private static final String LEAF_SHAPE = "record";
	
	public static String getCode(Node node) {
		StringBuilder sb = new StringBuilder();

		sb.append("strict digraph {\n");
		getVertices(node, sb);
		getEdges(node, sb);
		sb.append("}");
		
		return sb.toString();
	}
	
	private static void getVertices(Node n, StringBuilder sb) {
		sb.append(nodeToString(n) + 
				" [shape=\"" + (n.isLeaf() ? LEAF_SHAPE : INTERIOR_SHAPE) + "\",label=\"");
		
		if(!n.isLeaf()) { // interior
			//if(n.condition instanceof PixelThresholdCondition) {
			//	PixelThresholdCondition cond = (PixelThresholdCondition)n.condition;
			//	sb.append("[" + cond.x + "][" + cond.y + "] "
			//			+ "\\>" + " " + cond.threshold + "\\n");
			//}
			
		}
		sb.append("samples = " + n.samples + "\\n");
		sb.append("values = [");
		for(int i = 0; i < 10; i++)
			sb.append(n.values[i] + (i < 9 ? "," : ""));
		sb.append("]");
		
		if(n.isLeaf()) {
			DecimalFormat df = new DecimalFormat("#.##");
			sb.append("\\nweighted entropy = " + df.format(n.weightedEntropy) + "\\n");
			sb.append("weighted IG = " + df.format(n.weightedIG) + "\\n");
			sb.append("label = \\\"" + n.label + "\\\"");
		}
		
		sb.append("\"]\n");
		
		if(!n.isLeaf()) {   
			getVertices(n.left, sb);
			getVertices(n.right, sb);
		}
	}
	
	private static void getEdges(Node n, StringBuilder sb) {
		if(n.isLeaf())
			return;
		
		sb.append(nodeToString(n) + " -> " + nodeToString(n.left) + "\n");
		sb.append(nodeToString(n) + " -> " + nodeToString(n.right) + "\n");
		
		getEdges(n.left, sb);
		getEdges(n.right, sb);
	}
	
	private static String nodeToString(Node n) {
		Integer code = n.hashCode();
		return "_" + code.toString();
	}
}
