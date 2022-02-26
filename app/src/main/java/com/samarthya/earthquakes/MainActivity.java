package com.samarthya.earthquakes;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
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

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

	private ArrayList<Earthquake> earthquakes;
	private RecyclerView recyclerView;
	private CustomAdapter customAdapter;
	private LinearLayoutManager linearLayoutManager;
	private ProgressDialog progressDialog;
	private SimpleDateFormat sdf;
	public SharedPreferences defaultSharedPref;
	private SharedPreferences.Editor editor;
	private boolean settingFromDate;

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
		editor = defaultSharedPref.edit();
		settingFromDate = true;

		progressDialog = new ProgressDialog(this);
		progressDialog.show();
		progressDialog.setContentView(R.layout.progress_dialog_layout);

		Date todaysDate = Calendar.getInstance().getTime();
		Date earlyDate = thirtyDaysEarlier(todaysDate);
		sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		saveDatePreference(todaysDate, true);
		saveDatePreference(earlyDate, false);

		callExecution();

	}

	private void callExecution() {

		ExecutorService exec = Executors.newSingleThreadExecutor();
		Handler handler = new Handler(Looper.getMainLooper());

		exec.execute(() -> {

			// networking
			ExtractEarthquakes extract = new ExtractEarthquakes(defaultSharedPref);
			earthquakes = extract.init();

			// ui updation
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
	
		if (id == R.id.action_calendar) {

			DialogFragment datePickerFragment = new DatePickerFragment();
			datePickerFragment.show(getSupportFragmentManager(), "Date Picker");
			showToast("Select Starting Date");

		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDateSet(DatePicker datePicker, int year, int month, int day) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);

		Date date = calendar.getTime();

		if (settingFromDate) {

			saveDatePreference(date, false);
			settingFromDate = false;

			DialogFragment datePickerFragment = new DatePickerFragment();
			datePickerFragment.show(getSupportFragmentManager(), "Date Picker");
			showToast("Select Ending Date");

		} else {

			progressDialog.show();
			saveDatePreference(date, true);
			settingFromDate = true;
			callExecution();

		}

	}

	private void showToast(String message) {

		View toastView = getLayoutInflater()
				.inflate(R.layout.toast_layout, findViewById(R.id.toastLinearLayout));

		TextView tvToast = toastView.findViewById(R.id.tvToast);
		tvToast.setText(message);

		Toast toast = new Toast(this);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(toastView);
		toast.setGravity(Gravity.BOTTOM, 0, 40);
		toast.show();

	}

	private Date thirtyDaysEarlier(Date date) {

		Date early = new Date();
		early.setTime(date.getTime() - 30L * 86400000);
		return early;

	}

	private void saveDatePreference(Date date, boolean isEnd) {

		String strDate = sdf.format(date);

		if (isEnd) {
			editor.putString("endtime", strDate);
		} else {
			editor.putString("starttime", strDate);
		}

		editor.apply();

	}

}
