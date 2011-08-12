package com.scarviz.yakomama;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 電卓処理メインクラス
 * 
 * @author scarviz
 * 
 */
public class Main extends Activity {
	// 数字ボタン情報
	private ArrayList<NumButtonInfo> mNumBtnArray;
	// 計算内容
	private EditText mTxtCalc;
	// 過去計算内容
	private EditText mTxtPastCalc;
	// 計算用配列
	private ArrayList<String> mCalcArray;
	// 計算結果の有効フラグ
	private boolean isEnabledResult = false;
	
    // 設定値領域用
    private SharedPreferences mPrefs;
	
	// 演算子
	private static final String DIV = "÷";
	private static final String MALT = "×";
	private static final String MINUS = "-";
	private static final String PLUS = "+";
	private static final String EQUAL = "=";
	// 改行コード
	private static final String NEWLINE = "\n";
	// 文字：0
	private static final String ZERO_TXT = "0";

	// 識別用コード
    private static final int REQUEST_CODE_BG = 1;
    
	/**
	 * 数字ボタン情報
	 * 
	 */
	public static class NumButtonInfo{
		int ID;		// リソースID
		int Num;	// 数字
		
		NumButtonInfo(int id,int num){
			this.ID = id;
			this.Num = num;
		}
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calc);
        
        // 設定領域から前回設定した背景URIを取得する
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bgUriTxt = mPrefs.getString("URI", null);
        
        // 前回設定した背景URIが存在する場合
        if(bgUriTxt != null){
        	Uri uri = Uri.parse(bgUriTxt);
			// 背景画像を設定する
			SetBackGroundImage(uri);
        }
        
        // 計算内容の表示用EditTextを取得する
        mTxtCalc = (EditText)findViewById(R.id.txtCalc);
        mTxtPastCalc = (EditText)findViewById(R.id.txtPastCalc);
        
        // 数字ボタン格納用
        mNumBtnArray = new ArrayList<NumButtonInfo>();
        
        // 数字ボタン情報を作成する
        mNumBtnArray.add(new NumButtonInfo(R.id.button0,0));
        mNumBtnArray.add(new NumButtonInfo(R.id.button1,1));
        mNumBtnArray.add(new NumButtonInfo(R.id.button2,2));
        mNumBtnArray.add(new NumButtonInfo(R.id.button3,3));
        mNumBtnArray.add(new NumButtonInfo(R.id.button4,4));
        mNumBtnArray.add(new NumButtonInfo(R.id.button5,5));
        mNumBtnArray.add(new NumButtonInfo(R.id.button6,6));
        mNumBtnArray.add(new NumButtonInfo(R.id.button7,7));
        mNumBtnArray.add(new NumButtonInfo(R.id.button8,8));
        mNumBtnArray.add(new NumButtonInfo(R.id.button9,9));
        
        // 計算用配列
        mCalcArray = new ArrayList<String>();
        
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
	    	case R.id.buttonClr:
	    		// 計算内容をクリアする
	    		mTxtCalc.setText(null);
	    		// 計算用配列をクリアする
	    		mCalcArray.clear();
	    		// 過去計算内容を改行する
	    		mTxtPastCalc.append(NEWLINE);
	    		// 計算結果を無効にする
	    		isEnabledResult = false;
	    		break;
	    	case R.id.buttonDel:
	    		// 計算内容の末尾を削除する
	    		DelEndCalcTxt();
	    		break;
	    	case R.id.buttonDiv:
	    		// 計算内容を追加する
	    		AddCalcTxt(DIV,DIV);
	    		break;
	    	case R.id.buttonMult:
	    		AddCalcTxt(MALT,MALT);
	    		break;
	    	case R.id.buttonMinus:
	    		AddCalcTxt(MINUS,MINUS);
	    		break;
	    	case R.id.buttonPlus:
	    		AddCalcTxt(PLUS,PLUS);
	    		break;
	    	case R.id.buttonEqual:
	    		AddCalcTxt(EQUAL,EQUAL);
	    		// 計算を実行
	    		String resultTxt = ExecCalculation();
	    		
