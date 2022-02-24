package com.samarthya.earthquakes;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

	private final ArrayList<Earthquake> localDataSet;
	private static ExtractEarthquakes extract;

	public static class ViewHolder extends RecyclerView.ViewHolder {

		private final TextView mag, direction, place, date, time;
		private final ImageView magImageView;

		public ViewHolder(View view) {

			super(view);

			mag = view.findViewById(R.id.magnitude);
			direction = view.findViewById(R.id.direction);
			place = view.findViewById(R.id.place);
			date = view.findViewById(R.id.date);
			time = view.findViewById(R.id.time);
			magImageView = view.findViewById(R.id.circle);

			view.setOnClickListener(v -> {

				int listIndexSelected = getAdapterPosition();
				extract.openWebsiteIntent(view, listIndexSelected);

			});

		}

		public TextView getMagView() {
			return mag;
		}
		public TextView getDirectionView() {
			return direction;
		}
		public TextView getPlaceView() {
			return place;
		}
		public TextView getDateView() {
			return date;
		}
		public TextView getTimeView() {
			return time;
		}
		public ImageView getMagImageView() {
			return magImageView;
		}

	}

	public CustomAdapter(ArrayList<Earthquake> dataSet, ExtractEarthquakes extract) {

		localDataSet = dataSet;
		CustomAdapter.extract = extract;

	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

		View view = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.recycler_view_item_layout, viewGroup, false);

		return new ViewHolder(view);

	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, final int position) {

		Earthquake row = localDataSet.get(position);
		TextView mag, direction, place, date, time;
		ImageView magImageView = viewHolder.getMagImageView();

		mag = viewHolder.getMagView();
		direction = viewHolder.getDirectionView();
		place = viewHolder.getPlaceView();
		date = viewHolder.getDateView();
		time = viewHolder.getTimeView();

		mag.setText(row.mag);
		direction.setText(row.direction);
		place.setText(row.place);
		date.setText(row.date);
		time.setText(row.time);

		GradientDrawable magnitudeCircle = (GradientDrawable) magImageView.getDrawable();
		int magColor = getMagnitudeColor(row.mag, magImageView);
		magnitudeCircle.setColor(magColor);

	}

	@Override
	public int getItemCount() {
		return localDataSet.size();
	}

	private int getMagnitudeColor(String magnitude, View view) {

		int mag = (int) Double.parseDouble(magnitude);

		if (mag <= 1) {
			return view.getResources().getColor(R.color.magnitude1);
		}

		switch (mag) {

			case 2:
				return view.getResources().getColor(R.color.magnitude2);
			case 3:
				return view.getResources().getColor(R.color.magnitude3);
			case 4:
				return view.getResources().getColor(R.color.magnitude4);
			case 5:
				return view.getResources().getColor(R.color.magnitude5);
			case 6:
				return view.getResources().getColor(R.color.magnitude6);
			case 7:
				return view.getResources().getColor(R.color.magnitude7);
			case 8:
				return view.getResources().getColor(R.color.magnitude8);
			case 9:
				return view.getResources().getColor(R.color.magnitude9);
			default:
				return view.getResources().getColor(R.color.magnitude10);

		}

	}

}
