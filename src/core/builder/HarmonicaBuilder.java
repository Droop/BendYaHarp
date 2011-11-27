package core.builder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import core.MalformedHarmonicaException;
import core.harmonica.AirFlow;
import core.harmonica.Harmonica;
import core.harmonica.Note;
import core.harmonica.ReedPlate;
import core.harmonica.Note.TuningTemperament;


import static core.harmonica.AirFlow.*;
import static core.harmonica.BendType.*;


public class HarmonicaBuilder {

	//
	// Fields
	//
	
	Harmonica myHarp;
	TuningTemperament temperament = TuningTemperament.EqualTemperament;
	
	//
	// Constructor
	//
	
	public HarmonicaBuilder(String tuningName, ReedPlate blowPlate, ReedPlate drawPlate) throws MalformedHarmonicaException{
		super(tuningName,HarmonicaBuilder.generateHoles(blowPlate, drawPlate));
	}

	public HarmonicaBuilder(String tuningName, ReedPlate blowPlate, ReedPlate drawPlate, TuningTemperament temperament) throws MalformedHarmonicaException{
		super(tuningName,HarmonicaBuilder.generateHoles(blowPlate, drawPlate));
	}
	
	//
	// Accessors
	//

	public void setReed(int nbHole, AirFlow air, Note reed){
		tuningName = "modifiedHarp";
		holes[nbHole] = new Hole(air.equals(blow)?reed:getNaturalNote(nbHole,blow),air.equals(draw)?getNaturalNote(nbHole,draw):reed);
	}

	
	public int isValid(DataBasable h){
		
	}
	
	public void setValve(int holeNb, AirFlow air, boolean valved) {
		tuningName = "modifiedHarp";
		if (air.equals(blow))
			holes[holeNb].blowValve=valved;
		else
			holes[holeNb].drawValve=valved;
	}

	public void setHalfValved(){
		tuningName = "modifiedHarp";
		for (int i = 0; i < getNumberOfHoles(); i++){
			setValve(i, holes[i].getUpperNoteAirDirection(), true);
			setValve(i, holes[i].getUpperNoteAirDirection(), false);
		}
	}

	public void setFullValved(){
		tuningName = "modifiedHarp";
		for (Hole h : holes){
			h.blowValve=true;
			h.drawValve=true;
		}
	}

	//
	// Methods
	//
	
	
	public int nbDifferentsReeds(DataBasable n){
		
	}

