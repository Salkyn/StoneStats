package CardDeckDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CardDeckDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_CARDDECKS = "carddecks";
    public static final String COLUMN_CD_ID = "_id";
    public static final String COLUMN_CD_IDCARD = "idCard";
    public static final String COLUMN_CD_IDDECK = "idDeck";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_CARDDECKS + "(" + COLUMN_CD_ID
            + " integer primary key autoincrement, " + COLUMN_CD_IDCARD
            + " integer, " + COLUMN_CD_IDDECK
            + " integer)";

    private static final String DATABASE_CD_NAME = "cardgames.db";
    private static final int DATABASE_CD_VERSION = 5;

    public CardDeckDBHelper(Context context) {
        super(context, DATABASE_CD_NAME, null, DATABASE_CD_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(CardDeckDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDDECKS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(CardDeckDBHelper.class.getName(),
                "Downgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDDECKS);
        onCreate(db);
    }

}
