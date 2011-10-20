package src.core;

import java.util.Collection;

import src.StaticConfiguration;


public class Note implements Comparable<Note>{

	//
	// Fields
	//

	NoteName note;
	public int hauteur;

	//
	// Constructor
	//

	public Note(NoteName n, int hauteur) {
		super();
		this.note = n;
		this.hauteur = hauteur;
	}

	//
	// Accessor
	//

	public NoteName getNoteName(){
		return note;
	}

	/*
	 * 
	 */

	public static int getFrequency(Note n, TuningTemperament t){
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
	
	public static Collection<Note> transpose(Collection<Note> notes, int demiTon){
		
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
			return new Note(that, this.hauteur+1);
	}
	//
	// Import/Export
	//

	public String toString(){
		return note.toString()+hauteur;
	}

	public int compareTo(Note that){
		if (this.hauteur-that.hauteur!=0){
			return this.hauteur-that.hauteur;
		} else 
			return this.note.ordinal()-that.note.ordinal();
	}

	//
	// Subclass
	//

	public enum NoteName {
		DO("  DO", " c"), DOd(" DO#", "c#"), RE("  RE", "d"), REd(" RE#", "d#"), 
		MI("  MI", "e"), FA("  FA", "f"), FAd(" FA#", "f#"), SOL(" SOL", "g"), SOLd("SOL#", "g#"), 
		LA("  LA", "a"), LAd(" LA#", "a#"), SI("  SI", "b");

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
