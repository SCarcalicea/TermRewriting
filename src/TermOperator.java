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
		// Using the primaryNodes and terms we can deduct which term is replaced
		// Source is the node which will be replaced with the target
	}
	
	public void constructTermGraph() {

		// Using the primaryNodes and terms we can construct the final term graph
		System.out.println(terms.toString());
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
    		}
    		
    		if (!currentNode.isVisited()) {
    			for (Node node : terms.getRelations(currentNode)) {
    				if (!node.isVisited()) {
    					nodesLeft.addFirst(node);
    				}
    			}
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
		
		// For each element from listOfNodes
		// Get how many childs does it have
		// Get first child because all nodes have childs
			// If child has childs, then add them as well
			// Get next child if there is another one only if
			// The difference of node index is less than the number of childs for original node
		Entry<Integer, String> entry = listOfNodes.get(0);
		Integer nodePosition = entry.getKey();
		String nodeName = entry.getValue();
		
		Integer numberOfArguments = functions.getNumberOfVars(nodeName);
		Node root = new Node(nodeName, nodePosition);
		Node nodeFromGraph = terms.getNode(root);
		terms.getRelations(root).forEach(node -> {
			termsGraph.addEdge(nodeFromGraph, node);
			getChildOfChild(nodeFromGraph, node);
		});
			
		// TODO relations of relations are not correct
		if (numberOfArguments != null && numberOfArguments > 0 && nodeFromGraph != null) {
			for (int j = 1; j < listOfNodes.size(); j++) {
				Entry<Integer, String> subEntry = listOfNodes.get(j);
				Integer nodeSubPosition = subEntry.getKey();
				String nodeSubName = subEntry.getValue();
				Node childFromGraph = new Node(nodeSubName, nodeSubPosition);
				terms.getRelations(childFromGraph).forEach(node -> {
					termsGraph.addEdge(nodeFromGraph, node);
					getChildOfChild(nodeFromGraph, node);
				});
				numberOfArguments--;
					
				if(numberOfArguments == 0) break;
			}
		}
	}
	
	private void getChildOfChild(Node origin, Node nextNode) {
		List<Node> childs = terms.getRelations(nextNode);
		if (childs != null && !childs.isEmpty()) {
			childs.forEach(node -> getChildOfChild(origin, node));
		}
		
		termsGraph.addEdge(origin, nextNode);
	}
	
	public String display() {
		return termsGraph.toString();
	}

}
