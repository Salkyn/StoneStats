package CardDeckDB;

public class CardDeck {

    private int id;
    private int idCard;
    private int idDeck;

    public int getId() {
        return id;
    }

    public void setId(long id) {
        this.id = (int) id;
    }

    public int getIdCard() {
        return idCard;
    }

    public void setIdCard(long idCard) {
        this.idCard = (int) idCard;
    }

    public int getIdDeck() {
        return idDeck;
    }

    public void setIdDeck(long idDeck) {
        this.idDeck = (int) idDeck;
    }

}
