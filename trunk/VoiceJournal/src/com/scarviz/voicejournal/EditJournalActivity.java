package com.scarviz.voicejournal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ジャーナル編集用クラス
 * 
 * @author scarviz
 *
 */
public class EditJournalActivity extends Activity {
    // エラーコード
    private static final int ERR_CD_POSITION = -1;
    // 縮小用
    private static final int FITSIZE = 128;
    
    // ジャーナル用EditText
    EditText mtxtJournal;
    // リストの選択番号
    private int mPosition;
    // 日付
    private String mCreate;
    private String mUpdate;
	
    // 設定値領域用
    private SharedPreferences mPrefs;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.editjournal);
        
        // 設定領域から前回設定した背景URIを取得する
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bgUriTxt = mPrefs.getString("URI", null);
        
        Uri uri = null;
        // 前回設定した背景URIが存在する場合
        if(bgUriTxt != null){
        	uri = Uri.parse(bgUriTxt);
        }
		// 背景画像を設定する
		try {
			SetBackGroundImage(uri, FITSIZE);
		} catch (IOException e) {
			e.printStackTrace();
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
    		
    		// 日付を表示
            mCreate = intent.getStringExtra("CREATE");
            mUpdate = intent.getStringExtra("UPDATE");
        }
        
        // カスタムタイトルバーの設定
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitle);
        // 日付表示
        TextView txtDate = (TextView)findViewById(R.id.txtDate);
        txtDate.setText("登録日：" + mCreate + "\n" + "更新日：" + mUpdate);
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
     * ボタン押下時イベント。
     *
     */
    public void onClickButton(View view){
    	switch(view.getId()){
	    	case R.id.btnBack:
				// 編集をキャンセルして戻る
		    	CancelEdit();
	            break;
	        default:
	        	break;
    	}
    }
	
	/**
	 * 背景画像を設定する。
	 * 
	 * @param uri
	 * @param fitsize
	 * @throws IOException 
	 */
	private void SetBackGroundImage(Uri uri, int fitsize) throws IOException{
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
			// 画像読み込み無しモード
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			
			InputStream is = this.getContentResolver().openInputStream(uri);
			// URIからBitmapを画像読み込み無しで取得する
			bmp = BitmapFactory.decodeStream(is, null, opts);
			is.close();
			is = null;
			
			// 画像サイズから縮小サイズを算出する
			int width_size = 1 + (opts.outWidth / fitsize);
			int height_size = 1 + (opts.outHeight / fitsize);
			
			is = this.getContentResolver().openInputStream(uri);
			// 縮小サイズのより大きい方を選択する
			opts.inSampleSize = Math.max(width_size, height_size);
			opts.inJustDecodeBounds = false;
			// URIからBitmapを取得する
			bmp = BitmapFactory.decodeStream(is, null, opts);
			
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
	 * 編集をキャンセルして戻る。
	 * 
	 */
	private void CancelEdit(){
		// 結果にキャンセルをかえす
		setResult(RESULT_CANCELED);
		finish();
	}

    /**
     * 退避情報を再格納する。
     * 
     */
    private void GetInstanceState(Bundle inState){
    	// 退避情報を取得する
    	mPosition = inState.getInt("position");					// リストの選択番号
    	String txtJournal = inState.getString("txtJournal");	// ジャーナル内容 
    	mCreate = inState.getString("create");					// 登録日 
    	mUpdate = inState.getString("update");					// 更新日 

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
		outState.putInt("position", mPosition);			// リストの選択番号
		outState.putString("txtJournal", txtJournal);	// ジャーナル内容
		outState.putString("create", mCreate);			// 登録日
		outState.putString("update", mUpdate);			// 更新日
		
	}
    
	/**
	 * オプションメニューの生成
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    // XMLで定義したmenuを指定する。
	    inflater.inflate(R.menu.editmenu, menu);
	    return true;
	}
	
	/**
	 * オプションメニューの選択
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    int itemId = item.getItemId();
	    switch (itemId) {
		    // 編集取消し
		    case R.id.menu_cancel:
				// 編集をキャンセルして戻る
		    	CancelEdit();	
		        break;
		    default:
		    	break;
	    }
	    return true;
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
