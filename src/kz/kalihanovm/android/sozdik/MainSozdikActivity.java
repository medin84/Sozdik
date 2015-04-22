package kz.kalihanovm.android.sozdik;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;


public class MainSozdikActivity extends Activity implements OnClickListener, OnKeyListener {

	private MainSozdikActivity sozdikContext;

	private final String LOG_TAG = "SOZDIK";
	private static final String APP_PREFERENCES = "SozdikPrefs";
	private final static String LOCALE_RU = "ru";
	private final static String LOCALE_KK = "kk";

	private Vibrator vibe;
	private InputMethodManager imm;
	private SharedPreferences prefs;
	private DictionaryTranslateTask dtt;

	private AdView adView;
	private WebView webView;
	private EditText etTrTxt;
	private Button btnLangReverse;
	private Button btnClear;
	private Button btnTranslate;
	private LinearLayout tableRow;

	private String direction = LOCALE_RU + "/" + LOCALE_KK;
	private String[] kazSymbols = {};
	private HashMap <Integer, String> kazSList;


	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SuppressLint({ "SetJavaScriptEnabled", "WrongViewCast" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_sozdik);

		sozdikContext = this;

		prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
		vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		webView =         (WebView) findViewById(R.id.wvResult);
		etTrTxt =         (EditText) findViewById(R.id.etTrSrc);
		btnTranslate =    (Button) findViewById(R.id.btnTranslate);
		btnLangReverse =  (Button) findViewById(R.id.btnLangReverse);
		btnClear =        (Button) findViewById(R.id.btnClear);
		tableRow =        (LinearLayout) findViewById(R.id.tableRowKazSymbols);

		//
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(1, 1, 1, 1);

		kazSList = new HashMap();
		kazSymbols = getResources().getStringArray(R.array.kaz_symbol);
		for(String key : kazSymbols){
			kazSList.put(key.hashCode(), key);

			Button btnKey = new Button(this);
			btnKey.setId(key.hashCode());
			btnKey.setText(key);

			tableRow.addView(btnKey, params);
			btnKey.setOnClickListener(this);
		}

		etTrTxt.setOnKeyListener(this);
		btnTranslate.setOnClickListener(this);
		btnLangReverse.setOnClickListener(this);
		btnClear.setOnClickListener(this);

		//
		webView.getSettings().setJavaScriptEnabled(true);
		//webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.addJavascriptInterface(new SozdikJSInterface(), "sozdik");
		webView.loadUrl("file:///android_asset/TranslateOutput.html");

		webView.setWebViewClient(new WebViewClient(){
	        @Override
	        public void onPageFinished(WebView view, String url) {
	            super.onPageFinished(view, url);
	            webView.scrollTo(0, 0);
	        }
	    });

		//adMob
		LinearLayout llAd = (LinearLayout) findViewById(R.id.llAdView);
		adView = new AdView(this, AdSize.BANNER, "TEST_DEVICE_ID");
		AdRequest adRequest = new AdRequest();
		adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
		adRequest.addTestDevice("TEST_DEVICE_ID");
		adRequest.isTestDevice(this);
    	llAd.addView(adView);
    	adView.loadAd(adRequest);

	    //
	    if(! isNetworkConnected()){
			Toast.makeText(getBaseContext(), getResources().getString(R.string.msg_no_internet), Toast.LENGTH_LONG).show();
		}

	    // saved state
	    btnLangReverse.setText(prefs.getString("direction", direction));
	    etTrTxt.setText(prefs.getString("etTrTxt", ""));
	}

	@Override
	protected void onPause() {
		super.onPause();

		Editor prefsEditor = prefs.edit();
		prefsEditor.putString("direction", direction);
		prefsEditor.putString("etTrTxt", etTrTxt.getText().toString());
		prefsEditor.commit();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main_sozdik, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem){
		exitApp();
		return true;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnClear:
			vibe.vibrate(25);
			etTrTxt.setText("");
			break;

		case R.id.btnLangReverse:
			vibe.vibrate(25);
			if(direction.equals(LOCALE_RU + "/" + LOCALE_KK)){
				direction = LOCALE_KK + "/" + LOCALE_RU;
			} else {
				direction = LOCALE_RU + "/" + LOCALE_KK;
			}

			btnLangReverse.setText(direction);
			break;

		case R.id.btnTranslate:
			String etext = etTrTxt.getText().toString().trim();
	    	if(etext.length() > 0){
	    		vibe.vibrate(30);
	    		getDictionaryTranslate(direction, etext);
	    	}

			break;
		default:
			int id = v.getId();
			if(kazSList.containsKey(id)){
				vibe.vibrate(25);

				int st = etTrTxt.getSelectionStart();
				int end = etTrTxt.getSelectionEnd();
				String _et = etTrTxt.getText().toString();
				int _etlen = _et.length();

				if(_etlen > st){
					_et = _et.substring(0, st) + kazSList.get(id) + _et.substring(end, _etlen);
					etTrTxt.setText(_et);
					etTrTxt.setSelection(st + 1);
				} else {
					etTrTxt.append(kazSList.get(id));
				}
			}

			break;
		}
	}

	// kk keyboard key
	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event) {

		switch(view.getId()){
		case R.id.etTrSrc:
			if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
				String etext = etTrTxt.getText().toString().trim();

				if(etext.length() > 0){
					getDictionaryTranslate(direction, etext);
				}
			}
			break;
		}

		return false;
	}

	@Override
	public void onDestroy() {
		adView.destroy();
	    super.onDestroy();
	}

	// SozdikJSInterface
	final class SozdikJSInterface {

		SozdikJSInterface() {
			
		}

        public void translate(String direction, String word) {
        	sozdikContext.getDictionaryTranslate(direction, word);
        }

        public void webViewScrollTo(int x, int y) {
    		webView.scrollTo(x, y);
    	}
    }

	// isNetworkConnected
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return (ni != null);
	}

	//
	public void exitApp(){
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	//
	public void getDictionaryTranslate(String direction, String word) {

		try {
			if(! isNetworkConnected()){
				Toast.makeText(getBaseContext(), getResources().getString(R.string.msg_no_internet), Toast.LENGTH_LONG).show();
				return;
			}

			Toast.makeText(getBaseContext(), direction + ", " + word, Toast.LENGTH_SHORT).show();

			this.direction = direction;
		    imm.hideSoftInputFromWindow(etTrTxt.getWindowToken(), 0);

			String[] props = {direction, word};
			dtt = new DictionaryTranslateTask();
			dtt.execute(props);

			String resp = dtt.get();

			if(resp.length() == 0){
				resp = "{article: '" + getResources().getString(R.string.error_exception) + "', history: \" \"}";
			} else {
				resp = resp.replace("\"", "\\\"");
			}

			webView.loadUrl("javascript:(function() { showResult(\"" + resp + "\"); })()");
		} catch (Exception e) {
			Log.e(LOG_TAG, "getDictionaryTranslate", e);
		}
	}

}
