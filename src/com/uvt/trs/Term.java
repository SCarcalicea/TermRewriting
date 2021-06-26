package com.uvt.trs;


public class Term implements Comparable<Term> {
	
	private boolean visited = false;
	private int nodeNumber = 0;
	private String value = "";
	private String order = "";
	
	public Term(String value, Integer nodeNumber) {
		this.value = value;
		this.nodeNumber = nodeNumber;
	}
	
	public void setOrder(String order) {
		this.order = order;
	}
	
	public String getOrder() {
		return order;
	}
	
	public boolean isVisited() {
		return visited;
	}
	
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	public int getNodeNumber() {
		return nodeNumber;
	}
	
	public void setNodeNumber(int number) {
		this.nodeNumber = number;
	}
	
	public String getValue() {
		return value;
	}
	
	public Object clone() {
		Term clone = new Term(value, nodeNumber);
		clone.order = order;
		return clone;
	}
	
	// Change hashCode and equals to take into consideration the order as well
	public int hashCode() {
		return Integer.hashCode(nodeNumber) + value.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (obj instanceof Term) {
			return Integer.compare(nodeNumber, ((Term)obj).nodeNumber) == 0 && value.equals(((Term)obj).value);
		}
		
		return false;
	}

	@Override
	public int compareTo(Term o) {
		return Integer.compare(this.nodeNumber, o.nodeNumber);
	}
	
	public String toString() {
		return value;
	}
}
