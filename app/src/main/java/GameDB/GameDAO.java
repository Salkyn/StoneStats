package GameDB;

import java.util.ArrayList;
import java.util.List;

import DeckDB.Deck;
import DeckDB.DeckDAO;
import DeckDB.DeckDBHelper;
import GameDB.GameDBHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GameDAO {

	private GameDBHelper gameDbHelper;
	private DeckDBHelper deckDbHelper;
	private static SQLiteDatabase gameDatabase;
	private static SQLiteDatabase deckDatabase;
	private static String[] allColumns = { GameDBHelper.COLUMN_G_ID, GameDBHelper.COLUMN_G_COIN, GameDBHelper.COLUMN_G_VICTORY, 
		GameDBHelper.COLUMN_G_DECKCHOSEN, GameDBHelper.COLUMN_G_DATETIME, GameDBHelper.COLUMN_G_OPPCLASS };

	public GameDAO(Context context) {
		gameDbHelper = new GameDBHelper(context);
		deckDbHelper = new DeckDBHelper(context);
	}

	public void open() throws SQLException {
		gameDatabase = gameDbHelper.getWritableDatabase();
		deckDatabase = deckDbHelper.getWritableDatabase();
	}

	public void close() {
		gameDbHelper.close();
		deckDbHelper.close();
	}

	public static Game createGame(boolean coin, boolean victory, int deckChosen, int oppClass) {

		Game newGame = null;
		ContentValues values = new ContentValues();

		values.put(GameDBHelper.COLUMN_G_COIN, boolToInt(coin));
		values.put(GameDBHelper.COLUMN_G_VICTORY, boolToInt(victory));
		values.put(GameDBHelper.COLUMN_G_DECKCHOSEN, deckChosen);
		values.put(GameDBHelper.COLUMN_G_OPPCLASS, oppClass);

		long insertId = gameDatabase.insert(GameDBHelper.TABLE_GAMES, null, values);

		addGameToDeck(deckChosen);
		//A l'ajout de la game, il faut ajuster le nombre de games jouees par deckChosen dans DBDeck !!

		Cursor cursor = gameDatabase.query(GameDBHelper.TABLE_GAMES,
				allColumns, DeckDBHelper.COLUMN_D_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		newGame = cursorToGame(cursor);
		cursor.close();

		Cursor cursorCount = gameDatabase.rawQuery("SELECT COUNT (*) FROM " + GameDBHelper.TABLE_GAMES, null);
		cursorCount.moveToFirst();
		Log.w("GameDAO-addGameToDeck", "Nb of rows in DBGames : " + cursorCount.getLong(0));

		return newGame;
	}

	public static Game cursorToGame(Cursor cur) {
		Game g = new Game();
		g.setId(cur.getLong(0));
		g.setCoin(cur.getLong(1));
		g.setVictory(cur.getLong(2));
		g.setDeckChosen(cur.getLong(3));
		g.setDateTime(cur.getString(4));
		g.setOppClass(cur.getLong(5));
		return g;
	}

	private static void addGameToDeck(int id) {

		String query = "select " + DeckDBHelper.COLUMN_D_NBGAMES + " from " + DeckDBHelper.TABLE_DECKS + " where _id = ?";
		Cursor cursorDeck = deckDatabase.rawQuery(query, new String[] { id + "" });
		Log.w("GameDAO-addGameToDeck", "cursorDeck.getCount : " + cursorDeck.getCount());
		cursorDeck.moveToFirst();
		long nbGames = cursorDeck.getLong(0);

		ContentValues values = new ContentValues();
		values.put(DeckDBHelper.COLUMN_D_NBGAMES, nbGames + 1);

		deckDatabase.update(DeckDBHelper.TABLE_DECKS, values, "_id = ?", new String[]{id + ""});
	}

	public static int boolToInt(boolean b) {
		if(b) { return 1; }
		else {return 0; }
	}

	public List<Game> getGamesWithDeck(int deckId, int classId) {		
		Log.w("getGamesWithDeck", "launch with deckid="+deckId+", classid="+classId);
		List<Game> games = new ArrayList<Game>();
Cursor cursor = null;
		
		if(classId == 0) {
		cursor = gameDatabase.query(GameDBHelper.TABLE_GAMES,
				allColumns, GameDBHelper.COLUMN_G_DECKCHOSEN + " = " + deckId, null, null, null, null);
		}
		else {
			cursor = gameDatabase.query(GameDBHelper.TABLE_GAMES,
					allColumns, GameDBHelper.COLUMN_G_DECKCHOSEN + " = " + deckId + " AND " + GameDBHelper.COLUMN_G_OPPCLASS
					+ " = " + classId, null, null, null, null);
		}
		Log.w("getGamesWithDeck", "cursor.getCount "+cursor.getCount());
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Game game = GameDAO.cursorToGame(cursor);

			games.add(game);
			cursor.moveToNext();
		}
		cursor.close();
		return games;
	}

	public int getNbGames(List<Game> games) {
		return games.size();
	}

	public int[] getWinrates(List<Game> games) {
		Log.w("StatsReader/getWinrates", "launch with games length"+games.size());
		int nbVic = 0, nbVicCoin = 0, nbVicNoCoin = 0;
		int nbGamesCoin = 0, nbGamesNoCoin = 0;
		int winrate = 0, winrateC = 0, winrateNC = 0;
		int nbGames = games.size();

		for(int i = 0 ; i < nbGames ; i++) {
			Log.w("StatsReader/getWinrates", "Game nb "+i+games.get(i).getCoin()+" "+games.get(i).getVictory());

			if(games.get(i).getVictory()) {
				nbVic++;
				if(games.get(i).getCoin()) {
					nbVicCoin++;
				}
				else {
					nbVicNoCoin++;
				}
			}

			if(games.get(i).getCoin())
			{
				nbGamesCoin++;
			}
		}

		nbGamesNoCoin = nbGames - nbGamesCoin;
		Log.w("StatsReader/getWinrates", "Victories: "+nbVic+" "+nbVicCoin+" "+nbVicNoCoin);
		Log.w("StatsReader/getWinrates", "Games: "+nbGames+" "+nbGamesCoin+" "+nbGamesNoCoin);

		if(nbGames==0) {
			winrate = -1;
		} else {
			winrate = 100*nbVic/nbGames;
		}

		if(nbGamesCoin==0) {
			winrateC = -1;
		} else {
			winrateC = 100*nbVicCoin/nbGamesCoin;
		}

		if(nbGamesNoCoin==0) {
			winrateNC = -1;
		} else {
			winrateNC = 100*nbVicNoCoin/nbGamesNoCoin;
		}
		int[] result = {winrate, winrateC, winrateNC};
		
		return result;
	}

	public void deleteTout() {
		gameDatabase.delete(gameDbHelper.TABLE_GAMES, null, null);
		Log.i("DBGames", "Everything in GameDB is deleted ! Good job !");
	}
}
