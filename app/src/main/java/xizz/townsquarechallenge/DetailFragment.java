package xizz.townsquarechallenge;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import xizz.townsquarechallenge.object.Article;
import xizz.townsquarechallenge.object.ArticleContent;
import xizz.townsquarechallenge.object.ContentImage;
import xizz.townsquarechallenge.object.ContentText;

public class DetailFragment extends Fragment {
	private static final String TAG = DetailFragment.class.getSimpleName();
	private static final ViewGroup.LayoutParams LAYOUT_PARAMS = new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

	private LinearLayout layout;
	private ThumbnailDownloader<ImageView> thumbnailThread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");

		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		thumbnailThread = new ThumbnailDownloader<>(new Handler(), size.x);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView()");

		View v = inflater.inflate(R.layout.fragment_detail, container, false);
		layout = (LinearLayout) v.findViewById(R.id.article_detail);

		Article article = (Article) getArguments().getSerializable(DetailActivity.EXTRA_ARTICLE);
		Log.d(TAG, "article from argument: " + article);
		new FetchArticleTask().execute(article);
		return v;
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

	private void reloadDisplay(Article article) {
		layout.removeAllViews();

		TextView titleView = new TextView(getActivity());
		titleView.setText(Html.fromHtml(article.title));
		titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
		titleView.setTypeface(null, Typeface.BOLD);
		layout.addView(titleView);

		TextView authorDateView = new TextView(getActivity());
		StringBuilder authorDateStr = new StringBuilder();
		if (article.authors != null && article.authors.length > 0) {
			authorDateStr.append("by ");
			authorDateStr.append(article.authors[0]);
			for (int i = 1; i < article.authors.length; ++i) {
				authorDateStr.append(", ");
				authorDateStr.append(article.authors[i]);
			}
		}
		authorDateStr.append(" ");
		authorDateStr.append(article.date);
		authorDateStr.append("\n");
		authorDateView.setText(authorDateStr);
		layout.addView(authorDateView);

		addContentViews(getActivity(), layout, article.contents);
	}

	private void addContentViews(
			Context context, LinearLayout layout, ArticleContent[] contents) {
		for (ArticleContent content : contents) {
			if (content instanceof ContentText) {
				ContentText contentText = (ContentText) content;
				TextView textView = new TextView(context);
				textView.setText(Html.fromHtml(contentText.text));
				layout.addView(textView);
			} else if (content instanceof ContentImage) {
				final ContentImage contentImage = (ContentImage) content;
				final ImageView imageView = new ImageView(context);
//				new AsyncTask<Void, Void, Bitmap>() {
//					@Override
//					protected Bitmap doInBackground(Void... params) {
//						try {
//							byte[] bitmapBytes = ScreenCrushFetcher.getUrlBytes(contentImage.url);
//							return BitmapFactory
//									.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
//						} catch (IOException e) {
//							Log.e(TAG, "Error: " + e.getMessage());
//						}
//						return null;
//					}
//
//					@Override
//					protected void onPostExecute(Bitmap bitmap) {
//						imageView.setImageBitmap(bitmap);
//					}
//				}.execute();
				thumbnailThread.queueThumbnail(imageView, contentImage.url);
				layout.addView(imageView);
			}
		}
	}

	private class FetchArticleTask extends AsyncTask<Article, Void, Article> {
		@Override
		protected Article doInBackground(Article... params) {
			Article article = params[0];
			if (article.authors == null || article.contents == null)
				return ScreenCrushFetcher.getArticleDetail(params[0]);
			else
				return article;
		}

		@Override
		protected void onPostExecute(Article article) {
			reloadDisplay(article);
		}
	}
}
