package core.harmonica;

import core.harmonica.Note.NoteName;

public interface AbstractNote {
	
	public abstract Note getNote();

	public abstract NoteName getNoteName();

	public abstract Integer getHauteur();

	public AbstractNote transpose(int demiTon);
}