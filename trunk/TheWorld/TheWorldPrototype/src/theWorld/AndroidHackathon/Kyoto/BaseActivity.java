package theWorld.AndroidHackathon.Kyoto;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Toast;

public class BaseActivity extends Activity {

	// 効果音再生用オブジェクト
	SoundPool soundPool;
	int zukyun_se;
	int hit_se;

	// 音楽再生用：
	private MediaPlayer mPlayer;

	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 効果音再生　初期化
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		zukyun_se = soundPool.load(this, R.raw.zukyun, 1);
		hit_se = soundPool.load(this, R.raw.hit_se01, 1);
		
		
	}
	
	
	/**
	 * onResume。
	 * 音楽再生はここで行う。
	 * ※戻ってきたときに再生するため。
	 * 
	 */
	@Override
	protected void onResume() {
		super.onResume();

		// 音楽再生(先頭からスタートさせ、ループ再生させる)
		mPlayer = MediaPlayer.create(this, R.raw.opening_bgm);
		mPlayer.seekTo(0);
		mPlayer.setLooping(true);
		mPlayer.start();
	}
	
	public void PlaySE(){
		

		soundPool.play(hit_se, 100.0f, 100.0f, 0, 0, 1.0f);

	}
	
	
	

    /**
     * 音声認識を開始する。
     * 
     * @param promptMes
     *  音声認識のプロンプトのメッセージ
     * @param requestCode
     *  認証コード
     */
    public void StartVoiceRecognition(String promptMes, int requestCode){
    	 try {
             // インテント作成
    		 // 認識された音声を文字列として取得する
             Intent intent = new Intent(
                     RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
             // 音声認識に使う言語モデルを指定する
             intent.putExtra(
                     RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                     RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
             // プロンプトの表示内容を指定する
             intent.putExtra(
                     RecognizerIntent.EXTRA_PROMPT,promptMes);
             
             // インテントを発行する(音声認識ツールが戻り値ありモードで起動する)
             startActivityForResult(intent, requestCode);
         } catch (ActivityNotFoundException e) {
             // 音声認識ツールがインストールされていない場合、トーストでエラーメッセージを表示する
             Toast.makeText(this,
            		 R.string.err_mes_001, Toast.LENGTH_LONG).show();
         }
    }

 // 中断時処理
	@Override
	protected void onPause() {

		super.onPause();
		mPlayer.stop();
		mPlayer.release();

	}
    
    
    /**
     * アクティビティ終了時処理
     * 
     */
	// 終了時処理
	@Override
	protected void onDestroy() {

		super.onDestroy();

		// GCでの解放優先度をあげる
		mPlayer = null;
		soundPool = null;

	}
	
		
	/**
	 * キャラクタ情報クラス
	 * 
	 * @author scarviz
	 *
	 */
	public class CharaInfo{
		public int Attack;
		public int Defense;
		public int Quickness;
		public int HitPoint;
		
		/**
		 * キャラクタ情報
		 * 
		 * @param attack　攻撃力
		 * @param defense　防御力
		 * @param quickness　素早さ
		 * @param hp　HP
		 */
		public CharaInfo(int attack,int defense,int quickness,int hp){
			Attack = attack;
			Defense = defense;
			Quickness = quickness;
			HitPoint = hp;
		}
	}
}
