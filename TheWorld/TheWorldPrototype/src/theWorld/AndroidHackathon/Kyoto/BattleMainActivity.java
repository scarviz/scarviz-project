package theWorld.AndroidHackathon.Kyoto;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * バトル画面クラス
 * 
 * @author scarviz
 *
 */
public class BattleMainActivity extends Activity {
	// 認証用コード
    private static final int REQUEST_CODE_VOICE_RECO = 123;
    // プロンプト表示用
    private static final String PROMPT_MES = "相手より多く打ち込め！！";

    // 音量検出クラス用
    private VolumeDetection mVolumeDetection;
    private boolean isVolume = false;
    
	// バトルクラス用
	Battle mBattle;
    
    // キャラクタ情報
    //private CharaInfo mMyCharaInfo;    // 自キャラ情報
    //private CharaInfo mEnemyCharaInfo; // 敵キャラ情報
    
    // 攻撃最大値
    private static final int ATTACK_MAX = 100000;
    
    // タイマー
 	Timer mTimer = null;
 	// ハンドラ
	Handler mHandler;
	
 	// 各コントロール
 	TextView mBattleMes;
 	TextView mPower;
 	TextView mPointGauge;
 	RatingBar mRatingBar;
 	TextView mEnemyPoewr;
 	TextView mEnemyPointGauge;
 	RatingBar mEnemyRatingBar;
 	
