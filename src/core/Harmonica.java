package src.core;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static src.core.AirFlow.*;
import static src.core.BendType.*;

import org.jdom.Element;

import src.tools.HashedHashList;
import src.tools.MapedCollection;



public class Harmonica{

	//
	// Fields
	//

	private  String tuningName;


	private  Hole[] holes;

	//
	// Constructors
	//

	public Harmonica(String tuningName, ReedPlate blowPlate, ReedPlate drawPlate) throws MalformedHarmonicaException{
		this.tuningName = tuningName;
		generateHoles(blowPlate, drawPlate);
	}

	Harmonica(String tuningName, Hole[] holes) {
		this.tuningName = tuningName;
		this.holes = holes;
	}


	//
	// Accessors
	//

	public String getTuningName() {
		return tuningName;
	}

	public void setTuningName(String tuningName) {
		this.tuningName = tuningName;
	}
	
	public int getNumberOfHoles(){
		return holes.length;
	}

	/*
	 * 
	 */

	public Boolean isValved(int holeNb, AirFlow air) {
		return  air.equals(blow)?holes[holeNb].blowValve:holes[holeNb].drawValve;
	}
	
	/*
	 * 
	 */

	public void setReed(int nbHole, AirFlow air, Note reed){
		tuningName = "modifiedHarp";
		holes[nbHole] = new Hole(air.equals(blow)?reed:getNaturalNote(nbHole,blow),air.equals(draw)?getNaturalNote(nbHole,draw):reed);
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

	/*
	 * 
	 */

	public Note getNaturalNote(int holeNb, AirFlow air) {
		return air.equals(blow)?holes[holeNb].blow:holes[holeNb].draw;
	}

	public Note getNote(int holeNb, AirFlow air, int bendLevel) throws UnexistantNoteException{
		//TODO
		return null;
	}

	/*
	 * 
	 */

	public HashedHashList<Note, HarmonicaNote> getAllNotes(){
		HashedHashList<Note, HarmonicaNote> result = new HashedHashList<Note, HarmonicaNote>();
		for (int i = 0; i < getNumberOfHoles(); i++){
			for (HarmonicaNote hn : getNotes(i))
				result.add(hn.getNote(), hn);
		}
		return result;
	}

	public Collection<HarmonicaNote> getNotes(int holeNb){
		Collection<HarmonicaNote> result = new ArrayList<HarmonicaNote>();
		result.addAll(getNotes(holeNb,natural));
		result.addAll(getNotes(holeNb,bluesbend));
		result.addAll(getNotes(holeNb,overbend));
		result.addAll(getNotes(holeNb,valvedbend));
		return result;
	}


	public Collection<HarmonicaNote> getNotes(int holeNb, BendType bendType){
		Collection<HarmonicaNote> result = new ArrayList<HarmonicaNote>();
		result.addAll(getNotes(holeNb,blow,bendType));
		result.addAll(getNotes(holeNb,draw,bendType));
		return result;
	}

	
	public Collection<HarmonicaNote> getNotes(int holeNb, AirFlow air){
		Collection<HarmonicaNote> result = new ArrayList<HarmonicaNote>();
		for (BendType bendT : BendType.values()){
			result.addAll(getNotes(holeNb, air, bendT));
		}
		return result;		
	}
	
	public Collection<HarmonicaNote> getNotes(int holeNb, AirFlow air, BendType bendType){
		Collection<HarmonicaNote> result = new ArrayList<HarmonicaNote>();

		switch (bendType) {
		case natural:
			HarmonicaNote hn = 
			new HarmonicaNote(
					holeNb, 
					air, bendType, 0, 
					getNaturalNote(holeNb,air));
			result.add(hn);
			break;
		case bluesbend :
			if (!(isValved(holeNb,blow) && isValved(holeNb,draw))){
				if (getHole(holeNb).getUpperNoteAirDirection().equals(air)){
					List<Note>  bends = getHole(holeNb).getNotesBetween();
					int i = 1;
					for (Note n : bends)
						result.add(new HarmonicaNote(
								holeNb, 
								air, bendType, ++i,
								n));
				}
			}
			break;
		case overbend :
			if (!isValved(holeNb,blow) && !isValved(holeNb,draw))
				if (getHole(holeNb).getLowerNoteAirDirection().equals(air))
					result.add(new HarmonicaNote(
							holeNb, 
							air, bendType, 1,
							getHole(holeNb).getUpper().transpose(1)));
			break;		
		case valvedbend :
			if (isValved(holeNb,getHole(holeNb).getUpperNoteAirDirection()))
				if (getHole(holeNb).getLowerNoteAirDirection().equals(air))
					result.add(new HarmonicaNote(
							holeNb, 
							air, bendType, 1,
							getHole(holeNb).getLower().transpose(-1)));
			break;		
		}
		return result;
	}

	/*
	 * 
	 */

	public ReedPlate getPlate(AirFlow air){
		Note[] notes = new Note[getNumberOfHoles()];
		Boolean[] valves = new Boolean[getNumberOfHoles()];
		for (int i = 0; i < getNumberOfHoles(); i++){
			notes[i] = getNaturalNote(i,air);
			valves[i] = isValved(i,air);
		}
		return new ReedPlate(notes, valves);		
	}

	//
	// Methods
	//

	/**
	 * 
	 * @param demitons
	 * @return an harmonicas transposed
	 */
	public Harmonica transpose(int demitons){
		Hole[] holesT = new Hole[getNumberOfHoles()];
		Boolean[] valvesT = new Boolean[getNumberOfHoles()];
		for (int i = 0; i < getNumberOfHoles(); i++){
			holesT[i] = new Hole(getNaturalNote(i,blow).transpose(demitons), getNaturalNote(i,draw).transpose(demitons));
			holesT[i].blowValve = isValved(i,blow);
			holesT[i].drawValve = isValved(i,draw);
		}
		return new Harmonica(tuningName, holesT);
	}
	
	/**
	 * 
	 * @param notesToPlay : list of score's note
	 * @param optimiser : used to select best harmonica note when there is several choice
	 * @return the corresponding harmonica notes of the harmonica
	 */
	public HarmonicaScoreGenerator match(
			Collection<Note> notesToPlay, 
			Comparator<Collection<HarmonicaNote>> optimiser, 
			boolean transposeHarmonica){
		//TODO
		return null;
	}
	
	//
	// Import/Export
	//

	public String toString(){
		//TODO;
		throw new RuntimeException("todo");
	}

	public static Harmonica harmonicafromString(String harm){
		//TODO;
		throw new RuntimeException("todo");
	}



	public static HarmonicaNote harmonicaNotefromString(String harmNote){
		//TODO;
		return null;
	}

	//
	// Primitives
	//

	/**
	 *  
	 * @param notesToPlay : Collection of the notes of the score
	 * @param optimiser : the prefered playing way of the player
	 * @return the number of semitone to transpose the score in order to be best for the player given the harp (best tonality of the score)
	 * 
	 */
	protected int computeBestScoreTransposition(Collection<Note> notesToPlay, Comparator<Collection<HarmonicaNote>> optimiser){
		//TODO
		return 0;
	}

	/**
	 *  
	 * @param notesToPlay : Collection of the notes of the score
	 * @param optimiser : the prefered playing way of the player
	 * @return the number of semitone to transpose the score in order to be best for the player given the harp (best tonality of the score)
	 * 
	 */
	protected int computeBestHarmonicaTransposition(Collection<Note> notesToPlay, Comparator<Collection<HarmonicaNote>> optimiser){
		//TODO
		return 0;
	}

	protected Note harpNote2Note(HarmonicaNote harmNote) throws UnexistantNoteException{
		if (getAllNotes().containsValue(harmNote))
			return harmNote.getNote();
		else
			throw new UnexistantNoteException();
	}

	protected HarmonicaNote note2HarpNote(Note n, Comparator<Collection<HarmonicaNote>> optimiser) throws UnexistantNoteException {
		if (getAllNotes().containsKey(n))
			return getAllNotes().get(n);
		else
			throw new UnexistantNoteException();		
	}
	
	/*
	 * 
	 */
	
	private void generateHoles(ReedPlate blowPlate, ReedPlate drawPlate) throws MalformedHarmonicaException {
		if (blowPlate.getNumberOfReeds()!=drawPlate.getNumberOfReeds())
			throw new MalformedHarmonicaException();
		holes = new Hole[blowPlate.getNumberOfReeds()];
		for (int i = 0; i < getNumberOfHoles(); i++){
			holes[i] = new Hole(blowPlate.getReed(i), drawPlate.getReed(i));
			setValve(i,blow,blowPlate.isValved(i));
			setValve(i,draw,drawPlate.isValved(i));
		}

	}	

	private Hole getHole(int i){
		return holes[i];
	}


	//
	// Subclasses
	//


	public class HarmonicaNote implements Comparable<HarmonicaNote>{


		//
		// Fields
		//

		private final int holeNumber;
		private final AirFlow air;
		private final BendType bendType;
		private final int bendLevel;

		/*
		 * 
		 */

		private final Note myNote;

		//
		// Constructor
		//

		private HarmonicaNote(int holeNumber, AirFlow air, BendType bendType, int bendLevel, Note myNote) {
			super();
			this.holeNumber = holeNumber;
			this.air = air;
			this.bendLevel = bendLevel;
			this.myNote = myNote;
			this.bendType = bendType;
		}

		public BendType getBendType(){
			return bendType;
		}

		public Note getNote(){
			return myNote;
		}

		public String toString(){
			String result = ""+air+holeNumber;
			for (int i = 0; i < bendLevel; i++)
				result+=bendType;
			return result;

		}

		@Override
		public int compareTo(HarmonicaNote that) {
			return new Integer(this.bendLevel).compareTo(that.bendLevel);//TRI PAR TYPE AUSSI!!!!
		}
	}


	class Hole {

		//
		// Fields
		//

		public final Note blow;
		public final Note draw;

		public Boolean blowValve;
		public Boolean drawValve;


		//
		// Constructor
		//

		public Hole(Note blow, Note draw) {
			this.blow = blow;
			this.draw = draw;
		}

		//
		// Accessors
		//

		public Note getLower(){
			if (blow.compareTo(draw)<0){
				return blow;
			} else {
				return draw;
			}
		}

		public Note getUpper(){
			if (blow.compareTo(draw)>0){
				return blow;
			} else {
				return draw;
			}
		}

		public AirFlow getLowerNoteAirDirection(){
			if (blow.compareTo(draw)<0){
				return AirFlow.blow;
			} else {
				return AirFlow.draw;
			}
		}

		public AirFlow getUpperNoteAirDirection(){
			if (blow.compareTo(draw)>0){
				return AirFlow.blow;
			} else {
				return AirFlow.draw;
			}
		}

		//
		// Methods
		//


		public List<Note> getNotesBetween(){
			List<Note> result = new ArrayList<Note>();
			Note temp = getUpper();
			while (temp.transpose(-1).compareTo(this.getLower())>0)
				result.add(temp);
			Collections.sort(result);
			return result;
		}

		public boolean noteIsInInterval(Note n){
			return n.compareTo(getUpper())<=0 && n.compareTo(getLower())>=0;
		}

		public String toString(){
			return "|"+blow+"+ , "+draw+"- |" ;
		}
	}
}

///**
// * @param cyclicBlowPlate
// * @param cyclicDrawPlate
// * @param holeNb
// * @param startingPoint
// */
//public Harmonica(
//		List<NoteChrom> cyclicBlowPlate, List<NoteChrom> cyclicDrawPlate, 
//		int holeNb, int startingPoint, Note startingNote) {
//	if (cyclicBlowPlate.size()!=cyclicDrawPlate.size()){
//		System.err.println("aah");
//		System.exit(-1);
//	}
//
//	int j = startingPoint;
//	Note startingNoteBlow = startingNote, startingNoteDraw=startingNote;
//	for (int i = 0;i < holeNb; i++){
//		//			System.out.println("Hole "+i);
//		//			System.out.println("Note to match "+cyclicBlowPlate.get(j)+", "+cyclicDrawPlate.get(j));
//		//			System.out.println("Hauteur courante "+startingNoteBlow+", "+startingNoteDraw);
//		//			System.out.println("Resultat "+startingNoteBlow.getNextHauteur(cyclicBlowPlate.get(j))
//		//					+", "+startingNoteDraw.getNextHauteur(cyclicDrawPlate.get(j)));
//		Trou neoHole = 
//				new Trou(startingNoteBlow.getNextHauteur(cyclicBlowPlate.get(j)), 
//						startingNoteDraw.getNextHauteur(cyclicDrawPlate.get(j)));
//		holes.add(neoHole);
//		//
//		startingNoteBlow = neoHole.blow;
//		startingNoteDraw = neoHole.draw;
//		j++;
//		if (j>cyclicBlowPlate.size()-1)
//			j=0;
//
//	}
//}
//
//public Harmonica(ArrayList<Trou> harmonica) {
//	super();
//	this.holes = harmonica;
//}
//
//
//public Harmonica transpose(int demiTons){
//	ArrayList<Trou> neoHarp = new ArrayList<Trou>();
//	for  (Trou h : holes){
//		neoHarp.add(h.transpose(demiTons));
//	}
//	return new Harmonica(neoHarp);
//}
//
////Pas propre
//public Harmonica adjust(Interval init){
//	ArrayList<Trou> neoHarp = new ArrayList<Trou>();
//	neoHarp.addAll(holes);
//	int iBlow = 0;
//	int iDraw = 0;
//	//		System.out.println(neoHarp);
//	while (init.note1.isLower(new Note(holes.get(0).blow.getNoteChrom(), holes.get(0).blow.hauteur-iBlow))
//			||init.note2.isLower(new Note(holes.get(0).blow.getNoteChrom(), holes.get(0).blow.hauteur-iBlow)))
//		iBlow++;
//	while (init.note1.isLower(new Note(holes.get(0).draw.getNoteChrom(), holes.get(0).draw.hauteur-iDraw)) 
//			||init.note2.isLower(new Note(holes.get(0).draw.getNoteChrom(), holes.get(0).draw.hauteur-iDraw)) )
//		iDraw++;
//
//	for  (Trou h : neoHarp){
//		h.blow.hauteur-=iBlow;
//		h.draw.hauteur-=iDraw;
//	}
//	//		System.out.println(neoHarp);
//	return new Harmonica(neoHarp);
//}
//
//public int getHoleNumber() {
//	return holes.size();
//}
//
//public Trou getHole(int i) {
//	return holes.get(i);
//}
//
//public String showBlowPlate(){
//	String yo = "";
//	for (Trou h : holes)
//		yo=yo+" "+h.blow;
//			return yo;
//}
//public String showDrawPlate(){
//	String yo = "";
//	for (Trou h : holes)
//		yo=yo+" "+h.draw;
//			return yo;
//}
//public String toString(){
//	return "²²²²²²²²²²²²²²²²²²²²²²²²²²²\n"+
//			showBlowPlate()+"\n"+
//			showDrawPlate()+"\n"+
//			"²²²²²²²²²²²²²²²²²²²²²²²²²²²";
//}
//}

