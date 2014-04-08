package com.aic.compass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;

import android.R.string;
import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;

import com.google.android.gms.maps.model.LatLng;

public class DirectionActivity extends Activity{

	private LocationManager mLocationManager;
	private CSVReader reader;
	private List<Location> routePoints;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_direction);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		routePoints = new ArrayList<Location>();
		
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

	    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
	            0, mLocationListener);
	    
	    Log.e("CREATE", "IN CREATE");
	        
		try {
			/*File csvFolder = new File("/storage/emulated/0/", "airdroid/upload");
			Log.v("AIC", csvFolder.toString());*/
			File csvFile = new File("/system", "path" + ".csv");
			reader = new CSVReader(new FileReader(csvFile), ';');
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
		Log.e("LOG", "file read");
		
	    String[] nextLine;
	    try {
			while ((nextLine = reader.readNext()) != null) {
			    // nextLine[] is an array of values from the line
				String[] parts = nextLine[0].split(";");
				/*Log.e("DOUBLE", parts[0]);
				Log.e("DOUBLE1", parts[1]);*/
				LatLng loc = new LatLng(Double.parseDouble(parts[0]),
						Double.parseDouble(parts[1]));
				
				Location l = new Location("reverseGeocoded");
				l.setLatitude(loc.latitude);
				l.setLongitude(loc.longitude);
				routePoints.add(l);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	          
	}
	
	public void foo(View view){
			Log.e("LOG", "log\n");
			/*Location l1 = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(l1 != null)Log.e("LOG", "Get Location: \n" + l1.toString());
			else Log.e("LOG", "l1 is null");*/
			
			LatLng loc = new LatLng(Double.parseDouble("33.77794599"),
					Double.parseDouble("-84.40939124"));
			Location l1 = new Location("reverseGeocoded");
			l1.setLatitude(loc.latitude);
			l1.setLongitude(loc.longitude);
			displayRoutePoints();
			for (Location lo : routePoints) {
				Log.e("Calculation", l1.bearingTo(lo) + " " + l1.distanceTo(lo) + "\n");
			}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.direction, menu);
		return true;
	}
	
    private String displayRoutePoints(){
    	
    	String res = "";
    	
    	for (Location loc : routePoints) {
			res += loc.toString() + "  " + loc.getBearing() + "  " + "\n";
		}
    	return res;
    }

	@Override
	public void onDestroy() {
		// stop gps polling
		System.out.println("Stopping GPS poll");
		mLocationManager.removeUpdates(mLocationListener);

		// close csv file
		// write the location to the cvs file
		try {
			reader.close();

			/*
			 * // get the files in the internal storage
			 * 
			 * File fileList = getFilesDir(); if (fileList != null) { File[]
			 * filenames = fileList.listFiles(); for (File tmpf : filenames) {
			 * // Do something with the files System.out.println("file: " +
			 * tmpf.getName()); } }
			 */
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_direction,
					container, false);
			

			return rootView;
		}
	}
	
	private final LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
		
			Toast toast = Toast.makeText(getApplicationContext(),
					"GPS turned on. GPS tracking has started.",
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			
			TextView txtView = (TextView) findViewById(R.id.source);
			txtView.setText(Double.toString(location.getLatitude())
					+ ";" + Double.toString(location.getLongitude()) );
			
			TextView txtView1 = (TextView) findViewById(R.id.destination);
			txtView1.setText(displayRoutePoints());
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
	};
	

}
