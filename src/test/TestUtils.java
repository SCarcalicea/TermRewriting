package test;

public class TestUtils {
	
	public static String getLpoResult(int result) {
		if (result == 1) {
			return "GR";
		} else if (result == 0) {
			return "EQ";
		} else {
			return "NGR";
		}
	}

}
