package CardDB;

/**
 * Created by Maxence on 14/07/2015.
 */
public class Card {

    private int id;
    private String name;
    private int cost;
    private int attack;
    private int health;
    private int nbSeen;
    private int cardClass;

    public int getId() {
        return id;
    }

    public void setId(long id) {
        this.id = (int) id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = (int) cost;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(long attack) {
        this.attack = (int) attack;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(long health) {
        this.health = (int) health;
    }

    public int getNbSeen() {
        return nbSeen;
    }

    public void setNbSeen(long nbSeen) {
        this.nbSeen = (int) nbSeen;
    }

    public int getCardClass() {
        return cardClass;
    }

    public void setClass(long cardClass) {
        this.cardClass = (int) cardClass;
    }
}
