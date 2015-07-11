package xizz.townsquarechallenge;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import xizz.townsquarechallenge.object.Article;
import xizz.townsquarechallenge.util.ScreenCrushFetcher;
import xizz.townsquarechallenge.util.ThumbnailDownloader;

public class ArticleListFragment extends SwipeRefreshListFragment {

	private static final String TAG = ArticleListFragment.class.getSimpleName();

	private Callbacks callbacks;

	private ThumbnailDownloader<ImageView> thumbnailThread;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callbacks = (Callbacks) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		thumbnailThread = new ThumbnailDownloader<>(new Handler(), Application.ICON_SIZE);
		thumbnailThread.setListener(new ThumbnailDownloader.Listener<ImageView>() {
			@Override
			public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
				if (isVisible())
					imageView.setImageBitmap(thumbnail);
			}
		});
		thumbnailThread.start();
		thumbnailThread.getLooper();
		Log.d(TAG, "Background thread started");
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		new FetchArticlesTask().execute();
		setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				new FetchArticlesTask().execute();
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		thumbnailThread.clearQueue();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		thumbnailThread.quit();
		Log.d(TAG, "Background thread destroyed");
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Article article = (Article) l.getItemAtPosition(position);
		Log.d(TAG, article.title + " clicked");
		Log.d(TAG, "URL: " + article.url);
		Log.d(TAG, "JSON URL: " + article.jsonUrl);

		callbacks.onArticleSelected(article);
	}

	private void refreshList(List<Article> articles) {
		ArticleAdapter adapter = new ArticleAdapter(articles);
		setListAdapter(adapter);
		setRefreshing(false);
	}

	public interface Callbacks {
		void onArticleSelected(Article article);
	}

	private class ArticleAdapter extends ArrayAdapter<Article> {
		public ArticleAdapter(List<Article> articles) {
			super(getActivity(), android.R.layout.simple_list_item_1, articles);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater()
						.inflate(R.layout.list_item_article, parent, false);
			}
			final Article article = getItem(position);

			final ImageView iconView =
					(ImageView) convertView.findViewById(R.id.article_list_item_icon);
			iconView.setImageResource(R.drawable.blank);
			thumbnailThread.queueThumbnail(iconView, article.imageUrl);

			final TextView titleView =
					(TextView) convertView.findViewById(R.id.article_list_item_title);
			titleView.setText(Html.fromHtml(article.title));
			final TextView dateView =
					(TextView) convertView.findViewById(R.id.article_list_item_date);
			dateView.setText(article.date);

			return convertView;
		}
	}

	private class FetchArticlesTask extends AsyncTask<Void, Void, List<Article>> {
		@Override
		protected List<Article> doInBackground(Void... params) {
			return ScreenCrushFetcher.getArticleList();
		}

		@Override
		protected void onPostExecute(List<Article> articles) {
			refreshList(articles);
		}
	}
}
