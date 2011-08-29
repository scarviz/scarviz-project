package com.scarviz.voicejournal;

import java.util.ArrayList;

import com.scarviz.voicejournal.VoiceJournalActivity.JournalInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class Utils {

	/**
	 * 指定した文字列で検索を行い、リストを取得する
	 * 
	 * @param context
	 * @param contents
	 * @return
	 */
	public static ArrayList<JournalInfo> getJournalInfo(Context context,String contents){
		DatabaseHelper helper = null;
		SQLiteDatabase db = null;
		Cursor cur = null;
		ArrayList<JournalInfo> resultInfo = new ArrayList<JournalInfo>();
		VoiceJournalActivity voiceJournalAct = new VoiceJournalActivity();
		try{
			// Helperクラス生成
			helper = new DatabaseHelper(context);
			// 読み込み用SQLiteDatabaseを生成
			db = helper.getWritableDatabase();
			
			String selection = null;
			String[] selectionArgs = null;
			// 検索文字がNULLでない場合は、値を設定する
			if(!(contents == null)){
				selection = DatabaseHelper.FIELD_CONTENTS + " = ?";
				selectionArgs = new String[] {contents};
			}
			
			// 検索処理の実行
			cur = db.query(DatabaseHelper.TABLE_VOICE_JOURNAL,
					new String[] {BaseColumns._ID, DatabaseHelper.FIELD_POSITION, DatabaseHelper.FIELD_CONTENTS, 
									DatabaseHelper.FIELD_CREATE_DATE,DatabaseHelper.FIELD_UPDATE_DATE},
									selection,
									selectionArgs,
									null,null,DatabaseHelper.FIELD_POSITION,null);
			if(cur != null && cur.moveToFirst()){
				do{
					// 行データの各カラムから値を取得する
					int id = cur.getInt(cur.getColumnIndex(BaseColumns._ID));
					int position = cur.getInt(cur.getColumnIndex(DatabaseHelper.FIELD_POSITION));
					String cont = cur.getString(
							cur.getColumnIndex(DatabaseHelper.FIELD_CONTENTS));
					String createDate = cur.getString(
							cur.getColumnIndex(DatabaseHelper.FIELD_CREATE_DATE));
					String updateDate = cur.getString(
						cur.getColumnIndex(DatabaseHelper.FIELD_UPDATE_DATE));

					// ジャーナル情報リストに追加する
					resultInfo.add(voiceJournalAct.new JournalInfo(id,position,cont,createDate,updateDate));
				}
				while(cur.moveToNext());
			}
			
			return resultInfo;
		}finally{
			// 終了処理
			if(cur != null){
				cur.close();
			}
			if(db != null){
				db.close();
			}
			if(helper != null){
				helper.close();
			}
		}
	}
	
	/**
	 * 変更内容をDBに反映する
	 * 
	 * @param context
	 * @param info
	 */
	public static void updateJournalInfo(Context context, JournalInfo info){
		DatabaseHelper helper = null;
		SQLiteDatabase db = null;
		try{
			// Helperクラスの生成
			helper = new DatabaseHelper(context);
			// 書き込み用SQLiteDatabaseを生成
			db = helper.getWritableDatabase();
			
			// 書き込み用のデータを生成
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.FIELD_POSITION,info.Position);
			values.put(DatabaseHelper.FIELD_CONTENTS,info.Contents);
			values.put(DatabaseHelper.FIELD_CREATE_DATE,info.CreateDate);
			values.put(DatabaseHelper.FIELD_UPDATE_DATE,info.UpdateDate);
			
			// アップデート処理の実行
			db.update(DatabaseHelper.TABLE_VOICE_JOURNAL, values,
					BaseColumns._ID + " = ?",
					new String[]{Integer.toString(info.Id)});
		}finally{
			// 終了処理
			if(db != null){
				db.close();
			}
			if(helper != null){
				helper.close();
			}
		}
	}
	
	/**
	 * コンテンツを新しく登録
	 * 
	 * @param cotext
	 * @param info
	 */
	public static void createJournalInfo(Context context, JournalInfo info){
		DatabaseHelper helper = null;
		SQLiteDatabase db = null;
		try{
			// Helperクラスの生成
			helper = new DatabaseHelper(context);
			// 書き込み用SQLiteDatabaseを生成
			db = helper.getWritableDatabase();
			
			// 書き込み用のデータ生成
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.FIELD_POSITION,info.Position);
			values.put(DatabaseHelper.FIELD_CONTENTS,info.Contents);
			values.put(DatabaseHelper.FIELD_CREATE_DATE,info.CreateDate);
			values.put(DatabaseHelper.FIELD_UPDATE_DATE,info.UpdateDate);
			
			// インサート処理の実行
			db.insert(DatabaseHelper.TABLE_VOICE_JOURNAL,null,values);
		}finally{
			// 終了処理
			if(db != null){
				db.close();
			}
			if(helper != null){
				helper.close();
			}
		}
	}
	
	/**
	 * コンテンツを削除
	 * 
	 * @param cotext
	 * @param id
	 */
	public static void deleteJournalInfo(Context context, int id){
		DatabaseHelper helper = null;
		SQLiteDatabase db = null;
		try{
			// Helperクラスの生成
			helper = new DatabaseHelper(context);
			// 書き込み用SQLiteDatabaseを生成
			db = helper.getWritableDatabase();
			
			// インサート処理の実行
			db.delete(DatabaseHelper.TABLE_VOICE_JOURNAL,
					BaseColumns._ID + " = ?",
					new String[]{Integer.toString(id)});
		}finally{
			// 終了処理
			if(db != null){
				db.close();
			}
			if(helper != null){
				helper.close();
			}
		}
	}
}
