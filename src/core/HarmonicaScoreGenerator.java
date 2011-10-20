package src.core;

import java.util.Map;

import src.core.Harmonica.HarmonicaNote;

public class HarmonicaScoreGenerator {

	private final int transposition;
	private final boolean harmonicaISTransposed;
	private final Map<Note, HarmonicaNote> matching;
	
	public HarmonicaScoreGenerator(int transposition,
			boolean harmonicaISTransposed, Map<Note, HarmonicaNote> matching) {
		super();
		this.transposition = transposition;
		this.harmonicaISTransposed = harmonicaISTransposed;
		this.matching = matching;
	}

	public int getTransposition() {
		return transposition;
	}

	public boolean isHarmonicaISTransposed() {
		return harmonicaISTransposed;
	}

	public Map<Note, HarmonicaNote> getMatching() {
		return matching;
	}
}
