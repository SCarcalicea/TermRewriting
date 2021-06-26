package com.uvt.trs;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class BasicOperator {
	
	static Terms terms = new Terms(); 
	static Symbol functions = new Symbol(); 
	
	public static Map<Integer, String> getPrimaryNodes(List<String> onlyNodes) {
		Map<Integer, String> primaryNodes = new TreeMap<>();
		for (int i = 0; i < onlyNodes.size(); i++) {
			String node = onlyNodes.get(i);
			Integer numberOfDependencies = functions.getNumberOfVars(node);
			if (numberOfDependencies != null && numberOfDependencies > 0) {
				primaryNodes.put(i, node);
			}
			if (i >= onlyNodes.size()) break;
		}
		return primaryNodes;
	}

	public static void createTermFromInput(List<String> onlyNodes, boolean simpleTerm) {
		int position = 1;
		for (int i = 0; i < onlyNodes.size(); i++) {
			
			// Construct root node
			String nodeName = onlyNodes.get(i);
			Term currentNode = new Term(nodeName, i);
			
			if (terms.getNode(currentNode) != null) {
				currentNode.setOrder(terms.getNode(currentNode).getOrder());
				terms.setPositions(currentNode.getOrder());
			}
			
			Integer numberOfElements = functions.getNumberOfVars(nodeName);
			
			// Child items which were already included in graph are skipped
			if (numberOfElements == null || numberOfElements == 0) { 
				if (simpleTerm) {
					terms.addEdge(currentNode, null);
					return;
				}
				continue;
			}
			
			// Construct the graph with all terms
			for (int l = i + 1; l < onlyNodes.size(); l++) {
				if (numberOfElements!= null && numberOfElements > 0) {
					String childName = onlyNodes.get(l);
					Term child = new Term(childName, l);
					child.setOrder(currentNode.getOrder() + position);
					terms.setPositions(child.getOrder());
					if (!functions.containsSymbol(childName)) {
						terms.addVariable(child);
					}
					Integer numberOfElementsForChild = functions.getNumberOfVars(childName);
					
					// Skip this number of elements
					if (numberOfElementsForChild != null && numberOfElementsForChild > 0) { 
						l += calculateChildOfChildToSkip(child, l, onlyNodes, numberOfElementsForChild);
						terms.addEdge(currentNode, child);
						position++;
						numberOfElements--;
					} else {
						terms.addEdge(currentNode, child);
						position++;
						numberOfElements--;
					}
				} else {
					break;
				}
			}
			position = 1;
		}
	}
	
	public static Terms getTerm() {
		return terms;
	}

	public static void validate(String input, List<String> onlyNodes) {
		if (!validateInput(input, onlyNodes)) {
			System.out.println("Invalid input...");
			return;
		}
		System.out.println("Valid input");
	}

	public static List<String> formatInput(String input) {
		List<String> onlyNodes = new ArrayList<>();
		StringTokenizer nodes = new StringTokenizer(input, "(");
		while (nodes.hasMoreTokens()) {
			String node = nodes.nextToken();
			StringTokenizer subNodes = new StringTokenizer(node, "),");
			if (subNodes.hasMoreTokens()) {
				while (subNodes.hasMoreTokens()) {
					String subNode = subNodes.nextToken();
					onlyNodes.add(subNode);
				}
			} else {
				onlyNodes.add(node);
			}
		}
		return onlyNodes;
	}

	public static void setFunctions(String symbols) {
		StringTokenizer tokens = new StringTokenizer(symbols, ",");
		while (tokens.hasMoreElements()) {
			String token = tokens.nextToken();
			StringTokenizer internalTokens = new StringTokenizer(token, "/");
			while (internalTokens.hasMoreElements()) {
				functions.insertFunction(internalTokens.nextToken(), Integer.parseInt(internalTokens.nextToken()));
			}
		}
	}
	
	private static int calculateChildOfChildToSkip(Term child, int position, List<String> onlyNodes, int numberOfChildsToVisit) {
		
		// Visit numberOfChildsToVisit
		// If one child has more children then add to numberOfChildsToVisit
		// Else decreate numberOfChildsToVisit
		// When numberOfChildsToVisit == 0 break and return number of children to skip
		int result = numberOfChildsToVisit;
		for (int i = position +  1; i < onlyNodes.size(); i++) {
			
			if (numberOfChildsToVisit == 0) break;

			String nodeName = onlyNodes.get(i);
			Integer numberOfChilds = functions.getNumberOfVars(nodeName);
			if (numberOfChilds != null && numberOfChilds > 0) {
				result += numberOfChilds;
				numberOfChildsToVisit += numberOfChilds - 1;
			} else {
				numberOfChildsToVisit--;
			}
		}
		
		return result;
	}
	
	private static boolean validateInput(String input, List<String> onlyNodes) {
		char[] chars = input.toCharArray();
		int numberOfCommas = 0;
		int numberOfParanthesys = 0;
		
		for (char c : chars) {
			if (c == ',') {
				numberOfCommas++;
			}
			if (c == '(') {
				numberOfParanthesys++;
			}
			if (c == ')') {
				numberOfParanthesys--;
			}
		}
		
		int neededCommas = 0;
		for (String node : onlyNodes) {
			Integer nr = functions.getNumberOfVars(node);
			neededCommas += nr == null || nr == 0 ? 0 : functions.getNumberOfVars(node) - 1;
		}
		numberOfCommas -= neededCommas;
		
		return numberOfCommas == 0 && numberOfParanthesys == 0;
		
	}
	
	public static void reset() {
		terms.clear();
	}
}
