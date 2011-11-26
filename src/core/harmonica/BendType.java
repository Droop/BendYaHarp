package core.harmonica;


public enum BendType{		
	natural(""),
	bluesbend("^"), 
	overbend("°"), 
	valvedbend("v"),;

	String notation;
	private BendType(String notation) {	
		this.notation = notation;
	}	
}