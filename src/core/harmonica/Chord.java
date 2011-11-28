package core.harmonica;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import core.harmonica.Note.NoteName;


public class Chord extends LinkedList<Note> implements AbstractNote {

	private Chord(Collection<? extends Note> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Note getNote() {
		// TODO Auto-generated method stub
		return null;
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
	public AbstractNote transpose(int demiTon) {
		// TODO Auto-generated method stub
		return null;
	}
}
