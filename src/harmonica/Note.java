package src.harmonica;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import src.StaticConfiguration;
import src.harmonica.Note.NoteName;


public class Note implements AbstractNote, Comparable<AbstractNote>, Comparator<AbstractNote>{

	//
	// Fields
	//

	private final NoteName note;
	private final int hauteur;

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

	public int getEcart(Note that){
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

	//
	// Subclass
	//

	public enum NoteName {
		DO("  DO", "C"), DOd(" DO#", "C#"), RE("  RE", "D"), REd(" RE#", "D#"), 
		MI("  MI", "E"), FA("  FA", "F"), FAd(" FA#", "F#"), SOL(" SOL", "G"), SOLd("SOL#", "G#"), 
		LA("  LA", "A"), LAd(" LA#", "A#"), SI("  SI", "B");

		String french;
		String international;

		private NoteName(String french, String international) {
			this.french=french;
			this.international = international;
		}

		public String toString(){
			if (StaticConfiguration._notationType.equals(StaticConfiguration.key4notationType_french)){
				return this.french;
			} else if (StaticConfiguration._notationType.equals(StaticConfiguration.key4notationType_international)){
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
			}
			throw new UnexistantNoteException(s);
		}
	}

	public enum TuningTemperament {
		EqualTemperament, JustIntonation, Compromised;
	}

	//
	// Main
	//

	public static void main(String[] args){
		Note do3 = new Note(NoteName.DO, 3);
		Note do6 = new Note(NoteName.DO, 6);
		Note re5 = new Note(NoteName.RE,5);
		Note sol5 = new Note(NoteName.SOL,5);
		Note si5 = new Note(NoteName.SI,5);

		System.out.println(do3+" is upper "+re5+" ? "+do3.compareTo(re5));
		System.out.println(re5+" is upper "+do3+" ? "+re5.compareTo(do3));
		System.out.println(do6+" is upper "+do3+" ? "+do6.compareTo(do3));
		System.out.println(do3+" is upper "+do3+" ? "+do3.compareTo(do3));
		System.out.println("First fa after SOL 5 "+sol5.getNext(NoteName.FA));
		System.out.println("Ecart "+sol5.getEcart(sol5.getNext(NoteName.FA)));
		System.out.println("First fa befor SOL 5 "+sol5.getPrevious(NoteName.FA));
		System.out.println("Ecart "+sol5.getEcart(sol5.getPrevious(NoteName.FA)));
		System.out.println("transpose sol5 to SI5 (4) :  "+sol5.transpose(4));
		System.out.println("transpose si5 to Sol5 (-4) :  "+si5.transpose(-4));
		System.out.println("transpose si5 to do4 (-23) :  "+si5.transpose(-23));
		System.out.println("And back to sol!! :  HEEEINNN? "+sol5.getPrevious(NoteName.FA).transpose(sol5.getEcart(sol5.getPrevious(NoteName.FA))));
	}



}
