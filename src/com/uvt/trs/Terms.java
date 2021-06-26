package com.uvt.trs;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

public class Terms {
	
	private TreeMap<Term, LinkedList<Term>> edges;
	private List<String> positions = new ArrayList<>();
	private List<Term> variables = new ArrayList<>();
	
	public Terms() {
		edges = new TreeMap<>();
		positions.add("");
	}
	
	public void addEdge(Term fromNode, Term toNode) {
		validateEdge(fromNode);
		LinkedList<Term> relations = edges.get(fromNode);
		if (!relations.contains(toNode) && !fromNode.equals(toNode)) {
			relations.add(toNode);
			edges.put(fromNode, relations);
		}
	}
	
	public void addVariable(Term var) {
		variables.add(var);
	}
	
	public List<Term> vars() {
		List<Term> vars = new ArrayList<Term>();
		List<String> varsNames = new ArrayList<>();
		for (Term var : variables) {
			if (!varsNames.contains(var.getValue())) {
				vars.add(var);
				varsNames.add(var.getValue());
			}
		}
		return vars;
	}
	
	public boolean isGround() {
		return variables.isEmpty();
	}

	public LinkedList<Term> getRelations(Term forNode) {
		return edges.get(forNode) == null ? new LinkedList<>() : edges.get(forNode);
	}
	
	private void validateEdge(Term fromNode) {
		if (!edges.containsKey(fromNode)) {
			edges.put(fromNode, new LinkedList<>());
		}
	}
	
	public Term getNode(Term currentNode) {
		Term existingNode = null;
		List<Term> nodes = new ArrayList<>(edges.keySet());
		for (Term node : nodes) {
			if (node.equals(currentNode)) {
				return node;
			}
			for (Term nod : edges.get(node) ) {
				if (nod.equals(currentNode)) {
					return nod;
				}
			}
		}
		return existingNode;
	}
	
	public Term getStartingPoint() {
		Iterator<Term> it = edges.keySet().iterator();
		return it.hasNext() ? it.next() : null;
	}
	
	public void setPositions(String position) {
		if (!positions.contains(position)) {
			positions.add(position);
		}
	}
	
	public List<String> getPositions() {
		return positions;
	}
	
	public Terms getSubtermAtPosition(String position) {
		TreeMap<Term, LinkedList<Term>> subterm = new TreeMap<>();
		Iterator<Term> it = edges.keySet().iterator();
		while(it.hasNext()) {
			Term parent = it.next();
			if(parent.getOrder().equals(position)) {
				LinkedList<Term> rels = getRelations(parent);
				subterm.put(parent, rels);
				findSubterOfSubterm(rels, subterm);
				break;
			} 
		}
		return (Terms)constructTerm(subterm).clone();
	}
	
	public TreeMap<Term, LinkedList<Term>> returnSource() {
		return edges;
	}
	
	public Terms getFirstSubterm() {
		TreeMap<Term, LinkedList<Term>> subterm = new TreeMap<>();
		Iterator<Term> it = edges.keySet().iterator();
		while(it.hasNext()) {
			Term parent = it.next();
			if (it.hasNext()) {
				parent = it.next();
				LinkedList<Term> rels = getRelations(parent);
				subterm.put(parent, rels);
				findSubterOfSubterm(rels, subterm);
			} else {
				return null;
			}
			break;
		}
		return (Terms)constructTerm(subterm).clone();
	}
	
	private Terms constructTerm(TreeMap<Term, LinkedList<Term>> term) {
		Terms terms = new Terms();
		terms.edges = new TreeMap<>(term);
		return terms;
	}
	
	private void findSubterOfSubterm(LinkedList<Term> relations, TreeMap<Term, LinkedList<Term>> subterm) {
		LinkedList<Term> nodesLeft = new LinkedList<>();
		nodesLeft.addAll(relations);
		Term currentNode = null;
		while (!nodesLeft.isEmpty()) {
    		currentNode = nodesLeft.removeFirst();
    		LinkedList<Term> rels = getRelations(currentNode);
    		if (!rels.isEmpty()) {
    			subterm.put(currentNode, rels);
    			nodesLeft.addAll(rels);
    			continue;
    		}
    	}
	}
	
	public Terms replace(Term term, String position) {
		Terms terms = getSubtermAtPosition(position);
		if (terms.getStartingPoint() == null) {
			return null;
		}
		Term startPos = terms.getStartingPoint();
		List<Term> relations = terms.getRelations(startPos);
		for (int i = 0; i < relations.size(); i++ ) {
			Term replacement = new Term(term.getValue(), i);
			replacement.setOrder("" + i);
			terms.getRelations(startPos).set(i, replacement);
		}
		
		terms = (Terms)terms.clone();
		terms.updatePos();
		return terms;
	}
	
