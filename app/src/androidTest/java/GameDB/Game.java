package GameDB;

public class Game {

	private int id;
	private boolean coin;
	private boolean victory;
	private int deckChosen;
	private String dateTime;
	private int oppClass;
	
	public int getId() {
		return id;
	}

	public void setId(long id) {
		this.id = (int) id;
	}
	
	public boolean getCoin() {
		return coin;
	}
	
	public void setCoin(long coin) {
		if(coin == 0) {
			this.coin = false;
		}
		else {
			this.coin = true;
		}
	}
	
	public boolean getVictory() {
		return victory;
	}
	
	public void setVictory(long victory) {
		if(victory == 0) {
			this.victory = false;
		}
		else {
			this.victory = true;
		}
	}
	
	public int getDeckChosen() {
		return deckChosen;
	}

	public void setDeckChosen(long deckChosen) {
		this.deckChosen = (int) deckChosen;
	}
	
	public String getDateTime() {
		return dateTime;
	}
	
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	
	public int getOppClass() {
		return oppClass;
	}

	public void setOppClass(long oppClass) {
		this.oppClass = (int) oppClass;
	}
}
