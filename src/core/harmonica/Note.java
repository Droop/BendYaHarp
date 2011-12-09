package core.harmonica;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import core.Player;
import core.UnexistantNoteException;

import static core.harmonica.Note.NoteName.*;


public class Note implements AbstractNote, Comparable<AbstractNote>, Comparator<AbstractNote>{

	//
	// Subclass
	//

	public enum NoteName {
		DO("DO", "C"), DOd("DO#", "C#"), RE("RE", "D", true), REd("RE#", "D#"), 
		MI("MI", "E", true), FA("FA", "F"), FAd("FA#", "F#"), SOL("SOL", "G", true), SOLd("SOL#", "G#"), 
		LA("LA", "A", true), LAd("LA#", "A#"), SI("SI", "B", true);

		String french;
		String international;
		boolean hasBemol;

		private NoteName(String french, String international) {
			this.french=french;
			this.international = international;
			this.hasBemol=false;
		}

		private NoteName(String french, String international, boolean isBemol) {
			this.french=french;
			this.international = international;
			this.hasBemol=isBemol;
		}
		public String toString(){
			if (Player._notationType.equals(Player.key4notationType_french)){
				return this.french;
			} else if (Player._notationType.equals(Player.key4notationType_international)){
				return this.international;
			} else
				throw new RuntimeException("Wrong notation type in static configuration");

		}

		public NoteName transpose(int demitons) {
			return new Note(this,3).transpose(demitons).getNoteName();
		}

		public static NoteName fromString(String s) throws UnexistantNoteException{
			for (NoteName n : NoteName.values()){
				if (s.toUpperCase().equals(n.french) || s.toUpperCase().equals(n.international))
					return n;
				else if(n.hasBemol &&(
						s.toUpperCase().equals(n.french+"B")
						|| s.toUpperCase().equals(n.international+"B")))
					return NoteName.values()[n.ordinal()-1];
			}
			throw new UnexistantNoteException(s);
		}
	}

	public enum TuningTemperament {
		EqualTemperament, JustIntonation, Compromised;
	}

	//
	// Fields
	//

	private final NoteName note;
	private final int hauteur;

	public static final Note do3 = new Note(DO, 3);

	//
	// Constructor
	//

	public Note(NoteName n, int hauteur) {
		super();
		this.note = n;
		this.hauteur = hauteur;
	}

	public Note(AbstractNote n) {
		super();
		this.note = n.getNoteName();
		this.hauteur = n.getHauteur();
	}
	//
	// Accessor
	//

	@Override
	public Note getNote() {
		return this;
	}

	@Override
	public NoteName getNoteName(){
		return note;
	}

	@Override
	public Integer getHauteur() {
		return hauteur;
	}

	/*
	 * 
	 */


	public static int getFrequency(AbstractNote n, TuningTemperament t){
		throw new RuntimeException();
	}
	//
	// Methods
	//

	public Note transpose(int demiTon){
		int noteAbs = this.note.ordinal();
		int hauteur = this.hauteur;
		if (demiTon>=0){
			for (int i = 0; i<demiTon; i++){
				noteAbs++;
				if (noteAbs>= NoteName.values().length){
					noteAbs=0;
					hauteur++;
				} 
			}
		}else {
			for (int i = demiTon; i<0; i++){
				noteAbs--;
				if (noteAbs< 0){
					noteAbs=NoteName.values().length-1;
					hauteur--;
				} 
			}
		}
		return new Note(NoteName.values()[noteAbs], hauteur);
	}

	public static Collection<AbstractNote> transpose(Collection<AbstractNote> notes, int demiTon) throws UnexistantNoteException{
		Collection<AbstractNote> result = new ArrayList<AbstractNote>();
		for (AbstractNote n : notes){
			result.add(n.transpose(demiTon));
		}
		return result;
	}

	public static List<Note> getNotesBetween(Note blow, Note draw) {
		// TODO Auto-generated method stub
	}

	public int getEcartToReach(Note that){
		return (that.note.ordinal() - this.note.ordinal()) + 12 * (that.hauteur - this.hauteur); 
	}

	public Note getNext(NoteName that){
		if (that.ordinal()>this.note.ordinal())
			return new Note(that, this.hauteur);
		else
			return new Note(that, this.hauteur+1);
	}

	public Note getPrevious(NoteName that){
		if (this.note.ordinal()>that.ordinal())
			return new Note(that, this.hauteur);
		else
			return new Note(that, this.hauteur-1);
	}

	public int compareTo(AbstractNote that){
		if (this.hauteur-that.getHauteur()!=0){
			return this.hauteur-that.getHauteur();
		} else 
			return this.note.ordinal()-that.getNoteName().ordinal();
	}

	@Override
	public int compare(AbstractNote o1, AbstractNote o2) {
		return new Note(o1).compareTo(o2);
	}

	public static AbstractNote getLower(Collection<AbstractNote> notesToPlay) {
		return Collections.min(notesToPlay, new Note(NoteName.DO,3));
	}

	public static AbstractNote getUpper(Collection<AbstractNote> notesToPlay) {
		return Collections.max(notesToPlay, new Note(NoteName.DO,3));
	}

	/*
	 * 
	 */

	public String toString(){
		return note.toString()+hauteur;
	}

	public boolean equals(Object o){
		if (o instanceof Note){
			Note that = (Note) o;
			return this.note.equals(that.note) && this.hauteur==that.hauteur;
		} else
			return false;
	}

	/*
	 * 
	 */


	//
	// Main
	//

	public static void main(String[] args){
				Note fa2 = new Note(FA, 2);
				Note do3 = new Note(DO, 3);
				Note do6 = new Note(DO, 6);
				Note sol5 = new Note(SOL,5);
				Note re5 = new Note(RE,5);
				Note si5 = new Note(SI,5);
				Note mi6 = new Note(MI, 6);
		//		
		


				System.out.println("Ecart "+si5.getEcartToReach(mi6));
				System.out.println("Ecart "+fa2.getEcartToReach(do3));
				System.out.println(do3+" is upper "+re5+" ? "+do3.compareTo(re5));
				System.out.println(re5+" is upper "+do3+" ? "+re5.compareTo(do3));
				System.out.println(do6+" is upper "+do3+" ? "+do6.compareTo(do3));
				System.out.println(do3+" is upper "+do3+" ? "+do3.compareTo(do3));
				System.out.println("First fa after SOL 5 "+sol5.getNext(NoteName.FA));
				System.out.println("Ecart "+sol5.getEcartToReach(sol5.getNext(NoteName.FA)));
				System.out.println("First fa befor SOL 5 "+sol5.getPrevious(NoteName.FA));
				System.out.println("Ecart "+sol5.getEcartToReach(sol5.getPrevious(NoteName.FA)));
				System.out.println("transpose sol5 to SI5 (4) :  "+sol5.transpose(4));
				System.out.println("transpose si5 to Sol5 (-4) :  "+si5.transpose(-4));
				System.out.println("transpose si5 to do4 (-23) :  "+si5.transpose(-23));
				System.out.println("And back to sol!! :  HEEEINNN? "+sol5.getPrevious(NoteName.FA).transpose(sol5.getEcartToReach(sol5.getPrevious(NoteName.FA))));
	}



}
