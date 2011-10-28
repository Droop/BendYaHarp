package src.harmonica;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import src.harmonica.Note.NoteName;

public class Chord extends LinkedList<Note> implements AbstractNote {

	private Chord(Collection<? extends Note> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	@Override
	public NoteName getNoteName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getHauteur() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AbstractNote transpose(int demiTon) throws UnexistantNoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
