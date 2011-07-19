package com.scarviz.yakomama;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 電卓処理メインクラス
 * 
 * @author scarviz
 *
 * TODO :
 *  ・計算用配列には、次の文字(数字→演算子、または演算子→数字)を入力したタイミングで、現在の値を格納する。
 *  →数字の加工が不要になる？1文字ごとにメソッドを呼ばなくてすむ？
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
	    		// 最後に格納した値と、現在の追加内容の値をint型に変換する
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
	}
    
}