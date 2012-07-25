package com.scarviz.recordersample;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaRecorder;

public class MainActivity extends Activity {
    MediaRecorder recorder;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	/**
     * ボタン押下時イベント。
     *
     */
    public void onClickButton(View view){
    	switch(view.getId()){
	    	case R.id.btnRec:
	            // 録音開始
	    		StartRec();
	            break;
	    	case R.id.btnStop:
	    		// 録音停止
	    		StopRec();
	            break;
	        default:
	        	break;
    	}
    }
    
    @Override
	protected void onDestroy() {
		// TODO 自動生成されたメソッド・スタブ
		super.onDestroy();
		
		if(recorder != null){
			recorder.stop();
	        recorder.reset();
	        recorder.release();
		}
	}

    String FilePath;
	private void StartRec(){
    	recorder = new MediaRecorder();
    	recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    	recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    	recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
    	FilePath = Environment.getExternalStorageDirectory() + "/audio.3gp";
        recorder.setOutputFile(FilePath);
        
        try {
            recorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        recorder.start();
    	Toast.makeText(this, "録音を開始しました。", Toast.LENGTH_LONG).show();
    	TextView txt = (TextView)findViewById(R.id.txtStatus);
    	txt.setText("録音中\n<ファイル保存場所："+FilePath+">");
    }
    
    private void StopRec(){
    	if(recorder == null) return;
    	recorder.stop();
        recorder.reset();
        recorder.release();
        recorder = null;

    	Toast.makeText(this, "録音を停止しました。", Toast.LENGTH_LONG).show();
    	TextView txt = (TextView)findViewById(R.id.txtStatus);
    	txt.setText("停止中\n<ファイル保存場所："+FilePath+">");
    }
    
}
