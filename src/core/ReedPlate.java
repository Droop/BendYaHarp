package src.core;

import java.util.Collection;

import org.jdom.Element;


public class ReedPlate {

	private Note[] notes;
	private Boolean[] valves;

	//
	//
	//

	public ReedPlate(Note[] notes, Boolean[] valves) {
		super();
		this.notes = notes;
		this.valves = valves;
	}

	public ReedPlate(Note[] notes) {
		super();
		this.notes = notes;
		this.valves = new Boolean[notes.length];
		for (int i = 0 ; i < notes.length; i++){
			valves[i]=false;
		}
	}

	public ReedPlate(Collection<Note> notes, Boolean[] valves) {
		this((Note[]) notes.toArray(), valves);
	}

	public ReedPlate(Collection<Note> notes) {
		this((Note[]) notes.toArray());
	}

	//
	//
	//

	public int getNumberOfReeds(){
		return notes.length;
	}

	public Note[] getPlate(){
		return notes;
	}

	public Note getReed(int i){
		return notes[i];
	}

	public boolean isValved(int i){
		return valves[i];
	}

	/*
	 * 
	 */

	public String toString(){
		throw new RuntimeException("todo");
	}

	public Element toXML(){
		throw new RuntimeException("todo");
	}

	public static ReedPlate fromXML(Element e){
		throw new RuntimeException("todo");
	}
}
