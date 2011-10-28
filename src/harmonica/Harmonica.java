package src.harmonica;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

import static src.harmonica.AirFlow.*;
import static src.harmonica.BendType.*;


import src.builder.MalformedHarmonicaException;
import src.harmonica.Note.NoteName;
import src.tools.HashedHashList;



public class Harmonica{

	//
	// Fields
	//

	private  String tuningName;
	private Note tonalite;
	private Hole[] holes;

	//
	// Constructors
	//

	public Harmonica(String tuningName, ReedPlate blowPlate, ReedPlate drawPlate, Note tonalite) throws MalformedHarmonicaException {
		this.tuningName = tuningName;
		this.tonalite=tonalite;
		generateHoles(blowPlate,drawPlate);
	}	

	private void generateHoles(ReedPlate blowPlate, ReedPlate drawPlate) throws MalformedHarmonicaException {
		if (blowPlate.getNumberOfReeds()!=drawPlate.getNumberOfReeds())
			throw new MalformedHarmonicaException();
		holes = new Hole[blowPlate.getNumberOfReeds()];
		for (int i = 0; i < blowPlate.getNumberOfReeds(); i++){
			holes[i] = new Hole(blowPlate.getReed(i), drawPlate.getReed(i));
			holes[i].blowValve=blowPlate.isValved(i);
			holes[i].drawValve=drawPlate.isValved(i);
		}
	}
	
	//
	// Accessors
	//

	public String getTuningName() {
		return tuningName;
	}

	protected void setTuningName(String tuningName) {
		this.tuningName = tuningName;
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
	
	public void setTonalite(Note tonalite) {
		this.tonalite = tonalite;
	}

	public void transpose(int demitons){
		tonalite.transpose(demitons);
	}	
	
	public Note getTonalite() {
		return tonalite;
	}
	
	/*
	 * 
	 */

	public int getNumberOfHoles(){
		return holes.length;
	}
	
	protected Hole getHole(int i){
		return holes[i];
	}

	/*
	 * 
	 */


	/*
	 * 
	 */

	public Note getNaturalNote(int holeNb, AirFlow air) {
		return air.equals(blow)?holes[holeNb].getBlow():holes[holeNb].getDraw();
	}

	public Note getNote(int holeNb, AirFlow air, int bendLevel) throws UnexistantNoteException{
		List<HarmonicaNote<Note>> notes = getNotes(holeNb,air);
		if (bendLevel>=notes.size())
			throw new UnexistantNoteException();
		else
			return notes.get(bendLevel).getNote();
	}

	/*
	 * 
	 */

	public HashedHashList<Note, HarmonicaNote<Note>> getAllNotes(){
		HashedHashList<Note, HarmonicaNote<Note>> result = new HashedHashList<Note, HarmonicaNote<Note>>();
		for (int i = 0; i < getNumberOfHoles(); i++){
			for (HarmonicaNote<Note> hn : getNotes(i))
				result.add(hn.getNote(), hn);
		}
		return result;
	}

	public List<HarmonicaNote<Note>> getNotes(int holeNb){
		List<HarmonicaNote<Note>> result = new ArrayList<HarmonicaNote<Note>>();
		result.addAll(getNotes(holeNb,natural));
		result.addAll(getNotes(holeNb,bluesbend));
		result.addAll(getNotes(holeNb,overbend));
		result.addAll(getNotes(holeNb,valvedbend));
		Collections.sort(result);
		return result;
	}



	public List<HarmonicaNote<Note>> getNotes(int holeNb, BendType bendType){
		List<HarmonicaNote<Note>> result = new ArrayList<HarmonicaNote<Note>>();
		result.addAll(getNotes(holeNb,blow,bendType));
		result.addAll(getNotes(holeNb,draw,bendType));
		Collections.sort(result);
		return result;
	}


	public List<HarmonicaNote<Note>> getNotes(int holeNb, AirFlow air){
		List<HarmonicaNote<Note>> result = new ArrayList<HarmonicaNote<Note>>();
		for (BendType bendT : BendType.values()){
			result.addAll(getNotes(holeNb, air, bendT));
		}
		Collections.sort(result);
		return result;		
	}

	public List<HarmonicaNote<Note>> getNotes(int holeNb, AirFlow air, BendType bendType){
		List<HarmonicaNote<Note>> result = new ArrayList<HarmonicaNote<Note>>();

		switch (bendType) {
		case natural:
			HarmonicaNote<Note> hn = 
			new HarmonicaNote<Note>(
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
						result.add(new HarmonicaNote<Note>(
								holeNb, 
								air, bendType, ++i,
								n));
				}
			}
			break;
		case overbend :
			if (!isValved(holeNb,blow) && !isValved(holeNb,draw))
				if (getHole(holeNb).getLowerNoteAirDirection().equals(air))
					result.add(new HarmonicaNote<Note>(
							holeNb, 
							air, bendType, 1,
							getHole(holeNb).getUpper().transpose(1)));
			break;		
		case valvedbend :
			if (isValved(holeNb,getHole(holeNb).getUpperNoteAirDirection()))
				if (getHole(holeNb).getLowerNoteAirDirection().equals(air))
					result.add(new HarmonicaNote<Note>(
							holeNb, 
							air, bendType, 1,
							getHole(holeNb).getLower().transpose(-1)));
			break;		
		}
		Collections.sort(result);
		return result;
	}
	
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
	 * @param notesToPlay : list of score's note
	 * @param optimiser : used to select best harmonica note when there is several choice
	 * @return the corresponding harmonica notes of the harmonica
	 */
	public Map<AbstractNote,HarmonicaNote<Note>> match(
			Collection<AbstractNote> notesToPlay, 
			final PlayerPreferences optimiser){
		Collection<AbstractNote> notes=new ArrayList<AbstractNote>();
		HashedHashList<Note, HarmonicaNote<Note>> myNotes = getAllNotes();
		List<Map<AbstractNote,HarmonicaNote<Note>>> allPossible = new ArrayList<Map<AbstractNote,HarmonicaNote<Note>>>();

		//transposing : the score get higher		
		notes.addAll(notesToPlay);
		while (getLowerNote().compareTo(Note.getLower(notesToPlay))>=0 && getUpperNote().compareTo(Note.getUpper(notesToPlay))<=0){
			Map<AbstractNote,HarmonicaNote<Note>> newWay = new HashMap<AbstractNote, Harmonica.HarmonicaNote<Note>>();
			boolean worked = true;
			for (AbstractNote n : notes){
				if (myNotes.contains(n))
					newWay.put(n, optimiser.getPrefered(myNotes.get(n)));
				else {
					worked=false;
					break;
				}					
			}		
			if (worked){
				allPossible.add(newWay);
			}
			try {
				Note.transpose(notes, 1);
			} catch (UnexistantNoteException e) {
				break;
			}
		}
		//transposing : the score get lower
		notes.clear();
		notes.addAll(notesToPlay);
		while (getLowerNote().compareTo(Note.getLower(notes))>=0 && getUpperNote().compareTo(Note.getUpper(notes))<=0){
			Map<AbstractNote,HarmonicaNote<Note>> newWay = new HashMap<AbstractNote, Harmonica.HarmonicaNote<Note>>();
			boolean worked = true;
			for (AbstractNote n : notes){
				if (myNotes.contains(n))
					newWay.put(n, optimiser.getPrefered(myNotes.get(n)));
				else {
					worked=false;
					break;
				}					
			}		
			if (worked){
				allPossible.add(newWay);
			}
			try {
				Note.transpose(notes, -1);
			} catch (UnexistantNoteException e) {
				break;
			}
		}

		//choosing the best		
		Comparator<Map<AbstractNote,HarmonicaNote<Note>>> myPrefs = new Comparator<Map<AbstractNote,HarmonicaNote<Note>>>() {
			@Override
			public int compare(
					Map<AbstractNote, HarmonicaNote<Note>> o1,
					Map<AbstractNote, HarmonicaNote<Note>> o2) {
				return optimiser.compare(o1.values(),o2.values());
			}
		};		
		return Collections.max(allPossible, myPrefs);
	}
	
