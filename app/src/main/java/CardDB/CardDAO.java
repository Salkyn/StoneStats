package CardDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import CardDB.CardDBHelper;

/**
 * Created by Maxence on 14/07/2015.
 */
public class CardDAO {

    private CardDBHelper cardDbHelper;
    private SQLiteDatabase cardDatabase;
    private String[] allColumns = { CardDBHelper.COLUMN_C_ID, CardDBHelper.COLUMN_C_NAME, CardDBHelper.COLUMN_C_COST,
            CardDBHelper.COLUMN_C_HEALTH, CardDBHelper.COLUMN_C_ATTACK, CardDBHelper.COLUMN_C_NBSEEN, CardDBHelper.COLUMN_C_CLASS };


    public CardDAO(Context context) { cardDbHelper = new CardDBHelper(context); }

    public void open() throws SQLException {
        cardDatabase = cardDbHelper.getWritableDatabase();
    }

    public void close() {
        cardDbHelper.close();
    }

    public Card addCard(String name, int cost, int att, int health, int cardClass) {
        Card newCard = null;

        ContentValues values = new ContentValues();

        values.put(cardDbHelper.COLUMN_C_NAME, name);
        values.put(cardDbHelper.COLUMN_C_COST, cost);
        values.put(cardDbHelper.COLUMN_C_HEALTH, health);
        values.put(cardDbHelper.COLUMN_C_ATTACK, att);
        values.put(cardDbHelper.COLUMN_C_CLASS, cardClass);


        long insertId = cardDatabase.insert(CardDBHelper.TABLE_CARDS, null, values);
        Cursor cursor = cardDatabase.query(CardDBHelper.TABLE_CARDS,
                allColumns, CardDBHelper.COLUMN_C_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();

        newCard = cursorToCard(cursor);
        cursor.close();

        return newCard;
    }

    public Card cursorToCard(Cursor cursor) {
        Card card = new Card();
        card.setId(cursor.getLong(0));
        card.setName(cursor.getString(1));
        card.setCost(cursor.getLong(2));
        card.setHealth(cursor.getLong(3));
        card.setAttack(cursor.getLong(4));
        card.setNbSeen(cursor.getLong(5));
        card.setClass(cursor.getLong(6));

        return card;
    }

    public void deleteTout() {
        cardDatabase.delete("cards", null, null);
        Log.i("DBDecks", "Everything in CardDB is deleted ! Good job !");
    }

    public List<Card> getAllCardNames(int cost, int oppClassId) {

        List<Card> cards = new ArrayList<Card>();
        String textCard = null;

        Cursor cursor = null;

        if(cost != 8) {
            cursor = cardDatabase.query(CardDBHelper.TABLE_CARDS,
                    allColumns, cardDbHelper.COLUMN_C_COST + " = " + cost + " AND " + cardDbHelper.COLUMN_C_CLASS
                            + " IN (0, " + oppClassId + ")", null, null, null, cardDbHelper.COLUMN_C_NBSEEN + " DESC, "
                            + cardDbHelper.COLUMN_C_NAME);
        }
        else {
            cursor = cardDatabase.query(CardDBHelper.TABLE_CARDS,
                    allColumns, cardDbHelper.COLUMN_C_COST + " >= " + cost + " AND " + cardDbHelper.COLUMN_C_CLASS
                            + " IN (0, " + oppClassId + ")", null, null, null, cardDbHelper.COLUMN_C_NBSEEN + " DESC, "
                            + cardDbHelper.COLUMN_C_NAME);
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Card card = cursorToCard(cursor);
           /* textCard = ") ";
            if(card.getAttack() == 0 && card.getHealth() == 0) {
                textCard =  card.getName() + " (Spell" + textCard;
            }
            else {
                textCard = card.getName() + " (" + card.getAttack() + "/" + card.getHealth() + textCard;
            }*/
            cards.add(card);
            cursor.moveToNext();
        }
        cursor.close();
        return cards;
    }

    public void addView(String name) {
        String query = "select " + CardDBHelper.COLUMN_C_NBSEEN + " from " + CardDBHelper.TABLE_CARDS + " where " +
                CardDBHelper.COLUMN_C_NAME + " = ?";
        Cursor cursorDeck = cardDatabase.rawQuery(query, new String[] { name });

        cursorDeck.moveToFirst();
        long nbView = cursorDeck.getLong(0);

        ContentValues values = new ContentValues();
        values.put(CardDBHelper.COLUMN_C_NBSEEN, nbView+1);

        cardDatabase.update(CardDBHelper.TABLE_CARDS, values, CardDBHelper.COLUMN_C_NAME + " = ?", new String[]{name});

        Log.w("CardDAO-addView", "add view to card" + name);
    }

    public Card getCardFromId(int id) {

        Card card;

        Cursor cursor = cardDatabase.query(CardDBHelper.TABLE_CARDS,
                allColumns, CardDBHelper.COLUMN_C_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();

        card = cursorToCard(cursor);

        return card;
    }

    public Card getCardFromName(String name) {

        Card card;

        Cursor cursor = cardDatabase.query(CardDBHelper.TABLE_CARDS,
                allColumns, CardDBHelper.COLUMN_C_NAME + " = \"" + name + "\"", null, null, null, null);
        cursor.moveToFirst();

        card = cursorToCard(cursor);

        return card;
    }

    public void resetAllViews() {

        ContentValues resetViewValues = new ContentValues();
        resetViewValues.put(CardDBHelper.COLUMN_C_NBSEEN, 0);

        cardDatabase.update(CardDBHelper.TABLE_CARDS, resetViewValues, null, null);
    }

}
