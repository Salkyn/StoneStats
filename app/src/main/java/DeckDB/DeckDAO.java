package DeckDB;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DeckDAO {

	private DeckDBHelper deckDbHelper;
	private SQLiteDatabase deckDatabase;
	private String[] allColumns = { DeckDBHelper.COLUMN_D_ID, DeckDBHelper.COLUMN_D_NAME, 
			DeckDBHelper.COLUMN_D_CLASS, DeckDBHelper.COLUMN_D_NBGAMES, DeckDBHelper.COLUMN_D_ARCHIVED };

	public DeckDAO(Context context) {
		deckDbHelper = new DeckDBHelper(context);
	}

	public void open() throws SQLException {
		deckDatabase = deckDbHelper.getWritableDatabase();
	}

	public void close() {
		deckDbHelper.close();
	}

	public Deck createDeck(int deckClass, String name) {
		Log.i("DeckDAO", "Creating deck : " + name + " " + deckClass);
		Deck newDeck = null;
		ContentValues values = new ContentValues();

		values.put(DeckDBHelper.COLUMN_D_NAME, name);
		values.put(DeckDBHelper.COLUMN_D_CLASS, deckClass);
		values.put(DeckDBHelper.COLUMN_D_NBGAMES, 0);
		values.put(DeckDBHelper.COLUMN_D_ARCHIVED, 0);

		long insertId = deckDatabase.insert(DeckDBHelper.TABLE_DECKS, null, values);
		Cursor cursor = deckDatabase.query(DeckDBHelper.TABLE_DECKS,
				allColumns, DeckDBHelper.COLUMN_D_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		newDeck = cursorToDeck(cursor);
		cursor.close(); 
		return newDeck;
	}


	public List<Deck> getAllDecks() {

		List<Deck> decks = new ArrayList<Deck>();

		Cursor cursor = deckDatabase.query(DeckDBHelper.TABLE_DECKS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			decks.add(cursorToDeck(cursor));
			cursor.moveToNext();
		}
		cursor.close();


		return decks;
	}

	public void archiveAll() {
		ContentValues values = new ContentValues();
		values.put(DeckDBHelper.COLUMN_D_ARCHIVED, 1);
		deckDatabase.update(DeckDBHelper.TABLE_DECKS, values, null, null);
	}

	public int getIdFromName(String name) {
		int id = 0;
		
		String query = "select * from " + DeckDBHelper.TABLE_DECKS + " where name = ?";
		Cursor cursor = deckDatabase.rawQuery(query, new String[] { name });

		if(cursor.getCount() != 0) {
			cursor.moveToFirst();
			id = cursorToDeck(cursor).getId();
		} else {
			Log.w("getIdFromName", "Deck non trouve, cursor vide (param : " + name + ")");
		}
		return id;
	}

	public static Deck cursorToDeck(Cursor cursor) {
		Deck deck = new Deck();
		deck.setId(cursor.getLong(0));
		deck.setName(cursor.getString(1));
		deck.setDeckClass(cursor.getLong(2));
		deck.setNbGames(cursor.getLong(3));
		deck.setArchived(cursor.getLong(4));
		return deck;
	}

	public String classToString(int idClass) {
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

	public void deleteTout() {
		deckDatabase.delete(deckDbHelper.TABLE_DECKS, null, null);
		Log.i("DBDecks", "Everything in DeckDB is deleted ! Good job !");
	}

	public int countGames() {

		int nbGames = 0;

		String query = "select * from " + DeckDBHelper.TABLE_DECKS;
		Cursor cursor = deckDatabase.rawQuery(query, null);
		nbGames = cursor.getCount();

		return nbGames;
	}

	public void deleteDeck(int id) {
        deckDatabase.delete(DeckDBHelper.TABLE_DECKS, DeckDBHelper.COLUMN_D_ID + " = " + id, null);
	}
}
