package core;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import core.harmonica.Harmonica;
import core.harmonica.Note;
import core.harmonica.Harmonica.HarmonicaNote;



public class Player implements Comparator<Collection<? extends HarmonicaNote<?>>>{

	//
	// Fields
	//


	public static final String key4notationType_french="french";
	public static final String key4notationType_international="international";
	public static String _notationType=key4notationType_french;
	public static Boolean _showBemol=true;
	
	
	int iPrefertransposingScore;
	//1 : true; -1 false; 0 don't transpose
	
	//capacity
	
	int iCanBend;
	int iCanOverBend;
	int iCanCoverSeveralHole;
	
	//preference

	int naturalPriority;
	int softBendPriority;
	int deepBendPriority;
	int valvedBendPriority;
	int overBendPriority;
	
	//
	
	boolean iPreferReadOnThreeLines;
	
	//
	// Accessors
	//
	
	public void setMyPlayingStyle(int naturalPriority, int softBendPriority, int deepBendPriority, int valvedBendPriority, int overBendPriority){
		assert(
				naturalPriority!=softBendPriority 
				&& naturalPriority!=softBendPriority 
				&& naturalPriority!=deepBendPriority 
				&& naturalPriority!=valvedBendPriority 
				&& naturalPriority!=overBendPriority &&
				softBendPriority!=deepBendPriority 
				&&softBendPriority!=valvedBendPriority
				&&softBendPriority!=overBendPriority &&
				deepBendPriority!=valvedBendPriority
				&& deepBendPriority!=overBendPriority &&
				valvedBendPriority!=overBendPriority);
		this.naturalPriority=naturalPriority;
		this.softBendPriority=softBendPriority;
		this.deepBendPriority=deepBendPriority;
		this.valvedBendPriority=valvedBendPriority;
		this.overBendPriority=overBendPriority;
	}

	public int iPreferTransposingScore() {
		return iPrefertransposingScore;
	}

	public boolean iPreferReadOnThreeLines() {
		return iPreferReadOnThreeLines;
	}

	public void setiPrefertransposingScore(int iPrefertransposingScore) {
		this.iPrefertransposingScore = iPrefertransposingScore;
	}

	public int getiCanBend() {
		return iCanBend;
	}

	public void setiCanBend(int iCanBend) {
		this.iCanBend = iCanBend;
	}

	public int getiCanOverBend() {
		return iCanOverBend;
	}

	public void setiCanOverBend(int iCanOverBend) {
		this.iCanOverBend = iCanOverBend;
	}

	public int getiCanCoverSeveralHole() {
		return iCanCoverSeveralHole;
	}

	public void setiCanCoverSeveralHole(int iCanCoverSeveralHole) {
		this.iCanCoverSeveralHole = iCanCoverSeveralHole;
	}

	public void setMyPlayingCapacities(int iCanBend, int iCanOverBend, int iCanCoverSeveralHole){
		this.iCanBend=iCanBend;
		this.iCanOverBend=iCanOverBend;
		this.iCanCoverSeveralHole=iCanCoverSeveralHole;
	}
	
	//
	// Methods
	//
	
	public boolean iCanPlay(HarmonicaNote<?> o1){
		
	}
	
	public int compareNote(HarmonicaNote<?> o1,HarmonicaNote<?> o2) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public HarmonicaNote<Note> getPrefered(List<? extends HarmonicaNote<?>> list) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int compare(
			Collection<? extends HarmonicaNote<?>> o1,
			Collection<? extends HarmonicaNote<?>> o2) {
		// TODO Auto-generated method stub
		return 0;
	}
}