	public static int getScoreTransposition(Map<AbstractNote,HarmonicaNote<Note>> mapping){
		//TODO
	}
	


	//
	// Import/Export
	//

	public String toString(){
		return "\n Accordage "+tuningName+"\n"+getPlate(blow)+"\n"+getPlate(draw);
	}

	//
	// Primitives
	//

	private Note getLowerNote() {
		// TODO Auto-generated method stub
		return null;
	}


	private Note getUpperNote() {
		// TODO Auto-generated method stub
		return null;
	}
	//
	// Subclasses
	//


	public class HarmonicaNote<TNote extends AbstractNote> implements Comparable<HarmonicaNote<TNote>>, AbstractNote{


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

		private final TNote myNote;

		//
		// Constructor
		//

		private HarmonicaNote(int holeNumber, AirFlow air, BendType bendType, int bendLevel, TNote myNote) {
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

		public TNote getNote(){
			return myNote;
		}

		@Override
		public NoteName getNoteName() {
			return getNote().getNoteName();
		}

		@Override
		public Integer getHauteur() {
			return getNote().getHauteur();
		}

		public String toString(){
			String result = ""+air+holeNumber;
			for (int i = 0; i < bendLevel; i++)
				result+=bendType;
			return result;

		}
		
		public AirFlow getAirDirection() {
			return air;
		}

		@Override
		public int compareTo(HarmonicaNote<TNote> that) {
			if (new Integer(this.bendLevel).compareTo(that.bendLevel)!=0)
				return new Integer(this.bendLevel).compareTo(that.bendLevel);
			else
				return 	optimiser.compareNote(this,that);
		}

		@Override
		public AbstractNote transpose(int demiTon)
				throws UnexistantNoteException {
			AbstractNote n = getNote().transpose(demiTon);
			return optimiser.getPrefered(getAllNotes().get(n));
		}


	}


	protected class Hole {

		//
		// Fields
		//

		public final int blowRelative;
		public final int drawRelative;

		public Boolean blowValve;
		public Boolean drawValve;


		//
		// Constructor
		//

		public Hole(Note blow, Note draw) {
			this.blowRelative = blow.getEcart(Harmonica.this.tonalite);
			this.drawRelative = draw.getEcart(Harmonica.this.tonalite);
		}

		public Hole(int blow, int draw) {
			this.blowRelative = blow;
			this.drawRelative = draw;
		}
		
		//
		// Accessors
		//

		public Note getLower(){
			if (blow.compareTo(draw)<0){
				return getBlow();
			} else {
				return getDraw();
			}
		}

		public Note getBlow() {
			return Harmonica.this.tonalite.transpose(blowRelative);
		}

		public Note getDraw() {
			return Harmonica.this.tonalite.transpose(drawRelative);
		}

		public Note getUpper(){
			if (blow.compareTo(draw)>0){
				return getBlow();
			} else {
				return getDraw();
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

