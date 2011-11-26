package core.harmonica;

import core.HarpException;
import core.harmonica.Note.NoteName;

public class UnexistantNoteException extends HarpException implements AbstractNote {

	public UnexistantNoteException(String s) {
		super("\'"+s+"\'");
	}

	public UnexistantNoteException() {
	}

	@Override
	public NoteName getNoteName() {
		return null;
	}

	@Override
	public Integer getHauteur() {
		return null;
	}

	@Override
	public AbstractNote transpose(int demiTon) {
		return null;
	}

}