 	// ラウンドカウンタ
 	int roundCnt = 1;
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.battle_main);
		
		// 画面ロックされないようにする
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		// 各コントロールを取得する
		mBattleMes = (TextView)findViewById(R.id.battleMes);
		mPower = (TextView)findViewById(R.id.power);
		mPointGauge = (TextView)findViewById(R.id.pointGauge);
		mRatingBar = (RatingBar)findViewById(R.id.ratingBar);
		mEnemyPoewr = (TextView)findViewById(R.id.enemyPower);
		mEnemyPointGauge = (TextView)findViewById(R.id.enemyPointGauge);
		mEnemyRatingBar = (RatingBar)findViewById(R.id.enemyRatingBar);
		
		mHandler = new Handler();
		
		// TODO : キャラクタ情報を設定する
		//mMyCharaInfo = new CharaInfo(12,8,8,12);
		//mEnemyCharaInfo = new CharaInfo(8,12,10,10);
		
		// 音量検出クラスのインスタンス
		mVolumeDetection = new VolumeDetection();
		
		// バトルクラスのインスタンス
		mBattle = new Battle();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// ラウンド数表示
		//mBattleMes.setText(String.valueOf(roundCnt) + "R");
		
		// 定期処理
		ExecuteRegularProcessing();

        // TODO : 不要時に音量検出フラグを下げる
        //isVolume = false;
	}
	
	/**
	 * 定期処理。
	 * 
	 */
	private void ExecuteRegularProcessing(){
		// 音量検出開始
		mVolumeDetection.Start();
		
        // Timerを設定する
		mTimer = new Timer(true);
        // 第二引数の数値(ミリ秒)後にタスクを実行
		// 第三引数間隔で実行
		mTimer.schedule(new TimerTask(){
			@Override
			public void run() {
	            mHandler.post( new Runnable() {
	                public void run() {
	            		// ラウンド数表示
	            		mBattleMes.setText(String.valueOf(roundCnt) + "R");
						// 5秒後にバトル開始
						StartBattle();
	                }
	            });
			}},5*1000,10*1000);
	}
	
	/**
	 * バトルを開始する。
	 * 
	 */
	private void StartBattle(){
		// 「Fight!!」表示
		//mBattleMes.setText(R.string.battleMes01);
		// 音量検出フラグを上げる
		isVolume = true;
		
		// バトル処理開始
		mBattle.Start();
		
        // 音声認識開始
        //super.StartVoiceRecognition(PROMPT_MES,REQUEST_CODE_VOICE_RECO);
        
        // ラウンドカウンタを繰り上げる
        roundCnt++;
	}
	
	/**
     * アクティビティ終了時に結果を受け取る。
     * 
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 返った来たコードが認証用コードと一致する(自分で投げたインテント)、かつ、結果が正常の場合
        if (requestCode == REQUEST_CODE_VOICE_RECO 
        		&& resultCode == RESULT_OK) {
            // 結果用
        	String result = "";
            
            // 結果文字列を取得する
            ArrayList<String> resultList = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            
            // 類似する文字列を取得しているので、今回は最初の文字列を表示用として格納する
            result = resultList.get(0);
            
            //結果をトーストで表示する
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();

        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }
	
    /**
     * 一時停止からの再開イベント。
     * 
     */
    @Override
	protected void onRestart() {
		super.onRestart();
		// 音量検出中の場合
		if(isVolume){
			// 音量検出を再開する
			mVolumeDetection.Start();
			// バトル処理を再開する
			mBattle.Start();
		}
	}
    
	/**
	 * 一時停止時イベント。
	 * 
	 */
	@Override
	protected void onPause() {
		super.onPause();
		// 音量検出の停止
		mVolumeDetection.Stop();
		// バトル処理停止
		mBattle.Stop();
	}
	
	/**
	 * 終了時イベント。
	 * 
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// タイマーの停止
		mTimer.cancel();
		mTimer.purge();
		
		// GCに解放させるため優先度を上げる
		mTimer = null;
		mHandler = null;
		mBattleMes = null;
		mPower = null;
	 	mPointGauge = null;
	 	mRatingBar = null;
	 	mEnemyPoewr = null;
	 	mEnemyPointGauge = null;
	 	mEnemyRatingBar = null;
	 	mVolumeDetection = null;
	 	//mMyCharaInfo = null;
	 	//mEnemyCharaInfo = null;
	 	mBattle = null;
	}

	
	/**
	 * バトルクラス
	 * 
	 * @author scarviz
	 *
	 */
	private class Battle implements Runnable{
        // 処理停止状態フラグ
        private boolean isStop;
        //スレッド内から主スレッド(UIスレッド)で実行したい場合に使用
        private Handler handler = new Handler();
        // 音量値
        private int mVolume;
        // スリープ時間(ミリ秒)
        private static final int SLEEP_TIME = 1000;
        
        public Battle(){
        	isStop = true;
        }
        
        /**
         * 処理を開始する。
         * 
         */
        private void Start(){
			// TODO : 確認用
        	Log.i("battleClass","call start");
        	
        	// 停止状態の場合
            if(isStop){
            	mVolume = 0;
            	// 停止状態フラグを変更
                isStop = false;
                // スレッドを開始する
                (new Thread(this)).start();

				// TODO : 確認用
                Log.i("battleClass","thread start");
            }
        }
        
        /**
         * 処理を停止する。
         * 
         */
        private void Stop() {
			// TODO : 確認用
        	Log.i("battleClass","call stop");
        	
            // 停止状態にする
        	isStop = true;
        	// スレッド停止
    		(new Thread(this)).stop();
        }
        
		@Override
		public void run() {
			// TODO : 確認用
			Log.i("battleClass","call run");
			Log.i("init-volume",String.valueOf(mVolume));
			
			// 攻撃ゲージがMaxになるまで、または、停止状態になるまでまわす
			while(!isStop){
				// TODO : 確認用
				Log.i("battleClass","run while");
				
				// 音量を取得し、加算する
				int vol = mVolumeDetection.GetVolume();
				mVolume += vol;
				
				// TODO : 確認用
				Log.i("one-volume",String.valueOf(vol));
				Log.i("volume",String.valueOf(mVolume));
                
				//画面情報の描画
                handler.post(new Runnable() {
                    @Override
                    public void run() {
        				// パワーゲージを更新する
                    	SetPower();
                    }
                });
                
                // 描画のため、SLEEP_TIMEミリ秒スレッドをスリープさせる
                try {
                	Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {}
                
				// Max値以上になった場合
				if(mVolume >= ATTACK_MAX){
					mVolume = 0;
					// TODO
		            // 停止状態にする
		        	isStop = true;
		        	// スレッド停止
		    		(new Thread(this)).stop();
					break;
				}
			}
		}
		
		/**
		 * パワーゲージを設定する。
		 * 
		 */
		private void SetPower(){
			String power = "□□□□□□□□□□";
			int powerValue = (mVolume * 10)/ATTACK_MAX;
			
			if(powerValue <= 1){
				power = "■□□□□□□□□□";
			}
			else if(powerValue <= 2){
				power = "■■□□□□□□□□";
			}
			else if(powerValue <= 3){
				power = "■■■□□□□□□□";
			}
			else if(powerValue <= 4){
				power = "■■■■□□□□□□";
			}
			else if(powerValue <= 5){
				power = "■■■■■□□□□□";
			}
			else if(powerValue <= 6){
				power = "■■■■■■□□□□";
			}
			else if(powerValue <= 7){
				power = "■■■■■■■□□□";
			}
			else if(powerValue <= 8){
				power = "■■■■■■■■□□";
			}
			else if(powerValue <= 9){
				power = "■■■■■■■■■□";
			}
			else {
				power = "■■■■■■■■■■";
			}

			// TODO : 確認用
			Log.i("myVolume",String.valueOf(mVolume));
			Log.i("powerValue",String.valueOf(powerValue));
			Log.i("power",power);
			
			mPower.setText(power);
		}
	}

}
