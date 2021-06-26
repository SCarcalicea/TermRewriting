package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.uvt.trs.BasicOperator;
import com.uvt.trs.Symbol;
import com.uvt.trs.Term;
import com.uvt.trs.TermOperator;
import com.uvt.trs.Terms;

import javafx.util.Pair;

class MainTestSuite {
	
	@Test
	public void test_0() {
		System.out.println("#########################################################################################");
		System.out.println("Testing basics!!!");
		System.out.println("#########################################################################################");
		
		Terms terms = new Terms(); 
		Symbol functions = new Symbol(); 
		
		String invalid = "f(x,f(x))"; 
		String valid = "f(e,f(x,i(x)))";
		
		testInvalidInput(invalid);
		
		// Input data
		String input = valid;
		System.out.println(System.lineSeparator() + "Input is: " + input);
		
		// Symbol/functions definition
		String symbols = "g/3,f/2,i/1,e/0";
		System.out.println("Function symbols: " + symbols);
		
		// Format functions to a data structure that we can use
		BasicOperator.setFunctions(symbols);
		
		// Format the string to something usefull (basically remove '(', ')' and ',')
		List<String> onlyNodes = BasicOperator.formatInput(input);
		
		// Validate input before proceeding
		BasicOperator.validate(input, onlyNodes);
		
		// Print out the nodes only
//		onlyNodes.forEach(System.out::println);
		
		// Extract all the terms
		BasicOperator.createTermFromInput(onlyNodes, false);
		terms = BasicOperator.getTerm();
		
		// Format the input such as we get only major nodes dependencies
		Map<Integer, String> primaryNodes = BasicOperator.getPrimaryNodes(onlyNodes);
		
		System.out.println();
		System.out.println("Term[t]:");
		System.out.println(terms);
		System.out.println();
		System.out.println("Variables: ");
		System.out.println(terms.vars());
		
		System.out.println();
		System.out.println("Positions: ");
		System.out.println(terms.getPositions());
		System.out.println();
		
		System.out.println("Cardinality:");
		System.out.print("|t| = ");
		System.out.println(terms.cardinality());
		System.out.println();
		
		System.out.println("Term[t] is ground:");
		System.out.println(terms.isGround());
		System.out.println();
		
		Terms substituteTest = new Terms();
		Term root = new Term("f", 10);
		root.setOrder("");
		Term leafL = new Term("y", 11);
		leafL.setOrder("" + 1);
		Term leafR = new Term("z", 12);
		leafR.setOrder("" + 2);
		substituteTest.addEdge(root, leafL);
		substituteTest.addEdge(root, leafR);
		
		// This new class will handle term operations and construct the final graph
		int pos = 2;
		String position = terms.getPositions().get(pos);
		
		Terms subTerm =  terms.getSubtermAtPosition(position);
		System.out.println("Subterm at position: t|" + pos);
		System.out.println(subTerm);
		
		System.out.println("Substitution:");
		System.out.println(terms);
		System.out.println(substituteTest);
		System.out.println(terms.substitute(substituteTest, position));
		
		Term replaceWith = new Term("e", -10);
		System.out.println("Replace: ");
		System.out.println(substituteTest);
		System.out.println(replaceWith);
		Terms replacement = substituteTest.replace(replaceWith, "");
		System.out.println(System.lineSeparator() + "t[e]2: ");
		System.out.println(replacement);
		
		System.out.println("Is t[e]2: ground:");
		System.out.println(replacement.isGround());
		
		System.out.println(System.lineSeparator() + "Reconstruction of Term[t]:");
		System.out.println(terms);
		TermOperator operator = new TermOperator(primaryNodes, terms, functions);
		operator.constructTermGraph();
		String reconstructedTerm = operator.display();
		System.out.println(System.lineSeparator() + "Reconstruction:");
		System.out.println(reconstructedTerm);
		
		Assert.assertTrue("Testing basic concepts!", reconstructedTerm.equals("f[] - e[1]-f[2]-x[21]-i[22]-x[221]\r\n"));
		
	}

