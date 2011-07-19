package com.scarviz.selector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 入力画面クラス
 * 
 * @author scarviz
 *
 */
public class InputInfo extends Activity {
	// 各コントロール
	EditText mTxtName;	// 名前
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.inputinfo);
        
        // 各コントロールを取得
        mTxtName = (EditText)findViewById(R.id.txtName);	// 名前
	}

    /**
     * ボタン押下時イベント。
     *
     */
    public void onClickButton(View view){
    	switch(view.getId()){
	    	case R.id.btnDecision:
	    		// 名前を取得
	    		String name = mTxtName.getText().toString();
	    		// 名前が未入力の場合
	    		if(name.length() == 0)
	    		{
	    			Toast.makeText(this, R.string.err_mes_01, Toast.LENGTH_SHORT).show();
		    		break;
	    		}
	    		
	    		// 名前を持ってシナリオリストクラスに遷移する
				Intent intent = new Intent(this, ScenarioList.class);
				intent.putExtra("NAME", name);
				startActivity(intent);
				finish();
	    		break;
	    	default:
	    		break;
	    }
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// GCに優先的に解放させるため
		mTxtName = null;
	}

}
