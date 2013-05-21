package polyu.comp.mps.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import polyu.comp.mps.R;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class LocationActivity extends Activity {

	private ListView locationListView;
	private String path;
	private List<HashMap<String, Object>> fileNameList;
	public SimpleAdapter mAdapter;
	private int distanceThreshold = 1000;
	private LocationManager mLocationManager;
	public Location curLocation;
	private static final String TAG = "Location Activity";  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent = getIntent();
		path = intent.getStringExtra("curPath");
		Log.i("LocationActivity", "intent gain!!");	
		TextView curPath = (TextView)findViewById(R.id.curPath);
		curPath.setText(path);
		locationListView = (ListView)findViewById(R.id.myListView);
		fileNameList = new ArrayList<HashMap<String, Object>>();		
		ProduceFileNameList(path);

		String[] fromColumns = {"imageIcon", "imageName","distance"};
        int[] toViews = {	R.id.imageIcon, R.id.imageName, R.id.distance}; 
        
		mAdapter = new SimpleAdapter(this,
				fileNameList,R.layout.location_list_item,fromColumns,toViews);
		locationListView.setAdapter(mAdapter);
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
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
		//double distance = calDistaceToImage(curFile.getAbsolutePath());
		double distance = calDistaceBetweenImages("/mnt/sdcard/PhotoShare/LocationImages/28.jpg",
													curFile.getAbsolutePath());
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
	
	private double calDistaceToImage(String image1){
		double distance = -1.0;
		try {
			ExifInterface eia = new ExifInterface(image1);
			String strImageLatitude = eia.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
			String strImageLongitude = eia.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
			
			Location imageLocation = new Location("base");
			if(strImageLatitude != null){
				Log.i("Location", strImageLatitude);
				double dLatitudeBase = LocStrToDouble(strImageLatitude);
				imageLocation.setLatitude(dLatitudeBase);
			}
			if(strImageLongitude != null){
				Log.i("Location", ""+ strImageLongitude);
				double dLongitudeBase = LocStrToDouble(strImageLongitude);
				imageLocation.setLongitude(dLongitudeBase);
			}
			if(strImageLatitude != null && strImageLongitude != null) {
				distance  = curLocation.distanceTo(imageLocation);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return distance;
	}
	
	private double calDistaceBetweenImages(String image1, String image2){
		double distance = -1.0;
		try {
			ExifInterface eia = new ExifInterface(image1);
			String strImageLatitudeBase = eia.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
			String strImageLongitudeBase = eia.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
			
			ExifInterface eib = new ExifInterface(image2);
			String strImageLatitudeCmp = eib.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
			String strImageLongitudeCmp = eib.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
			
			Location imageLocationBase = new Location("base");
			if(strImageLatitudeBase != null){
				Log.i("Location", strImageLatitudeBase);
				double dLatitudeBase = LocStrToDouble(strImageLatitudeBase);
				imageLocationBase.setLatitude(dLatitudeBase);
			}
			if(strImageLongitudeBase != null){
				Log.i("Location", ""+ strImageLongitudeBase);
				double dLongitudeBase = LocStrToDouble(strImageLongitudeBase);
				imageLocationBase.setLongitude(dLongitudeBase);
			}
			
			Location imageLocationCmp = new Location("Cmp");
			if(strImageLatitudeCmp != null){
				Log.i("Location", strImageLatitudeCmp);
				double dLatitudeCmp = LocStrToDouble(strImageLatitudeCmp);
				imageLocationCmp.setLatitude(dLatitudeCmp);
			}
			if(strImageLongitudeCmp != null){
				Log.i("Location", ""+ strImageLongitudeCmp);
				double dLongitudeCmp = LocStrToDouble(strImageLongitudeCmp);
				imageLocationCmp.setLongitude(dLongitudeCmp);
			}
			distance  = imageLocationBase.distanceTo(imageLocationCmp);
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
