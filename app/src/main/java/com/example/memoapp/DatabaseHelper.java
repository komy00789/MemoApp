package com.example.memoapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    // データベース名
    private static final String DB_NAME = "memo_DB.sqlite";
    // バージョン情報
    private static final int DATABASE_VERSION = 1;

    // コンストラクタ
    public DatabaseHelper(Context context) {
        super(context, DB_NAME,null,DATABASE_VERSION);
    }

    /**
     * 初期状態に一度だけ呼び出されるメソッド。
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //テーブル作成用SQL文字列の作成。
        StringBuilder sb = new StringBuilder();

        // SQL文
        sb.append("CREATE TABLE memo_DB (");
        sb.append("_id INTEGER PRIMARY KEY,");
        sb.append("uuid TEXT,");
        sb.append("memo TEXT,");
        sb.append("date TEXT");
        sb.append(");");

        String sql = sb.toString();
        //SQLの実行。
        db.execSQL(sql);
    }

    /**
     * アップデートされた時に呼び出される処理
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // テーブルを削除する
        db.execSQL("DROP TABLE IF EXISTS memo_DB");
        // 新しくテーブルを作成する
        onCreate(db);
    }
}
