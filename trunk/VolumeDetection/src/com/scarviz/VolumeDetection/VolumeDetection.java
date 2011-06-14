package com.scarviz.VolumeDetection;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 音量検出Activityクラス
 * 
 * @author scarviz
 *
 */
public class VolumeDetection extends Activity {
	// 音量検出Runnableクラス用
	private VolumeDetectionRunnable mVolDetectRun;
	// TextView用
	private TextView txtVol;
	// UIスレッド要求用ハンドラ
	private Handler mHandler = new Handler();
	
	// デシベル
	private static final String UNIT_DB = "db";
	// 改行コード
	private static final String NEWLINE = "\n";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // 画面ロックされないようにする
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        // TextViewの取得
        txtVol = (TextView)findViewById(R.id.txtVol);
    }

    /**
     * 入力待ち状態時処理。
     * 
     */
	@Override
	protected void onResume() {
		super.onResume();
		mVolDetectRun = new VolumeDetectionRunnable();
		// リスナー登録
		mVolDetectRun.setOnVolumeReachedListener(
				new VolumeDetectionRunnable.OnReachedVolumeListener() {
					// 音量検出時
					@Override
					public void OnReachedVolume(final short volume) {
						// 別スレッドからUIスレッドに要求する
						mHandler.post(new Runnable(){
							public void run(){
								// TODO : デシベルに変換する
								//float val 
								//= (float)(20.0 * Math.log10(volume / 32767.0));
								//// 画面に出力する。改行して追加していく
								//txtVol.append(val + UNIT_DB + NEWLINE);
								txtVol.append(String.valueOf(volume) + NEWLINE);
							}
						});
					}
				});
		// 別スレッド開始
		new Thread(mVolDetectRun).start();
		
	}
	
	/**
	 * 遷移時処理(Activityが裏に移動した時の処理)。
	 * 
	 */
	@Override
	protected void onPause() {
		super.onPause();
		// 音量検出停止
		mVolDetectRun.stop();
	}

	/**
	 * Activity終了時処理。
	 * 
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 音量検出停止
		mVolDetectRun.stop();
		// GCのメモリ解放の優先度を上げるため
		mVolDetectRun = null;
		txtVol = null;
		mHandler = null;
	}
    
}