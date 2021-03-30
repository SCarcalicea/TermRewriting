

public class Node implements Comparable<Node> {
	
	private boolean visited = false;
	private int nodeNumber = 0;
	private String value = "";
	
	public Node(String value, Integer nodeNumber) {
		this.value = value;
		this.nodeNumber = nodeNumber;
	}
	
	public boolean isVisited() {
		return visited;
	}
	
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	public int getNodeNumber() {
		return nodeNumber;
	}
	
	public String getValue() {
		return value;
	}
	
	public int hashCode() {
		return Integer.hashCode(nodeNumber) + value.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (obj instanceof Node) {
			return Integer.compare(nodeNumber, ((Node)obj).nodeNumber) == 0 && value.equals(((Node)obj).value);
		}
		
		return false;
	}

	@Override
	public int compareTo(Node o) {
		return Integer.compare(this.nodeNumber, o.nodeNumber);
	}
}
