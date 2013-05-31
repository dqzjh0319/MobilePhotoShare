package polyu.comp.mps.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import polyu.comp.mps.MyApplication;
import polyu.comp.mps.R;
import polyu.comp.mps.util.JsonUtil;
import polyu.comp.mps.util.LocationUtil;
import polyu.comp.mps.util.SharedUtil;
import polyu.comp.mps.util.SyncTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ShareImageActivity extends Activity{

	private MyApplication myApp; 
	private List<Map<String, Object>> fileNameList;
	private File path;
	private ListView mylv;
	public SimpleAdapter mAdapter;
	private String optAbsPath;
	private String photoName;
	private String myDir;
	public Stack<String> pathList;
	private Dialog fileOptDialog;
	private EditText et_PhotoTitle;
	private EditText et_PhotoComments;
	private LocationUtil locationUtil;
	private Dialog commentsDialog;
	private MenuItem searchItem;
	public ImageLoader imageLoader;
	DisplayImageOptions options;
	private String sharedList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//locationUtil = new LocationUtil(ShareImageActivity.this);
		myApp = (MyApplication) getApplication();
		
		Intent intent = getIntent();
		myDir = intent.getStringExtra("curPath");
		//Log.i("LocationActivity", "start successfully");
		String url = "http://"+ myApp.getIPAddr() +":8080/MPServer/photoAction!obtainSharePhoto";
		sharedList = SharedUtil.getJson(url);
		//Toast.makeText(getApplicationContext(), sharedList,Toast.LENGTH_SHORT).show();
		if(sharedList != null)
			Log.i("SharedImageActivity",sharedList);
		else
			Log.i("SharedImageActivity","sharedList is null");
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
										.showStubImage(R.drawable.unloaded_icon)
										.showImageForEmptyUri(R.drawable.image_icon)
										.showImageOnFail(R.drawable.image_icon)
										.resetViewBeforeLoading()
										.displayer(new RoundedBitmapDisplayer(20))
										.build();
		
        mylv = (ListView)findViewById(R.id.myListView);
              
		fileNameList = new ArrayList<Map<String, Object>>();
		path = new File(myDir);
		TextView curPath = (TextView)findViewById(R.id.curPath);
		curPath.setText("  " + path.getAbsolutePath().toString());
		File[] files = path.listFiles();
		pathList = new Stack<String>();
		getFileName(files);
		DecodeAddUrlSimple(sharedList);
		//DecodeAddUrl(sharedList);
        String[] fromColumns = {"Icon", "Name"};
        int[] toViews = {	R.id.itemImage, R.id.itemName}; // The TextView in simple_list_item_1

		/*mAdapter = new SimpleAdapter(this,
				fileNameList,R.layout.simple_list_item,fromColumns,toViews);*/
        mylv.setOnItemClickListener(new myOnLVItemClickListener());
        mylv.setAdapter(new ItemAdapter());
	}

	private void DecodeAddUrl(String strList){
		Log.i("ShareImageActivity", strList);
		String[] singleUnit = strList.split(",");
		for(int i = 0; i <singleUnit.length; i+=2 ){
			HashMap<String, Object> map = new HashMap<String, Object>();
			int ids = singleUnit[i].indexOf("url");
			int ide = singleUnit[i].indexOf("\"", 12);
			String strUrl = singleUnit[i].substring(ids+6, ide);
			strUrl = strUrl.replace("\\/", "/");
			ids = singleUnit[i+1].indexOf("name");
			ide = singleUnit[i+1].indexOf("\"", 10);
			String strName = singleUnit[i+1].substring(ids+7, ide);
			Log.i("ShareImageActivity", strUrl);
			map.put("Name", strName);
			map.put("AbsPath", strUrl);
			map.put("flag", false);
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
			map.put("Name", strName);
			map.put("AbsPath", strUrl);
			map.put("flag", false);
			fileNameList.add(map);
		}
	}
	
	class myOnLVItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			Log.i("ItemClicked", "ItemClicked");
			String uri = fileNameList.get(arg2).get("AbsPath").toString();
			String f = fileNameList.get(arg2).get("flag").toString();
			if(f.equals("true")){
				DisplayImage("file://"+uri);
			}
			else {
				DisplayImage(uri);
			}
		}

		public void DisplayImage(String image){
			Intent toImageIntent = new Intent(ShareImageActivity.this, ImagePagerActivity.class);
			toImageIntent.putExtra("uri", image);
			startActivity(toImageIntent);
		}

	}

	class MyDialogItemOnClickListener implements android.content.DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.i("DialogClickEvent", Integer.toString(which));
			File tmpFile = new File(optAbsPath);
			switch(which){
			case 0: DeleteOptFile(tmpFile);break;
			case 1: RenameOptFile(tmpFile);break;
			case 2: UploadOptFile(tmpFile);break;
			case 3: ShareOptFile(tmpFile);break;
			}
		}
	}

	public void DeleteOptFile(File optFile){
		if(optFile.isDirectory()){
			File[] tmpfs = optFile.listFiles();
			for(File tf : tmpfs){
				if(tf.isDirectory()){
					DeleteOptFile(tf);
				}
				else{
					tf.delete();
				}
			}
			optFile.delete();
		}
		else
		{
			optFile.delete();
		}
		Log.i("DeleteOptFile", optFile.getPath() + ", "+ path.getAbsolutePath()); 
		if(optFile.getParent().equals(path.getAbsolutePath())){
			getFileName(path.listFiles());
			mAdapter.notifyDataSetChanged();
		}
	}

	public void RenameOptFile(File optFile){

	}

	public void UploadOptFile(final File optFile){
		fileOptDialog.dismiss();
		AlertDialog.Builder commentsDialogBuilder = new AlertDialog.Builder(ShareImageActivity.this);
		commentsDialogBuilder.setTitle(R.string.Setting);
		View view = LayoutInflater.from(this).inflate(R.layout.layout_uploadcomments, null);
		commentsDialogBuilder.setView(view);
		Button btn_UploadOK = (Button) view.findViewById(R.id.btn_UploadOK);
		Button btn_UploadCancel = (Button) view.findViewById(R.id.btn_UploadCancel);
		et_PhotoTitle = (EditText) view.findViewById(R.id.etPhotoTitle);
		et_PhotoComments = (EditText) view.findViewById(R.id.etPhotoComments);
		commentsDialog = commentsDialogBuilder.create();
		
		btn_UploadOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String photoTitle = et_PhotoTitle.getText().toString();
				String photoComments = et_PhotoComments.getText().toString();
				String url = "http://" + myApp.getIPAddr() + ":8080/MPServer/photoAction!addPhoto?photo.photoTitle="+photoTitle+
						    "&photo.isShared=0"+"&photo.photoAuthor="+myApp.getUserName()+
						    "&photo.photoComments="+photoComments+
						    "&photo.picPath=/upload/pic/"+photoName+
							"&photo.location="+locationUtil.getLocationInfo();
				JsonUtil.getJson(url);
				//UploadFileTask uploadFileTask = new UploadFileTask(MainActivity.this);
				//uploadFileTask.execute(optFile.getAbsolutePath());
			}
		});
		
		btn_UploadCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				commentsDialog.dismiss();
			}
		});
		
		commentsDialog.show();

	}
	
	public void ShareOptFile(File optFile){
		fileOptDialog.dismiss();
		String url = "http://158.132.11.225:8080/MPServer/photoAction!sharePhoto?photo.photoTitle="+photoName+
			    "&photo.isShared=1"+"&photo.photoAuthor="+myApp.getUserName()+
			    "&photo.picPath="+photoName+
				"&photo.location="+locationUtil.getLocationInfo();
		if(JsonUtil.getJson(url) != true) {
			//UploadFileTask uploadFileTask=new UploadFileTask(MainActivity.this);
			//uploadFileTask.execute(optFile.getAbsolutePath());
		}
	}
	
	@Override
	public void onBackPressed() {
		if(pathList.empty()){
			super.onBackPressed();
		}
		else{
			String tmpStr = pathList.pop();
			path = new File(tmpStr);
			TextView tmpTV = (TextView)findViewById(R.id.curPath);
			tmpTV.setText(tmpStr);
			File[] files = path.listFiles();
			Log.i("onBackPressed", "Current path is " + tmpStr);
			getFileName(files);
			mAdapter.notifyDataSetChanged();
		}
	}

	private void getFileName(File[] files) {
		Log.i("getFileName", "Accessed!!!");
		fileNameList.clear();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					String fileName = file.getName().toString();
					String AbsPath = file.getAbsolutePath();
					map.put("Name", fileName);
					map.put("Icon", R.drawable.folder_icon);
					map.put("AbsPath", AbsPath);
					map.put("flag", true);
					fileNameList.add(map);
				} else {
						String fileName = file.getName();
						HashMap<String, Object> map = new HashMap<String, Object>();
						String AbsPath = file.getAbsolutePath();
						map.put("Name", fileName);
						map.put("Icon", R.drawable.image_icon);
						map.put("AbsPath", AbsPath);
						map.put("flag", true);
						fileNameList.add(map);
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		searchItem = menu.findItem(R.id.menu_search);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_update: 
				Log.i("MenuItem", "Update Clicked");
				doUpdate();
				return true;
			case R.id.menu_search:
				Log.i("MenuItem", "Search Clicked");
				onSearchRequested();
				return true;
			case R.id.item_clear_memory_cache:
				imageLoader.clearMemoryCache();
				return true;
			case R.id.item_clear_disc_cache:
				imageLoader.clearDiscCache();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}	

	@Override
	public boolean onSearchRequested() {
		// TODO Auto-generated method stub
		Bundle mBundle = new Bundle();

		mBundle.putString("path", path.getAbsolutePath());
		startSearch("test", false, mBundle, false);
		return true;
		//return super.onSearchRequested();
	}

	public void doUpdate(){
		Thread SyncThread = new Thread(new SyncTask());
		SyncThread.start();
		
		try {
			SyncThread.join();
		} catch (Exception e) {
			
		}
	}
	
	class ItemAdapter extends BaseAdapter {

		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		private class ViewHolder {
			public TextView text;
			public ImageView image;
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
				view = getLayoutInflater().inflate(R.layout.simple_list_item, parent, false);
				holder = new ViewHolder();
				holder.text = (TextView) view.findViewById(R.id.itemName);
				holder.image = (ImageView) view.findViewById(R.id.itemImage);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.text.setText((String)fileNameList.get(position).get("Name"));

			String f = fileNameList.get(position).get("flag").toString();
			if(f == "true") {
				imageLoader.displayImage("file://" + fileNameList.get(position).get("AbsPath").toString(), 
							holder.image, 
							options, 
							animateFirstListener);
			}
			else {
				imageLoader.displayImage(fileNameList.get(position).get("AbsPath").toString(), 
						holder.image, 
						options, 
						animateFirstListener);
			}

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