	private void testInvalidInput(String invalid) {
		String input = invalid;
		System.out.println("Input is: " + input);
		
		// Symbol/functions definition
		String symbols = "g/3,f/2,i/1,e/0";
		System.out.println("Function symbols: " + symbols);
		
		// Format functions to a data structure that we can use
		BasicOperator.setFunctions(symbols);
		
		// Format the string to something usefull (basically remove '(', ')' and ',')
		List<String> onlyNodes = BasicOperator.formatInput(input);
		
		// Validate input before proceeding
		BasicOperator.validate(input, onlyNodes);
	}

	@Test
	void test_1() {
		System.out.println("#########################################################################################");
		System.out.println("Testing unification!!!");
		System.out.println("#########################################################################################");
		
		Terms leftTerm1 = new Terms();
		Term root = new Term("f", 10);
		root.setOrder("");
		Term leafL = new Term("x", 11);
		leafL.setOrder("" + 1);
		Term leafR = new Term("x", 12);
		leafR.setOrder("" + 2);
		leftTerm1.addEdge(root, leafL);
		leftTerm1.addEdge(root, leafR);
		
		Terms leftTerm2 = new Terms();
		Term root2 = new Term("x", 112);
		root2.setOrder("");
		leftTerm2.addEdge(root2, null);
		
		Terms rightTerm1 = new Terms();
		Term root1 = new Term("f", 101);
		root.setOrder("");
		Term leafL1 = new Term("x", 111);
		leafL.setOrder("" + 1);
		Term leafR1 = new Term("y", 121);
		leafR.setOrder("" + 2);
		rightTerm1.addEdge(root1, leafL1);
		rightTerm1.addEdge(root1, leafR1);
		
		TermOperator operator = new TermOperator();
		
		// Unify left 1 with right 1 (decompose, delete, orient)
		System.out.println("Unification (1): ");
		System.out.println((Terms)leftTerm1.clone() + "=?");
		System.out.println((Terms)rightTerm1.clone());
		List<Pair<Terms, Terms>> unificatonRules1 = operator.unificacation(leftTerm1, rightTerm1);
		System.out.println("Unifier/s: ");
		System.out.println(unificatonRules1.get(0).getKey() + " -> " + System.lineSeparator() +  unificatonRules1.get(0).getValue());
		
		// Unify left 2 with right 1 (decompose, delete, orient)
		System.out.println("Unification (2): ");
		System.out.println((Terms)rightTerm1.clone() + "=?");
		System.out.println((Terms)leftTerm2.clone());
		List<Pair<Terms, Terms>> unificatonRules2 = operator.unificacation(rightTerm1, leftTerm2);
		System.out.println("Unifier/s: ");
		System.out.println(unificatonRules2.get(0).getKey() + " -> " + System.lineSeparator() +  unificatonRules2.get(0).getValue());
		
		// Accumulate all unification rules into one in order to execute eliminate (eliminate)
		List<Pair<Terms, Terms>> addUnifications = new ArrayList<>();
		addUnifications.addAll(unificatonRules1);
		addUnifications.addAll(unificatonRules2);
		
		// Unify left 2 with right 1 (decompose, delete, orient)
		System.out.println("Execute eliminate for the following unifiers: ");
		System.out.println(unificatonRules1.get(0).getKey() + " -> " + System.lineSeparator() +  unificatonRules1.get(0).getValue());
		System.out.println(unificatonRules2.get(0).getKey() + " -> " + System.lineSeparator() +  unificatonRules2.get(0).getValue());
		List<Pair<Terms, Terms>> finalUnificationResult = operator.eliminateUnification(addUnifications);
		System.out.println("Unifier/s: ");
		for (int i = 0; i < finalUnificationResult.size(); i++) {
			System.out.println(finalUnificationResult.get(i).getKey() + " -> " + System.lineSeparator() +  finalUnificationResult.get(i).getValue());
		}
		
		Assert.assertEquals(unificatonRules1.size(), 1);
		Assert.assertEquals(unificatonRules2.size(), 1);
		Assert.assertEquals(finalUnificationResult.size(), 2);
		
	}
	
