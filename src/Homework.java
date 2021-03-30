import java.util.HashMap;
import java.util.Map;

public class Homework {

public static void main(String [] args) {
		
		// Sample inputs
		Map<Integer, Integer> inputData = new HashMap<>();
		
		inputData.put(2, 2);
		inputData.put(3, 2);
		inputData.put(4, 3);
		inputData.put(5, 3);
		inputData.put(5, 4);
		
		inputData.forEach(Homework::function);
		
	}
	

	// 	Implementation of:
	//	a(0,n) = n+1
	//	a(m+1, 0) = a(m,1)
	//	a(m+1, n+1) = a(m, (a(m+1, n)))
	// Does it ever terminate ???
	public static int function(int m, int n) {
		if (m == 0) {        // a(0,n) = n+1
			return n+1;
		} else if (n == 0) { // a(m+1, 0) = a(m,1)
			return function(m-1, 1);
		} else {             // a(m+1, n+1) = a(m, (a(m+1, n)))
			return function(m-1, function(m, n-1));
		}
	}
	
}
