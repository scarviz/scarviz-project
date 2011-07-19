package com.scarviz.selector;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class InputInfo extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.inputinfo);
	}

	@Override
	protected void onDestroy() {
		// TODO 自動生成されたメソッド・スタブ
		super.onDestroy();
	}

}