	@Test
	void test_2() {
		System.out.println("#########################################################################################");
		System.out.println("Testing LPO!!!");
		System.out.println("#########################################################################################");
		
		Terms leftTerm1 = new Terms();
		Term root = new Term("f", 10);
		root.setOrder("");
		Term leafL = new Term("x", 11);
		leafL.setOrder("" + 1);
		Term leafR = new Term("x", 12);
		leafR.setOrder("" + 2);
		leftTerm1.addEdge(root, leafL);
		leftTerm1.addEdge(root, leafR);
		
		// LPO 1
		Terms leftTerm2 = new Terms();
		Term root2 = new Term("x", 112);
		root2.setOrder("");
		leftTerm2.addEdge(root2, null);
		
		// LPO2A
		Terms constantTerm = new Terms();
		Term rootConst = new Term("e", 112);
		rootConst.setOrder("");
		constantTerm.addEdge(rootConst, null);
		
		// LPO2B
		Terms termB = new Terms();
		Term rootB = new Term("i", 10);
		rootB.setOrder("");
		Term leaf = new Term("x", 11);
		leaf.setOrder("" + 1);
		termB.addEdge(rootB, leaf);
		
		// LPO2C
		Terms termC = new Terms();
		Term rootC = new Term("f", 10);
		rootC.setOrder("");
		Term leafC = new Term("x", 11);
		leafC.setOrder("" + 1);
		termC.addEdge(rootC, leafC);
		
		Symbol symbols = new Symbol();
		symbols.insertFunction("e", 0);
		TermOperator operator = new TermOperator(null, null, symbols);
		
		
		System.out.println("LPO: ");
		System.out.println((Terms)leftTerm1.clone() + "->" +  System.lineSeparator() + (Terms)leftTerm2.clone());
		int orient1 = operator.executeLPO((Terms)leftTerm1.clone(), (Terms)leftTerm2.clone());
		System.out.println("Result = " + TestUtils.getLpoResult(orient1) + System.lineSeparator());
		
		System.out.println((Terms)leftTerm1.clone() + "->" +  System.lineSeparator() + (Terms)constantTerm.clone());
		int orient2 = operator.executeLPO((Terms)leftTerm1.clone(), (Terms)constantTerm.clone());
		System.out.println("Result = " + TestUtils.getLpoResult(orient2) + System.lineSeparator());
		
		System.out.println((Terms)leftTerm1.clone() + "->" +  System.lineSeparator() + (Terms)termB.clone());
		int orient3 = operator.executeLPO((Terms)leftTerm1.clone(), (Terms)termB.clone());
		System.out.println("Result = " + TestUtils.getLpoResult(orient3) + System.lineSeparator());
		
		System.out.println((Terms)leftTerm1.clone() + "->" +  System.lineSeparator() + (Terms)termC.clone());
		int orient4 = operator.executeLPO((Terms)leftTerm1.clone(), (Terms)termC.clone());
		System.out.println("Result = " + TestUtils.getLpoResult(orient4) + System.lineSeparator());
		
		Assert.assertEquals(orient1, 1);
		Assert.assertEquals(orient2, 1);
		Assert.assertEquals(orient3, 1);
		Assert.assertEquals(orient4, 1);
		
	}
	
