

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class Graph {
	
	private TreeMap<Node, LinkedList<Node>> edges;
	
	public Graph() {
		edges = new TreeMap<>();
	}
	
	public void addEdge(Node fromNode, Node toNode) {
		
		validateEdge(fromNode);
		LinkedList<Node> relations = edges.get(fromNode);
		if (!relations.contains(toNode) && !fromNode.equals(toNode)) {
			relations.add(toNode);
			edges.put(fromNode, relations);
		}
	}

	public LinkedList<Node> getRelations(Node forNode) {
		return edges.get(forNode);
	}
	
	private void validateEdge(Node fromNode) {
		if (!edges.containsKey(fromNode)) {
			edges.put(fromNode, new LinkedList<>());
		}
	}
	
	public Node getNode(Node currentNode) {

		Node existingNode = null;
		List<Node> nodes = new ArrayList<>(edges.keySet());
		for (Node node : nodes) {
		
			if (node.equals(currentNode)) {
				return node;
			}
			
			for (Node nod : edges.get(node) ) {
				if (nod.equals(currentNode)) {
					return nod;
				}
			}
		}

		return existingNode;
	}
	
	public Node getStartingPoint() {
		return edges.keySet().iterator().next();
	}
	
	public Set<Node> getStartingPoints() {
		return edges.keySet();
	}
	
	public String toString() {
		
		StringBuilder stringRepresentation = new StringBuilder();
		for (Entry<Node, LinkedList<Node>> entry : edges.entrySet()) {
			stringRepresentation.append(entry.getKey().getValue() + " -> ");
			for (Node nodes : entry.getValue()) {
				stringRepresentation.append(nodes.getValue() + "->");
			}
			stringRepresentation.append(System.lineSeparator());
		}
		
		return stringRepresentation.toString();
	}
}
