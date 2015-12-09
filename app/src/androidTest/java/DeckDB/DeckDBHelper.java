package DeckDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DeckDBHelper extends SQLiteOpenHelper {

	public static final String TABLE_DECKS = "decks";
	public static final String COLUMN_D_ID = "_id";
	public static final String COLUMN_D_NAME = "name";
	public static final String COLUMN_D_CLASS = "class";
	public static final String COLUMN_D_NBGAMES = "nb_games";
	public static final String COLUMN_D_ARCHIVED = "archived";

	private static final String DATABASE_D_NAME = "decks.db";
	private static final int DATABASE_D_VERSION = 5;

	private static final String DATABASE_CREATE = "create table "
			+ TABLE_DECKS + "(" + COLUMN_D_ID
			+ " integer primary key autoincrement, " + COLUMN_D_NAME
			+ " text not null, "+ COLUMN_D_CLASS
			+ " integer, " + COLUMN_D_NBGAMES
			+ " integer, " + COLUMN_D_ARCHIVED
			+ " boolean NOT NULL default 0)";

	public DeckDBHelper(Context context) {
		super(context, DATABASE_D_NAME, null, DATABASE_D_VERSION);
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
		    db.execSQL("DROP TABLE IF EXISTS " + TABLE_DECKS);
		    onCreate(db);
	}

}