	@Test
	void test_3() {
		System.out.println("#########################################################################################");
		System.out.println("Testing critical pairs 1 !!!");
		System.out.println("#########################################################################################");
		
		Terms leftTerm1 = new Terms();
		Term root = new Term("f", 10);
		root.setOrder("");
		Term leafL = new Term("x", 11);
		leafL.setOrder("" + 1);
		Term leafR = new Term("y", 12);
		leafR.setOrder("" + 2);
		leftTerm1.addEdge(root, leafL);
		leftTerm1.addEdge(root, leafR);
		
		Terms leftTerm2 = new Terms();
		Term root2 = new Term("x", 112);
		root2.setOrder("");
		leftTerm2.addEdge(root2, null);
		
		TermOperator operator = new TermOperator();
		
		
		System.out.println("Critical pair for: ");
		System.out.println((Terms)leftTerm1.clone() + "->" + System.lineSeparator() + (Terms)leftTerm2.clone());
		System.out.println((Terms)leftTerm1.clone() + "->" + System.lineSeparator() + (Terms)leftTerm2.clone());
		
		Pair<Terms, Terms> pair1 = new Pair<>((Terms)leftTerm1.clone(), (Terms)leftTerm2.clone());
		Pair<Terms, Terms> pair2 = new Pair<>((Terms)leftTerm1.clone(), (Terms)leftTerm2.clone());

		// Substitution for f[e]p nedds to be implemented like replace
		List<Pair<Terms, Terms>> criticalPair = operator.criticalPairs(pair1, pair2);
		System.out.println(System.lineSeparator() + "!!!!  No critical pairs for above terms. !!!!");
		
		Assert.assertEquals(criticalPair.size(), 0);
	}
	
	@Test
	void test_4() {
		System.out.println("#########################################################################################");
		System.out.println("Testing critical pairs 2 !!!");
		System.out.println("#########################################################################################");
		
		Terms leftTerm1 = new Terms();
		Term root = new Term("f", 10);
		Term root1 = new Term("z", 101);
		Term leafL = new Term("f", 11);
		Term leafR = new Term("x", 12);
		Term leafR2 = new Term("y", 13);
		leftTerm1.addEdge(root, root1);
		leftTerm1.addEdge(root, leafL);
		leftTerm1.addEdge(leafL, leafR);
		leftTerm1.addEdge(leafL, leafR2);
		
		Terms rightTerm1 = new Terms();
		Term rootR = new Term("f", 10);
		Term rootR1 = new Term("f", 101);
		Term leafRL = new Term("x", 11);
		Term leafRR = new Term("y", 12);
		Term leafRR2 = new Term("z", 13);
		rightTerm1.addEdge(rootR, leafRR2);
		rightTerm1.addEdge(rootR, rootR1);
		rightTerm1.addEdge(rootR1, leafRL);
		rightTerm1.addEdge(rootR1, leafRR);
		
		Terms leftTerm2 = new Terms();
		Term rootL = new Term("f", 10);
		Term rootL2 = new Term("i", 101);
		Term leafLT2 = new Term("x", 11);
		Term leafL2 = new Term("x", 12);
		leftTerm2.addEdge(rootL, rootL2);
		leftTerm2.addEdge(rootL, leafL2);
		leftTerm2.addEdge(rootL2, leafLT2);
		
		Terms rightTerm2 = new Terms();
		Term rootR2 = new Term("e", 10);
		rightTerm2.addEdge(rootR2, null);
		
		
		TermOperator operator = new TermOperator();
		
		
		System.out.println("Critical pair for: ");
		System.out.println((Terms)leftTerm1.clone() + "->" + System.lineSeparator() + (Terms)leftTerm2.clone());
		System.out.println((Terms)leftTerm1.clone() + "->" + System.lineSeparator() + (Terms)rightTerm2.clone());
		
		Pair<Terms, Terms> pair1 = new Pair<>((Terms)leftTerm1.clone(), (Terms)rightTerm1.clone());
		Pair<Terms, Terms> pair2 = new Pair<>((Terms)leftTerm2.clone(), (Terms)rightTerm2.clone());

		List<Pair<Terms, Terms>> criticalPair = operator.criticalPairs(pair1, pair2);
		System.out.println(criticalPair.get(0).getKey() + " -> " + System.lineSeparator() +  criticalPair.get(0).getValue());
		
		Assert.assertEquals(criticalPair.size(), 1);
	}
	
