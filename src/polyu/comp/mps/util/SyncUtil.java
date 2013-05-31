package polyu.comp.mps.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import android.util.Log;

public class SyncUtil {
	
	private static HttpClient httpClient;
	
	public static void download(String validateURL) throws Exception {
	    InputStream is = null;
	    BufferedInputStream bis = null;
	    FileOutputStream fos = null;
	    BufferedOutputStream bos = null;
	    try {
	        httpClient = new DefaultHttpClient(new BasicHttpParams());
	        HttpPost httpRequest = new HttpPost(validateURL);//validateURL是的请求地址
	        HttpResponse response = httpClient.execute(httpRequest);
	        Header[] headers = response.getAllHeaders();
	        long size = 0;//文件大小
	        String suff = "";//文件后缀名
	        String tmpName = "";
	        for(Header h : headers) {
	            if("Content-Disposition".equals(h.getName())) {
	                suff = h.getValue();
	                Log.i("janken", suff);
	            } else if ("Content-Length".equals(h.getName())) {
	                size = Long.valueOf(h.getValue());
	                Log.i("janken", size + "");
	            } else if ("tmpName".equals(h.getName())) {
	            	tmpName = h.getValue();
	            	Log.i("jan", tmpName);
	            }
	        }
	        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
	            throw new Exception("请求失败");
	        }
	        HttpEntity resEntity = response.getEntity();
	        is = resEntity.getContent();//获得文件的输入流
	        bis = new BufferedInputStream(is);
	        Log.i("SyncUtil", suff);
	        File newFile = new File("/mnt/sdcard/PhotoShare/MyImages/" + suff.substring(21));
	        fos = new FileOutputStream(newFile);
	        bos = new BufferedOutputStream(fos);
	             
	        byte[] bytes = new byte[4096];
	        int len = 0;//最后一次的长度可能不足4096
	        while((len = bis.read(bytes)) > 0) {
	            bos.write(bytes,0,len);
	        }
	        bos.flush();
	    } finally {
	        if(bis != null)bis.close();
	        if(bos != null)bos.close();
	        if(fos != null)fos.close();
	        httpClient.getConnectionManager().shutdown();
	    }
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
}
