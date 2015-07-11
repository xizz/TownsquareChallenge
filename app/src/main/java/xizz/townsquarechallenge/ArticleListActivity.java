package xizz.townsquarechallenge;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import xizz.townsquarechallenge.object.Article;

public class ArticleListActivity extends SingleFragmentActivity
		implements ArticleListFragment.Callbacks {

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_masterdetail;
	}

	@Override
	protected Fragment createFragment() {
		return new ArticleListFragment();
	}

	@Override
	public void onArticleSelected(Article article) {
		if (findViewById(R.id.detailFragmentContainer) == null) {
			// start an new activity
			Intent intent = new Intent(this, DetailActivity.class);
			intent.putExtra(DetailActivity.EXTRA_ARTICLE, article);
			startActivity(intent);
		} else {
			// load fragment to the right side of current activity
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

			Fragment oldDetail = fragmentManager.findFragmentById(R.id.detailFragmentContainer);
			Fragment newDetail = new DetailFragment();
			Bundle args = new Bundle();
			args.putSerializable(DetailActivity.EXTRA_ARTICLE, article);
			newDetail.setArguments(args);

			if (oldDetail != null) {
				fragmentTransaction.remove(oldDetail);
			}
			fragmentTransaction.add(R.id.detailFragmentContainer, newDetail);
			fragmentTransaction.commit();
		}
	}
}