	@Test
	public void test_5() {
		System.out.println("#########################################################################################");
		System.out.println("Testing basic completion!!!");
		System.out.println("#########################################################################################");
		
		Terms leftTerm1 = new Terms();
		Term root = new Term("f", 10);
		Term root1 = new Term("z", 101);
		Term leafL = new Term("f", 11);
		Term leafR = new Term("x", 12);
		Term leafR2 = new Term("y", 13);
		leftTerm1.addEdge(root, root1);
		leftTerm1.addEdge(root, leafL);
		leftTerm1.addEdge(leafL, leafR);
		leftTerm1.addEdge(leafL, leafR2);
		
		Terms leftTerm2 = new Terms();
		Term rootL = new Term("e", 10);
		leftTerm2.addEdge(rootL, null);
		
		List<Pair<Terms, Terms>> setOfRules = new ArrayList<>();
		Pair<Terms, Terms> rule = new Pair<>((Terms)leftTerm1.clone(), (Terms)leftTerm2.clone());
		setOfRules.add(rule);
		
		System.out.println(setOfRules.get(0).getKey() + " -> " + System.lineSeparator() +  setOfRules.get(0).getValue());
		TermOperator operator = new TermOperator();
		if (operator.executeBasicCompletion(setOfRules) == null) {
			System.out.println(System.lineSeparator() + "Basic completion procedure completed succesfully!!!" + System.lineSeparator());
		}
		
		for (Pair<Terms, Terms> result : operator.getResultOfCompletion()) {
			System.out.println(result.getKey() + " -> " + System.lineSeparator() +  result.getValue());
		}
		
		Assert.assertEquals(operator.getResultOfCompletion().size(), 2);
	}
	
	@Test
	public void test_6() {
		System.out.println("#########################################################################################");
		System.out.println("Test all functionality!!!");
		System.out.println("#########################################################################################");
		
		List<Terms> terms = new ArrayList<Terms>(); 
		List<Pair<Terms, Terms>> setOfRules = new ArrayList<>();
		
		String t1 = "*(i(x),x))";
		String t2 = "e";
		
//		String t3 = "(*(e, x))";
//		String t4 = "e";
		
		List<String> termList = Arrays.asList(t1, t2);
		
		for (String term : termList) {
			// Input data
			String input = term;
			System.out.println(System.lineSeparator() + "Input is: " + input);
			
			// Symbol/functions definition
			String symbols = "g/3,*/2,i/1,e/0";
			System.out.println("Function symbols: " + symbols);
			
			// Format functions to a data structure that we can use
			BasicOperator.reset();
			BasicOperator.setFunctions(symbols);
			
			// Format the string to something usefull (basically remove '(', ')' and ',')
			List<String> onlyNodes = BasicOperator.formatInput(input);
			
			// Validate input before proceeding
			BasicOperator.validate(input, onlyNodes);
			
			// Extract all the terms
			BasicOperator.createTermFromInput(onlyNodes, onlyNodes.size() == 1);
			terms.add((Terms)BasicOperator.getTerm().clone());
			BasicOperator.reset();
		}
		
		Pair<Terms, Terms> rule = new Pair<Terms, Terms>(terms.get(0), terms.get(1));
		setOfRules.add(rule);
		
		System.out.println(setOfRules.get(0).getKey() + " -> " + System.lineSeparator() +  setOfRules.get(0).getValue());
		TermOperator operator = new TermOperator();
		if (operator.executeBasicCompletion(setOfRules) == null) {
			System.out.println(System.lineSeparator() + "(7.12)Basic completion procedure completed succesfully!!!" + System.lineSeparator());
		}
		
		for (Pair<Terms, Terms> result : operator.getResultOfCompletion()) {
			System.out.println(result.getKey() + " -> " + System.lineSeparator() +  result.getValue());
		}
		
		Assert.assertEquals(operator.getResultOfCompletion().size(), 2);
		
		System.out.println("#########################################################################################");
		System.out.println("Done!!!");
		System.out.println("#########################################################################################");
		
	}
}
