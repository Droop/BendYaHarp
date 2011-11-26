package src.harmonica;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import src.harmonica.Note.NoteName;




public class ReedPlate {

	private final Note[] notes;
	private final Boolean[] valves;
	private Note tonalite = new UnexistantNoteException();

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

	public ReedPlate(String[] plate) throws UnexistantNoteException {
		notes = new Note[plate.length];
		valves = new Boolean[plate.length];
		for (int i = 0; i < plate.length; i++){
			if (plate[i].endsWith("v")){
				valves[i]=true;
				plate[i].replace("v", "");
			} else
				valves[i]=false;
			NoteName newNote=	NoteName.fromString(plate[i]);
			notes[i]=(i==0)?new Note(newNote,3):(notes[i-1].getNext(newNote));
		}
//		System.out.println(Arrays.asList(plate)+" -> "+Arrays.asList(notes));
	}
	
	public ReedPlate(List<Note> notes, Boolean[] valves) {
		this((Note[]) notes.toArray(), valves);
	}

	public ReedPlate(List<Note> notes) {
		this((Note[]) notes.toArray());
	}

	//
	//
	//


	public int getNumberOfReeds(){
		return notes.length;
	}

	public AbstractNote[] getPlate(){
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
		return Arrays.asList(notes).toString();
	}
}