	public Terms replaceForCriticalPair(Terms replacement, String position) {
		Terms terms = this;
		if (terms.getStartingPoint() == null) {
			return null;
		}
		
		Term startPos = terms.getStartingPoint();
		List<Term> relations = terms.getRelations(startPos);
		for (int i = 0; i < relations.size(); i++ ) {
			Term replaceChild = (Term)replacement.getStartingPoint().clone();
			replaceChild.setNodeNumber(i);
			terms.getRelations(startPos).set(i, replaceChild);
		}
		
		((Terms)terms.clone()).updatePos();
		return terms;
	}
	
	public Terms substitute(Terms replaceWith, String position) {
		
		Terms temp = (Terms)this.clone();
		
		// Get term to be substituted with
		List<Term> roots = new ArrayList<>();
		for (Term root : temp.getStartingPoints()) {
			for (Term term : temp.getRelations(root)) {
				if (term.getOrder().equals(position)) {
					temp.getRelations(root).remove(term);
					temp.addEdge(root, replaceWith.getStartingPoint());
					roots.add(term);
					break;
				}
			}
			break;
		}
		
		for (Term root : roots) {
			if (temp.getStartingPoints().contains(root)) {
				temp.edges.remove(root);
			}
		}
		
		for (Term root : replaceWith.getStartingPoints()) {
			Random rand = new Random();
			int upperBound = 1000;
			Term newRoot = new Term(root.getValue(), rand.nextInt(upperBound));
			for (Term term : replaceWith.getRelations(root)) {
				if (term != null) {
					Term newLeaf = new Term(term.getValue(), rand.nextInt(upperBound));
					
					temp.addEdge(newRoot, newLeaf);
				}
			}
		}
		
		return (Terms)temp.clone();
		
	}
	
	private void updatePos() {
		String initialPos = "";
		positions.clear();
		for (Term root : this.getStartingPoints()) {
			int subPosition = 1;
			root.setOrder(initialPos);
			positions.add(initialPos);
			for (Term child : this.getRelations(root)) {
				child.setOrder(initialPos + subPosition);
				positions.add(initialPos + subPosition);
				subPosition++;
				if (this.getRelations(root).indexOf(child) == this.getRelations(root).size()-1) {
					initialPos = child.getOrder();
				}
			}
		}
	}
	
	public int cardinality() {
		return positions.size();
	}

	public Set<Term> getStartingPoints() {
		return edges.keySet();
	}
	
	public void clear() {
		edges.clear();
	}
	
	public Object clone() {
		TreeMap<Term, LinkedList<Term>> deepCopy = new TreeMap<>();
		for (Term roots : edges.keySet()) {
			Term rootClone = (Term) roots.clone();
			LinkedList<Term> relations = new LinkedList<>();
			for (Term childs : this.getRelations(roots)) {
				if (childs != null) {
					relations.add((Term)childs.clone());
				}
			}
			deepCopy.put(rootClone, relations);
		}
		
		Terms response = constructTerm(deepCopy);
		response.updatePos();
		
		return response;
	}
	
	public boolean equals(Object objectToCompareWith) {
		
		Terms that = (Terms) objectToCompareWith;
		
		if (this.getStartingPoints().size() != that.getStartingPoints().size()) {
			return false;
		} else {
			
			Term startLeft = this.getStartingPoint();
			Term startRight = that.getStartingPoint();
			
			if (!startLeft.getValue().equalsIgnoreCase(startRight.getValue())) {
				return false;
			}
			
			if (this.getRelations(startLeft).size() != that.getRelations(startRight).size()) {
				return false;
			}
			
		}
		
		return true;
	}
	
	public String toString() {
		StringBuilder stringRepresentation = new StringBuilder();
		for (Entry<Term, LinkedList<Term>> entry : edges.entrySet()) {
			stringRepresentation.append(entry.getKey().getValue() + "[" + entry.getKey().getOrder() + "]" + " - ");
			for (Term nodes : entry.getValue()) {
				if (nodes == null) {
					stringRepresentation.append("[]");
				} else {
					stringRepresentation.append(nodes.getValue() + "[" + nodes.getOrder() + "]");
					if (entry.getValue().indexOf(nodes) != entry.getValue().size() - 1) {
						stringRepresentation.append("-");
					}
				}
			}
			stringRepresentation.append(System.lineSeparator());
		}
		return stringRepresentation.toString();
	}
}