	public int maxReedDifference(DataBasable n){
		
	}

}
//
//
//	
//	public void addHole(NoteChrom low, int lowHauteur, NoteChrom high, int highHauteur){
//		add(new Interval(new Note(low, lowHauteur), new Note(high, highHauteur)));
//	}
//
//	public boolean isPossible(Harmonica a){
//		for (int i =0; i < a.getNumberOfHoles(); i++){
//			if (!get(i).noteIsInInterval(a.getHole(i).blow)){
////				System.out.println(a.getHole(i).blow+" is not in "+configurator.get(i));
//				return false;				
//			}
//			if (!get(i).noteIsInInterval(a.getHole(i).draw) ){
////				System.out.println(a.getHole(i).draw+" is not in "+configurator.get(i));
//				return false;								
//			}
//		}
//		return true;
//	}
//	
//	public Note getFirstNote(){
//		return Note.getLower(get(0).note1, get(0).note2);
//	}
//}
//class Seydel {
//
//
//	
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		
//	BuildedHarmonica configurator= new BuildedHarmonica();
//		configurator.addHole(SOL, 2, LAd, 4);
//		configurator.addHole(MI, 3, DOd, 5);
//		configurator.addHole(SOL, 3, FA, 5);
//		configurator.addHole(SI, 3, SOL, 5);
//		configurator.addHole(DO, 4, LA, 5);
//		configurator.addHole(MI, 4, DO, 6);
//		configurator.addHole(SOL, 4, MI, 6);
//		configurator.addHole(LAd, 4, FAd, 6);
//		configurator.addHole(SI, 4, SOLd, 6);
//		configurator.addHole(REd, 5, SI, 6);
//		configurator.addHole(FAd, 5, REd, 6);
//		configurator.addHole(MI, 6, REd, 7);
//
//		List<NoteChrom> blowPlate = Arrays.asList(
//				new NoteChrom[]{FA,  LAd,   RE,  SOL,  DO});
//
//		List<NoteChrom> drawPlate = Arrays.asList(
//				new NoteChrom[]{RE,  SOL,  SI,  MI,   LA, });
//
////		List<NoteChrom> blowPlate = Arrays.asList(
////				new NoteChrom[]{MI,  LA,   RE,  SOL,  DO, FA,  LA,   RE,  SOL,  DO});
////
////		List<NoteChrom> drawPlate = Arrays.asList(
////				new NoteChrom[]{RE,  SOL,  SI,  FA,   LA, RE,  SOL,  SI,  MI,   LA});
////
////		List<NoteChrom> drawPlate = Arrays.asList(
////				new NoteChrom[]{RE,  SOL,  DO, FA,  LAd,   RE,  SOL,  DO, FA, LAd});
////
////		List<NoteChrom> blowPlate = Arrays.asList(
////				new NoteChrom[]{SI,  FA,   LA, RE,  SOL,  SI,  FA,   LA, RE,  SOL});
//		for (int demitons = 0; demitons < 12; demitons++)
//			for (int startingpoint = 0; startingpoint < blowPlate.size(); startingpoint++){
//				Harmonica harp = 
//					new Harmonica(blowPlate, drawPlate, 
//							12,	startingpoint, configurator.getFirstNote()).transpose(demitons).adjust(configurator.get(0));
//
////				System.out.println("testing : start point "+startingpoint+" transp "+demitons);
////					System.out.println(harp);
//				if (configurator.isPossible(harp)){
//					
//					System.err.println("HarpFound! : start point "+startingpoint+" transp "+demitons);
//					System.out.println(harp);
//					System.out.flush();
//				}
////					System.out.println();
//			}
//				System.out.println("fin");
//	}
//}
//class Harponline {
//
//
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//
//		BuildedHarmonica configurator= new BuildedHarmonica();
//		configurator.addHole(RE, 3, MI, 4);
//		configurator.addHole(FAd, 3, SOL, 4);
//		configurator.addHole(LA, 3, SI, 4);
//		configurator.addHole(RE, 4, RE, 5);
//		configurator.addHole(FAd, 4, FAd, 5);
//		configurator.addHole(LA, 4, LA, 5);
//		configurator.addHole(SI, 4, DO, 6);
//		configurator.addHole(RE, 5, MI, 6);
//		configurator.addHole(FA, 5, SOL, 6);
//		configurator.addHole(LA, 5, DO, 7);
//
//		List<NoteChrom> drawPlate = Arrays.asList(
//				new NoteChrom[]{RE,  SOL,  DO, FA,  LAd});
//
//		List<NoteChrom> blowPlate = Arrays.asList(
//				new NoteChrom[]{SI,  MI,   LA, RE,  SOL });
//
//		int startingpoint = 0;
////		for (startingpoint = 0; startingpoint < blowPlate.size(); startingpoint++)
//			for (int demitons = -6; demitons < 6; demitons++){
//				Harmonica harp = 
//					new Harmonica(blowPlate, drawPlate, 
//							10,	startingpoint, configurator.getFirstNote()).transpose(demitons).adjust(configurator.get(0));
//
//											System.out.println("testing : start point "+startingpoint+" transp "+demitons);
////												System.out.println(harp);
////				if (configurator.isPossible(harp)){
////					System.err.println("HarpFound! : start point "+startingpoint+" transp "+demitons);
//					System.out.println(harp);
//					System.out.flush();
//				}
//				//					System.out.println();
////			}
//		System.out.println("fin");
//	}
//}
