package it.duccius.musicplayer;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class _ListItemActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_item, menu);
		return true;
	}

}
