package com.scarviz.voicejournal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

/**
 * ボイスジャーナルメインクラス
 * 
 * @author scarviz
 *
 */
public class VoiceJournalActivity extends ListActivity implements LocationListener,OnItemLongClickListener{
	// 識別用コード
    private static final int REQUEST_CODE_VOICE_RECO = 1;
    private static final int REQUEST_CODE_LIST_ITEM = 2;
    private static final int REQUEST_CODE_BG = 3;
    // プロンプト表示用
    private static final String PROMPT_MES = "記録します";
    // エラーコード
    private static final int ERR_CD_POSITION = -1;
    // 空白
    private static final String BLANK = " ";
    // 改行コード
    private static final String NEWLINE = "\n";
    // Evernoteへ新規ノート作成
    public static final String ACTION_NEW_NOTE = "com.evernote.action.CREATE_NEW_NOTE";

    // LocationManager用
    private LocationManager mManager;

    // リストアダプター用
    ArrayAdapter<String> mAdapter;
    // ジャーナル情報用
    ArrayList<JournalInfo> mJournalInfo;
    
    // 新規フラグ
    boolean mIsAddNew = false;
    
    // 設定値領域用
    private SharedPreferences mPrefs;
    
    /**
     * ジャーナル情報構造体
     * 
     */
    final public class JournalInfo{
    	public int Id;			// 一意値
    	public int Position;	// リスト位置
    	public String Contents; // コンテンツ内容
    	public String CreateDate;// 登録日
    	public String UpdateDate;// 更新日
		
