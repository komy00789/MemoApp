package com.example.memoapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * nyanさんの。リサイクラービューについて
 * https://akira-watson.com/android/recyclerview.html
 * Qiita.同じくリサイクラービューについて
 * https://qiita.com/naoi/items/f8a19d6278147e98bbc2
 */

public class MainActivity extends AppCompatActivity {
    //Logcat
    private static final String TAG = "MainActivity";

    // DatabaseHelperクラス宣言
    static DatabaseHelper helper = null;
    // SimpleAdapter宣言
    static SimpleAdapter adapter = null;
    // ArrayListの定義
    static final ArrayList<Map<String, String>> memoList = new ArrayList<>();
    // SELECT文
    static String sql = "SELECT * FROM memo_DB";
    // classの宣言
    DeleteDialogFragment dialogFragment;

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
            Cursor c = db.rawQuery(sql, null);
            // Cursorの先頭行があるかどうか確認、移動
            boolean next = c.moveToFirst();

            // 取得した全ての行を設定
            while (next) {
                // 取得したカラムの順番(0から始まる)と型を指定してデータを取得する
//                String uuidNum = c.getString(1);
                String memoTxt = c.getString(2);
                String dayTxt = c.getString(3);

                // リストに表示するのは10文字まで
//                if(uuidNum.length() >= 10){
//                    uuidNum = uuidNum.substring(0, 11) + "...";
//                }
                if (memoTxt.length() > 10){
                    memoTxt = memoTxt.substring(0, 11) + "...";
                }

                // 値を設定
                Map<String,String> data = new HashMap<>();
//                data.put("uuid",uuidNum);
                data.put("date",dayTxt);
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
        String[] from = {"memo","date"};
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

            // rawQueryでデータを取得
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();

            // 値の取得用変数の初期化
            int count = 0;
            String getID = "";
            String uuID = "";
            String memoDb = "";
            String dayDb = "";

            // 値を取得
            while (id+1 != count){
                getID = c.getString(c.getColumnIndex("_id"));
                uuID = c.getString(c.getColumnIndex("uuid"));
                memoDb = c.getString(c.getColumnIndex("memo"));
                dayDb = c.getString(c.getColumnIndex("date"));
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
            // FragmentManagerの取得
            FragmentManager fragmentManager = getSupportFragmentManager();
            // FragmentClassの初期化
            dialogFragment = new DeleteDialogFragment(MainActivity.this, adapter,memoList,position);

            // ダイアログを表示
            dialogFragment.show(fragmentManager, "DeleteDialogFragment");

            // trueにすることで通常のクリックイベントを発生させない
            return true;
        }
    }
}

