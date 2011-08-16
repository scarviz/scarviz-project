package com.scarviz.voicejournal;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * ジャーナル編集用クラス
 * 
 * @author scarviz
 *
 */
public class EditJournalActivity extends Activity {
    // エラーコード
    private static final int ERR_CD_POSITION = -1;
    // ジャーナル用EditText
    EditText mtxtJournal;
    // リストの選択番号
    private int mPosition;
	
    // 設定値領域用
    private SharedPreferences mPrefs;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.editjournal);

        // 設定領域から前回設定した背景URIを取得する
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bgUriTxt = mPrefs.getString("URI", null);
        
        // 前回設定した背景URIが存在する場合
        if(bgUriTxt != null){
        	Uri uri = Uri.parse(bgUriTxt);
			// 背景画像を設定する
			SetBackGroundImage(uri);
        }
        
        // ジャーナル用EditText
        mtxtJournal = (EditText)findViewById(R.id.txtJournal);

        // 退避情報が存在する場合
        if(savedInstanceState != null){
	        // 退避情報を再格納する
	        GetInstanceState(savedInstanceState);
        }
        else{
            Intent intent = getIntent();
            mPosition = intent.getIntExtra("TITLE_NO",ERR_CD_POSITION);
            String item = intent.getStringExtra("ITEM");
    		// EditTextに表示する
    		mtxtJournal.setText(item);
        }
	}

    /**
     * キーダウンイベント。
     * 
     */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//バックキーボタン押下時
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// インテントのインスタンス生成
			Intent intent = new Intent();
			// 編集内容を取得
			String txtJournal = mtxtJournal.getText().toString();
			
			// 呼び出し元に返す
			intent.putExtra("TITLE_NO", mPosition);
			intent.putExtra("ITEM", txtJournal);
			setResult(RESULT_OK, intent);
			finish();
			return false;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 背景画像を設定する。
	 * 
	 * @param uri
	 */
	private void SetBackGroundImage(Uri uri){
		Bitmap bmp = null;

		// 全体を囲っているLinearLayout
        LinearLayout editLayout = (LinearLayout)findViewById(R.id.edit);
        
        // 背景URIがNULLの場合
        if(uri == null){
        	// 背景をリセットする
        	editLayout.setBackgroundDrawable(null);
        	return;
        }
        
		try {
			// URIからBitmapを取得する
			bmp = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
        
        // 背景画像を取得
        Drawable backGroundImg = new BitmapDrawable(bmp);
        // 背景画像を設定する
        editLayout.setBackgroundDrawable(backGroundImg);
        
	}

    /**
     * 退避情報を再格納する。
     * 
     */
    private void GetInstanceState(Bundle inState){
    	// 退避情報を取得する
    	mPosition = inState.getInt("position");					// リストの選択番号
    	String txtJournal = inState.getString("txtJournal");	// ジャーナル内容 

    	// 取得した値を再格納する
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
		outState.putInt("position", mPosition);					// リストの選択番号
		outState.putString("txtJournal", txtJournal);			// ジャーナル内容
	}

    /**
     * アクティビティ終了時処理。
     * 
     */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// GCに優先的に開放させる
		mtxtJournal = null;
		mPrefs = null;
	}

}
