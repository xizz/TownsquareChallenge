package xizz.townsquarechallenge;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

public abstract class FragmentActivity extends Activity {
	protected abstract Fragment createFragment();

	protected int getLayoutResId() {
		return R.layout.activity_fragment;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResId());
		FragmentManager manager = getFragmentManager();
		if (manager.findFragmentById(R.id.fragmentContainer) == null) {
			manager.beginTransaction().add(R.id.fragmentContainer, createFragment()).commit();
		}
	}
}
