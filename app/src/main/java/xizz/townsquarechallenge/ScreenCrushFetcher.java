package xizz.townsquarechallenge;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import xizz.townsquarechallenge.object.Article;
import xizz.townsquarechallenge.object.ArticleContent;
import xizz.townsquarechallenge.object.ContentImage;
import xizz.townsquarechallenge.object.ContentText;

public class ScreenCrushFetcher {
	private static final String TAG = ScreenCrushFetcher.class.getSimpleName();

	private static final String ARTICLES_URL =
			"http://screencrush.com/restapp/site/screencrush.com/uri/latest";

	public static List<Article> getArticleList() {
		JSONArray jsonArray = getArticlesJSONArray();
		List<Article> articles = new ArrayList<>();
		try {
			for (int i = 0; i < jsonArray.length(); ++i) {
				Article article = new Article();
				article.id = jsonArray.getJSONObject(i).getInt("id");
				article.title = jsonArray.getJSONObject(i).getString("title");
				article.date = jsonArray.getJSONObject(i).getString("date");
				article.url = jsonArray.getJSONObject(i).getString("url");
				article.imageUrl = jsonArray.getJSONObject(i).getString("thumbnail");
				article.jsonUrl = article.url.replace("http://screencrush.com/",
						"http://screencrush.com/restapp/site/screencrush.com/uri/");
				articles.add(article);
				Log.d(TAG, "article ID: " + article.id);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Log.d(TAG, articles.size() + " received");
		return articles;
	}

	public static byte[] getUrlBytes(String urlSpec) throws IOException {
		URL url = new URL(urlSpec);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = connection.getInputStream();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
				return new byte[0];

			int bytesRead;
			byte[] buffer = new byte[1024];
			while ((bytesRead = in.read(buffer)) > 0)
				out.write(buffer, 0, bytesRead);
			out.close();
			return out.toByteArray();
		} finally {
			connection.disconnect();
		}
	}

	public static Article getArticleDetail(Article article) {
		String jsonStr = fetchJSONString(article.jsonUrl);
		if (jsonStr != null && !jsonStr.startsWith("{")) {
			Log.w(TAG, "Did not get JSON from server, using local copy for article: "
					+ article.jsonUrl);
			jsonStr = Application.sArticles.get(article.id);
		}
		Log.d(TAG, "JSON: " + jsonStr);
		try {
			JSONObject result = new JSONObject(jsonStr);
			JSONObject articleJSON = result
					.getJSONObject("gizmo")
					.getJSONObject("gizmos")
					.getJSONObject("single/singleGizmo")
					.getJSONObject("data");
			JSONArray authors = articleJSON.getJSONArray("authors");
			article.authors = new String[authors.length()];
			for (int i = 0; i < authors.length(); ++i) {
				article.authors[i] = authors.getJSONObject(i).getString("name");
				Log.d(TAG, "Author: " + article.authors[i]);
			}
			JSONArray podContent = articleJSON.getJSONArray("podContent");
			article.contents = getArticleContents(podContent);
		} catch (JSONException e) {
			Log.e(TAG, "Error parsing JSON: " + e.getMessage());
		}
		return article;
	}

	private static ArticleContent[] getArticleContents(JSONArray podContent) throws JSONException {
		ArrayList<ArticleContent> contents = new ArrayList<>();
		for (int i = 0; i < podContent.length(); ++i) {
			JSONObject jsonObject = podContent.getJSONObject(i);
			if (ArticleContent.TEXT.equals(jsonObject.getString("type"))) {
				ContentText content =
						new ContentText(jsonObject.getJSONObject("data").getString("text"));
				Log.d(TAG, content.text);
				contents.add(content);
			} else if (ArticleContent.IMAGE.equals(jsonObject.getString("type"))) {
				ContentImage content =
						new ContentImage(jsonObject.getJSONObject("data").getString("thumbnail"));
				Log.d(TAG, content.url);
				contents.add(content);
			}

		}
		return contents.toArray(new ArticleContent[contents.size()]);
	}

	private static JSONArray getArticlesJSONArray() {
		String jsonStr = fetchJSONString(ARTICLES_URL);
		if (jsonStr != null && !jsonStr.startsWith("{")) {
			Log.w(TAG, "Did not get JSON from server, using local copy for articles: "
					+ ARTICLES_URL);
			jsonStr = Application.sArticlesJsonString;
		}

		try {
			JSONObject result = new JSONObject(jsonStr);
			JSONArray articles = result
					.getJSONObject("gizmo")
					.getJSONObject("gizmos")
					.getJSONObject("row-standard")
					.getJSONArray("data");
			Log.d(TAG, articles.length() + " articles found");
			for (int i = 0; i < articles.length(); ++i)
				Log.d(TAG, "article title: " + articles.getJSONObject(i).getString("title"));
			return articles;
		} catch (JSONException e) {
			Log.e(TAG, "Error parsing JSON: " + e.getMessage());
		}

		return null;
	}

	private static String fetchJSONString(String urlStr) {
		StringBuilder jsonResult = new StringBuilder();
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "text/json");

			conn.connect();

			Log.d(TAG, "Response Code: " + conn.getResponseCode());
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				Log.e(TAG, "HTTP Response: " + conn.getResponseCode());
				return null;
			}

			InputStream inputStream = conn.getInputStream();
			if (inputStream == null)
				return null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = reader.readLine()) != null) {
				jsonResult.append(line);
				jsonResult.append("\n");
			}
			reader.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if (conn != null)
				conn.disconnect();
		}
		Log.d(TAG, "JSON: " + jsonResult);
		return jsonResult.toString();
	}
}
