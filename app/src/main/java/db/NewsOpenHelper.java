package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/11/29.
 */

public class NewsOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "news.db";
    public static final String TABLE_TOP = "topstories";
    public static final String TABLE_STORIES = "stories";
    public static final String TABLE_THEME = "themetitle";

    private static final int DB_VERSION = 1;
    private Context mContext;

    /*
    "create table contacts( _id integer primary key autoincrement, " +
            "name text not null, email text not null);";
     */
    private static final String CREAT_TAB_STOP = "create table " + TABLE_TOP + "(" +
            "_id integer primary key autoincrement," +
            "title text," +
            "position integer" +
            " );";

    private static final String CREAT_TAB_STORIES = "create table " + TABLE_STORIES + "(" +
            "_id integer primary key autoincrement," +
            "title text," +
            "refreshcount integer," +
            "date text," +
            "themeId integer " +
            " );";
    private static final String CREAT_TAB_THTOP = "create table " + TABLE_THEME + "(" +
            "_id integer primary key autoincrement," +
            "title text," +
            "themeId integer" +
            " );";


    public NewsOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAT_TAB_STOP);
        db.execSQL(CREAT_TAB_STORIES);
        db.execSQL(CREAT_TAB_THTOP);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Toast.makeText(mContext, "数据库更新啦", Toast.LENGTH_SHORT).show();
    }
}
