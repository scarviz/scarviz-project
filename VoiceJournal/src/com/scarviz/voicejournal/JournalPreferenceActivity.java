package com.scarviz.voicejournal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class JournalPreferenceActivity extends PreferenceActivity {
    private static final int REQUEST_CODE_BG = 1;
    // 設定値領域用
    private SharedPreferences mPrefs;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        
		// 背景選択
        PreferenceScreen screenPref = (PreferenceScreen)findPreference(getText(R.string.pref_background_select));  
        //OnPreferenceClickListenerをハンドルする  
        screenPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){  
            @Override  
            public boolean onPreferenceClick(Preference preference) {  
                return onClickPref(preference);  
            }});
        // 背景リセット
        screenPref = (PreferenceScreen)findPreference(getText(R.string.pref_background_reset));  
        //OnPreferenceClickListenerをハンドルする  
        screenPref.setOnPreferenceClickListener(new OnPreferenceClickListener(){  
            @Override  
            public boolean onPreferenceClick(Preference preference) {  
                return onClickPref(preference);  
            }}); 

		// 区切り線選択
        ListPreference listPref = (ListPreference)findPreference(getText(R.string.pref_line_select));
        //OnPreferenceClickListenerをハンドルする  
        listPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){  
            @Override  
            public boolean onPreferenceChange(Preference preference, Object newValue) {  
                return onChangeLine(preference, newValue);  
            }});
	}

	@Override
	protected void onResume() {
		super.onResume();

        // 前回選択している区切り線色をサマリーに表示する
        ListPreference listPref = (ListPreference)findPreference(getText(R.string.pref_line_select));
        String lineId = mPrefs.getString(getText(R.string.pref_line_select).toString(), null);
        if(lineId != null){
        	SetLineColorAsSummary(listPref, lineId);
        }
	}

	/**
     * 設定画面の項目選択時イベント。
     *
     */
    public boolean onClickPref(Preference preference){
    	String key = preference.getKey();
    	
    	// 画像選択画面に遷移する
    	if(key.equals(getText(R.string.pref_background_select))){
	    	Intent intentBG = new Intent();
	    	intentBG.setType("image/*");
	    	intentBG.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intentBG, REQUEST_CODE_BG);
    	}
		// "背景リセット"を選択時
    	else if(key.equals(getText(R.string.pref_background_reset))){
			// 設定領域から背景URIを削除する
			SetPrefsUri(null);
			// メッセージを表示する
			Toast.makeText(JournalPreferenceActivity.this,
	           		 R.string.mes_pref_bg_reset, Toast.LENGTH_SHORT).show();
    	}
    	
    	return true;
    }
    
	/**
     * 区切り線の色選択時イベント。
     *
     */
    public boolean onChangeLine(Preference preference, Object newValue){
    	// 選択色をサマリーに表示する
    	SetLineColorAsSummary((ListPreference)preference, (String)newValue);
		
		// LineIDを取得する
    	String id = GetLineIDBySelectedItem(preference.getSummary().toString());
		// 設定領域に区切り線IDを設定する
		SetPrefsLineId(id);
    	return true;
    }
    
    /**
     * 区切り線色をサマリーに設定する。
     * 
     * @param preference
     * @param value
     */
    private void SetLineColorAsSummary(ListPreference listpref, String value){
    	// 選択色をサマリーに表示する
		int listId = listpref.findIndexOfValue(value);
		CharSequence[] entries;
		entries = listpref.getEntries();
		listpref.setSummary(entries[listId]);
    }
    
    /**
     * 選択したアイテムからLineIDを取得する。
     * 
     * @param item
     * @return
     */
    private String GetLineIDBySelectedItem(String item){
    	String result = null;
    	
    	if(item.equals(getText(R.string.line_default).toString())){result = null;}
    	else if(item.equals(getText(R.string.line_black).toString())){result = String.valueOf(R.drawable.line_black);}
    	else if(item.equals(getText(R.string.line_blue).toString())){result = String.valueOf(R.drawable.line_blue);}
    	else if(item.equals(getText(R.string.line_brown).toString())){result = String.valueOf(R.drawable.line_brown);}
    	else if(item.equals(getText(R.string.line_gray).toString())){result = String.valueOf(R.drawable.line_gray);}
    	else if(item.equals(getText(R.string.line_green).toString())){result = String.valueOf(R.drawable.line_green);}
    	else if(item.equals(getText(R.string.line_lightblue).toString())){result = String.valueOf(R.drawable.line_lightblue);}
    	else if(item.equals(getText(R.string.line_orange).toString())){result = String.valueOf(R.drawable.line_orange);}
    	else if(item.equals(getText(R.string.line_pink).toString())){result = String.valueOf(R.drawable.line_pink);}
    	else if(item.equals(getText(R.string.line_purple).toString())){result = String.valueOf(R.drawable.line_purple);}
    	else if(item.equals(getText(R.string.line_red).toString())){result = String.valueOf(R.drawable.line_red);}
    	else if(item.equals(getText(R.string.line_white).toString())){result = String.valueOf(R.drawable.line_white);}
    	else if(item.equals(getText(R.string.line_yellow).toString())){result = String.valueOf(R.drawable.line_yellow);}
    	
    	return result;
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
	    	// 背景画像変更
	    	case REQUEST_CODE_BG:
	    		Uri uri = data.getData();
	    		// 設定値領域に背景URIを設定する
	    		SetPrefsUri(uri);
				// メッセージを表示する
				Toast.makeText(JournalPreferenceActivity.this,
		           		 R.string.mes_pref_bg_set, Toast.LENGTH_SHORT).show();
	    		break;
	    	default:
	    		break;
    	}
        
        super.onActivityResult(requestCode, resultCode, data);
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
     * 設定値領域に区切り線IDを設定する。
     * 
     * @param id
     */
    private void SetPrefsLineId(String id){
		Editor editor = mPrefs.edit();
		
		// 背景URIがNULLでない場合
		if(id != null){
			// 設定値領域に背景URIを設定する
			editor.putString("LINE_ID", id);
		}
		else{
			// 背景URIを削除する
			editor.remove("LINE_ID");
		}
		
		editor.commit();
    }
}
