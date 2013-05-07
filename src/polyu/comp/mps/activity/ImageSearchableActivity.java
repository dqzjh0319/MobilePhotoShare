package polyu.comp.mps.activity;

import polyu.comp.mps.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class ImageSearchableActivity extends Activity {

	private List<Map<String, Object>> fileNameList;
	private ListView srlv;
	public SimpleAdapter srAdapter;
	private String path;
	private File curDir;
	private String query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_searchable_layout);

		srlv = (ListView)findViewById(R.id.lv_search_result);
		fileNameList = new ArrayList<Map<String, Object>>();		

		Log.i("ImageSearchableActivity", "onCreate called!!!");
	    // Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      query = intent.getStringExtra(SearchManager.QUERY);

	      Bundle bundled=intent.getBundleExtra(SearchManager.APP_DATA);  
	      if(bundled!=null){  
	    	  path = bundled.getString("path");  
              Log.i("ImageSearchableActivity", path + " got successfully!!!");
	      }
	      else {
	    	  Log.i("ImageSearchableActivity", " no bundle data!!!");
	      }
	    }
        
        curDir = new File(path);
		File[] files = curDir.listFiles();
		doMySearch(files);
		String[] fromColumns = {"Icon", "Name"};
        int[] toViews = {	R.id.itemImage, R.id.itemName}; // The TextView in simple_list_item_1

        //MyAdapter mAdapter = new MyAdapter(this);
		srAdapter = new SimpleAdapter(this,
				fileNameList,R.layout.simple_list_item,fromColumns,toViews);
        srlv.setOnItemClickListener(new mysrOnLVItemClickListener());
        //srlv.setOnItemLongClickListener(new myOnItemLongClickListener());
        srlv.setAdapter(srAdapter);
	}

	class mysrOnLVItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Log.i("ItemClicked", "ha ha ha...");
			String p = (String) fileNameList.get(arg2).get( 
            		"AbsPath");
			File curImg = new File(p);
			if(!curImg.isDirectory())
			{
				DisplayImage(curImg);
				return;
			}
		}

		public void DisplayImage(File image){
			Intent toImageIntent = new Intent(Intent.ACTION_VIEW);
			toImageIntent.setDataAndType(Uri.fromFile(image), "image/*");
			startActivity(toImageIntent);
		}

	}

	private void doMySearch(File[] files) {
		fileNameList.clear();
		if (files != null) {
			for (File file : files) {
				if(file.getName().indexOf(query)<0)
					continue;
				if (file.isDirectory()) {
					//Log.i("getFileName", file.getName().toString()
						//	+ file.getPath().toString());
					HashMap<String, Object> map = new HashMap<String, Object>();
					String fileName = file.getName().toString();
					String AbsPath = file.getAbsolutePath();
					map.put("Name", fileName);
					map.put("Icon", R.drawable.folder_icon);
					map.put("AbsPath", AbsPath);
					fileNameList.add(map);
				} else {
						String fileName = file.getName();
						HashMap<String, Object> map = new HashMap<String, Object>();
					/*if (fileName.endsWith(".*")) {
						HashMap<String, String> map = new HashMap<String, String>();
						String s = fileName.substring(0,
								fileName.lastIndexOf(".")).toString();
						Log.i("zeng", "ÎÄ¼þÃûtxt£º£º   " + s);
						map.put("Name", fileName.substring(0,
								fileName.lastIndexOf(".")));*/
						String AbsPath = file.getAbsolutePath();
						map.put("Name", fileName);
						map.put("Icon", R.drawable.image_icon);
						map.put("AbsPath", AbsPath);
						fileNameList.add(map);
					//}
				}
			}
		}
	}
}