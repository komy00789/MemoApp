package com.example.memoapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //Logcat
    private static final String TAG = "MainActivity";

    // DatabaseHelperクラス宣言
    DatabaseHelper helper = null;
    // SimpleAdapter宣言
    SimpleAdapter adapter = null;
    // ArrayListの定義
    final ArrayList<Map<String, String>> memoList = new ArrayList<>();
    // 再描画用フラグ
    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_floating);

        // フローティングボタン
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new FloatingButton());

        // DBから値を取得
        if (helper == null) {
            helper = new DatabaseHelper(this);
        }

        // DB処理
        getDB();

        flag = false;

        // ListViewの取得
        ListView lv = findViewById(R.id.lv);
        // リスト項目をクリックした時の処理
        lv.setOnItemClickListener(new ListItemClickListener());
        // リスト項目を長押しクリックした時の処理
        lv.setOnItemLongClickListener(new ListItemLongClickListener());
    }

    /**
     * DB設定用メソッド
     */
    public void getDB (){
        //データベースを取得する
        SQLiteDatabase db = helper.getWritableDatabase();

        // DB処理
        try {
            // rawQueryでデータを取得
            Cursor c = db.rawQuery("SELECT * FROM memo_DB", null);
            // Cursorの先頭行があるかどうか確認、移動
            boolean next = c.moveToFirst();

            // 取得した全ての行を設定
            while (next) {
                // 取得したカラムの順番(0から始まる)と型を指定してデータを取得する
                String uuidNum = c.getString(1);
                String memoTxt = c.getString(2);

                if(uuidNum.length() >= 10){
                    // リストに表示するのは10文字まで
                    uuidNum = uuidNum.substring(0, 11) + "...";
                }

//                memoTxt = memoTxt.substring(0, 11) + "..."; なぜこれはだめなのか

                // 値を設定
                Map<String,String> data = new HashMap<>();
                data.put("uuid",uuidNum);
                data.put("memo",memoTxt);
                memoList.add(data);

                next = c.moveToNext();
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            db.close();
        }

        // SimpleAdapter生成
        String[] from = {"memo","uuid"};
        int[] to = {android.R.id.text1, android.R.id.text2};

        adapter = new SimpleAdapter(this, memoList, android.R.layout.simple_list_item_2, from,to);

        // ListView取得
        ListView lv = findViewById(R.id.lv);
        lv.setAdapter(adapter);

    }

    /**
     * MainActivityへ戻った際の処理
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Flagがtrueの際、処理が行われる
        if (flag) {
            // 画面情報の削除処理
            memoList.clear();
            // 再描画のため呼び出し
            getDB();
        }
        flag = true;
    }


    /**
     * floatingButton用リスナ、画面遷移
     */
    private class FloatingButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Intentの生成
            Intent intent = new Intent(MainActivity.this, MemoActivity.class);
            intent.putExtra("uuid", "");

            Log.i(TAG, "do");

            // 第２画面を起動
            startActivity(intent);

            // トースト
            Toast.makeText(MainActivity.this, "新規作成", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * リスト項目をクリックした時の処理
     */
    public class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //データベースを取得する
            SQLiteDatabase db = helper.getWritableDatabase();

            // SELECT文をrawQuery()に
            String sql = "SELECT *, rowid FROM memo_DB ORDER BY _id";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();

            // 値の取得用変数の初期化
            int count = 0;
            String getID = "";
            String uuID = "";
            String memoDb = "";

            // 値を取得
            while (id+1 != count){
                getID = c.getString(c.getColumnIndex("_id"));
                uuID = c.getString(c.getColumnIndex("uuid"));
                memoDb = c.getString(c.getColumnIndex("memo"));
                count++;
                c.moveToNext();
            }

            db.close();

            // インテント作成  第二引数にはパッケージ名からの指定で、遷移先クラスを指定
            Intent intent = new Intent(MainActivity.this, MemoActivity.class);
            intent.putExtra("_id", getID);
            intent.putExtra("uuid", uuID);
            intent.putExtra("memo", memoDb);

            // activityの起動
            startActivity(intent);
        }
    }


    /**
     * リスト項目を長押しクリックした時削除する処理
     */
    public class ListItemLongClickListener implements AdapterView.OnItemLongClickListener{
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            //データベースを取得する
            SQLiteDatabase db = helper.getWritableDatabase();

            try {
            // SELECT句をrawQueryに
             String sql = "SELECT * FROM memo_DB ORDER BY _id";
             Cursor c = db.rawQuery(sql, null);
             c.moveToFirst();

             // プライマリーキーの取得
             String getID = c.getString(c.getColumnIndex("_id"));
             // delete()を使い該当の値を削除
             db.delete("memo_DB", "_id = ?", new String[]{getID} );

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
                db.close();
            }

            // 長押しした項目を画面から削除
            memoList.remove(position);
            adapter.notifyDataSetChanged();

            // trueにすることで通常のクリックイベントを発生させない
            return true;
        }
    }
}

