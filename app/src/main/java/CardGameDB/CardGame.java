package CardGameDB;

/**
 * Created by Maxence on 24/07/2015.
 */
public class CardGame {

    private int id;
    private int idCard;
    private int idGame;
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

    public int getIdGame() {
        return idGame;
    }

    public void setIdGame(long idGame) {
        this.idGame = (int) idGame;
    }

    public int getIdDeck() {
        return idDeck;
    }

    public void setIdDeck(long idDeck) {
        this.idDeck = (int) idDeck;
    }

}
