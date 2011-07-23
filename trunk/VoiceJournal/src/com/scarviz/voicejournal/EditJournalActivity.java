package com.scarviz.voicejournal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * ジャーナル編集用クラス
 * 
 * @author scarviz
 *
 */
public class EditJournalActivity extends Activity {
    // ジャーナル用EditText
    EditText mtxtJournal;
    private int mPosition;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.editjournal);

        // ジャーナル用EditText
        mtxtJournal = (EditText)findViewById(R.id.txtJournal);

        // 退避情報が存在する場合
        if(savedInstanceState != null){
	        // 退避情報を再格納する
	        GetInstanceState(savedInstanceState);
        }
        else{
            Intent intent = getIntent();
            mPosition = intent.getIntExtra("TITLE_NO",0);
            String item = intent.getStringExtra("ITEM");
        
    		// EditTextに表示する
    		mtxtJournal.setText(item);
        }
	}
	
    /**
     * 退避情報を再格納する。
     * 
     */
    private void GetInstanceState(Bundle inState){
    	// 退避情報を取得する
    	int position = inState.getInt("position");	// リストの選択番号
    	String txtJournal = inState.getString("txtJournal");	// ジャーナル内容 
    	
    	// 取得した値を再格納する
    	mPosition = position;	// リストの選択番号
    	mtxtJournal.setText(txtJournal);	// ジャーナル内容 
    }
    
    /**
     * 保持している情報を退避させる。
     * 
     */
    @Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 退避する値の取得
    	String txtJournal = mtxtJournal.getText().toString();	// ジャーナル内容 
    	
    	// 情報を退避させる
		outState.putInt("position", mPosition);	// リストの選択番号
		outState.putString("txtJournal", txtJournal);	// ジャーナル内容
	}
    
    
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//バックキーボタン押下時
		if (keyCode == KeyEvent.KEYCODE_BACK) {
				// 呼び出し元に返す
				Intent intent = new Intent();
				intent.putExtra("TITLE_NO", mPosition);
				intent.putExtra("ITEM", mtxtJournal.getText().toString());
				setResult(RESULT_OK, intent);
				finish();
				return false;
			}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// GCに優先的に開放させる
		mtxtJournal = null;
	}

}
