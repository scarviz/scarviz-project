package com.scarviz.selector;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * シナリオリストクラス
 * 
 * @author scarviz
 *
 */
public class ScenarioList extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.scenariolist);
        
        // ArrayAdapterを生成する
        ArrayAdapter<String> adapter 
        = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        // アイテムを追加
        adapter.add("タイトル１");
        adapter.add("タイトル２");
        adapter.add("タイトル３");
        adapter.add("タイトル４");
        adapter.add("タイトル５");
        // リストに設定する
        getListView().setAdapter(adapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		// TODO : 選択した行を呼び出し元に返して終了する
		Intent intent = new Intent();
		intent.putExtra("TITLE_NO", position);  
		setResult(RESULT_OK, intent);  
		finish();  
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
