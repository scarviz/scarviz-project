package com.scarviz.selector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;

public class Option extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.option);
		setResetDialog();
	}

    /** 初期化ダイアログ設定 */
    private void setResetDialog() {
        PreferenceScreen pref = (PreferenceScreen)findPreference("option_reset");
        pref.setOnPreferenceClickListener(new OnPreferenceClickListener(){
            public boolean onPreferenceClick(Preference preference) {
            	/** 確認ダイアログ */
            	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(preference.getContext());
                alertDialogBuilder.setTitle(getString(R.string.option_reset_title));
                alertDialogBuilder.setMessage(R.string.option_reset_message);
                /** OKボタン選択 */
                alertDialogBuilder.setPositiveButton(R.string.option_button_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            	 /** 初期化処理 */
                            	 SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                            	 editor.clear();
                            	 editor.commit();
                            }
                        });
                /** キャンセルボタン選択 */
                alertDialogBuilder.setNegativeButton(R.string.option_button_cancel,null);
                alertDialogBuilder.setCancelable(true);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
				return true;
			}});
    }

}
