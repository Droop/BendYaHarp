package src.api;
import static src.harmonica.NoteChrom.*;

import java.util.Arrays;
import java.util.List;

import src.harmonica.AbstractNote;
import src.harmonica.Harmonica;
import src.harmonica.Note;
import src.harmonica.NoteChrom;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AbstractNote n = new Note(RE, 3);
//		System.out.println(n.transpose(0));
//		System.out.println(n.transpose(2));
//		System.out.println(n.transpose(4));
//		System.out.println(n.transpose(5));
//		System.out.println(n.transpose(7));
//		System.out.println(n.transpose(9));
//		System.out.println(n.transpose(11));
//		System.out.println(n.transpose(12));
//		System.out.println(n.transpose(14));
//		System.out.println("²²²²²²²²²²");
		System.out.println(n.getNextHauteur(RE));
		System.out.println(n.getNextHauteur(FA));
		System.out.println(n.getNextHauteur(DO));
		System.out.println(Arrays.asList(NoteChrom.values()));

		System.out.println();
		System.out.println();
		List<NoteChrom> blowPlate = Arrays.asList(
				new NoteChrom[]{MI,  LA,   RE,  SOL,  DO, FA,  LA,   RE,  SOL,  DO});

		List<NoteChrom> drawPlate = Arrays.asList(
				new NoteChrom[]{RE,  SOL,  SI,  FA,   LA, RE,  SOL,  SI,  MI,   LA});
		
		Harmonica harp = 
			new Harmonica(blowPlate, drawPlate, 
					10,	9, new Note(DO, 3)).transpose(0);
		
		System.out.println(harp);
		
	}

}
