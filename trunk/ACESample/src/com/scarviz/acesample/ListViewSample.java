package com.scarviz.acesample;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * ListViewのサンプルクラス
 * 
 * @author scarviz
 *
 */
public class ListViewSample extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.itemlist);
		
        // ArrayAdapterを生成する
        ArrayAdapter<String> adapter 
        = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        // アイテムを追加
        adapter.add("アイテム1");
        adapter.add("アイテム2");
        adapter.add("アイテム3");
        // リストに設定する
        getListView().setAdapter(adapter);
	}

	/**
	 * リストのアイテム選択時イベント
	 * 
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		// 選択した行を呼び出し元に返して終了する
		Intent intent = new Intent();
		intent.putExtra("ITME_NO", position);  
		setResult(RESULT_OK, intent);  
		finish();  
	}

}
