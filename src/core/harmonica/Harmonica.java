package core.harmonica;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

import core.MalformedHarmonicaException;
import core.Player;
import core.UnexistantNoteException;
import core.harmonica.Note.NoteName;
import core.tools.HashedHashList;

import static core.harmonica.AirFlow.*;
import static core.harmonica.BendType.*;
import static core.harmonica.Note.NoteName.*;





public class Harmonica {

	//
	// Fields
	//

	private final  String tuningName;

	private final Note firstHoleBlow;
	private final Note firstHoleBlowTonalite;

	private final Hole[] holes;

	//
	// Constructors
	//

	public Harmonica(String tuningName, ReedPlate blowPlate, ReedPlate drawPlate, Note platesTonality) throws MalformedHarmonicaException {
		this.tuningName = tuningName;

		firstHoleBlow=blowPlate.getReed(0);
		firstHoleBlowTonalite = platesTonality;

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

	public String getName() {
		return tuningName;
	}

	//	public Harmonica getHalfValved(){
	//		String tuningName=tuningName.replace("halffValved", "").replace("fullValved", "")+"halffValved"; 
	//		ReedPlate blowPlate = getPlate(new Note(DO,3),blow);
	//		ReedPlate drawPlate = getPlate(new Note(DO,3),draw);
	//		for (int i = 0; i < getNumberOfHoles(); i++){
	//			setValve(i, holes[i].getLowerNoteAirDirection(), true);
	//			setValve(i, holes[i].getUpperNoteAirDirection(), false);
	//		}
	//	}
	//
	//	public Harmonica getFullValved(){
	//		tuningName = tuningName.replace("halfValved", "").replace("fullValved", "")+"fullValved";
	//		for (Hole h : holes){
	//			h.blowValve=true;
	//			h.drawValve=true;
	//		}
	//	}
	/*
	 * 
	 */

	public Boolean isValved(int holeNb, AirFlow air) {
		return  air.equals(blow)?holes[holeNb].blowValve:holes[holeNb].drawValve;
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

	public Note getNaturalNote(int holeNb, Note tonalite, AirFlow air) {
		return air.equals(blow)?holes[holeNb].getBlow(tonalite):holes[holeNb].getDraw(tonalite);
	}

	public Note getNote(int holeNb, Note tonalite, AirFlow air, int bendLevel) throws UnexistantNoteException{
		List<HarmonicaNote<Note>> notes = getNotes(holeNb,tonalite,air);
		if (bendLevel>=notes.size())
			throw new UnexistantNoteException();
		else
			return notes.get(bendLevel).getNote();
	}

	/*
	 * 
	 */

	public Collection<Chord> getChords(Note tonalite, Player capacities){

	}

	public HashedHashList<Note, HarmonicaNote<Note>> getAllNotes(Note tonalite){
		HashedHashList<Note, HarmonicaNote<Note>> result = new HashedHashList<Note, HarmonicaNote<Note>>();
		for (int i = 0; i < getNumberOfHoles(); i++){
			for (HarmonicaNote<Note> hn : getNotes(i,tonalite))
				result.add(hn.getNote(), hn);
		}
		return result;
	}

	public List<HarmonicaNote<Note>> getNotes(int holeNb, Note tonalite){
		List<HarmonicaNote<Note>> result = new ArrayList<HarmonicaNote<Note>>();
		result.addAll(getNotes(holeNb,tonalite,natural));
		result.addAll(getNotes(holeNb,tonalite,bluesbend));
		result.addAll(getNotes(holeNb,tonalite,overbend));
		result.addAll(getNotes(holeNb,tonalite,valvedbend));
		Collections.sort(result);
		return result;
	}



	public List<HarmonicaNote<Note>> getNotes(int holeNb, Note tonalite, BendType bendType){
		List<HarmonicaNote<Note>> result = new ArrayList<HarmonicaNote<Note>>();
		result.addAll(getNotes(holeNb,tonalite,blow,bendType));
		result.addAll(getNotes(holeNb,tonalite,draw,bendType));
		Collections.sort(result);
		return result;
	}


	public List<HarmonicaNote<Note>> getNotes(int holeNb, Note tonalite, AirFlow air){
		List<HarmonicaNote<Note>> result = new ArrayList<HarmonicaNote<Note>>();
		for (BendType bendT : BendType.values()){
			result.addAll(getNotes(holeNb,tonalite, air, bendT));
		}
		Collections.sort(result);
		return result;		
	}

	public List<HarmonicaNote<Note>> getNotes(int holeNb, Note tonalite, AirFlow air, BendType bendType){
		List<HarmonicaNote<Note>> result = new ArrayList<HarmonicaNote<Note>>();

		switch (bendType) {
		case natural:
			HarmonicaNote<Note> hn = 
			new HarmonicaNote<Note>(
					holeNb, 
					air, bendType, 0, 
					getNaturalNote(holeNb, tonalite, air));
			result.add(hn);
			break;
		case bluesbend :
			if (!(isValved(holeNb,blow) && isValved(holeNb,draw))){
				if (getHole(holeNb).getUpperNoteAirDirection().equals(air)){
					List<Note>  bends = getHole(holeNb).getNotesBetween(tonalite);
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
							getHole(holeNb).getUpper(tonalite).transpose(1)));
			break;		
		case valvedbend :
			if (isValved(holeNb,getHole(holeNb).getUpperNoteAirDirection()))
				if (getHole(holeNb).getLowerNoteAirDirection().equals(air))
					result.add(new HarmonicaNote<Note>(
							holeNb, 
							air, bendType, 1,
							getHole(holeNb).getLower(tonalite).transpose(-1)));
			break;		
		}
		Collections.sort(result);
		return result;
	}

	public ReedPlate getPlate(Note tonalite, AirFlow air){
		Note[] notes = new Note[getNumberOfHoles()];
		Boolean[] valves = new Boolean[getNumberOfHoles()];
		for (int i = 0; i < getNumberOfHoles(); i++){
			notes[i] = getNaturalNote(i,tonalite,air);
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
			final Player optimiser){
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

	/* (non-Javadoc)
	 * @see src.harmonica.DataBasable#getPattern()
	 */
	public static Pattern getPattern(){
		return Pattern.compile(
				"NAME(.*)\n(?:.*\n*)*?BLOW(.*)\n(?:.*\n*)*?DRAW(.*)\n", 
				Pattern.CASE_INSENSITIVE);//.compile("(([^)]*))");;
	}

	/* (non-Javadoc)
	 * @see src.harmonica.DataBasable#fromMatcher(java.util.regex.Matcher)
	 */
	public static Harmonica fromMatcher(Matcher m) throws MalformedHarmonicaException, UnexistantNoteException{
		String name = m.group(1);
		String[] blow = m.group(2).replaceFirst("[ \t\n\f\r]++", "").split("[ \t\n\f\r]++");
		String[] draw = m.group(3).replaceFirst("[ \t\n\f\r]++", "").split("[ \t\n\f\r]++");
		return new Harmonica(name, new ReedPlate(blow), new ReedPlate(draw), Note.do3);
	}

	public String toString(){
		return "\n Accordage "+tuningName+" (en DO) :\n"+getPlate(Note.do3, blow)+"\n"+getPlate(Note.do3, draw);
	}

	//	public String toStringWithBends(Player optimiser){
	//		return "\n Accordage "+tuningName+"\n"+getPlate(blow)+"\n"+getPlate(draw);
	//	}
	//
	// Primitives
	//

	public int hashCode(){
		int c = 3;
		for (int i = 0; i < getNumberOfHoles(); i++)
			c+= getHole(i).hashCode()*Math.pow(7, i);
		return c;
	}

	
	public boolean equals(Object o){
		if (o instanceof Harmonica){
			Harmonica that = (Harmonica) o;

			if (that.getNumberOfHoles()!=this.getNumberOfHoles())
				return false;
			else {
				for (int i = 0; i < this.getNumberOfHoles(); i++)
					if (!this.getHole(i).equals(that.getHole(i)))
						return false;
			}
			return true;
		}else
			return false;
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
		public AbstractNote transpose(int demiTon) {
			AbstractNote n = getNote().transpose(demiTon);
			return optimiser.getPrefered(getAllNotes().get(n));
		}


	}


	protected class Hole {

		//
		// Fields
		//

		public final int blowRelative; //the number of demiton from the first hole blow to this hole blow
		public final int drawRelative; //the number of demiton from the first hole blow to this hole draw

		public Boolean blowValve; 
		public Boolean drawValve;


		//
		// Constructor
		//

		public Hole(Note blow, Note draw) {
			this.blowRelative = firstHoleBlow.getEcartToReach(blow);
			this.drawRelative = firstHoleBlow.getEcartToReach(draw);
		}


		//
		// Accessors
		//

		public Note getLower(Note tonalite){
			if (blow.compareTo(draw)<0){
				return getBlow(tonalite);
			} else {
				return getDraw(tonalite);
			}
		}

		public Note getUpper(Note tonalite){
			if (blow.compareTo(draw)>0){
				return getBlow(tonalite);
			} else {
				return getDraw(tonalite);
			}
		}

		public Note getBlow(Note tonalite) {
			Note blowNote =  firstHoleBlow.transpose(blowRelative);//the note in firstHoleBlowTonalite
			return blowNote.transpose(firstHoleBlowTonalite.getEcartToReach(tonalite));
		}

		public Note getDraw(Note tonalite) {
			Note drawNote =  firstHoleBlow.transpose(drawRelative);//the note in firstHoleBlowTonalite
			return drawNote.transpose(firstHoleBlowTonalite.getEcartToReach(tonalite));
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

		public List<Note> getNotesBetween(Note tonalite) {
			return Note.getNotesBetween(getBlow(tonalite), getDraw(tonalite));
		}
		//
		// Methods
		//



		public boolean noteIsInInterval(Note tonalite, Note n){
			return n.compareTo(getUpper(tonalite))<=0 && n.compareTo(getLower(tonalite))>=0;
		}

		public boolean equals(Object o){
			if (o instanceof Hole){
				Hole that = (Hole) o;
				return this.blowRelative==that.blowRelative && this.blowValve==that.blowValve 
						&& this.drawRelative==that.drawRelative && this.drawValve==that.drawValve;
			} else
				return false;
		}
		
		public int hashCode(){
			return (blowValve?1:0)+   (drawValve?1:0) * 2 +blowRelative * 10 + drawRelative * 100; 
		}
		
		public String toString(){
			return "|"+blow+"+ , "+draw+"- |" ;
		}
	}
}

















//	public boolean equals(Object o){
//		if (o instanceof Harmonica){
//			Harmonica that = (Harmonica) o;
//
//			if (that.getNumberOfHoles()!=this.getNumberOfHoles())
//				return false;
//			else {
//				System.out.println(this.tuningName+" 1 "+this.tonalite+" "+this.getNaturalNote(0, blow)+" "+Note.do3.getEcartToReach(this.getNaturalNote(0, blow)));
//				that.transpose(Note.do3.getEcartToReach(that.getNaturalNote(0, blow)));
//				assert(that.getNaturalNote(0, blow).equals(Note.do3));
//				this.transpose(Note.do3.getEcartToReach(this.getNaturalNote(0, blow)));
//				System.out.println(this.tuningName+" 2 "+this.tonalite+" "+this.getNaturalNote(0, blow)+" "+Note.do3.getEcartToReach(this.getNaturalNote(0, blow)));
//				assert(this.getNaturalNote(0, blow).equals(Note.do3));
//				for (int i = 0; i < this.getNumberOfHoles(); i++)
//					if (!this.getNaturalNote(i, blow).equals(that.getNaturalNote(i, blow))
//							|| !this.getNaturalNote(i, blow).equals(that.getNaturalNote(i, draw)))
//						return false;				
//			}
//			return true;
//		}else
//			return false;
//	}


/*
 * 
 */
//
//public void setTonalite(Note tonalite) {
//	this.tonalite = tonalite;
//}
//
//public void transpose(int demitons){
//	tonalite.transpose(demitons);
//}	
//
//public Note getTonalite() {
//	return tonalite;
//}











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

