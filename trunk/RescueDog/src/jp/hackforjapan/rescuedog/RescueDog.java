package jp.hackforjapan.rescuedog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class RescueDog extends Activity implements LocationListener{
	private TextView statusTextView;
	// TODO : テストコード
	//private TextView mTextView;
    private LocationManager mManager;
    private SharedPreferences mPrefs;
    
    // キー値
    private static final String KEY_LATI = "key_lati";
    private static final String KEY_LONG = "key_long";
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.main);
        
        // ステータス表示用
        statusTextView = (TextView)findViewById(R.id.statusText);
        
        // TODO : テストコード
        //mTextView = (TextView)findViewById(R.id.textView1);
        
        // LocationManagerのインスタンス生成
        mManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        
        // SharedPreferencesのインスタンス生成
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

	@Override
	protected void onPause() {
		super.onPause();
		// リスナーを解除
		mManager.removeUpdates(this);
		
		// ステータス表示を初期値に変更
		statusTextView.setText(R.string.status_001);
	}

	/**
	 * ボタン押下時処理
	 * 
	 * @param view
	 */
	public void onClickButton(View view) {
		switch(view.getId()){
			case R.id.button1:
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
					// ステータス表示を取得中に変更
					statusTextView.setText(R.string.status_002);
		
				}catch(Exception e){
					// エラーログ出力
					Log.e("err",e.toString());
					
					Toast.makeText(this,R.string.mes_003, Toast.LENGTH_SHORT).show();
				}
			break;
			case R.id.button2:
				// 前回の位置情報を取得する
				double latitude = GetLocationFile(KEY_LATI);
				double longitude = GetLocationFile(KEY_LONG);
		    	// TODO : テストコード
		    	//mTextView.setText("緯度＝"+latitude+",経度＝"+longitude);
				// 救助検ページに遷移する
		    	AccessOnePage(latitude,longitude);
		    break;
		}
	}
    
	/**
	 *  位置情報が変化したら呼び出される
	 *  
	 */
	@Override
    public void onLocationChanged(Location location){
		// 緯度と経度を取得する
		double latitude = location.getLatitude();
    	double longitude = location.getLongitude();
    	
    	// 位置情報を保存する
    	SetLocationFile(latitude,longitude);
    	
    	// TODO : テストコード
    	//mTextView.setText("緯度＝"+latitude+",経度＝"+longitude);
    	// 救助検ページに遷移する
    	AccessOnePage(latitude,longitude);
    }
    
    /**
     *  救助検ページに遷移する
     *  
     * @param latitude
     * @param longitude
     */
    private void AccessOnePage(double latitude,double longitude){
		Intent intent=new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://one.jaws-ug.jp/get.php?lat="+latitude+"&lon="+longitude));
		startActivity(intent);
    }
    
    /**
     * 位置情報を保存する
     * 
     * @param latitude
     * @param longitude
     */
    private void SetLocationFile(double latitude, double longitude){
    	Editor editor = mPrefs.edit();
    	
    	// 緯度と経度を設定する
    	editor.putString(KEY_LATI, String.valueOf(latitude));
    	editor.putString(KEY_LONG, String.valueOf(longitude));
    	
    	// TODO : 保存した日付
    	
    	// 設定を保存する
    	editor.commit();
    }
    
    /**
     * キー値に当たる位置情報を取得する
     * 
     * @param key
     * @return
     */
    private double GetLocationFile(String key){
    	double result = 0.0;
    	
    	// キー値に当たる位置情報を取得する
    	result = Double.parseDouble(mPrefs.getString(key, "0.0"));
    	
    	return result;
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
	
	/**
	 * オプションメニューの生成
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    // XMLで定義したmenuを指定する。
	    inflater.inflate(R.menu.mainmenu, menu);
	    return true;
	}
	
	/**
	 * オプションメニューの選択
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    int itemId = item.getItemId();
	    switch (itemId) {
	    case R.id.menu_aboutapp:
	    	AlertDialog.Builder dlg = new AlertDialog.Builder(this);
	    	dlg.setTitle(R.string.menu_aboutapp);
	    	dlg.setIcon(android.R.drawable.ic_dialog_info);
	    	dlg.setMessage(R.string.mes_aboutapp);
	    	dlg.show();
	        break;
	    }
	    return true;
	}
}