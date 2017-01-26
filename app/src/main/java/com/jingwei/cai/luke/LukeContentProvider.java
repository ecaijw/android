package com.jingwei.cai.luke;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.util.HashMap;

import static com.jingwei.cai.luke.LukeContentProvider.MyContentProviderMetaData.UserTableMetaData.CONTENT_URI;
import static com.jingwei.cai.luke.LukeContentProvider.MyContentProviderMetaData.UserTableMetaData.TABLE_NAME;

public class LukeContentProvider extends ContentProvider {
    // CONTENT_URI 的字符串必须是唯一
//    public static final Uri CONTENT_URI = Uri.parse("content://com.WangWeiDa.MyContentprovider");
    // 如果有子表，URI为：
    public static final Uri CONTENT_URI = Uri.parse("content://com.luke.provider/user");

    public static class MyContentProviderMetaData {
        //URI的指定，此处的字符串必须和声明的authorities一致
        public static final String AUTHORITIES = "com.luke.provider";
        //数据库名称
        public static final String DATABASE_NAME = "luke_provider.db";
        //数据库的版本
        public static final int DATABASE_VERSION = 1;
        //表名
        public static final String USERS_TABLE_NAME = "user";

        public static final class UserTableMetaData implements BaseColumns {
            //表名
            public static final String TABLE_NAME = "user";
            //访问该ContentProvider的URI
            public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITIES + "/user");
            //该ContentProvider所返回的数据类型的定义
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.myprovider.user";
            public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.myprovider.user";
            //列名
            public static final String USER_NAME = "name";
            //默认的排序方法
            public static final String DEFAULT_SORT_ORDER = "_id desc";

            public static final String[] PROJECTION_ALL = new String[]{USER_NAME};
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, MyContentProviderMetaData.DATABASE_NAME, null, MyContentProviderMetaData.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //创建用于存储数据的表
            db.execSQL("Create table " + TABLE_NAME + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db); // 调用onCreate()再进行创建
        }
    }

    public static final int INCOMING_USER_COLLECTION = 1;
    public static final int INCOMING_USER_SINGLE = 2;
    public static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(MyContentProviderMetaData.AUTHORITIES, "/user", INCOMING_USER_COLLECTION);
        sUriMatcher.addURI(MyContentProviderMetaData.AUTHORITIES, "/user/#", INCOMING_USER_SINGLE);
    }
    private DatabaseHelper mDbHelper;
    public static final HashMap mUserProjection;
    static {
        mUserProjection = new HashMap();
        mUserProjection.put(MyContentProviderMetaData.UserTableMetaData._ID, MyContentProviderMetaData.UserTableMetaData._ID);
        mUserProjection.put(MyContentProviderMetaData.UserTableMetaData.USER_NAME, MyContentProviderMetaData.UserTableMetaData.USER_NAME);
    }

    public LukeContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        Helper.log("delete: " + selection);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = db.delete(TABLE_NAME, selection, selectionArgs);
        Helper.log("delete: " + count + " items");
        return count;
    }

    @Override
    public String getType(Uri uri) {
        Helper.log("getType: " + uri.toString());
        switch (sUriMatcher.match(uri)) {
            case INCOMING_USER_COLLECTION:
                return MyContentProviderMetaData.UserTableMetaData.CONTENT_TYPE;
            case INCOMING_USER_SINGLE:
                return MyContentProviderMetaData.UserTableMetaData.CONTENT_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);

        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Helper.log("insert: " + uri.toString());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(MyContentProviderMetaData.UserTableMetaData.TABLE_NAME, null, values);
        if (rowId > 0) {
            Helper.log("insert: " + uri.toString());
            Uri insertedUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(insertedUri, null);
            return insertedUri;
        }
        return uri;
    }

    @Override
    public boolean onCreate() {
        Helper.log("onCreate(): ");
        mDbHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Helper.log("query: " + uri.toString());
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);
        qb.setProjectionMap(mUserProjection);
        switch (sUriMatcher.match(uri)) {
            case INCOMING_USER_COLLECTION:
                break;
            case INCOMING_USER_SINGLE:
                qb.appendWhere(MyContentProviderMetaData.UserTableMetaData._ID + "=" + uri.getPathSegments().get(1));
                break;
        }
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = MyContentProviderMetaData.UserTableMetaData.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Helper.log("update: " + uri.toString());

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = db.update(MyContentProviderMetaData.UserTableMetaData.TABLE_NAME, values, selection, selectionArgs);
        Helper.log("update: " + count + " items");
        return count;
    }
}
