package com.uvt.trs;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javafx.util.Pair;

public class TermOperator {
	
	private static final int GR = 1;   // Greater
	private static final int EQ = 0;   // Equals
	private static final int NGE = -1; // Cannot orient
	
	private Map<Integer, String> primaryNodes = new TreeMap<>();
	private Terms terms = new Terms();
	private Terms termsGraph = new Terms();
	private Symbol functions = new Symbol();
	private List<Pair<Terms, Terms>> resultOfBasicCompletion = new ArrayList<>();
	
	public TermOperator(Map<Integer, String> primaryNodes, Terms terms, Symbol functions) {
		this.primaryNodes = primaryNodes;
		this.terms = terms;
		this.functions = functions;
	}
	
	public TermOperator() {
	}
	
	public List<Pair<Terms, Terms>> getResultOfCompletion() {
		return resultOfBasicCompletion;
	}
	
	
	public List<Pair<Terms, Terms>> criticalPairs(Pair<Terms, Terms> rule1, Pair<Terms, Terms> rule2) {
		
		ArrayList<Pair<Terms, Terms>> criticalPairs = new ArrayList<>();
		
		Terms l1 = rule1.getKey();
		Terms r1 = rule1.getValue();
		Terms l2 = rule2.getKey();
		Terms r2 = rule2.getValue();
		
		Terms termLeft = l1.getFirstSubterm() == null ? l1 : l1.getFirstSubterm();
		Terms termRight = l2;
		String posToReplace = "";
		
		if (termLeft.getRelations(termLeft.getStartingPoint()) != null) {
			posToReplace = termLeft.getRelations(termLeft.getStartingPoint()).get(0).getOrder();
		} 
		
		List<Pair<Terms, Terms>> unified = unificacation(termLeft, termRight);
		for (Pair<Terms, Terms> unif : unified) {
			Terms termL = unif.getKey();
			Terms termR = unif.getValue();
			
			// Right 1
			Terms thetaR1 = null;
			Terms resultR1 = null;
			while(true) {
				thetaR1 = r1.getFirstSubterm() == null ? r1 : (Terms)r1.getFirstSubterm().clone();
				for (Term term : thetaR1.getRelations(thetaR1.getStartingPoint())) {
					if (term.getValue().equals(termL.getStartingPoint().getValue())) {
						resultR1 = thetaR1.substitute(termR, term.getOrder());
					}
				}
				resultR1 = resultR1 == null ? thetaR1 : resultR1;
				break;
			}
			
			// Left 1[Right2]p
			Terms thetaL1R2 = (Terms)l1.getFirstSubterm().clone();
			thetaL1R2.replaceForCriticalPair(r2, posToReplace);
			if (resultR1 != null && thetaL1R2 != null) {
				Pair<Terms, Terms> pairs = new Pair<>((Terms)resultR1.clone(), (Terms)thetaL1R2.clone());
				criticalPairs.add(pairs);
			}
		}
		return criticalPairs;
	}
	
	public int executeLPO(Terms termLeft, Terms termRight) {
		return lexicographicPathOrdering(termLeft, termRight); 
	}

	private int lexicographicPathOrdering(Terms termLeft, Terms termRight) {
		
		if(termLeft.equals(termRight)) {
			return EQ;
		}
			
		if (termLeft.getRelations(termLeft.getStartingPoint()).stream().map(term -> term.getValue()).collect(Collectors.toList()).contains(termRight.getStartingPoint().getValue()) && termRight.getFirstSubterm() == null) { // LPO1
			System.out.println("LPO1");
			return GR;
		} 
		else {
			
			if (functions.containsSymbol(termRight.getStartingPoint().getValue()) && functions.getNumberOfVars(termRight.getStartingPoint().getValue()) == 0) {  // LPO2A
				System.out.println("LPO2A");
				return GR;
			} else if (!termLeft.getStartingPoint().getValue().equals(termRight.getStartingPoint().getValue())) { // LPO2B
				System.out.println("LPO2B");
				if (termRight.getFirstSubterm() == null) {
					for (Term term :termLeft.getRelations(termLeft.getStartingPoint())) {
						Terms rightTerm = new Terms();
						rightTerm.addEdge(term, null);
						return executeLPO(termLeft, rightTerm);
					}
				} else {
					return executeLPO(termLeft, termRight.getFirstSubterm()); // Bug here (first subterm not working properly)
				}
			} else if (termLeft.getStartingPoint().getValue().equals(termRight.getStartingPoint().getValue())) { // LPO2C
				System.out.println("LPO2C");
				if (termRight.getFirstSubterm() == null) {
					for (Term term :termRight.getRelations(termRight.getStartingPoint())) {
						Terms leftTerm = new Terms();
						leftTerm.addEdge(term, null);
						return executeLPO(termRight, leftTerm);
					}
				} else {
					return executeLPO(termLeft.getFirstSubterm(), termRight);
				}
			} else {
				return NGE;
			}
		}
		
		return NGE;
	}
	
