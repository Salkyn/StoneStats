package CardDeckDB;

/**
 * Created by Maxence on 27/07/2015.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CardDeckDAO {

    private CardDeckDBHelper carddeckDbHelper;
    private static SQLiteDatabase carddeckDatabase;
    private static String[] allColumns = { CardDeckDBHelper.COLUMN_CD_ID, CardDeckDBHelper.COLUMN_CD_IDCARD,
            CardDeckDBHelper.COLUMN_CD_IDDECK};

    public CardDeckDAO(Context context) {
        carddeckDbHelper = new CardDeckDBHelper(context);
    }

    public void open() throws SQLException {
        carddeckDatabase = carddeckDbHelper.getWritableDatabase();
    }

    public void close() {
        carddeckDatabase.close();
    }

    public static CardDeck createCD(int idCard, int idDeck) {

        CardDeck cd;
        ContentValues values = new ContentValues();

        values.put(CardDeckDBHelper.COLUMN_CD_IDCARD, idCard);
        values.put(CardDeckDBHelper.COLUMN_CD_IDDECK, idDeck);

        long insertId = carddeckDatabase.insert(CardDeckDBHelper.TABLE_CARDDECKS, null, values);

        Cursor cursor = carddeckDatabase.query(CardDeckDBHelper.TABLE_CARDDECKS,
                allColumns, CardDeckDBHelper.COLUMN_CD_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        cd = cursorToCD(cursor);
        cursor.close();

        return cd;
    }

    public static CardDeck cursorToCD(Cursor cur) {
        CardDeck cd = new CardDeck();
        cd.setId(cur.getLong(0));
        cd.setIdCard(cur.getLong(1));
        cd.setIdDeck(cur.getLong(2));

        return cd;
    }

    public void removeCD(int idCard, int idDeck) {

        Cursor cursorDel = carddeckDatabase.query(CardDeckDBHelper.TABLE_CARDDECKS,
                allColumns, carddeckDbHelper.COLUMN_CD_IDCARD + " = " + idCard +
                        " AND " + carddeckDbHelper.COLUMN_CD_IDDECK + " = " + idDeck , null, null, null, null);

        cursorDel.moveToFirst();
        long idCd = cursorToCD(cursorDel).getId();

        carddeckDatabase.delete(CardDeckDBHelper.TABLE_CARDDECKS, CardDeckDBHelper.COLUMN_CD_ID + " = " + idCd, null);

    }

    public List<CardDeck> getCDFromDeck(int idDeck) {

        List<CardDeck> cds = new ArrayList<CardDeck>();

        Cursor cursor = carddeckDatabase.query(CardDeckDBHelper.TABLE_CARDDECKS,
                allColumns, carddeckDbHelper.COLUMN_CD_IDDECK + " = " + idDeck , null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            CardDeck cd = cursorToCD(cursor);
            cds.add(cd);
            cursor.moveToNext();
        }
        cursor.close();

        return cds;
    }

    public void deleteTout() {
        carddeckDatabase.delete(carddeckDbHelper.TABLE_CARDDECKS, null, null);
        Log.i("DBCardDeck", "Everything in CardDeckDB is deleted ! Good job !");
    }
}
