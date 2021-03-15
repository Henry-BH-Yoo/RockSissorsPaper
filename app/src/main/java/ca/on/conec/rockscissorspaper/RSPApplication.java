package ca.on.conec.rockscissorspaper;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class RSPApplication extends Application {

    private static final String DB_NAME = "RSP.db";
    private static final int DB_VERSION = 1;

    private SQLiteOpenHelper helper;


    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS tbl_stats(id INTEGER PRIMARY KEY AUTOINCREMENT , game_result_value INTEGER , game_result_time TEXT)";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS tbl_stats";

    @Override
    public void onCreate() {

        /**
         *  id  int
         *  gameResultValue (Win: 1 / Tie : 2 / Loss : 3)   int
         *  gameResultTime ( epoch time)    text
         *
         *  id  resultValue resultTime
         *  1   2           2021-03-13 16:22:00.000
         *  2   2           2021-03-13 16:22:10.000
         *  3   3           2021-03-13 16:22:20.000
         *
         *  SELECT COUNT(a
         *
         */

        helper = new SQLiteOpenHelper(this, DB_NAME , null , DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(CREATE_TABLE);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL(DROP_TABLE);
                onCreate(db);

            }
        };

        super.onCreate();
    }

    public boolean addResult(int gameResultValue) {
        SQLiteDatabase db = helper.getWritableDatabase();
        boolean returnValue = false;

        try {
            ContentValues contentValues = new ContentValues();

            contentValues.put("game_result_value" , gameResultValue);
            contentValues.put("game_result_time" , new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            long result = db.insert("tbl_stats" , null , contentValues);

            if( result == -1 ) {
                returnValue = false;
            } else {
                returnValue = true;
            }

        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if( db != null) {
                db.close();
            }
        }

        return returnValue;
    }


    public String getStatisticsPastMinute() {
        String returnText = "";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {

            String query = "SELECT TYPE.game_result_value , CASE WHEN count IS NULL THEN 0 ELSE count END AS count FROM ( \n" +
                    "SELECT 1 AS game_result_value \n" +
                    "UNION ALL \n" +
                    "SELECT 2 AS game_result_value \n" +
                    "UNION ALL \n" +
                    "SELECT 3 AS game_result_value \n" +
                    ") TYPE LEFT OUTER JOIN ( \n" +
                    "SELECT game_result_value , COUNT(game_result_value) as count FROM tbl_stats \n" +
                    "WHERE strftime('%s','now' , 'localtime') - strftime('%s',game_result_time) <= 60 \n" +
                    "GROUP BY game_result_value \n" +
                    ") RESULT \n" +
                    "ON TYPE.game_result_value = RESULT.game_result_value \n" +
                    "ORDER BY TYPE.game_result_value ASC";


            cursor = db.rawQuery(query , null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do {
                        returnText += cursor.getInt(cursor.getColumnIndex("count")) + "-";
                    } while(cursor.moveToNext());
                }
            }
            returnText = returnText.substring(0 , returnText.length() - 1);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(cursor != null) {
                cursor.close();
            }

            if(db != null) {
                db.close();
            }
        }

        return returnText;
    }


    public String getStatisticsAllTime() {
        String returnText = "";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        try {

            String query = "SELECT TYPE.game_result_value , CASE WHEN count IS NULL THEN 0 ELSE count END AS count FROM ( \n" +
                    "SELECT 1 AS game_result_value \n" +
                    "UNION ALL \n" +
                    "SELECT 2 AS game_result_value \n" +
                    "UNION ALL \n" +
                    "SELECT 3 AS game_result_value \n" +
                    ") TYPE LEFT OUTER JOIN ( \n" +
                    "SELECT game_result_value , COUNT(game_result_value) as count FROM tbl_stats \n" +
                    "GROUP BY game_result_value \n" +
                    ") RESULT \n" +
                    "ON TYPE.game_result_value = RESULT.game_result_value \n" +
                    "ORDER BY TYPE.game_result_value ASC";


            cursor = db.rawQuery(query , null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do {
                        returnText += cursor.getInt(cursor.getColumnIndex("count")) + "-";
                    } while(cursor.moveToNext());
                }
            }

            returnText = returnText.substring(0 , returnText.length() - 1);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(cursor != null) {
                cursor.close();
            }

            if(db != null) {
                db.close();
            }
        }

        return returnText;
    }

    public boolean resetTable() {
        SQLiteDatabase db = helper.getWritableDatabase();
        boolean returnValue = false;

        try {

            long result = db.delete("tbl_stats", null , null);

            if( result == -1 ) {
                returnValue = false;
            } else {
                returnValue = true;
            }

        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if( db != null) {
                db.close();
            }
        }

        return returnValue;
    }


}
