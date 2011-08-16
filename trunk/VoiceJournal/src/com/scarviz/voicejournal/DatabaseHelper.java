package com.scarviz.voicejournal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHelper extends SQLiteOpenHelper {
	// DBデータ
	private static final String DATABASE_NAME = "voicejournaldatabase.db";
	private static final int DATABASE_VERSION = 1;
	
	// テーブル名
	public static final String TABLE_VOICE_JOURNAL = "VOICE_JOURNAL";
	// カラム名
	public static final String FIELD_POSITION = "POSITION";			// リスト位置
	public static final String FIELD_CONTENTS = "CONTENTS";			// コンテンツ内容
	public static final String FIELD_CREATE_DATE = "CREATE_DATE";	// 登録日
	public static final String FIELD_UPDATE_DATE = "UPDATE_DATE";	// 更新日
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// データベース処理開始
		db.beginTransaction();
		try{
			// テーブル作成を実行
			db.execSQL("CREATE TABLE " + TABLE_VOICE_JOURNAL
					+ " ("
					+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ FIELD_POSITION + " INTEGER,"
					+ FIELD_CONTENTS + " TEXT,"
					+ FIELD_CREATE_DATE + " TEXT,"
					+ FIELD_UPDATE_DATE + " TEXT"
					+ ");");
			
			// SQL処理を反映
			db.setTransactionSuccessful();
		}finally{
				// データベース処理終了
				db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// テーブルが存在する場合は削除する
		db.execSQL("DROP TABLE IF EXISTS DIARY");
		
		// テーブルを生成する
		onCreate(db);
	}

}