	public List<Pair<Terms, Terms>> unificacation(Terms termLeft, Terms termRight) {
		
		List<Pair<Terms, Terms>> result = new ArrayList<>();
		
		Pair<Terms, Terms> pair = null;
		
		// Decompose
		// Bug here for f(f(x,y),z) =? f(i(x),x) the output should be two unification rules
			if (termLeft.getRelations(termLeft.getStartingPoint()).size() == termRight.getRelations(termRight.getStartingPoint()).size()) {
				System.out.println("Unification DECOMPOSE...");
				for (int i = 0; i < termLeft.getRelations(termLeft.getStartingPoint()).size(); i++) {
					Terms unif1 = new Terms();
					Terms unif2 = new Terms();
					setUnificationTerm(termLeft, unif1, i);
					setUnificationTerm(termRight, unif2, i);
					pair = new Pair<Terms, Terms>(unif1, unif2);
					// Orient
					System.out.println("Unification ORIENT...");
					result.add(orient((Terms)pair.getKey().clone(), (Terms)pair.getValue().clone())); 
				}
			} else { 
				// Orient
				System.out.println("Unification ORIENT...");
				pair = new Pair<Terms, Terms>(termLeft, termRight);
				result.add(orient((Terms)pair.getKey().clone(), (Terms)pair.getValue().clone())); 
			}
		
		// Delete
		System.out.println("Unification DELETE...");
		result = result.stream().filter(term -> !term.getKey().equals(term.getValue())).collect(Collectors.toList());
		
		result = eliminateUnification(result);
		
		return result; 
	}

	private Pair<Terms, Terms> orient(Terms termLeft, Terms termRight) {
		Pair<Terms, Terms> pair;
		if (termLeft.getRelations(termLeft.getStartingPoint()).size() > termRight.getRelations(termRight.getStartingPoint()).size()) {
			pair = new Pair<Terms, Terms>(termRight, termLeft);
		} else {
			pair = new Pair<Terms, Terms>(termLeft, termRight);
		}
		return pair;
	}
	
	// Rules should arrive oriented here
	public List<Pair<Terms,Terms>> eliminateUnification(List<Pair<Terms,Terms>> unified) {
		List<Pair<Terms, Terms>> result = new ArrayList<>();
		
		List<List<Pair<Terms, Terms>>> pairsToBeEliminated = new ArrayList<>();
		List<Integer> indexForRemoval = new ArrayList<>();
		
		for (int i = 0; i < unified.size(); i++) {
			Pair<Terms, Terms> previousRule = unified.get(0);
			for (int j = i; j < unified.size(); j++) {
				
				if (i == j) {
					continue;
				}
			
				// Eliminate case 1 (left can be transformed using previous rule)
				Pair<Terms, Terms> nextRule = unified.get(j);
				if (previousRule.getKey().equals(nextRule.getKey()) || previousRule.getKey().equals(nextRule.getValue())) {
					ArrayList<Pair<Terms, Terms>> pairs = new ArrayList<>();
					pairs.add(previousRule);
					pairs.add(nextRule);
					pairsToBeEliminated.add(pairs);
					indexForRemoval.add(i);
				} else {
					result.add(previousRule);
				}
			}
		}
		
		for (List<Pair<Terms, Terms>> pairs : pairsToBeEliminated) {
			Pair<Terms, Terms> previousRule = pairs.get(0);
			Pair<Terms, Terms> nextRule = pairs.get(1);
			
			
			if (previousRule.getKey().equals(nextRule.getKey())) {
				int firstRuleSize = previousRule.getValue().getRelations(previousRule.getValue().getStartingPoint()).size();
				int secondRuleSize = nextRule.getValue().getRelations(nextRule.getValue().getStartingPoint()).size();
				
				if (firstRuleSize > secondRuleSize) {
					// x =? y; x =? f(x)
					Pair<Terms, Terms> newRule = new Pair<>(nextRule.getValue(), previousRule.getValue());
					result.add(newRule);
				} else {
					// x =? f(x); x =? y 
					Pair<Terms, Terms> newRule = new Pair<>(previousRule.getValue(), nextRule.getValue());
					result.add(newRule);
				}
			} 
		}
		
		for (Pair<Terms, Terms> pairs : unified) {
			if (!indexForRemoval.contains(unified.indexOf(pairs))) {
				result.add(pairs);
			}
			
		}
		
		return result;
	}
	
