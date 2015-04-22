package kz.kalihanovm.android.sozdik;

import android.os.AsyncTask;
import android.util.Log;

public class DictionaryTranslateTask extends AsyncTask <String, Void, String> {

	private final static String LOG_TAG = "Sozdik:DTTask";

	@Override
	protected String doInBackground(String... props) {

		String res = DictionaryTranslate.getData(props[0], props[1]);

		if (BuildConfig.DEBUG) {
			Log.i(LOG_TAG, "doInBackground: " + res);
		}

		return res;
	}
}
