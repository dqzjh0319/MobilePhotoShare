package polyu.comp.mps.activity;

import polyu.comp.mps.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;


public class PanelActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_panel_layout);
		ImageButton myImages = (ImageButton)findViewById(R.id.btn_MyImages);
		myImages.setOnClickListener(new MyImageOnClickListener());
		ImageButton sharedImages = (ImageButton)findViewById(R.id.btn_SharedImages);
		sharedImages.setOnClickListener(new SharedImageOnClickListener());
		ImageButton picAroundme = (ImageButton)findViewById(R.id.btn_picAroundMe);
		picAroundme.setOnClickListener(new PicAroundMeOnClickListener());
		ImageButton exit = (ImageButton)findViewById(R.id.btn_Signout);
		exit.setOnClickListener(new ExitOnClickListener());
	}
	
	class MyImageOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent pnltoVLIntent = new Intent(PanelActivity.this, MainActivity.class);
			pnltoVLIntent.putExtra("curPath", "/mnt/sdcard/PhotoShare/MyImages");
			startActivity(pnltoVLIntent);
		}	
	}
	
	class SharedImageOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent phltoVLIntent = new Intent(PanelActivity.this, ShareImageActivity.class);
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
	
	class ExitOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent MyIntent = new Intent(Intent.ACTION_MAIN);
			MyIntent.addCategory(Intent.CATEGORY_HOME);
			startActivity(MyIntent);
		}
		
		
	}
}
