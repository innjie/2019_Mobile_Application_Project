package ddwu.mobile.final_project.ma02_20170966;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DiaryDBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "diary_db";
    public final static String TABLE_NAME = "contact_table";
    public final static String COL_ID = "_id";
    public final static String COL_MONTH = "month";
    public final static String COL_DAY = "day";
    public final static String COL_TITLE = "title";
    public final static String COL_COMMENT = "comment";
    public final static String COL_FEEL = "feel";
    public final static String COL_LOCATION = "location";
    public final static String COL_WEATHER = "weather";
    public final static String COL_MAP = "map";

    public DiaryDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement,"
                + COL_MONTH + " TEXT, " + COL_DAY + " TEXT, " + COL_TITLE + " TEXT, " + COL_COMMENT + " , "
                + COL_FEEL + " TEXT, " + COL_LOCATION + " TEXT, " + COL_WEATHER + "  TEXT ," + COL_MAP + " TEXT);");


//		샘플 데이터
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '9', '11',  'aaa', 'aaa', 'good', 'Seoul', 'null', 'null');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '10', '24', 'bbb', 'bbb', 'good', 'Seoul', 'null', 'null');");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '10', '19', 'ccc', 'ccc', 'bad', 'Seoul', 'null', 'null');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }
}
