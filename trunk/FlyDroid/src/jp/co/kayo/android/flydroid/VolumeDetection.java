package jp.co.kayo.android.flydroid;

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
     * 入力待ち状態時処理。
     * 
     */
	public short GetVolume() {
		mVolDetectRun = new VolumeDetectionRunnable();
		// リスナー登録
		mVolDetectRun.setOnVolumeReachedListener(
				new VolumeDetectionRunnable.OnReachedVolumeListener() {
					// 音量検出時
					@Override
					public void OnReachedVolume(final short volume) {
						new Runnable(){
							public void run(){
								mVolume = volume;
							}
						};
					}
				});
		// 別スレッド開始
		new Thread(mVolDetectRun).start();
		
		return mVolume;
	}
    
}