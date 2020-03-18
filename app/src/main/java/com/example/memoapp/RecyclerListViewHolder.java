package com.example.memoapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolderクラス
 * RecyclerView.ViewHolderクラスを継承する
 * 各アイテムの画面部品を保持する役割
 */
public class RecyclerListViewHolder extends RecyclerView.ViewHolder {

    // memo内容と時間、日付けを表示するためのTextView
    public TextView _tvMemo;
    public TextView _creationDate;

    // コンストラクタ
    // 渡ってくるのは一行毎のレイアウト
    // lv一項目分のレイアウトなのだが、新規で作成しなければならない 黄色い教科書P381,382　
    public RecyclerListViewHolder(@NonNull View itemView) {
        super(itemView);
        _tvMemo = itemView.findViewById(R.id.tvMemo);
        _creationDate = itemView.findViewById(R.id.creationDate);
    }
}
