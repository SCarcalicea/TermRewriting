package com.uvt.trs;
import java.util.HashMap;
import java.util.Map;

public class Symbol {
	private Map<String, Integer> symbols = new HashMap<>();
	
	public void insertFunction(String name, Integer numberOfChilds) {
		symbols.put(name, numberOfChilds);
	}
	
	public Integer getNumberOfVars(String name) {
		return symbols.get(name);
	}
	
	public boolean containsSymbol(String value) {
		return symbols.containsKey(value);
	}
}
