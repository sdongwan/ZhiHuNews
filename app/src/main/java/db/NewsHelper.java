package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UnknownFormatConversionException;

import bean.StoriesBean;
import bean.TopStoriesBean;

/**
 * Created by Administrator on 2016/11/29.
 */

public class NewsHelper {

    private static NewsHelper mNewsHelper;
    private static NewsOpenHelper mNewsOpenHelper;
    private static SQLiteDatabase mSqLiteDatabase;


    private static final int TYPE_STORIES = 0;
    private static final int TYPE_TOP_STORIES = 1;


    private NewsHelper() {

    }

    public static NewsHelper getInstance(Context context) {
        if (mNewsHelper == null) {
            mNewsHelper = new NewsHelper();
            mNewsOpenHelper = new NewsOpenHelper(context);
            mSqLiteDatabase = mNewsOpenHelper.getWritableDatabase();
            return mNewsHelper;
        }
        return mNewsHelper;
    }

    public void insertTopStories(List<TopStoriesBean> topStoriesBean) {
        int result = mSqLiteDatabase.delete(NewsOpenHelper.TABLE_TOP, null, null);
        if (result < 0) {

            throw new UnknownFormatConversionException("插入出错");
        }

        for (int i = 0; i < topStoriesBean.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("title", topStoriesBean.get(i).getTitle());
            mSqLiteDatabase.insert(NewsOpenHelper.TABLE_TOP, null, contentValues);
        }
    }


    public List<TopStoriesBean> getTopStories() {
        List<TopStoriesBean> lists = new ArrayList<>();
        Cursor cursor = mSqLiteDatabase.rawQuery("select * from " + NewsOpenHelper.TABLE_TOP + " ", null);
        while (cursor.moveToNext()) {
            TopStoriesBean topStoriesBean = new TopStoriesBean();
            topStoriesBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            topStoriesBean.setImage("");
            lists.add(topStoriesBean);

        }
        cursor.close();
        return lists;
    }


    /*
      public void updateTopStoriesBean(List<TopStoriesBean> topStoriesBean) {
            for (int i = 0; i < topStoriesBean.size(); i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.putString("title", topStoriesBean.getString(i).getTitle());
                int result = mSqLiteDatabase.update(NewsOpenHelper.TABLE_TOP, contentValues
                        , "select * from " + NewsOpenHelper.TABLE_TOP + " where position= ? ", new String[]{i + ""});
                if (result > 0) {
                    // TODO: 2016/12/3
                    throw new UnsupportedOperationException("更新数据出错");
                }
            }
        }
     */

    public void insertStoriesBeanByIDd(List<StoriesBean> storiesBean, int themeId, int refreshcount) {
        List<StoriesBean> list = getStoriesById(themeId, refreshcount);
        if (list != null) {
            mSqLiteDatabase.delete(NewsOpenHelper.TABLE_STORIES,
                    " themeId = ? and refreshcount = ? ",
                    new String[]{themeId + "", refreshcount + ""});

        }


        for (int i = 0; i < storiesBean.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("title", storiesBean.get(i).getTitle());
            contentValues.put("themeId", themeId);
            contentValues.put("refreshcount", refreshcount);
            mSqLiteDatabase.insert(NewsOpenHelper.TABLE_STORIES, null, contentValues);
        }
    }


    public void insertStoriesBeanByCount(List<StoriesBean> storiesBean, int refreshCount, String date) {
        List<StoriesBean> list = getStoriesByCount(refreshCount);
        if (list != null) {
            mSqLiteDatabase.delete(NewsOpenHelper.TABLE_STORIES,
                    " refreshcount = ?",
                    new String[]{refreshCount + ""});
        }

        for (int i = 0; i < storiesBean.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("title", storiesBean.get(i).getTitle());
            contentValues.put("refreshcount", refreshCount);
            contentValues.put("date", date);
            mSqLiteDatabase.insert(NewsOpenHelper.TABLE_STORIES, null, contentValues);
        }


    }


    public List<StoriesBean> getStoriesByCount(int refreshCount) {
        List<StoriesBean> lists = new ArrayList<>();
        Cursor cursor = mSqLiteDatabase.rawQuery("select * from " + NewsOpenHelper.TABLE_STORIES + " where refreshcount = ?  ", new String[]{refreshCount + ""});
        while (cursor.moveToNext()) {
            StoriesBean storiesBean = new StoriesBean();
            storiesBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            storiesBean.setMyDate(cursor.getString(cursor.getColumnIndex("date")));
            storiesBean.setImages(null);// TODO: 2016/12/4
            lists.add(storiesBean);
        }
        cursor.close();
        return lists;

    }

    public List<StoriesBean> getStoriesById(int themeId, int refreshCount) {
        List<StoriesBean> lists = new ArrayList<>();
        Cursor cursor = mSqLiteDatabase.rawQuery("select * from " + NewsOpenHelper.TABLE_STORIES + "  where themeId = ? and refreshcount = ?   ", new String[]{themeId + "", refreshCount + ""});
        while (cursor.moveToNext()) {
            StoriesBean storiesBean = new StoriesBean();
            storiesBean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            storiesBean.setImages(null);// TODO: 2016/12/4
            lists.add(storiesBean);

        }
        cursor.close();
        return lists;

    }

    public String getThemeTitleById(int themeId) {
        Cursor cursor = mSqLiteDatabase.rawQuery("select * from " + NewsOpenHelper.TABLE_THEME + "  where themeId = ?   ", new String[]{themeId + ""});
        String title = "";
        while (cursor.moveToNext()) {
            title = cursor.getString(cursor.getColumnIndex("title"));
        }

        return title;

    }

    public void insertThemeTileById(String title, int themeId) {
        int result = mSqLiteDatabase.delete(NewsOpenHelper.TABLE_THEME,
                " themeId = ? ",
                new String[]{themeId + ""});
        if (result < 0) {
            throw new UnsupportedOperationException("删除数据出错");
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("themeId", themeId);
        contentValues.put("title", title);
        mSqLiteDatabase.insert(NewsOpenHelper.TABLE_THEME, null, contentValues);

    }


    /*

     public void updateStoriesBean(List<StoriesBean> storiesBean, int refreshCount, String date) {
        for (int i = 0; i < storiesBean.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.putString("title", storiesBean.getString(i).getTitle());
            contentValues.putString("refreshcount", refreshCount);
            contentValues.putString("date", date);
            int result = mSqLiteDatabase.update(NewsOpenHelper.TABLE_STORIES, contentValues
                    , "select * from " + NewsOpenHelper.TABLE_STORIES + " where refreshCount =? ", new String[]{refreshCount + ""});
            if (result > 0) {
                // TODO: 2016/12/3

            }
        }
    }


     */


}
