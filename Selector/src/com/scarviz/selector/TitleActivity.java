package com.scarviz.selector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TitleActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title);
    }

    public void onClickContinueButton(View v) {
    	Intent intent = new Intent(this, ScenarioList.class);
    	startActivity(intent);
    }

    public void onClickStartButton(View v) {
    	Intent intent = new Intent(this, InputInfo.class);
    	startActivity(intent);
    }

    public void onClickOptionButton(View v) {
    	Intent intent = new Intent(this, Option.class);
    	startActivity(intent);
    }

}