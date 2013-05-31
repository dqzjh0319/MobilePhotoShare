package polyu.comp.mps.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import polyu.comp.mps.MyApplication;
import polyu.comp.mps.R;
import polyu.comp.mps.activity.MainActivity.myOnLVItemClickListener;
import polyu.comp.mps.util.LocationSharedUtil;
import polyu.comp.mps.util.LocationUtil;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class LocationActivity extends Activity {

	private ListView locationListView;
	private String path;
	private List<HashMap<String, Object>> fileNameList;
	public SimpleAdapter mAdapter;
	private int distanceThreshold = 1000;
	public Location curLocation;
	private static final String TAG = "Location Activity";
	private MyApplication myApp;
	private String sharedList;
	private DisplayImageOptions options;
	public ImageLoader imageLoader;
	private LocationUtil locationUtil;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageLoader = ImageLoader.getInstance();
		locationUtil = new LocationUtil(LocationActivity.this);
		options = new DisplayImageOptions.Builder()
										.showStubImage(R.drawable.unloaded_icon)
										.showImageForEmptyUri(R.drawable.image_icon)
										.showImageOnFail(R.drawable.image_icon)
										.resetViewBeforeLoading()
										.cacheInMemory()
										.cacheOnDisc()
										.displayer(new RoundedBitmapDisplayer(20))
										.build();
		myApp = (MyApplication) getApplication();
		String url = "http://"+ myApp.getIPAddr() +":8080/MPServer/photoAction!locatePhoto?photo.location="+locationUtil.getLocationInfo();
		sharedList = LocationSharedUtil.getJson(url);
		//Toast.makeText(this, sharedList, 5000).show();
		fileNameList = new ArrayList<HashMap<String, Object>>();
		DecodeAddUrlSimple(sharedList);
		Log.i("LocationActivity",sharedList);
		locationListView = (ListView)findViewById(R.id.myListView);
		/*Intent intent = getIntent();
		path = intent.getStringExtra("curPath");
		Log.i("LocationActivity", "intent gain!!");	
		TextView curPath = (TextView)findViewById(R.id.curPath);
		curPath.setText(path);
				
		ProduceFileNameList(path);*/
        
		locationListView.setAdapter(new ItemAdapter());
		locationListView.setOnItemClickListener(new myOnItemClickListener());
	}
	
	class myOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			Log.i("ItemClicked", "ItemClicked");
			String uri = fileNameList.get(arg2).get("AbsPath").toString();
			DisplayImage(uri);
		}

		public void DisplayImage(String image){
			Intent toImageIntent = new Intent(LocationActivity.this, ImagePagerActivity.class);
			toImageIntent.putExtra("uri", image);
			startActivity(toImageIntent);
		}

	}
	
	
	private void DecodeAddUrl(String strList){
		String[] singleUnit = strList.split(",");
		for(int i = 0; i <singleUnit.length/2; i+=2 ){
			HashMap<String, Object> map = new HashMap<String, Object>();
			int ids = singleUnit[i].indexOf("url");
			int ide = singleUnit[i].indexOf("\"", 12);
			String strUrl = singleUnit[i].substring(ids+6, ide);
			strUrl = strUrl.replace("\\/", "/");
			
			ids = singleUnit[i+1].indexOf("name");
			ide = singleUnit[i+1].indexOf("\"", 10);
			String strName = singleUnit[i+1].substring(ids+7, ide);
			
			ids = singleUnit[i+2].indexOf("distance");
			ide = singleUnit[i+2].indexOf("\"", 14);
			String strDist = singleUnit[i+2].substring(ids+11, ide);
			
			Log.i("ShareImageActivity", strUrl);
			map.put("Name", strName);
			map.put("AbsPath", strUrl);
			map.put("distance", strDist);
			fileNameList.add(map);
		}
	}
	
	private void DecodeAddUrlSimple(String strList){
		strList = strList.substring(4);
		String[] singleUnit = strList.split("\\|");
		for(int i = 0; i <singleUnit.length; i++ ){
			HashMap<String, Object> map = new HashMap<String, Object>();
			String[] singleFactor = singleUnit[i].split(",");
			String strName = singleFactor[0];
			String strUrl = singleFactor[1];
			String strDist = singleFactor[2];
			double d = Double.parseDouble(strDist);
			int dist= (int)(d/100.);
			if(dist < 10) {
				strDist = "within "+dist+"00m";
			}
			else {
				dist /= 10;
				strDist = "within "+dist+"km";
			}
			map.put("Name", strName);
			map.put("AbsPath", strUrl);
			map.put("distance", strDist);
			fileNameList.add(map);
		}
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
	class ItemAdapter extends BaseAdapter {

		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		private class ViewHolder {
			public TextView text;
			public ImageView image;
			public TextView txtDist;
		}

		@Override
		public int getCount() {
			return fileNameList.size();
		}

		@Override
		public Object getItem(int position) {
			return fileNameList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.location_list_item, parent, false);
				holder = new ViewHolder();
				holder.text = (TextView) view.findViewById(R.id.imageName);
				holder.image = (ImageView) view.findViewById(R.id.imageIcon);
				holder.txtDist = (TextView) view.findViewById(R.id.distance);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.text.setText((String)fileNameList.get(position).get("Name").toString());
			holder.txtDist.setText((String)fileNameList.get(position).get("distance").toString());

			imageLoader.displayImage(fileNameList.get(position).get("AbsPath").toString(), 
						holder.image, 
						options, 
						animateFirstListener);

			return view;
		}
	}
	
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
