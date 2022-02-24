package com.samarthya.earthquakes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

	private ArrayList<Earthquake> earthquakes;
	private RecyclerView recyclerView;
	private CustomAdapter customAdapter;
	private LinearLayoutManager linearLayoutManager;
	private ProgressDialog progressDialog;
	public SharedPreferences defaultSharedPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		earthquakes = new ArrayList<>();
		recyclerView = findViewById(R.id.recyclerView);
		customAdapter = new CustomAdapter(earthquakes, null);
		linearLayoutManager = new LinearLayoutManager(MainActivity.this);
		recyclerView.setAdapter(customAdapter);
		recyclerView.setLayoutManager(linearLayoutManager);
		defaultSharedPref  = PreferenceManager.getDefaultSharedPreferences(this);

		progressDialog = new ProgressDialog(this);
		progressDialog.show();
		progressDialog.setContentView(R.layout.progress_dialog_layout);

		ExecutorService exec = Executors.newSingleThreadExecutor();
		Handler handler = new Handler(Looper.getMainLooper());
		Date todaysDate = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

		exec.execute(() -> {

			// do networking here
			ExtractEarthquakes extract = new ExtractEarthquakes(defaultSharedPref);
			earthquakes = extract.init();
			String stringDate = sdf.format(todaysDate);

			// update ui here
			handler.post(() -> updateUi(extract, earthquakes.size()));

		});

	}

	private void updateUi(ExtractEarthquakes extract, int dataSize) {

		if (dataSize == 0) {

			TextView tvEmpty = findViewById(R.id.tvEmpty);
			ImageView ivEmpty = findViewById(R.id.ivEmpty);

			tvEmpty.setVisibility(View.VISIBLE);
			ivEmpty.setVisibility(View.VISIBLE);
			recyclerView.setVisibility(View.GONE);

			progressDialog.hide();

			return;

		}

		customAdapter = new CustomAdapter(earthquakes, extract);

		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration
				(recyclerView.getContext(),linearLayoutManager.getOrientation());

		customAdapter.notifyItemChanged(0);
		recyclerView.addItemDecoration(dividerItemDecoration);
		recyclerView.setAdapter(customAdapter);

		progressDialog.hide();

	}

	@Override
	protected void onPause() {

		super.onPause();
		progressDialog.dismiss();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.action_bar_menu, menu);
		return true;
		
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		
		int id = item.getItemId();
		
		if (id == R.id.action_settings) {

			Intent settingsActivityIntent =
					new Intent(this, com.samarthya.earthquakes.SettingsActivity.class);
			startActivity(settingsActivityIntent);

			return true;

		}
		
		return super.onOptionsItemSelected(item);
	}
}
