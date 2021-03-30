import java.util.HashMap;
import java.util.Map;

public class RecursiveThing {
	
	private static int counter = 0;
	
	public static void main(String [] args) {
		
		// Sample input
		Map<Integer, Integer> inputData = new HashMap<>();
		
		inputData.put(2, 2);
		inputData.put(3, 2);
		inputData.put(4, 3);
		inputData.put(5, 3);
		inputData.put(5, 4);
		
//		inputData.entrySet().forEach(e -> System.out.println(function(e.getKey(), e.getValue())));
		
		System.out.println(function(4,1));  // 4, 14, 106, 65533
//		System.out.println(counter);
		
		// if m is 1 and only n grows then the number of recursive calls is (n+m)*2
		// if n is 1 and only m grows then the number of recursive calls is more than exponential
		
	}
	
	// ackerman`s function
	public static int function(int m, int n) {
		
//		System.out.println(counter++);
		
//		System.out.println("m=" + m + " | " + "n=" + n);
		if (m == 0) {        // a(0,n) = n+1
			return n+1;
		} else if (n == 0) { // a(m+1, 0) = a(m,1)
			return function(m-1, 1);
		} else {             // a(m+1, n+1) = a(m, (a(m+1, n)))
			return function(m-1, function(m, n-1));
		}
	}

}
