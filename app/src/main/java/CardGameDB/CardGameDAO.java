package CardGameDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CardGameDAO {

    private CardGameDBHelper cardgameDbHelper;
    private static SQLiteDatabase cardgameDatabase;
    private static String[] allColumns = { CardGameDBHelper.COLUMN_CG_ID, CardGameDBHelper.COLUMN_CG_IDCARD,
            CardGameDBHelper.COLUMN_CG_IDGAME, CardGameDBHelper.COLUMN_CG_IDDECK};

    public CardGameDAO(Context context) {
        cardgameDbHelper = new CardGameDBHelper(context);
    }

    public void open() throws SQLException {
        cardgameDatabase = cardgameDbHelper.getWritableDatabase();
    }

    public void close() {
        cardgameDatabase.close();
    }

    public static CardGame createCG(int idCard, int idDeck, int idGame) {

        CardGame cg;
        ContentValues values = new ContentValues();

        values.put(CardGameDBHelper.COLUMN_CG_IDCARD, idCard);
        values.put(CardGameDBHelper.COLUMN_CG_IDGAME, idGame);
        values.put(CardGameDBHelper.COLUMN_CG_IDDECK, idDeck);

        long insertId = cardgameDatabase.insert(CardGameDBHelper.TABLE_CARDGAMES, null, values);

        Cursor cursor = cardgameDatabase.query(CardGameDBHelper.TABLE_CARDGAMES,
                allColumns, CardGameDBHelper.COLUMN_CG_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        cg = cursorToCG(cursor);
        cursor.close();

        return cg;
    }

    public static CardGame cursorToCG(Cursor cur) {
        CardGame cg = new CardGame();
        cg.setId(cur.getLong(0));
        cg.setIdCard(cur.getLong(1));
        cg.setIdGame(cur.getLong(2));
        cg.setIdDeck(cur.getLong(3));

        return cg;
    }

    public void deleteTout() {
        cardgameDatabase.delete(cardgameDbHelper.TABLE_CARDGAMES, null, null);
        Log.i("DBCardGames", "Everything in CardGameDB is deleted ! Good job !");
    }
}
