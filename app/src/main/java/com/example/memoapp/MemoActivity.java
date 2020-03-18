package com.example.memoapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class MemoActivity extends AppCompatActivity {
    //Logcat
    private static final String TAG = "MemoActivity";

    // DatabaseHelperクラスを定義
    DatabaseHelper helper = null;
    // 新規フラグ
    boolean Flag = false;
    // _idとuuidとmemoの値取得用変数
    String id = "";
    String memo = "";
    String indexID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

         // アクションバーの生成
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        String[] x = {"a","i","i"};
        for (String ary:x){
            System.out.println(ary);
        }

        // データベースから値を取得する
        if (helper == null) {
            helper = new DatabaseHelper(this);
        }

        // ListActivityからインテントを取得
        Intent intent = this.getIntent();

        // intentから値を取得
        indexID = intent.getStringExtra("_id");
        id = intent.getStringExtra("uuid");
        memo = intent.getStringExtra("memo");

        // 画面に表示する処理
        if(id.equals("")){
            // 新規作成の場合Flagをtrueに
            Flag = true;
        }else{
            // 編集の場合 データベースから値を取得して表示
            SQLiteDatabase db = helper.getWritableDatabase();

            try {
                EditText edText = findViewById(R.id.edText);
                edText.setText(memo);

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                db.close();
            }
        }
    }

    /**
     * アクションバーの[←][保存]を押した時の処理
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            //[保存]を押した時
            case R.id.save:
                // データベースオブジェクトの取得
                DatabaseHelper helper = new DatabaseHelper(MemoActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();

                // DBへ保存
                try {
                    if (Flag){
                        // 新規作成の場合

                        // 入力内容の取得
                        EditText edText = findViewById(R.id.edText);
                        String txtStr = edText.getText().toString();

                        // 新しくuuidを発行する
                        id = UUID.randomUUID().toString();

                        // INSERT文で値をDBに保存
                        String sql = "INSERT INTO memo_DB (uuid, memo,date) VALUES (?,?,?)";
                        SQLiteStatement stmt = db.compileStatement(sql);
                        stmt.bindString(1, id);
                        stmt.bindString(2,txtStr);

                        String day = getNowDate();
                        stmt.bindString(3, day);

                        stmt.executeInsert();
                        Flag = false;

                    }else {
                        // 更新の場合

                        // 入力内容の取得
                        EditText edText = findViewById(R.id.edText);
                        String txtStr = edText.getText().toString();

                        // ContentValuesで値を設定
                        ContentValues value = new ContentValues();
                        value.put("memo", txtStr);

                        // UPDATE文で更新
                        db.update("memo_DB", value,"_id = ? " ,new String[] { indexID } );

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }finally {
                    // トースト
                    Toast.makeText(this, "保存しました", Toast.LENGTH_SHORT).show();
                    db.close();
                    return true;
                }

            // [←]戻るを押したとき
            case android.R.id.home:
                int id = item.getItemId();
                if (id == android.R.id.home) {

                    finish();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * アクションバーに保存用のボタンを実装
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memo_actionbar, menu);
        return true;
    }


    /**
     * 日付け取得用
     * @return
     */
    public static String getNowDate(){
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

}
