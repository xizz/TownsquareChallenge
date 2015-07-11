package xizz.townsquarechallenge.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ThumbnailDownloader<T> extends HandlerThread {
	private static final String TAG = ThumbnailDownloader.class.getSimpleName();
	private static final int MESSAGE_DOWNLOAD = 0;
	Map<T, String> requestMap = Collections.synchronizedMap(new HashMap<T, String>());
	private Handler handler;
	private Handler responseHandler;
	private Listener<T> listener;
	private int size;

	public ThumbnailDownloader(Handler responseHandler, int size) {
		super(TAG);
		this.responseHandler = responseHandler;
		this.size = size;
	}

	public void setListener(Listener<T> listener) {
		this.listener = listener;
	}

	public void queueThumbnail(T token, String url) {
		Log.d(TAG, "Got an URL: " + url);
		requestMap.put(token, url);

		handler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();
	}

	public void clearQueue() {
		handler.removeMessages(MESSAGE_DOWNLOAD);
		requestMap.clear();
	}

	@Override
	protected void onLooperPrepared() {
		Log.d(TAG, "onLooperPrepared()");
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == MESSAGE_DOWNLOAD) {
					T t = (T) msg.obj;
					Log.d(TAG, "Got a request for url: " + requestMap.get(t));
					handleRequest(t);
				}
			}
		};
	}

	private void handleRequest(final T token) {
		try {
			final String url = requestMap.get(token);
			if (url == null)
				return;

			byte[] bitmapBytes = ScreenCrushFetcher.getUrlBytes(url);

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length, options);
			options.inSampleSize = BitmapResizer.calculateInSampleSize(options, size, size);
			options.inJustDecodeBounds = false;
			final Bitmap bitmap =
					BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length, options);
			Log.d(TAG, "Bitmap created");

			responseHandler.post(new Runnable() {
				@Override
				public void run() {
					if (!url.equals(requestMap.get(token)))
						return;
					requestMap.remove(token);
					listener.onThumbnailDownloaded(token, bitmap);
				}
			});
		} catch (IOException e) {
			Log.w(TAG, "Downloading image failed: ", e);
		}
	}

	public interface Listener<T> {
		void onThumbnailDownloaded(T handle, Bitmap thumbnail);
	}
}
