package com.scarviz.android.buildinfo;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

/**
 * ビルド情報クラス
 * 
 * @author scarviz
 *
 */
public class AndroidBuildInfo extends Activity {
	TextView txtBuildInfo;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        txtBuildInfo = (TextView)findViewById(R.id.txtBuildInfo);
    }
	
    @Override
	protected void onResume() {
		super.onResume();
		
		// ビルド情報を取得する
		String text =GetAndroidBuildInfo();
		// ビルド情報をTextViewで表示する
    	txtBuildInfo.setText(text);
	}
	
    /**
     * ビルド情報を取得する。
     * 
     * @return　ビルド情報
     */
    private String GetAndroidBuildInfo(){
    	String text = "※ BuildInfo ※\n"
    		+ "ボード名称:" + Build.BOARD + "\n"
    		+ "ブートローダのバージョン番号:" + Build.BOOTLOADER + "\n"
    		+ "ブランド名:" + Build.BRAND + "\n"
    		+ "ネイティブコードの命令セット:" + Build.CPU_ABI + "\n"
    		+ "ネイティブコードの第2命令セット:" + Build.CPU_ABI2 + "\n"
    		+ "デバイス名:" + Build.DEVICE + "\n"
    		+ "ビルドID:" + Build.DISPLAY + "\n"
    		+ "ビルド識別子:" + Build.FINGERPRINT + "\n"
    		+ "ハードウェア名:" + Build.HARDWARE + "\n"
    		+ "ホスト名:" + Build.HOST + "\n"
    		+ "変更番号:" + Build.ID + "\n"
    		+ "製造者名:" + Build.MANUFACTURER + "\n"
    		+ "モデル名:" + Build.MODEL + "\n"
    		+ "製品名:" + Build.PRODUCT + "\n"
    		+ "無線ファームウェアのバージョン:" + Build.RADIO + "\n"
    		+ "ビルドのタグ名:" + Build.TAGS + "\n"
    		+ "システム時刻:" + Build.TIME + "\n"
    		+ "ビルドタイプ:" + Build.TYPE + "\n"
    		+ "情報不明時の識別子:" + Build.UNKNOWN + "\n"
    		+ "ユーザ情報:" + Build.USER + "\n"
    		+ "開発コードネーム:" + Build.VERSION.CODENAME + "\n"
    		+ "ソースコード管理番号:" + Build.VERSION.INCREMENTAL + "\n"
    		+ "バージョン番号:" + Build.VERSION.RELEASE + "\n"
    		+ "フレームワークのバージョン情報:" + Build.VERSION.SDK_INT + "\n";
    	
    	return text;
    }
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		// GCに優先的に解放させるため
		txtBuildInfo = null;
    }
}