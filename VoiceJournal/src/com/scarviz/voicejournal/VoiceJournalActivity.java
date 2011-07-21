package com.scarviz.voicejournal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * ボイスジャーナルメインクラス
 * 
 * @author scarviz
 *
 */
public class VoiceJournalActivity extends Activity implements LocationListener{
	// 返ってきた時の認証用コード(数字は適当なもので良い)
    private static final int REQUEST_CODE_VOICE_RECO = 123;
    // プロンプト表示用
    private static final String PROMPT_MES = "記録します";
	// 改行コード
	private static final String NEWLINE = "\n";

    // LocationManager用
    private LocationManager mManager;
    // ジャーナル用EditText
    EditText mtxtJournal;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // LocationManagerのインスタンス生成
        mManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        
        // ジャーナル用EditText
        mtxtJournal = (EditText)findViewById(R.id.txtJournal);
        
        // 退避情報が存在する場合
        if(savedInstanceState != null){
	        // 退避情報を再格納する
	        GetInstanceState(savedInstanceState);
        }
    }
    
    /**
     * ボタン押下時イベント。
     *
     */
    public void onClickButton(View view){
    	switch(view.getId()){
	    	case R.id.btnRec:
	            // 音声認識開始
	            StartVoiceRecognition();
	            break;
	    	case R.id.btnGps:
	    		// 位置情報更新開始
	    		StartLocationUpdates();
	            break;
	        default:
	        	break;
    	}
    }
    
    /**
     * 音声認識を開始する。
     * 
     */
    private void StartVoiceRecognition(){
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
                     RecognizerIntent.EXTRA_PROMPT,PROMPT_MES);
             
             // インテントを発行する(音声認識ツールが戻り値ありモードで起動する)
             startActivityForResult(intent, REQUEST_CODE_VOICE_RECO);
         } catch (ActivityNotFoundException e) {
             // 音声認識ツールがインストールされていない場合、トーストでエラーメッセージを表示する
             Toast.makeText(this,
            		 R.string.err_mes_001, Toast.LENGTH_LONG).show();
         }
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
            
            // 結果をジャーナル用EditTextに追加
            mtxtJournal.append(result + NEWLINE);
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
     * 位置情報の更新を開始する。
     * 
     */
    private void StartLocationUpdates(){
		try{
			String provider = mManager.getBestProvider(new Criteria(), true);
			if(provider == null){
				// GPSが有効になっていない
				// ダイアログを開いて有効にするように促す
				AlertDialog.Builder dlg = new AlertDialog.Builder(this);
				dlg.setTitle(R.string.dlg_title);
				dlg.setIcon(android.R.drawable.ic_dialog_alert);
				dlg.setMessage(R.string.dlg_mes_001);
				dlg.setPositiveButton(R.string.dlg_btn_ok, 
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO : 設定画面を開く
							}});
				dlg.show();
				return;
			}
			// 位置情報の取得開始
			mManager.requestLocationUpdates(provider, 0, 0, this);

		}catch(Exception e){
			// エラーログ出力
			Log.e("err",e.toString());
			
			Toast.makeText(this,R.string.err_mes_002, Toast.LENGTH_SHORT).show();
		}
    }
    
	/**
	 *  位置情報が変化したら呼び出される
	 *  
	 */
	@Override
	public void onLocationChanged(Location location) {
		// 緯度と経度を取得する
		double latitude = location.getLatitude();
    	double longitude = location.getLongitude();
    	
    	// 表示用
    	String locationInfo = "緯度＝"+latitude+",経度＝"+longitude;
    	try{
    		// 座標を住所に変換する
    		locationInfo = ConvertAddress(latitude,longitude,this);
    	}
    	catch(IOException ex){
			// エラーログ出力
			Log.e("err",ex.toString());
		}
    	
    	// 取得情報を表示
		Toast.makeText(this, locationInfo, Toast.LENGTH_SHORT).show();

        // 緯度と経度をジャーナル用EditTextに追加
        mtxtJournal.append(locationInfo + NEWLINE);
        
		// リスナーを解除(ボタンワンクリックで1回取得するだけ)
		mManager.removeUpdates(this);
	}
	
	/**
	 * 座標を住所に変換する。
	 * 
	 * @param latitude
	 * @param longitude
	 * @param context
	 * @return
	 * @throws IOException
	 */
	private String ConvertAddress(double latitude, double longitude, Context context) throws IOException{
		// 戻り値用結果
		String result = new String();

		//　geocoedrの実体化
		Geocoder geocoder = new Geocoder(context, Locale.JAPAN);
		// 住所情報を取得(最大5項目)
		List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 5);

		// 空でない場合
		if (!addressList.isEmpty()){
			// 住所情報
			Address address = addressList.get(0);
			StringBuffer strbuf = new StringBuffer();

			//　住所情報をつなげていく
			String buf;
			for (int i = 0; (buf = address.getAddressLine(i)) != null; i++){
				strbuf.append(buf+"　");
			}

			result = strbuf.toString();
		}
		
		return result;
	}
	
    /**
     * 退避情報を再格納する。
     * 
     */
    private void GetInstanceState(Bundle inState){
    	// 退避情報を取得する
    	String txtJournal = inState.getString("txtJournal");	// ジャーナル内容 
    	
    	// 取得した値を再格納する
    	mtxtJournal.setText(txtJournal);	// ジャーナル内容 
    }
    
    /**
     * 保持している情報を退避させる。
     * 
     */
    @Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 退避する値の取得
    	String txtJournal = mtxtJournal.getText().toString();	// ジャーナル内容 
    	
    	// 情報を退避させる
		outState.putString("txtJournal", txtJournal);	// ジャーナル内容
	}

    /**
     * アクティビティ停止時処理
     * 
     */
	@Override
	protected void onPause() {
		super.onPause();
		// リスナーを解除
		mManager.removeUpdates(this);
	}
	
    /**
     * アクティビティ終了時処理
     * 
     */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// GCに優先的に開放させる
		mManager = null;
		mtxtJournal = null;
	}


    /**
     * プロバイダが無効になったら呼び出される
     */
	@Override
	public void onProviderDisabled(String provider) {
	}

	/**
	 * プロバイダが有効になったら呼び出される
	 */
	@Override
	public void onProviderEnabled(String provider) {
	}

	/**
	 * プロバイダの状態が変化したら呼び出される
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {		
	}
}