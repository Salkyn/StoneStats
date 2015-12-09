package CardGameDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Maxence on 24/07/2015.
 */
public class CardGameDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_CARDGAMES = "cardgames";
    public static final String COLUMN_CG_ID = "_id";
    public static final String COLUMN_CG_IDCARD = "idCard";
    public static final String COLUMN_CG_IDGAME = "idGame";
    public static final String COLUMN_CG_IDDECK = "idDeck";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_CARDGAMES + "(" + COLUMN_CG_ID
            + " integer primary key autoincrement, " + COLUMN_CG_IDCARD
            + " integer, " + COLUMN_CG_IDGAME
            + " integer, " + COLUMN_CG_IDDECK
            + " integer)";

    private static final String DATABASE_CG_NAME = "cardgames.db";
    private static final int DATABASE_CG_VERSION = 5;

    public CardGameDBHelper(Context context) {
        super(context, DATABASE_CG_NAME, null, DATABASE_CG_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(CardGameDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDGAMES);
        onCreate(db);
    }

}