		/**
		 * コンストラクタ。
		 * 
		 * @param id　一意値
		 * @param position　リスト位置
		 * @param contents コンテンツ内容
		 * @param createDate　登録日
		 * @param updateDate　更新日
		 */
    	public JournalInfo(int id,int position,String contents,String createDate,String updateDate){
    		Id = id;
    		Position = position;
    		Contents = contents;
    		CreateDate = createDate;
    		UpdateDate = updateDate;
    	}
    }
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journallist);
        
        // 設定領域から前回設定した背景URIを取得する
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bgUriTxt = mPrefs.getString("URI", null);
        
        // 前回設定した背景URIが存在する場合
        if(bgUriTxt != null){
        	Uri uri = Uri.parse(bgUriTxt);
			// 背景画像を設定する
			SetBackGroundImage(uri);
        }

        // 退避情報が存在する場合
        if(savedInstanceState != null){
	        // 退避情報を再格納する
	        GetInstanceState(savedInstanceState);
        }
        
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
        // 回転時対応
        // 検索用EditText
		EditText txtSearch = (EditText)findViewById(R.id.txtSearch);
		// 検索文字を取得する
		String searchStr = txtSearch.getText().toString();
		// 検索文字が空白の場合はNULLに変更する
		if((searchStr.trim()).equals("")){
			searchStr = null;
		}
        
        // LocationManagerのインスタンス生成
        mManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        
        // ArrayAdapterを生成する
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        // DBからアイテムを取得し、リストに追加する
        mJournalInfo = Utils.getJournalInfo(this, searchStr);
        for(JournalInfo item : mJournalInfo){
            mAdapter.add(item.Contents);
        }
        
        // リストに設定する
        getListView().setAdapter(mAdapter);
        
        // 長押しイベントリスナーを設定する
        getListView().setOnItemLongClickListener(this);
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
	    	case R.id.btnSearch:
	    		// 検索開始
	    		StartSearch();
	    		break;
	        default:
	        	break;
    	}
    }

    /**
     * リスト選択時イベント。
     * 
     */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		// リスト取得
		ListView list = getListView();
		// 選択項目を取得
		String item = (String)list.getItemAtPosition(position);
		
		// 編集用Activityに遷移する
		Intent intent = new Intent(this, EditJournalActivity.class);
		intent.putExtra("TITLE_NO", position);  
		intent.putExtra("ITEM",item);
		startActivityForResult(intent, REQUEST_CODE_LIST_ITEM);
	}
	
	/**
	 * リスト選択長押しイベント。
	 * 
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
			long id) {
		// コンテキスト
		final Context context = this;
		// ダイアログ
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setTitle(R.string.dlg_selected_title);
		dlg.setItems(new String[] {"Evernoteへ追加","削除"}, 
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which){
							// "Evernoteへ追加"を選択時
							case 0:
								// Evernoteへ選択時処理
								SelectedEvernote(position);
								break;
							// "削除"を選択時
							case 1:
								// 削除選択時処理
								SelectedDelete(context, position);
								break;
							default:
								break;
						}
						
					}
				});
		dlg.show();
		
		return true;
	}
	
	/**
	 * Evernoteへ選択時処理。
	 * 
	 * @param position
	 */
	private void SelectedEvernote(final int position){
		// リスト取得
		ListView list = getListView();
		// 選択項目を取得
		String item = (String)list.getItemAtPosition(position);
		
		Intent intent = new Intent();
	    intent.setAction(ACTION_NEW_NOTE);
		// 選択項目を設定
		intent.putExtra(Intent.EXTRA_TEXT, item);
		try{
			// Evernoteへ遷移
			startActivity(intent);
		}catch (android.content.ActivityNotFoundException ex) {
			// 遷移に失敗した場合、エラーメッセージを表示する
			Toast.makeText(VoiceJournalActivity.this,
	           		 R.string.err_mes_004, Toast.LENGTH_SHORT).show();
		} 
	}
	
	/**
	 * 削除選択時処理。
	 * 
	 * @param context
	 * @param position
	 */
	private void SelectedDelete(final Context context, final int position){
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		// 削除確認ダイアログ
		dlg.setTitle(R.string.dlg_del_title);
		dlg.setIcon(android.R.drawable.ic_dialog_alert);
		dlg.setMessage(R.string.dlg_del_mes_001);
		// OKボタン
		dlg.setPositiveButton(R.string.dlg_btn_ok, 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// リスト取得
						ListView list = getListView();
						// 選択項目を取得
						String item = (String)list.getItemAtPosition(position);
						// 項目を削除
						mAdapter.remove(item);
						// ジャーナル情報、DBから削除する
						RemoveJournal(context,item);
						
						// 削除完了メッセージを表示
						Toast.makeText(VoiceJournalActivity.this,
			            		 R.string.mes_edit_del, Toast.LENGTH_SHORT).show();
					}});
		// キャンセルボタン
		dlg.setNegativeButton(R.string.dlg_btn_cancel, 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 何もしない
					}});
		dlg.show();
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
    	// 結果がOKでない場合
    	if (resultCode != RESULT_OK){return;}
    	
    	// 処理毎の結果
    	switch(requestCode){
    		// 音声認識
	    	case REQUEST_CODE_VOICE_RECO:
	            // 結果用
	        	String result = "";
	            // 結果文字列を取得する
	            ArrayList<String> resultList = data.getStringArrayListExtra(
	                    RecognizerIntent.EXTRA_RESULTS);
	            // 類似する文字列を取得しているので、今回は最初の文字列を表示用として格納する
	            result = resultList.get(0);
	            
	            //結果をトーストで表示する
	            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
	            
	            // スペースを改行に置換する
	            try{
	            	result = result.replaceAll(BLANK, NEWLINE);
	            }
	            catch(Exception e){
	    			// エラーログ出力
	    			Log.e("err",e.toString());
	    		}
	            
	            // 結果をジャーナル用EditTextに追加
	            AddJournal(result);
	    		break;
	    	// ジャーナル編集
	    	case REQUEST_CODE_LIST_ITEM:
	    		// 新規フラグをローカルに移して、フラグを下げる
	    		boolean isNewAdd = mIsAddNew;
				mIsAddNew = false;
				
	    		// 結果情報を取得する
	            int position = data.getIntExtra("TITLE_NO",ERR_CD_POSITION);
	            String newItem = data.getStringExtra("ITEM");
	    		// 正常に結果を受け取れなかった場合
	            if(position == ERR_CD_POSITION){
		            Toast.makeText(this, R.string.err_mes_003, Toast.LENGTH_SHORT).show();
	            	break;
	            }
	           
    			// 新規追加の場合
    			if(isNewAdd){
    				// 空の場合はそのまま終了
    				if((newItem.trim()).equals("") || newItem.equals(null)){ break; }
    		    	
    				// データを追加して終了
    		    	AddJournal(newItem);
    	            Toast.makeText(this, R.string.mes_edit_save, Toast.LENGTH_SHORT).show();
    	    		break;
    			}
	            
	            // リスト取得
	    		ListView list = getListView();
	    		// 選択項目を取得
	    		String item = (String)list.getItemAtPosition(position);
	    		// 変更点がない場合、処理を終了する
	    		if(newItem.equals(item)){
	    			break; 
	    		}

	    		// 編集結果を反映する
	    		// 前回情報を削除する
				mAdapter.remove(item);
				// 空の場合は削除したまま終了
				if((newItem.trim()).equals("") || newItem.equals(null)){
					// ジャーナル情報、DBから削除する
					RemoveJournal(this,item);
					break;
				}
				// 同じ位置に編集データを挿入する
				mAdapter.insert(newItem, position);

				// ジャーナル情報分まわす
				for(int i = 0; i < mJournalInfo.size(); i++){
					// 編集対象項目の場合
					if(mJournalInfo.get(i).Contents.equals(item)){
						// 編集前情報を取得
						JournalInfo oldInfo = mJournalInfo.get(i);
						// 編集後のジャーナル情報を作成
						JournalInfo newInfo
							= new JournalInfo(oldInfo.Id, oldInfo.Position, newItem, oldInfo.CreateDate, GetToDay());
						// ジャーナル情報を更新
						mJournalInfo.set(i, newInfo);
						// DBを更新
						Utils.updateJournalInfo(this, newInfo);
						break;
					}
				}
	            Toast.makeText(this, R.string.mes_edit_save, Toast.LENGTH_SHORT).show();
	    		break;
	    	// 背景画像変更
	    	case REQUEST_CODE_BG:
	    		Uri uri = data.getData();
	    		// 背景画像を設定する
	    		SetBackGroundImage(uri);
	    		// 設定値領域に背景URIを設定する
	    		SetPrefsUri(uri);
	    		break;
	    	default:
	    		break;
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
								// 設定画面を開く
								startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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
		// リスナーを解除(ボタンワンクリックで1回取得するだけ)
		mManager.removeUpdates(this);
		
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
		AddJournal(locationInfo);
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
			for (int i = 0; i <= address.getMaxAddressLineIndex(); i++){
				buf = address.getAddressLine(i);
				strbuf.append(buf+"　");
			}

			result = strbuf.toString();
		}
		
		return result;
	}
	
	/**
	 * 検索を開始する。
	 * 
	 */
	private void StartSearch(){
		EditText txtSearch = (EditText)findViewById(R.id.txtSearch);
		
		LinearLayout searchLayout = (LinearLayout)findViewById(R.id.search_layout);
		// 非表示の場合
		if(searchLayout.getVisibility() == View.GONE){
			// 検索領域を表示する
			searchLayout.setVisibility(View.VISIBLE);
		}
		else{
			// 検索領域を非表示にする
			searchLayout.setVisibility(View.GONE);
			
			// 検索文字を取得する
			String searchStr = txtSearch.getText().toString();
			// 検索文字が空白の場合はNULLに変更する
			if((searchStr.trim()).equals("")){
				searchStr = null;
			}
			
			// リストをリセットする
	        mAdapter.clear();
			// 検索処理
			mJournalInfo = Utils.getJournalInfo(this, searchStr);
	        // DBから取得した情報をリストに追加する
	        for(JournalInfo item : mJournalInfo){
	            mAdapter.add(item.Contents);
	        }
	        // リストに設定する
	        getListView().setAdapter(mAdapter);
			
		}
	}
	
	/**
	 * 今日の日付を取得する
	 * 
	 * @return
	 */
	private String GetToDay(){
		String result = null;
		// 今日の日付を取得する
		final Calendar calendar = Calendar.getInstance();
		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH);
		final int day = calendar.get(Calendar.DAY_OF_MONTH);
		final int hour = calendar.get(Calendar.HOUR_OF_DAY);
		final int minute = calendar.get(Calendar.MINUTE);
		final int second = calendar.get(Calendar.SECOND);
		// 今日の日付を整形する
		result = year + "/" + month + "/" + day + " " + hour + ":" + minute + ":" + second;
		
		return result;
	}
	
	/**
	 * ジャーナル情報とDBに追加する。
	 * 
	 * @param text
	 */
	private void AddJournal(String text){
		// 一番上に追加する
		mAdapter.insert(text, 0);
		
		// 今日の日付を取得する
		String toDay = GetToDay();
		
		// DB更新処理
		JournalInfo info = new JournalInfo(0, 0, text, toDay, toDay);
		Utils.createJournalInfo(this, info);
		
		// ジャーナル情報に追加
		mJournalInfo.add(info);
	}
	
	/**
	 * ジャーナル情報とDBから削除する。
	 * 
	 * @param context
	 * @param item
	 */
	private void RemoveJournal(Context context, String item){
		// ジャーナル情報分まわす
		for(int i = 0; i < mJournalInfo.size(); i++){
			// 削除対象項目の場合
			if(mJournalInfo.get(i).Contents.equals(item)){
				// DBから削除
				Utils.deleteJournalInfo(context, mJournalInfo.get(i).Id);
				// ジャーナル情報から削除
				mJournalInfo.remove(i);
				break;
			}
		}
	}
	
	/**
	 * 背景画像を設定する。
	 * 
	 * @param uri
	 */
	private void SetBackGroundImage(Uri uri){
		Bitmap bmp = null;

		// 全体を囲っているLinearLayout
        LinearLayout journal = (LinearLayout)findViewById(R.id.journal);
        
        // 背景URIがNULLの場合
        if(uri == null){
        	// 背景をリセットする
        	journal.setBackgroundDrawable(null);
        	return;
        }
        
		try {
			// URIからBitmapを取得する
			bmp = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
        
        // 背景画像を取得
        Drawable backGroundImg = new BitmapDrawable(bmp);
        // 背景画像を設定する
        journal.setBackgroundDrawable(backGroundImg);
        
	}
	
    /**
     * 設定値領域に背景URIを設定する。
     * 
     * @param uri
     */
    private void SetPrefsUri(Uri uri){
		Editor editor = mPrefs.edit();
		
		// 背景URIがNULLでない場合
		if(uri != null){
			// 設定値領域に背景URIを設定する
			editor.putString("URI", uri.toString());
		}
		else{
			// 背景URIを削除する
			editor.remove("URI");
		}
		
		editor.commit();
    }
	
    /**
     * 退避情報を再格納する。
     * 
     */
    private void GetInstanceState(Bundle inState){
    	// 退避情報を取得する
    	mIsAddNew = inState.getBoolean("isAddNew");	// 新規フラグ
    }
    
    /**
     * 保持している情報を退避させる。
     * 
     */
    @Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
    	// 情報を退避させる
		outState.putBoolean("isAddNew", mIsAddNew);	// 新規フラグ
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
		    // 新規追加
		    case R.id.menu_add:
		    	// 新規フラグを立てる
		    	mIsAddNew = true;
		    	// 編集用Activityに遷移する
				Intent intent = new Intent(this, EditJournalActivity.class);
				intent.putExtra("TITLE_NO", 0);  
				intent.putExtra("ITEM","");
				startActivityForResult(intent, REQUEST_CODE_LIST_ITEM);
		        break;
		    // 背景変更
		    case R.id.menu_background:
		    	//  画像変更オプションの設定処理
		    	SetOptionBackground();
		    	break;
		    default:
		    	break;
	    }
	    return true;
	}
	
	/**
	 * 画像変更オプションの設定処理。
	 * 
	 */
	private void SetOptionBackground(){
		// 設定用ダイアログ
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setTitle(R.string.dlg_selected_title);
		dlg.setItems(new String[] {"背景画像選択","背景リセット"}, 
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which){
							// "背景画像選択"を選択時
							case 0:
						    	// 画像選択画面に遷移する
						    	Intent intentBG = new Intent();
						    	intentBG.setType("image/*");
						    	intentBG.setAction(Intent.ACTION_GET_CONTENT);
								startActivityForResult(intentBG, REQUEST_CODE_BG);
								break;
							// "背景リセット"を選択時
							case 1:
								// 設定領域から背景URIを削除する
								SetPrefsUri(null);
								// 背景をリセットする
								SetBackGroundImage(null);
								break;
							default:
								break;
						}
						
					}
				});
		dlg.show();
	}

    /**
     * アクティビティ停止時処理。
     * 
     */
	@Override
	protected void onPause() {
		super.onPause();
		// リスナーを解除
		mManager.removeUpdates(this);
	}
	
    /**
     * アクティビティ終了時処理。
     * 
     */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// GCに優先的に開放させる
		mManager = null;
		mAdapter = null;
		mPrefs = null;
	}


    /**
     * プロバイダが無効になったら呼び出される。
     */
	@Override
	public void onProviderDisabled(String provider) {
	}

	/**
	 * プロバイダが有効になったら呼び出される。
	 */
	@Override
	public void onProviderEnabled(String provider) {
	}

	/**
	 * プロバイダの状態が変化したら呼び出される。
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {		
	}
}