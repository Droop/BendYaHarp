package src.harmonica;

import src.harmonica.Note.NoteName;

public interface AbstractNote {

	public abstract NoteName getNoteName();

	public abstract Integer getHauteur();

	public AbstractNote transpose(int demiTon);
}