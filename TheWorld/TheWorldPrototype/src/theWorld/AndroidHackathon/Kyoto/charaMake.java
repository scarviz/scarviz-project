package theWorld.AndroidHackathon.Kyoto;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class charaMake extends BaseActivity implements OnClickListener {
	// 返ってきた時の認証用コード(数字は適当なもので良い)
	private static final int REQUEST_CODE_VOICE_RECO = 123;
	private static final String PROMPT_MES = null;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chara_make);

		// 各ボタンのリスナーを設定
		findViewById(R.id.button_select_stand).setOnClickListener(this);
		findViewById(R.id.button_select_power).setOnClickListener(this);
		findViewById(R.id.button_toSelectRival).setOnClickListener(this);

	}

	/**
	 * ボタン押下時イベント。
	 * 
	 */
	public void onClick(View view) {
		switch (view.getId()) {

		// スタンドを選択
		case R.id.button_select_stand:
			// Intent intent = new Intent(this, charaMake.class);
			// startActivity(intent);
			// finish();
			Log.i("aaa", "aa");
			super.StartVoiceRecognition(PROMPT_MES, REQUEST_CODE_VOICE_RECO);

			super.PlaySE();

			break;
		// パワーを検出
		case R.id.button_select_power:
			Intent intent = new Intent(this, voiceOraora.class);
			startActivity(intent);
			// finish();

			super.PlaySE();

			break;
		// 次へ進む
		case R.id.button_toSelectRival:
			Intent intent2 = new Intent(this, selectRival.class);
			startActivity(intent2);

			
			super.PlaySE();
			
			finish();
			break;
		}

	}

	/**
	 * アクティビティ終了時に結果を受け取る。
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 返った来たコードが認証用コードと一致する(自分で投げたインテント)、かつ、結果が正常の場合
		if (requestCode == REQUEST_CODE_VOICE_RECO && resultCode == RESULT_OK) {
			// 結果用
			String result = "";

			// 結果文字列を取得する
			ArrayList<String> resultList = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			// 類似する文字列を取得しているので、今回は最初の文字列を表示用として格納する
			result = resultList.get(0);

			// 結果をトーストで表示する
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();

			if (result.equals("the world")) {
				Log.i("stand", "the world");
				TextView a = (TextView) findViewById(R.id.stand_name);
				a.setText("スタンド：the world");

			} else if (result.equals("スタープラチナ")) {
				Log.i("stand", "スタープラチナ");
				TextView a = (TextView) findViewById(R.id.stand_name);
				a.setText("スタンド：スタープラチナ");
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}


}
