package polyu.comp.mps.activity;

import polyu.comp.mps.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
			Intent phltoVLIntent = new Intent(PanelActivity.this, LocationActivity.class);
			phltoVLIntent.putExtra("curPath", "/mnt/sdcard/PhotoShare/LocationImages");
			startActivity(phltoVLIntent);
		}
		
		
	}
}
