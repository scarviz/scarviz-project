package com.scarviz.VolumeDetection;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * 音量検出Runnableクラス
 * 
 * @author scarviz
 *
 */
public class VolumeDetectionRunnable implements Runnable {
	// 録音フラグ
	private boolean isRecording = true;
	// サンプリングレート 8kHz
	private static final int SAMPLE_RATE = 8000;
	// リスナー
	private OnReachedVolumeListener mListener;
	
	/**
	 * 録音停止
	 * 
	 */
	public void stop(){
		isRecording = false;
	}

	/**
	 * リスナーを設定する
	 * 
	 * @param listener
	 */
	public void setOnVolumeReachedListener(
			OnReachedVolumeListener listener){
		mListener = listener;
	}

	/**
	 * 音量検出時のリスナー
	 * 
	 * @author scarviz
	 *
	 */
	public interface OnReachedVolumeListener {
		void OnReachedVolum(short volume);
	}
	
	/**
	 * スレッドを開始する(録音開始)
	 * 
	 */
	@Override
	public void run() {
		// プロセスの優先度を上げる
		android.os.Process.setThreadPriority(
				android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		// バッファサイズを取得する(サンプルレート8kHz,モノラル,16ビット)
		int bufferSize = AudioRecord.getMinBufferSize(
				SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		// レコーダを取得する(サンプルレート8kHz,モノラル,16ビット)
		AudioRecord audioRecord = new AudioRecord(
				MediaRecorder.AudioSource.MIC,
				SAMPLE_RATE,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				bufferSize);
		// バッファ
		short[] buffer = new short[bufferSize];
		
		// 録音開始
		audioRecord.startRecording();
		// 録音フラグがtrueの場合は処理を続ける
		while(isRecording){
			// バッファを読み込んで書き込む
			audioRecord.read(buffer, 0, bufferSize);
			short maxVol = 0;
			// バッファ分まわす
			for(short item : buffer){
				// バッファ中の最大音量を取得する
				maxVol = (short)Math.max(maxVol, item);
			}
			
			if(mListener != null){
				// リスナー実行
				mListener.OnReachedVolum(maxVol);
				// ループから抜ける
				break;
			}
		}
		
		// 録音終了
		audioRecord.stop();
		// 解放する
		audioRecord.release();
	}
}
