package DeckDB;

public class Deck {

	private int id;
	private int deckClass;
	private int nbGames;
	private String name;
	private boolean archived;

	public int getId() {
		return id;
	}

	public void setId(long id) {
		this.id = (int) id;
	}

	public int getDeckClass() {
		return deckClass;
	}

	public void setDeckClass(long deckClass) {
		this.deckClass = (int) deckClass;
	}

	public int getNbGames() {
		return nbGames;
	}

	public void setNbGames(long nbGames) {
		this.nbGames = (int) nbGames;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean getArchived() {
		return archived;
	}
	
	public void setArchived(long archived) {
		if(archived == 0) {
			this.archived = false;
		}
		else {
			this.archived = true;
		}
	}

	public static String classToString(int idClass) {
		String strClass = null;

		switch(idClass) {
			case 0: strClass = "All";
				break;
			case 1: strClass = "Paladin";
				break;
			case 2: strClass = "Warrior";
				break;
			case 3: strClass = "Hunter";
				break;
			case 4: strClass = "Shaman";
				break;
			case 5: strClass = "Druid";
				break;
			case 6: strClass = "Rogue";
				break;
			case 7: strClass = "Priest";
				break;
			case 8: strClass = "Warlock";
				break;
			case 9: strClass = "Mage";
				break;
			default: strClass = "Invalid class";
				break;
		}

		return strClass;
	}
}
