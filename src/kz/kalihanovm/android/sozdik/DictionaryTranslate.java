package kz.kalihanovm.android.sozdik;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;

import android.util.Log;

public class DictionaryTranslate {

	private final static String LOG_TAG = "Sozdik:DTRequest";
	private final static String URL_DICT_TRANS = "http://sozdik.kz/ru/dictionary/translate/";
	private final static String PARAM_AJAX = "/?tm=";

	public static String getData(String direction, String word) {

		String response = "";
		String _url = "";

		try {
			_url = URL_DICT_TRANS + direction + "/" + URLEncoder.encode(word, "UTF-8") + PARAM_AJAX + "" + new Date().getTime();
			URL url = new URL(_url);
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-Agent", "Opera/9.80 (Windows NT 6.1; MRA 6.0 (build 5680)) Presto/2.12.388 Version/12.12");

			HttpURLConnection httpConnection = (HttpURLConnection) connection;

			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = httpConnection.getInputStream();
				BufferedReader buffReader = new BufferedReader(new InputStreamReader(in));

				String line = "";
				while((line = buffReader.readLine()) != null){
					response += line;
				}

				response = new String(response.getBytes(), "UTF-8");
				buffReader.close();
			}

			if (BuildConfig.DEBUG) {
				Log.i(LOG_TAG, _url + "\n" + response);
			}
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, _url + "\n" + "MalformedURLException", e);
		} catch (IOException e) {
			Log.e(LOG_TAG, _url + "\n" + "IOException", e);
		}

		return response;
	}

}
