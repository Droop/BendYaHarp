package score;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import music.AbstractNote;
import music.Note;
import music.TonalityHandler;


public class RelativeScore {

	private List<Integer> score;
	private TonalityHandler tonality;
	
	/*
	 * 
	 */
	
	public RelativeScore(List<AbstractNote> score, Note tonality){
		this.tonality = new TonalityHandler(score.get(0).getNote(), tonality);
		this.score = new ArrayList<Integer>();
		for (AbstractNote n : score){
			this.score.add(this.tonality.getRelative(n.getNote()));
		}
	}

	/*
	 * 
	 */
		
	public List<AbstractNote> getRelativeScore(Note tonality){ 
		List<AbstractNote> result = new ArrayList<AbstractNote>();
		for (Integer r : score)
			result.add(this.tonality.getNote(r,tonality));
		return result;
	}
	
	public Set<AbstractNote> getNotesSet(Note tonality){
		return new HashSet<AbstractNote>(getRelativeScore(tonality));
	}
}
