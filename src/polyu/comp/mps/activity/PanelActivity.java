package polyu.comp.mps.activity;


import java.io.IOException;

import polyu.comp.mps.R;
import polyu.comp.mps.R.id;
import polyu.comp.mps.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;


public class PanelActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_panel_layout);
		ImageButton myImages = (ImageButton)findViewById(R.id.btn_MyImages);
		myImages.setOnClickListener(new MyImageOnClickListener());
		ImageButton sharedImages = (ImageButton)findViewById(R.id.btn_SharedImages);
		sharedImages.setOnClickListener(new SharedImageOnClickListener());
		ImageButton picAroundme = (ImageButton)findViewById(R.id.btn_picAroundMe);
		picAroundme.setOnClickListener(new PicAroundMeOnClickListener());
	}
	
	class MyImageOnClickListener implements OnClickListener{
		public void onClick(View v) {
			Intent pnltoVLIntent = new Intent(PanelActivity.this, MainActivity.class);
			pnltoVLIntent.putExtra("curPath", "/mnt/sdcard/PhotoShare/MyImages");
			startActivity(pnltoVLIntent);
		}	
	}
	
	class SharedImageOnClickListener implements OnClickListener{
		public void onClick(View v) {
			Intent phltoVLIntent = new Intent(PanelActivity.this, MainActivity.class);
			phltoVLIntent.putExtra("curPath", "/mnt/sdcard/PhotoShare/SharedImages");
			startActivity(phltoVLIntent);
		}
	}
	
	class PicAroundMeOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			double dist = calDistaceBetweenImages(
					"/mnt/sdcard/DCIM/test/28.jpg",
					"/mnt/sdcard/DCIM/test/29.jpg");
			Log.i("Location", ""+ dist);
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
			Float result = null;
			String[] DMS = strLoc.split(",", 3);
			
			String[] stringD = DMS[0].split("/", 2);
		    Double D0 = new Double(stringD[0]);
		    Double D1 = new Double(stringD[1]);
		    Double FloatD = D0/D1;

			String[] stringM = DMS[1].split("/", 2);
			Double M0 = new Double(stringM[0]);
			Double M1 = new Double(stringM[1]);
			Double FloatM = M0/M1;
	
			String[] stringS = DMS[2].split("/", 2);
			Double S0 = new Double(stringS[0]);
			Double S1 = new Double(stringS[1]);
			Double FloatS = S0/S1;
			
			result = new Float(FloatD + (FloatM/60) + (FloatS/3600));

			return result;
		}
		
	}
}