	    		// 現在の計算内容として計算結果を表示する
	    		mTxtCalc.setText(resultTxt);
	    		// 計算用配列をクリアし、計算結果を格納する
	    		mCalcArray.clear();
	    		mCalcArray.add(resultTxt);
	    		// 過去計算内容に計算結果を表示し、改行する
	    		mTxtPastCalc.append(resultTxt + NEWLINE);
	    		
	    		// 表示されている計算結果は継続して利用するため有効とする
	    		isEnabledResult = true;
	    		break;
	    	default:
	    		for(NumButtonInfo btnInfo : mNumBtnArray){
	    			// 押されたボタンの数字と一致した場合
	    			if(btnInfo.ID == view.getId()){
	    				String numTxt = String.valueOf(btnInfo.Num);
	    	    		AddCalcTxt(numTxt,numTxt);
	    	    		// ループから抜ける
	    	    		break;
	    			}
	    		}
	    		break;
    	}
    }
    
    /**
     * ハードキーボタン押下時(Down)イベント
     * 
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// 各キーが押されたかどうか
    	boolean hasPushedKey = false;
    	
    	// メタキーの状態を取得する
    	int metaState = event.getMetaState();

    	// ALTが組み合わせて押された場合
    	if ((metaState & KeyEvent.META_ALT_ON) != 0) {
        	// メタキーと組み合わせるキーイベント実行処理
        	hasPushedKey = ExcuteKeyEventByMetaKey(keyCode, KeyEvent.META_ALT_ON);
    	}
    	// SHIFTが組み合わせて押された場合
    	else if((metaState & KeyEvent.META_SHIFT_ON) != 0){
        	// メタキーと組み合わせるキーイベント実行処理
        	hasPushedKey = ExcuteKeyEventByMetaKey(keyCode, KeyEvent.META_SHIFT_ON);
    	}
    	else{
	    	// キーイベント実行処理
	    	hasPushedKey = ExcuteKeyEvent(keyCode);
    	}
    	
    	// 各キーが押された場合は、基底クラスのキーイベントを呼ばない
    	if(hasPushedKey){
    		return false;
    	}
    	else{
    		return super.onKeyDown(keyCode, event);
    	}
    }
    
    /**
     * キーイベント処理。
     * 
     * @param keyCode
     * @return
     */
    private boolean ExcuteKeyEvent(int keyCode){
    	// 各キーが押されたかどうか
    	boolean hasPushedKey = true;
    	
    	switch(keyCode){
    	// CLEARキー押下時
    	case KeyEvent.KEYCODE_CLEAR:
    		// 計算内容をクリアする
    		mTxtCalc.setText(null);
    		// 計算用配列をクリアする
    		mCalcArray.clear();
    		// 過去計算内容を改行する
    		mTxtPastCalc.append(NEWLINE);
    		// 計算結果を無効にする
    		isEnabledResult = false;
    		break;
    	// DELキー押下時
    	case KeyEvent.KEYCODE_DEL:
    		// 計算内容の末尾を削除する
    		DelEndCalcTxt();
    		break;
    	// /キー押下時(÷)
    	case KeyEvent.KEYCODE_SLASH:
    		AddCalcTxt(DIV,DIV);
    		break;
    	// *キー押下時(×)
    	case KeyEvent.KEYCODE_STAR:
    		AddCalcTxt(MALT,MALT);
    		break;
    	// -キー押下時
    	case KeyEvent.KEYCODE_MINUS:
    		AddCalcTxt(MINUS,MINUS);
    		break;
    	// +キー押下時
    	case KeyEvent.KEYCODE_PLUS:
    		AddCalcTxt(PLUS,PLUS);
    		break;
    	// イコールキーまたはエンターキー押下時
    	case KeyEvent.KEYCODE_EQUALS:
    	case KeyEvent.KEYCODE_ENTER:
    		AddCalcTxt(EQUAL,EQUAL);
    		// 計算を実行
    		String resultTxt = ExecCalculation();
    		
    		// 現在の計算内容として計算結果を表示する
    		mTxtCalc.setText(resultTxt);
    		// 計算用配列をクリアし、計算結果を格納する
    		mCalcArray.clear();
    		mCalcArray.add(resultTxt);
    		// 過去計算内容に計算結果を表示し、改行する
    		mTxtPastCalc.append(resultTxt + NEWLINE);
    		
    		// 表示されている計算結果は継続して利用するため有効とする
    		isEnabledResult = true;
    		break;
    	// 数字キーの場合
    	case KeyEvent.KEYCODE_0:
    	case KeyEvent.KEYCODE_1:
    	case KeyEvent.KEYCODE_2:
    	case KeyEvent.KEYCODE_3:
    	case KeyEvent.KEYCODE_4:
    	case KeyEvent.KEYCODE_5:
    	case KeyEvent.KEYCODE_6:
    	case KeyEvent.KEYCODE_7:
    	case KeyEvent.KEYCODE_8:
    	case KeyEvent.KEYCODE_9:
    		// 数字キーのEnum値から7引いた値が、押された数値になる
			String numTxt = String.valueOf(keyCode - 7);
    		AddCalcTxt(numTxt,numTxt);
    		break;
    	default:
    		// 各キーは押されなかったのでfalseにする
    		hasPushedKey = false;
    		break;
    	}
    	
    	return hasPushedKey;
    }
    
    /**
     * メタキーと組み合わせるキーイベント処理。
     * 
     * @param keyCode
     * @param metaKey
     * @return
     */
    private boolean ExcuteKeyEventByMetaKey(int keyCode,int metaKey){
    	// 各キーが押されたかどうか
    	boolean hasPushedKey = true;
    	
    	switch(keyCode){
    	// /キー
    	case KeyEvent.KEYCODE_SLASH:
    		// ALT + / キー押下時(×)
    		if(metaKey == KeyEvent.META_ALT_ON){
        		AddCalcTxt(MALT,MALT);
    		}
    		break;
    	// -キー
    	case KeyEvent.KEYCODE_MINUS:    		
    		// SHIFT + - キー押下時(=)
    		if(metaKey == KeyEvent.META_SHIFT_ON){
        		AddCalcTxt(EQUAL,EQUAL);
        		// 計算を実行
        		String resultTxt = ExecCalculation();
        		
        		// 現在の計算内容として計算結果を表示する
        		mTxtCalc.setText(resultTxt);
        		// 計算用配列をクリアし、計算結果を格納する
        		mCalcArray.clear();
        		mCalcArray.add(resultTxt);
        		// 過去計算内容に計算結果を表示し、改行する
        		mTxtPastCalc.append(resultTxt + NEWLINE);
        		
        		// 表示されている計算結果は継続して利用するため有効とする
        		isEnabledResult = true;
    		}
    		break;
    	// Lキー
    	case KeyEvent.KEYCODE_L:
    		// ALT + L キー押下時(+)
    		if(metaKey == KeyEvent.META_ALT_ON){
        		AddCalcTxt(PLUS,PLUS);
    		}
    		break;
    	default:
    		// 各キーは押されなかったのでfalseにする
    		hasPushedKey = false;
    		break;
    	}
    	
    	return hasPushedKey;
    }

	/**
     * 計算内容を追加する。
     * 
     * @param calcTxt
     * @param pastTxt
     */
    private void AddCalcTxt(String calcTxt,String pastTxt){
    	// 追加内容がイコールの場合
    	if(calcTxt.equals(EQUAL)){
    		mTxtPastCalc.append(pastTxt);
    		return;
    	}
    	
		// 配列数を取得する
		int calcArrSize = mCalcArray.size();
	
		// 初めて配列に格納する場合
		if(calcArrSize == 0){
	    	// 追加内容が演算子の場合
	    	if(IsOperator(calcTxt)){
	    		// 演算子の前に"0"を格納する
	    		mCalcArray.add(ZERO_TXT);
	        	// 計算内容のテキストに"0"を追加
	    		mTxtCalc.append(ZERO_TXT);
	    		mTxtPastCalc.append(ZERO_TXT);
	    	}
    		// 計算用配列に追加する
    		mCalcArray.add(calcTxt);
    		
        	// 計算内容のテキストに追加
    		mTxtCalc.append(calcTxt);
    		mTxtPastCalc.append(pastTxt);
    		
    		return;
		}

		// 最後に格納した値を取得する
		String prevCalc = mCalcArray.get(calcArrSize - 1);
		
		// 最後に格納した値が演算子の場合
		if(IsOperator(prevCalc)){
	    	// 追加内容が演算子の場合(演算子を連続入力)
	    	if(IsOperator(calcTxt)){
	    			// トースト表示
	    			Toast.makeText(this, R.string.err_mes_001, Toast.LENGTH_LONG).show();
	    			// 計算内容を追加せず戻る
	    			return;
	    	}
    		
    		// 計算用配列に追加する
    		mCalcArray.add(calcTxt);
    	
		}
    	// 最後に格納した値が演算子でない場合(数字の場合)、かつ追加内容が演算子の場合
    	else if(IsOperator(calcTxt)){
    		// 前回の計算結果が有効の場合
    		if(isEnabledResult){
    	    	// 過去計算内容のテキストに追加
    			mTxtPastCalc.append(prevCalc);
    		}
    		// 計算用配列に追加する
    		mCalcArray.add(calcTxt);
    	}
    	// 最後に格納した値が演算子でない場合(数字の場合)、かつ追加内容が演算子でない場合(数字の連続入力場合)
    	else{
    		// 前回の計算結果が有効の場合
    		if(isEnabledResult){
    			// 最後に格納した値を置き換える
	    		mCalcArray.set(calcArrSize - 1, calcTxt);
	    		// 計算内容のテキストをクリアする
	    		mTxtCalc.setText(null);
    		}
    		else{
	    		// 最後に格納した値と、現在の追加内容の値をdouble型に変換する
	    		double prevNum = Double.parseDouble(prevCalc);
	    		double nowNum = Double.parseDouble(calcTxt);
	    		
	    		// 連続した数字の文字列を値として認識するため追加内容を下1桁に見立てる
	    		// 小数点以下が0の場合、表示される計算結果は整数部のみとする
	    		String nextNumTxt 
	    			= GetTextAscertainsDecimal(prevNum * 10 + nowNum);
	    		
	    		// 最後に格納した値を置き換える
	    		mCalcArray.set(calcArrSize - 1, nextNumTxt);
    		}
    	}
    	
    	// 計算内容のテキストに追加
		mTxtCalc.append(calcTxt);
		mTxtPastCalc.append(pastTxt);
		
		if(isEnabledResult){
			// 前回の計算結果を無効にする(新しく計算を行っているため)
			isEnabledResult = false;
		}
    }
    
    /**
     * 計算内容の末尾を削除する。
     * 
     */
    private void DelEndCalcTxt(){
    	// TODO : 作成中
    	/*
    	int calcArrSize = mCalcArray.size();
    	// 未入力の場合は何もしない
    	if(calcArrSize <= 0){return;}
		
    	// 計算用配列から削除する
		mCalcArray.remove(mCalcArray.size() - 1);
		
    	// 計算内容のテキストから削除する
		int txtCalcLen = mTxtCalc.length();
		String txt = mTxtCalc.getText().delete(txtCalcLen - 1 , txtCalcLen - 1).toString();
		mTxtCalc.setText(txt);
		int txtPastLen = mTxtPastCalc.length();
		mTxtPastCalc.setText(mTxtPastCalc.getText().delete(txtPastLen - 1 , txtPastLen - 1).toString());
		*/
    }
    
    /**
     * 演算子かどうか。
     * 
     * @param objectTxt 対象文字
     * @return 結果
     */
    private boolean IsOperator(String objectTxt){
    	// 結果用
    	boolean result = false;
    	
    	// 演算子の場合
    	if(objectTxt.equals(DIV) 
    			|| objectTxt.equals(MALT) 
				|| objectTxt.equals(MINUS) 
				|| objectTxt.equals(PLUS)){
    		// 結果をtrueとする
    		result = true;
    	}
    	
    	return result;
    }
    
    /**
     * 計算を実行する。
     * 
     * @return 計算結果
     */
    private String ExecCalculation(){
    	// 計算結果用
    	String resultNumTxt = ZERO_TXT;
    	
    	// 計算用配列の要素数を取得する
    	int calcArrSize = mCalcArray.size();

    	// 要素数が0の場合(イコールのみ入力)
    	if(calcArrSize == 0){
    		return resultNumTxt;
    	}
    	// 要素数が2以下の場合(数字のみ、または数字と演算子を格納してイコールを入力した場合)
    	else if(calcArrSize <= 2){
    		resultNumTxt = mCalcArray.get(0);
    		return resultNumTxt;
    	}
    	else{
    		// 加減算のみの配列用
    		ArrayList<String> addSubtArray = new ArrayList<String>();
    		// 元の配列の要素数カウント用
    		int orgCnt = 0;
    		// 計算時の値用
    		double aheadNum = 0.0;
    		double backNum = 0.0;
    		// 加減算配列の要素数用
    		int addSubtArrSize = 0;
    		
    		// カウントが計算用配列の要素数になるまでまわす
    		while(orgCnt < calcArrSize){
    			// 現在の計算用配列の値
    			String calcTxt = mCalcArray.get(orgCnt);
    			
    			// 乗除算の場合
    			if(calcTxt.equals(DIV) || calcTxt.equals(MALT)){
        			// 次に取得する値が存在しない場合(最後が演算子で終わっている場合)
        			if((orgCnt + 1) >= calcArrSize){
        				// ループから抜ける
        				break;
        			}
        			
        			// 加減算配列の要素数
        			addSubtArrSize = addSubtArray.size();
        			
    				// 加減算配列に最後格納した値を取得する
        			aheadNum = Double.parseDouble(addSubtArray.get(addSubtArrSize - 1));
    				// 計算用配列から次の値を取得する
    				backNum = Double.parseDouble(mCalcArray.get(orgCnt + 1));
    				
    				// 計算処理を行う
    				aheadNum = Calc(calcTxt,aheadNum,backNum);
    				
    				// 計算結果を最後に格納した値と置き換える
    				addSubtArray.set(addSubtArrSize - 1, String.valueOf(aheadNum));
    				
    				// 次の要素まで見ているので、その先に進める
    				orgCnt+=2;
    			}
    			else{
    				addSubtArray.add(calcTxt);
    				orgCnt++;
    			}
    		}

    		// 初期値を設定するので1からスタート
    		int cnt = 1;
    		// 初期化
    		aheadNum = Double.parseDouble(addSubtArray.get(0));
    		backNum = 0.0;
    		String operator = null;
    		addSubtArrSize = addSubtArray.size();
    		
    		// カウントが加減算配列の要素数になるまでまわす
    		while(cnt < addSubtArrSize){
    			// 次に取得する値が存在しない場合(最後が演算子で終わっている場合)
    			if((cnt + 1) >= addSubtArrSize){
    				break;
    			}
    			// 演算子を取得
    			operator = addSubtArray.get(cnt);
    			// 次の値を取得
    			backNum = Double.parseDouble(addSubtArray.get(cnt + 1));
    				
				// 計算処理を行う
    			aheadNum = Calc(operator,aheadNum,backNum);
				
				// 見終わった要素数分加算する
				cnt+=2;
    		}
    		
    		// 小数点以下が0の場合、表示される計算結果は整数部のみとする
    		resultNumTxt = GetTextAscertainsDecimal(aheadNum);
    		
    		// GCで優先的に解放する用
    		addSubtArray = null;
    	}
    	
    	return resultNumTxt;
    }
    
    /**
     * 計算処理。
     * aheadNum operator backNum として計算される。
     * 
     * @param operator 演算子
     * @param aheadNum 先の数字
     * @param backNum 後の数字
     * @return　計算結果
     */
    private double Calc(String operator ,double aheadNum, double backNum){
    	// 計算結果用
    	double resultNum = 0;
    	
    	// 演算子から計算処理を行う
    	if(operator.equals(DIV)){ resultNum = aheadNum / backNum;}
    	else if(operator.equals(MALT)){ resultNum = aheadNum * backNum;}
    	else if(operator.equals(MINUS)){ resultNum = aheadNum - backNum;}
    	else if(operator.equals(PLUS)){ resultNum = aheadNum + backNum;}
    	
    	return resultNum;
    }
    
    /**
     * 小数を見分けた文字列を取得する。
     * 
     * 小数以下が0の場合は、小数以下を切り捨てたものを返す。
     * 小数以下が0でない場合は、そのまま返す。
     * 
     * @param num 数値
     * @return 小数を見分けた文字列
     */
    private String GetTextAscertainsDecimal(double num){
    	// 結果用
    	String result = ZERO_TXT;
    	
    	// 整数部
    	int intPart = (int)num;
    	// 小数部
    	double decimalPart = num - intPart;
    	
    	// 小数部が0の場合
    	if(decimalPart == 0.0){
    		// 整数部のみ戻すようにする
    		result = String.valueOf(intPart);
    	}
    	else{
    		// そのまま戻すようにする
    		result = String.valueOf(num);
    	}
    	
    	return result;
    }
    
    /**
     * 退避情報を再格納する。
     * 
     */
    private void GetInstanceState(Bundle inState){
    	// 退避情報を取得する
    	String txtCalc = inState.getString("txtCalc");			// 計算内容
    	String txtPastCalc = inState.getString("txtPastCalc");	// 過去計算内容
    	
    	// 取得した値を再格納する
    	mTxtCalc.setText(txtCalc);			// 計算内容
    	mTxtPastCalc.setText(txtPastCalc);	// 過去計算内容
    	isEnabledResult = inState.getBoolean("isEnabledResult", false);	// 計算結果の有効フラグ
    	mCalcArray = inState.getStringArrayList("calcArray");			// 計算用配列
    }
    
    /**
     * 保持している情報を退避させる。
     * 
     */
    @Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 退避する値の取得
    	String txtCalc = mTxtCalc.getText().toString();			// 計算内容
    	String txtPastCalc = mTxtPastCalc.getText().toString();	// 過去計算内容
    	
    	// 情報を退避させる
		outState.putString("txtCalc", txtCalc);					// 計算内容
		outState.putString("txtPastCalc", txtPastCalc);			// 過去計算内容
		outState.putBoolean("isEnabledResult", isEnabledResult);// 計算結果の有効フラグ
		outState.putStringArrayList("calcArray", mCalcArray);	// 計算用配列
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
     * Activity終了時イベント。
     * 
     */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// GCで優先的に解放する用
		mNumBtnArray = null;
		mTxtCalc = null;
		mTxtPastCalc = null;
		mCalcArray = null;
		mPrefs = null;
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
	 * 背景画像を設定する。
	 * 
	 * @param uri
	 */
	private void SetBackGroundImage(Uri uri){
		Bitmap bmp = null;

		// 全体を囲っているLinearLayout
        LinearLayout mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
        
        // 背景URIがNULLの場合
        if(uri == null){
        	// 背景をリセットする
        	mainLayout.setBackgroundDrawable(null);
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
        mainLayout.setBackgroundDrawable(backGroundImg);
        
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
}