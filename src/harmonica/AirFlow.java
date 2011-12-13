package harmonica;

public enum AirFlow{
	blow("+"), 
	draw("-");

	String notation;
	private AirFlow(String notation) {	
		this.notation = notation;
	}		
}