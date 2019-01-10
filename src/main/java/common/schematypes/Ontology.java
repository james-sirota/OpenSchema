package common.schematypes;

import java.io.Serializable;

public class Ontology implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2843412270027611135L;
	
	private String from;
	private String name;
	private String to;

	public Ontology(String from, String name, String to) {
		this.from = from;
		this.name = name;
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
	public String toString()
	{
		return(from + " -- " + name + " --> " + to);
	}
	
	public String getOntology()
	{
		return (from + " -- " + name + " --> " + to);
	}
}
