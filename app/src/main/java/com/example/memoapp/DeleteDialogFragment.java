package com.example.memoapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Map;

public class DeleteDialogFragment extends DialogFragment {
    //Logcat
    private static final String TAG = "DeleteDialogFragment";

    // DatabaseHelperクラス宣言
    DatabaseHelper helper = null;

    // SELECT文
    String sql = "SELECT * FROM memo_DB";

    // 文字列用変数の初期化
    private String call = "";

    // コンストラクタ用変数の宣言
    SimpleAdapter adapter;
    ArrayList<Map<String, String>> memoList;
    int position;
    Context context;

    /**
     * コンストラクタ
     * @param context MainActivity
     * @param adapter Mainで設定したadapter
     * @param memoList Mainで設定したListView
     * @param position 長押し選択した場所
     */
    public DeleteDialogFragment(Context context, SimpleAdapter adapter, ArrayList<Map<String,String>> memoList, int position){
        this.context = context;
        this.adapter = adapter;
        this.memoList = memoList;
        this.position = position;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // ダイアログフィルターを生成
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // DBから値を取得
        if (helper == null) {
            helper = new DatabaseHelper(context);
        }

        // ダイアログ用文字
        String[] choice = {"はい","いいえ"};

        // ダイアログのメッセージを設定
        builder.setMessage("選択した項目を削除しますか？");

        // PositiveButton(はい)を選択した場合
        builder.setPositiveButton(choice[0], new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                call = "削除しました";

                //削除の処理
                //データベースを取得する
                SQLiteDatabase db = helper.getWritableDatabase();

                try {
                    // rawQueryでデータを取得
                    Cursor c = db.rawQuery(sql, null);
                    c.moveToFirst();

                    // 探索用変数
                    int count = 0;
                    String getID ="";
                    // whileで該当項目の探索
                    while (position+1 != count){
                        // プライマリーキーの取得
                        getID = c.getString(c.getColumnIndex("_id"));
                        count++;
                        c.moveToNext();
                    }

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

                Toast.makeText(context, call, Toast.LENGTH_SHORT).show();
            }
        });

        // NegativeButton(いいえ)を選択した場合
        builder.setNegativeButton(choice[1], new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // キャンセル処理
                call = "キャンセルしました";

                Toast.makeText(context, call, Toast.LENGTH_SHORT).show();
            }
        });

        // ダイアログオブジェクトを生成しreturn
        AlertDialog dialog = builder.create();

        return dialog;
    }
}
