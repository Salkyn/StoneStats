package CardDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Maxence on 14/07/2015.
 */
public class CardDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_CARDS = "cards";
    public static final String COLUMN_C_ID = "_id";
    public static final String COLUMN_C_NAME = "name";
    public static final String COLUMN_C_COST = "cost";
    public static final String COLUMN_C_HEALTH = "health";
    public static final String COLUMN_C_ATTACK = "attack";
    public static final String COLUMN_C_NBSEEN = "nbSeen";
    public static final String COLUMN_C_CLASS = "cardClass";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_CARDS + "(" + COLUMN_C_ID
            + " integer primary key autoincrement, " + COLUMN_C_NAME
            + " text not null, "+ COLUMN_C_COST
            + " integer, " + COLUMN_C_HEALTH
            + " integer, " + COLUMN_C_ATTACK
            + " integer, " + COLUMN_C_NBSEEN
            + " integer default 0, " + COLUMN_C_CLASS
            + " integer)";

    private static final String DATABASE_C_NAME = "cards.db";
    private static final int DATABASE_C_VERSION = 1;

    public CardDBHelper(Context context) {
        super(context, DATABASE_C_NAME, null, DATABASE_C_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(CardDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDS);
        onCreate(db);
    }

}
