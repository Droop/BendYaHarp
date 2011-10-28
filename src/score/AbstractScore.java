package src.score;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import src.PlayerPreferences;
import src.harmonica.AbstractNote;
import src.harmonica.Harmonica;
import src.harmonica.HarmonicaDataBase;
import src.harmonica.Note;
import src.harmonica.Harmonica.HarmonicaNote;

public abstract class AbstractScore {

	//
	// Fields
	//

	protected String score;

	//
	// Constructor
	//

	public AbstractScore(String score){
		this.score = score;
	}

	//
	// Methods
	//

	public abstract List<List<AbstractNote>> getNotesFrom(String score);

	public abstract void grave();

	public abstract  String getUnexistentNoteValue() ;

	//
	// Methods
	//

	public List<HarmonicaLines> generateHarmonicaLines(Harmonica h, PlayerPreferences p){
		List<HarmonicaLines> result = new ArrayList<AbstractScore.HarmonicaLines>();

		for (List<AbstractNote> notes : getNotesFrom(score)){
			if (p.iPreferReadOnThreeLines())
				result.add(graveSurTroisLigne(h, notes, p));
			else 
				result.add(graveSurUneLigne(h, notes, p));
		}
		return result;
	}

	public Harmonica findBestHarp(HarmonicaDataBase hdb, PlayerPreferences p){

	}

	public static Harmonica findBestHarp(Collection<AbstractScore> scores, HarmonicaDataBase hdb, PlayerPreferences p){

	}
	
	//
	// Primitives
	//

	private HarmonicaLines graveSurUneLigne(Harmonica h, List<AbstractNote> notes, PlayerPreferences p){
		Map<AbstractNote,HarmonicaNote<Note>> matching = h.match(notes, p);

		String result ="";
		for (AbstractNote n : notes){
			result+=matching.get(n)+" ";
		}

		Map<String, String>	linesForHarp = new HashMap<String, String>();
		linesForHarp.put("main",result);
		return new HarmonicaLines(linesForHarp, Harmonica.getScoreTransposition(matching), p.iPreferTransposingScore());
	}

	private HarmonicaLines graveSurTroisLigne(Harmonica h, List<AbstractNote> notes, PlayerPreferences p){
		Map<AbstractNote,HarmonicaNote<Note>> matching = h.match(notes, p);

		String draw ="";
		String empty ="";
		String blow ="";
		for (AbstractNote n : notes){
			switch (matching.get(n).getAirDirection()) {
			case blow:
				blow+=matching.get(n)+" ";
				empty +="_";
				draw+=getUnexistentNoteValue();
				break;

			case draw:
				blow+=getUnexistentNoteValue();
				empty +="_";
				draw+=matching.get(n)+" ";
				break;
			}
		}

		Map<String, String>	linesForHarp = new HashMap<String, String>();
		linesForHarp.put("blow",blow);
		linesForHarp.put("draw",draw);
		linesForHarp.put("empty",empty);
		return new HarmonicaLines(linesForHarp, Harmonica.getScoreTransposition(matching), p.iPreferTransposingScore());

	}

	//
	//
	//

	protected class HarmonicaLines{
		Map<String, String> linesForHarp;
		int scoreTransposition;
		int transposeScore;

		private HarmonicaLines(Map<String, String> linesForHarp,
				int scoreTransposition, int transposeScore) {
			super();
			this.linesForHarp = linesForHarp;
			this.scoreTransposition = scoreTransposition;
			this.transposeScore = transposeScore;
		}

		public Map<String, String> getLinesForHarp() {
			return linesForHarp;
		}
		public int getScoreTransposition() {
			return scoreTransposition;
		}
		public boolean isTransposeScore() {
			return transposeScore;
		}		
	}

}