	// Basic completion procedure
	// https://jamboard.google.com/d/14lM_vSxPrKVGWJe3rz0gZk8OZGGjyw1dnasxD1xbdIg/viewer?f=2
	/*
	 * List of rules/functions
	 * Success if procedure terminates and "Fail" otherwise
	 * If we cannot orient then "Fail"
	 * Orient list of rules
	 * for the list of rules
	 * 		generate critical pairs
	 * 			for each critical pair
	 * 			reduce to normal form
	 * 			orient, "Fail" if we cannot orient
	 * 			add oriented rule to the set of rules
	 * output set of rules if procedure completes succesfully
	 */
	public List<Pair<Terms, Terms>> executeBasicCompletion(List<Pair<Terms, Terms>> setOfRules) {
		List<Pair<Terms, Terms>> result = new ArrayList<>();
		System.out.println("Execute basic completion procedure:" + setOfRules.size());
		
		if (setOfRules == null || setOfRules.isEmpty()) {
			return null;
		}
		
		// Orient set of rules
		List<Pair<Terms, Terms>> oriented = null;
		oriented = orientSetOfRules(setOfRules, oriented);
		
		if (oriented == null) return null;
		
		
		// Generate critical pairs first part
		List<Pair<Terms, Terms>> criticalPairLToR = null;
		for (int i = 0; i < oriented.size(); i++) {
			for (int j = i; j < oriented.size(); j++) {
				criticalPairLToR = criticalPairs(oriented.get(i), oriented.get(j));
				result.addAll(criticalPairLToR);
			}
		}
		
		// Generate critical pairs second part
		if (oriented.size() > 1) {
			List<Pair<Terms, Terms>> criticalPairRToL = null;
			for (int i = oriented.size(); i >= 0; i--) {
				for (int j = i; j >= 0; j--) {
					criticalPairRToL = criticalPairs(oriented.get(i), oriented.get(j));
					result.addAll(criticalPairRToL);
				}
			}
		}
		
		resultOfBasicCompletion.addAll(oriented);

		if (result.isEmpty()) {
			return null;
		}
		
		List<Pair<Terms, Terms>> orientedResult = null;
		orientedResult = orientSetOfRules(result, orientedResult);
		if (orientedResult == null) {
			return null;
		}
		
		// Add new rules and execute basic completion procedure again.
		return executeBasicCompletion(orientedResult); 
		
	}

	private List<Pair<Terms, Terms>> orientSetOfRules(List<Pair<Terms, Terms>> setOfRules,
			List<Pair<Terms, Terms>> oriented) {
		for (Pair<Terms, Terms> rule : setOfRules) {
			int lpo = executeLPO(rule.getKey(), rule.getValue());
			if (lpo == GR || lpo == EQ) {
				Pair<Terms, Terms> orientedRule = new Pair<>(rule.getKey(), rule.getValue());
				oriented = new ArrayList<>();
				oriented.add(orientedRule);
			} else {
				Pair<Terms, Terms> orientedRule = new Pair<>(rule.getValue(), rule.getKey());
				oriented = new ArrayList<>();
				oriented.add(orientedRule);
			}
			
		}
		return oriented;
	}

	// Deprecated
	/*
	private List<Pair<Terms, Terms>> orientTerms(List<Pair<Terms, Terms>> setOfRules) {
		List<Pair<Terms, Terms>> oriented = new ArrayList<>();
		for (Pair<Terms, Terms> pair : setOfRules) {
			int orientation = executeLPO(pair.getKey(), pair.getValue());
			if (orientation == 1 || orientation == 0) {
				oriented.add(pair);
			} else {
				return null; // Fail marker
			}
		}
		return oriented;
	}
	*/

	private void setUnificationTerm(Terms termLeft, Terms termForUnification, int position) {
		Term startNode = termLeft.getStartingPoint();
		LinkedList<Term> relations = termLeft.getRelations(startNode);
		Term leftSide = relations.get(position) == null ? startNode : relations.get(position);
		if (termLeft.getRelations(leftSide).size() == 0) {
			termForUnification.addEdge(leftSide, null);
		} else {
			for (Term term : termLeft.getRelations(leftSide)) {
				termForUnification.addEdge(leftSide, term);
			}
			
		}
	}
	
	public void constructTermGraph() {

		// Using the primaryNodes and terms we can construct the final term graph
		List<Entry<Integer, String>> listOfNodes = new ArrayList<>();
		primaryNodes.entrySet().forEach(listOfNodes::add);
		
    	LinkedList<Term> nodesLeft = new LinkedList<>();
    	nodesLeft.add(terms.getStartingPoint());
    	Term currentNode = null;
    	ArrayList<Term> path = new ArrayList<Term>(); // Keep track of visited nodes
    	while (!nodesLeft.isEmpty()) {
    		
    		currentNode = nodesLeft.removeFirst();
    		if (!path.contains(currentNode)) {
    			path.add(currentNode);
    			currentNode.setVisited(true);
    		}
    		
   			for (Term node : terms.getRelations(currentNode)) {
 				nodesLeft.addLast(node);
   			}
    		
   			currentNode.setVisited(true);
    		if (nodesLeft.isEmpty()) { // Find the next node that was not visited and make it the next starting point

    			Iterator<Term> nodeIt = terms.getStartingPoints().iterator();
    			while (nodeIt.hasNext()) {

    				Term startingPoint = nodeIt.next();
					if (startingPoint.isVisited()) {
						continue;
					}
					
					nodesLeft.add(startingPoint);
					break;
    			}
    		}
    	}
    	
    	for (Term node : path) {
    		Term startNode = termsGraph.getStartingPoint();
    		termsGraph.addEdge(termsGraph.getStartingPoint() == null ? node : startNode, node);
    	}
	}
	
	public String display() {
		return termsGraph.toString();
	}

}
