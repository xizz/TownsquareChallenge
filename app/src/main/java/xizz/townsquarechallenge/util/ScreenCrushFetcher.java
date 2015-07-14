package xizz.townsquarechallenge.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import xizz.townsquarechallenge.Application;
import xizz.townsquarechallenge.object.Article;
import xizz.townsquarechallenge.object.ArticleContent;
import xizz.townsquarechallenge.object.ContentImage;
import xizz.townsquarechallenge.object.ContentText;
import xizz.townsquarechallenge.object.ContentVideo;

public class ScreenCrushFetcher {
	private static final String TAG = ScreenCrushFetcher.class.getSimpleName();

	private static final String ARTICLES_URL =
			"http://screencrush.com/restapp/site/screencrush.com/uri/latest";

	public static List<Article> getArticleList() {
		JSONArray jsonArray = getArticlesJSONArray();
		List<Article> articles = new ArrayList<>();
		if (jsonArray == null)
			return articles;
		try {
			for (int i = 0; i < jsonArray.length(); ++i) {
				Article article = new Article();
				article.id = jsonArray.getJSONObject(i).getInt("id");
				article.title = jsonArray.getJSONObject(i).getString("title");
				article.date = jsonArray.getJSONObject(i).getString("date");
				article.url = jsonArray.getJSONObject(i).getString("url");
				article.imageUrl = jsonArray.getJSONObject(i).getString("thumbnail_guid");
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
			JSONObject contentJson = podContent.getJSONObject(i);
			JSONObject data = contentJson.getJSONObject("data");
			ArticleContent content = null;
			if (ArticleContent.TEXT.equals(contentJson.getString("type"))) {
				content = new ContentText(data.getString("text"));
			} else if (ArticleContent.IMAGE.equals(contentJson.getString("type"))) {
				content = new ContentImage(data.getString("thumbnail_guid"));
			} else if (ArticleContent.VIDEO.equals(contentJson.getString("type"))
					&& "video".equals(data.getString("type"))) {
				content = new ContentVideo(data.getString("thumbnail_url"), data.getString("url"));
			}
			if (content != null)
				contents.add(content);
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
			inputStream.close();
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
