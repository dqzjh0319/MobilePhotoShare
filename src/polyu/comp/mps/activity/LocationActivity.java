package polyu.comp.mps.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyu.comp.mps.R;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class LocationActivity extends Activity {

	private ListView locationListView;
	private String path;
	private List<Map<String, Object>> fileNameList;
	public SimpleAdapter mAdapter;
	private int distanceThreshold = 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.i("LocationActivity", "onCreate called!!");
		Intent intent = getIntent();
		path = intent.getStringExtra("curPath");
		Log.i("LocationActivity", "intent gain!!");		
		TextView curPath = (TextView)findViewById(R.id.curPath);
		curPath.setText(path);
		locationListView = (ListView)findViewById(R.id.myListView);
		double dist = calDistaceBetweenImages(
				"/mnt/sdcard/DCIM/test/28.jpg",
				"/mnt/sdcard/DCIM/test/29.jpg");
		Log.i("Location", ""+ dist);
		fileNameList = new ArrayList<Map<String, Object>>();		
		ProduceFileNameList(path);
		String[] fromColumns = {"imageIcon", "imageName","distance"};
        int[] toViews = {	R.id.imageIcon, R.id.imageName, R.id.distance}; // The TextView in simple_list_item_1

		mAdapter = new SimpleAdapter(this,
				fileNameList,R.layout.location_list_item,fromColumns,toViews);
		locationListView.setAdapter(mAdapter);
	}
	
	private void ProduceFileNameList(String dir) {
		fileNameList.clear();
		File curFile = new File(dir);
		File[] files = curFile.listFiles();
		if (files != null) {
			for (File file : files) {
				if(file.isDirectory())
					continue;
				else {
					HashMap<String, Object> tmpHM = ConstructSingleItem(file);
					if (tmpHM != null)
						fileNameList.add(tmpHM);
				}
			}
		}
	}
	
	private HashMap<String, Object> ConstructSingleItem(File curFile) {
		double distance = calDistaceBetweenImages("/mnt/sdcard/DCIM/test/28.jpg", curFile.getAbsolutePath());
		int intDist = (int)(distance/100.) + 1;
		if (intDist > distanceThreshold) {
			return null;
		}
		
		HashMap<String, Object> singleItem = new HashMap<String, Object>();
		String distInfo = null;
		if (intDist < 10) {
			distInfo = new String("Within "+intDist+"00m");
		}
		else {
			intDist /= 10;
			distInfo = new String("Within "+intDist+"km");
		}
		singleItem.put("distance", distInfo);
		singleItem.put("imageName", curFile.getName());
		singleItem.put("imageIcon", R.drawable.image_icon);
		singleItem.put("AbsPath", curFile.getAbsolutePath());
		return singleItem;
	}
	
	private double calDistaceBetweenImages(String image1, String image2){
		double distance = -1.0;
		try {
			ExifInterface eia = new ExifInterface(image1);
			ExifInterface eib = new ExifInterface(image2);
			String strLatitudeBase = eia.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
			String strLongitudeBase = eia.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
			String strLatitudeCmp = eib.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
			String strLongitudeCmp = eib.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
			
			Location imageBase = new Location("base");
			if(strLatitudeBase != null){
				Log.i("Location", strLatitudeBase);
				double dLatitudeBase = LocStrToDouble(strLatitudeBase);
				imageBase.setLatitude(dLatitudeBase);
			}
			if(strLongitudeBase != null){
				Log.i("Location", ""+ strLongitudeBase);
				double dLongitudeBase = LocStrToDouble(strLongitudeBase);
				imageBase.setLongitude(dLongitudeBase);
			}
			Location imageCmp = new Location("cmp");
			if(strLatitudeCmp != null){
				Log.i("Location", ""+ strLatitudeCmp);
				double dLatitudeCmp = LocStrToDouble(strLatitudeCmp);
				imageCmp.setLatitude(dLatitudeCmp);
			}
			if(strLongitudeCmp != null){
				Log.i("Location", ""+ strLongitudeCmp);
				double dLongitudeCmp = LocStrToDouble(strLongitudeCmp);
				imageCmp.setLongitude(dLongitudeCmp);
			}
			distance  = imageBase.distanceTo(imageCmp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return distance;
	}
	
	private double LocStrToDouble(String strLoc){
		String[] DMS = strLoc.split(",", 3);
		
		String[] stringD = DMS[0].split("/", 2);
		double D0 = Double.parseDouble(stringD[0]);
		double D1 = Double.parseDouble(stringD[1]);
	    double FloatD = D0/D1;

		String[] stringM = DMS[1].split("/", 2);
		double M0 = Double.parseDouble(stringM[0]);
		double M1 = Double.parseDouble(stringM[1]);
	    double FloatM = M0/M1;

		String[] stringS = DMS[2].split("/", 2);
		double S0 = Double.parseDouble(stringS[0]);
		double S1 = Double.parseDouble(stringS[1]);
	    double FloatS = S0/S1;
	    
		double result = FloatD + (FloatM/60) + (FloatS/3600);

		return result;
	}
}
