import java.util.ArrayList;

public class Node implements Comparable<Node>{
	ArrayList<Node> children;
	Node parent;
	
	int[] data;
	String path = "";
	int zeroIndex;
	int distanceToFinish;

	Node(int[] data, String path, int zeroIndex, int distanceToFinish, Node parent){
		this.parent = parent;
		this.data = data;
		this.path = path;
		children = new ArrayList<Node>();
		this.zeroIndex = zeroIndex;
		this.distanceToFinish = distanceToFinish;
	}

	public boolean checkForCompletion() {
		if(data[data.length-1] != 0) {
			return false;
		}

		for(int i = 0; i < data.length-1; i++) {
			if(data[i] !=  i+1) {
				return false;
			}
		}	
		return true;
	}

	@Override
	public int compareTo(Node other) {
		return (path.length() + distanceToFinish) - (other.path.length() + other.distanceToFinish);
	}
}