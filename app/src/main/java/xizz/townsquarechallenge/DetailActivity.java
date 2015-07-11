package xizz.townsquarechallenge;

import android.app.Fragment;
import android.os.Bundle;

public class DetailActivity extends SingleFragmentActivity {
	public static final String EXTRA_ARTICLE = "article";
	private static final String TAG = DetailActivity.class.getSimpleName();

	@Override
	protected Fragment createFragment() {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_ARTICLE, getIntent().getSerializableExtra(EXTRA_ARTICLE));
		Fragment fragment = new DetailFragment();
		fragment.setArguments(args);
		return fragment;
	}
}
