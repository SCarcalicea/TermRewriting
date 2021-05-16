import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class TermOperator {
	
	private Map<Integer, String> primaryNodes = new TreeMap<>();
	private Graph terms = new Graph();
	private Graph termsGraph = new Graph();
	private Symbol functions = new Symbol();
	
	public TermOperator(Map<Integer, String> primaryNodes, Graph terms, Symbol functions) {
		this.primaryNodes = primaryNodes;
		this.terms = terms;
		this.functions = functions;
	}
	
	public void replaceTerm(Node source, Node target) {
		System.out.println("Replacing terms: ...");
		// Using the primaryNodes and terms we can deduct which term is replaced
		// Source is the node which will be replaced with the target
	}
	
	public void constructTermGraph() {

		// Using the primaryNodes and terms we can construct the final term graph
		List<Entry<Integer, String>> listOfNodes = new ArrayList<>();
		primaryNodes.entrySet().forEach(listOfNodes::add);
		
    	LinkedList<Node> nodesLeft = new LinkedList<>();
    	nodesLeft.add(terms.getStartingPoint());
    	Node currentNode = null;
    	ArrayList<Node> path = new ArrayList<Node>(); // Keep track of visited nodes
    	while (!nodesLeft.isEmpty()) {
    		
    		currentNode = nodesLeft.removeFirst();
    		if (!path.contains(currentNode)) {
    			path.add(currentNode);
    			currentNode.setVisited(true);
    		}
    		
   			for (Node node : terms.getRelations(currentNode)) {
 				nodesLeft.addLast(node);
   			}
    		
   			currentNode.setVisited(true);
    		if (nodesLeft.isEmpty()) { // Find the next node that was not visited and make it the next starting point

    			Iterator<Node> nodeIt = terms.getStartingPoints().iterator();
    			while (nodeIt.hasNext()) {

    				Node startingPoint = nodeIt.next();
					if (startingPoint.isVisited()) {
						continue;
					}
					
					nodesLeft.add(startingPoint);
					break;
    			}
    		}
    	}
    	
    	for (Node node : path) {
    		Node startNode = termsGraph.getStartingPoint();
    		termsGraph.addEdge(termsGraph.getStartingPoint() == null ? node : startNode, node);
    	}
	}
	
	public String display() {
		return termsGraph.toString();
	}

}
