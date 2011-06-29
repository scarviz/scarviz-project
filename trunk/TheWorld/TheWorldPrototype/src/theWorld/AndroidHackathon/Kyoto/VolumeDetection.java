package theWorld.AndroidHackathon.Kyoto;

import android.util.Log;

/**
 * 音量検出クラス
 * 
 * @author scarviz
 *
 */
public class VolumeDetection {
	// 音量検出Runnableクラス用
	private VolumeDetectionRunnable mVolDetectRun;
	// 音量値
	private short mVolume;

	/**
	 * コンストラクタ
	 * 
	 */
	public VolumeDetection()
	{
		// 初期値
		mVolume = 0;
	}
	
	/**
	 * スレッドを開始する。
	 * 
	 */
	public void Start()
	{
		try{
			mVolDetectRun = new VolumeDetectionRunnable();
			// リスナー登録
			mVolDetectRun.setOnVolumeReachedListener(
					new VolumeDetectionRunnable.OnReachedVolumeListener() {
						// 音量検出時
						@Override
						public void OnReachedVolume(final short volume) {
							mVolume = volume;
						}
					});
			// 別スレッド開始
			new Thread(mVolDetectRun).start();	
		}
		catch(Exception ex){
			Log.d("listenrEX","ex:" + ex.getMessage());
		}
	}

    /**
     * 音量値を取得する。
     * 
     */
	public short GetVolume() {
		return mVolume;
	}
    
	/**
	 * スレッドを停止する。
	 * 
	 */
	public void Stop()
	{
		// スレッド停止
		new Thread(mVolDetectRun).stop();
	}
}