import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class FunctionValidation {
	
	static Graph terms = new Graph(); // All terms
	static Graph finalResult = new Graph(); // Connections between terms
	static Symbol functions = new Symbol(); // Function rules

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		// Sample data
		String f1 = "f(x,f(x))"; // Incorect
		String f2 = "f(e,i(f(x,e)))"; // Correct
		String f3 = "f(g(x,y,z),i(f(g(i(e),x,y),e)))"; // Correct
		
		// Input data
		String input = f3;
		System.out.println("Input is: " + input);
		
		// Symbol/functions definition
		String symbols = "g/3,f/2,i/1,e/0";
		System.out.println("Function definition: " + symbols);
		
		// Format functions to a data structure that we can use
		StringTokenizer tokens = new StringTokenizer(symbols, ",");
		while (tokens.hasMoreElements()) {
			String token = tokens.nextToken();
			StringTokenizer internalTokens = new StringTokenizer(token, "/");
			while (internalTokens.hasMoreElements()) {
				functions.insertFunction(internalTokens.nextToken(), Integer.parseInt(internalTokens.nextToken()));
			}
		}
		
		// Format the string to something usefull (basically remove '(', ')' and ',')
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
		
		// Validate input before proceeding
		if (!validateInput(input, onlyNodes)) {
			System.out.println("Invalid input...");
			System.exit(-1);
		}
		System.out.println("Valid input");
		
		// Print out the nodes only
//		onlyNodes.forEach(System.out::println);
		
		// Extract all the terms
		for (int i = 0; i < onlyNodes.size(); i++) {
			
			// Construct root node
			String nodeName = onlyNodes.get(i);
			Node currentNode = new Node(nodeName, i);
			Integer numberOfElements = functions.getNumberOfVars(nodeName);
			
			// Child items which were already included in graph are skipped
			if (numberOfElements == null || numberOfElements == 0) { 
				continue;
			}
			
			// Construct the graph with all terms
			for (int l = i + 1; l < onlyNodes.size(); l++) {
				if (numberOfElements!= null && numberOfElements > 0) {
					String childName = onlyNodes.get(l);
					Node child = new Node(childName, l);
					Integer numberOfElementsForChild = functions.getNumberOfVars(childName);
					
					// Skip this number of elements
					// TODO There is an error (if a function contains another function, then we need to skip more elements)
					if (numberOfElementsForChild != null && numberOfElementsForChild > 0) { 
						l += calculateChildOfChildToSkip(child, l, onlyNodes, numberOfElementsForChild);
						terms.addEdge(currentNode, child);
						numberOfElements--;
					} else {
						terms.addEdge(currentNode, child);
						numberOfElements--;
					}
				} else {
					break;
				}
			}
		}
		
		// Format the input such as we get only major nodes dependencies
		Map<Integer, String> primaryNodes = new TreeMap<>();
		for (int i = 0; i < onlyNodes.size(); i++) {
			String node = onlyNodes.get(i);
			Integer numberOfDependencies = functions.getNumberOfVars(node);
			if (numberOfDependencies != null && numberOfDependencies > 0) {
				primaryNodes.put(i, node);
			}
			if (i >= onlyNodes.size()) break;
		}
		
		// This new class will handle term operations and construct the final graph
		TermOperator operator = new TermOperator(primaryNodes, terms, functions);
//		operator.replaceTerm(new Node("source", 0), new Node("target", 1));
		operator.constructTermGraph();
		
		
		System.out.println(primaryNodes);
		
		System.out.println("Terms: ");
		System.out.println(terms);
		System.out.println(operator.display());
	}
	
	private static int calculateChildOfChildToSkip(Node child, int position, List<String> onlyNodes, int numberOfChildsToVisit) {
		
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
}
