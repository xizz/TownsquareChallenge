package xizz.townsquarechallenge;

import android.util.TypedValue;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Application extends android.app.Application {
	public static String sArticlesJsonString;
	public static HashMap<Integer, String> sArticles = new HashMap<>();
	public static int ICON_SIZE;

	@Override
	public void onCreate() {
		super.onCreate();

		ICON_SIZE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				96, getResources().getDisplayMetrics());

		// Code below exist for providing demo json result when http request fails.
		BufferedReader reader;

		try {
			InputStream in = getResources().openRawResource(R.raw.articles);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
			reader.close();
			sArticlesJsonString = jsonString.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			InputStream in = getResources().openRawResource(R.raw.article01);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
			reader.close();
			sArticles.put(257396, jsonString.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			InputStream in = getResources().openRawResource(R.raw.article02);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
			reader.close();
			sArticles.put(257399, jsonString.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			InputStream in = getResources().openRawResource(R.raw.article03);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
			reader.close();
			sArticles.put(257386, jsonString.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			InputStream in = getResources().openRawResource(R.raw.article04);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
			reader.close();
			sArticles.put(256840, jsonString.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			InputStream in = getResources().openRawResource(R.raw.article05);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
			reader.close();
			sArticles.put(257129, jsonString.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			InputStream in = getResources().openRawResource(R.raw.article06);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
			reader.close();
			sArticles.put(257356, jsonString.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			InputStream in = getResources().openRawResource(R.raw.article07);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
			reader.close();
			sArticles.put(257358, jsonString.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			InputStream in = getResources().openRawResource(R.raw.article08);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
			reader.close();
			sArticles.put(257352, jsonString.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			InputStream in = getResources().openRawResource(R.raw.article09);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
			reader.close();
			sArticles.put(257329, jsonString.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			InputStream in = getResources().openRawResource(R.raw.article10);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}
			reader.close();
			sArticles.put(257324, jsonString.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
