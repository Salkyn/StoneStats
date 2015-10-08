package GameDB;

import DeckDB.DeckDBHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GameDBHelper extends SQLiteOpenHelper {

	public static final String TABLE_GAMES = "games";
	public static final String COLUMN_G_ID = "_id";
	public static final String COLUMN_G_COIN = "coin";
	public static final String COLUMN_G_VICTORY = "victory";
	public static final String COLUMN_G_DECKCHOSEN = "deckChosen";
	public static final String COLUMN_G_DATETIME = "dateTime";
	public static final String COLUMN_G_OPPCLASS = "oppClass";

	private static final String DATABASE_G_NAME = "games.db";
	private static final int DATABASE_G_VERSION = 1;

	private static final String DATABASE_CREATE = "create table "
			+ TABLE_GAMES + "(" + COLUMN_G_ID
			+ " integer primary key autoincrement, " + COLUMN_G_COIN
			+ " status boolean NOT NULL default 0, " + COLUMN_G_VICTORY
			+ " status boolean NOT NULL default 0, " + COLUMN_G_DECKCHOSEN
			+ " integer, " + COLUMN_G_DATETIME
			+ " datetime default CURRENT_TIMESTAMP, " + COLUMN_G_OPPCLASS
			+ " integer);";

	public GameDBHelper(Context context) {
		super(context, DATABASE_G_NAME, null, DATABASE_G_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DeckDBHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
		onCreate(db);
	}
}
