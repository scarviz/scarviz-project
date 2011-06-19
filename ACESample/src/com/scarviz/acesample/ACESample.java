package com.scarviz.acesample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ACE勉強会用サンプルクラス
 * 
 * @author scarviz
 *
 */
public class ACESample extends Activity {
	Toast mToastShort;
	Toast mToastLong;
	TextView mTxtToast;

	private static final int REQUEST_CODE = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // ToastのLayoutの初期設定(2.4.4)
        InitToastLayout_244();
    }
    
    /**
     * ボタン押下時イベント
     * 
     * @param view
     */
    public void onClickButton(View view){
    	switch(view.getId()){
    		// Toastテスト(短時間表示)ボタン
	    	case R.id.btnToastShort:
	    		mTxtToast.setText(R.string.mes01);
	    		mToastShort.show();
	    		break;
	    	// Toastテスト(長時間表示)ボタン
	    	case R.id.btnToastLong:
	    		mTxtToast.setText(R.string.mes02);
	    		mToastLong.show();
	    		break;	    	
	    	// ListView表示ボタン
	    	case R.id.btnListView:
				Intent intent = new Intent(this, ListViewSample.class);
				startActivityForResult(intent,REQUEST_CODE);
	    		break;
	    	default:
	    		break;
    	}
    }
    
    /**
     * ToastのLayoutの初期設定を行う。
     * (2.4.4)
     * 
     */
    private void InitToastLayout_244(){
        // Toastのカスタムレイアウト
        LayoutInflater inflater = getLayoutInflater();  
        View toastView 
        	= inflater.inflate(R.layout.toast_custom_layout, null);  
        mTxtToast = (TextView)toastView.findViewById(R.id.txt_toast);
        
        // Toast(短時間表示用)
        mToastShort = new Toast(this);
        mToastShort.setDuration(Toast.LENGTH_SHORT);
        mToastShort.setView(toastView);
        // Toast(長時間表示用)
        mToastLong = new Toast(this);
        mToastLong.setDuration(Toast.LENGTH_LONG);
        mToastLong.setView(toastView);
    }
    
    /**
	 * 遷移先から戻ってきたときの処理を行う。
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// リストから選択したアイテム情報
		if(requestCode == REQUEST_CODE){
			if(resultCode == Activity.RESULT_OK){
		        // 遷移元からパラメータを取得
		        int itemNo = data.getIntExtra("ITME_NO", 0);
		        // 表示するメッセージ作成(itemNoは0スタートなので+1する)
		        String mes
		        	= "アイテム" + String.valueOf((itemNo + 1)) + "を選択しました。";
		        // Toast表示
		        Toast.makeText(this, mes, Toast.LENGTH_LONG).show();
			}
		}
	}
}