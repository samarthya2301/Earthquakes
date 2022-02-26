package com.samarthya.earthquakes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class ExtractEarthquakes {

	private ArrayList<Earthquake> earthquakes;
	private final SharedPreferences defaultSp;

	ExtractEarthquakes(SharedPreferences defaultSp) {
		this.defaultSp = defaultSp;
	}

	public ArrayList<Earthquake> init() {

		String jsonResponse = getJsonResponse();
		earthquakes = new ArrayList<>();

		try {

			JSONObject root = new JSONObject(jsonResponse);
			JSONArray features = root.getJSONArray("features");

			String mag, direction, date, place, time, url;
			DecimalFormat decimalFormatter = new DecimalFormat("#0.0");

			for (int i = 0; i < features.length(); i++)
			{

				JSONObject eachEarthquake = features.getJSONObject(i);
				JSONObject properties = eachEarthquake.getJSONObject("properties");

				mag = decimalFormatter.format(properties.getDouble("mag"));

				if (properties.getString("place").contains(" of ")) {

					String[] fullPlace = properties.getString("place").split(" of ");
					direction = fullPlace[0] + " of";
					place = fullPlace[1];

				} else {

					direction = "0km of";
					place = properties.getString("place");

				}

				if (place.length() > 15) {
					place = place.substring(0, 15) + "...";
				}

				Date dateToDisplay = new Date(properties.getLong("time"));

				SimpleDateFormat formatter = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
				date = formatter.format(dateToDisplay);

				formatter = (SimpleDateFormat) SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
				time = formatter.format(dateToDisplay);

				url = properties.getString("url");

				if ( !place.equals("null")) {
					earthquakes.add(new Earthquake(mag, direction, place, date, time, url));
				}

			}

		} catch (JSONException e) {
			Log.d("error", "JSONException: " + e.getLocalizedMessage());
		} catch (Exception e) {
			Log.d("error", "Exception: " + e.getLocalizedMessage());
		}

		return earthquakes;

	}

	private String getJsonResponse() // throws IOException
	{

		String jsonResponse = "";
		InputStream inputStream;
		URL url;
		HttpURLConnection httpURLConnection = null;
		Scanner readFromInputStream = null;

		try {

			url = new URL(buildPreferencedUrl());

			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setReadTimeout(10000);
			httpURLConnection.setConnectTimeout(10000);
			httpURLConnection.connect();

			inputStream = httpURLConnection.getInputStream();
			readFromInputStream = new Scanner(inputStream);

			while (readFromInputStream.hasNext())
				jsonResponse = jsonResponse.concat(readFromInputStream.nextLine());

		} catch (IOException e) {
			Log.d("error", "IOException: " + e.getLocalizedMessage());
			e.printStackTrace();
		} catch (Exception e) {
			Log.d("error", e.getLocalizedMessage() + "");
			e.printStackTrace();
		} finally {

			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}

			if (readFromInputStream != null) {
				readFromInputStream.close();
			}

		}

		return jsonResponse;

	}

	private String buildPreferencedUrl() {

		String baseUrl = "https://earthquake.usgs.gov/fdsnws/event/1/query?";
		Uri baseUri = Uri.parse(baseUrl);
		Uri.Builder uriBuilder = baseUri.buildUpon();

		String order_by, earthquake_limit, min_mag, starttime, endtime;
		starttime = defaultSp.getString("starttime", "2022-01-01");
		endtime = defaultSp.getString("endtime", "2022-02-01");
		min_mag = String.valueOf(defaultSp.getInt("min_mag" , 3));
		earthquake_limit = defaultSp.getString("earthquake_limit", "20");
		order_by = defaultSp.getString("order_by", "time");

		uriBuilder.appendQueryParameter("format", "geojson");
		uriBuilder.appendQueryParameter("starttime", starttime);
		uriBuilder.appendQueryParameter("endtime", endtime);
		uriBuilder.appendQueryParameter("minmagnitude", min_mag);
		uriBuilder.appendQueryParameter("maxmagnitude", "11");
		uriBuilder.appendQueryParameter("limit", earthquake_limit);
		uriBuilder.appendQueryParameter("orderby", order_by);

		return uriBuilder.toString();

	}

	public void openWebsiteIntent(@NonNull View view, int indexSelected) {

		Intent openWebPage = new Intent(Intent.ACTION_VIEW);
		Uri urlToOpen = Uri.parse(earthquakes.get(indexSelected).url);
		openWebPage.setData(urlToOpen);
		view.getContext().startActivity(openWebPage);

	}

}
