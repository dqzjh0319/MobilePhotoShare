package polyu.comp.mps.util;

import java.io.IOException;
import org.apache.http.HttpEntity;  
import org.apache.http.HttpResponse;  
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;  
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;  
import org.apache.http.util.EntityUtils;  
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {
	
	public static boolean getJson(String url) {
		String result = null;  
		
		try {        
            HttpClient httpclient = new DefaultHttpClient();  
            HttpGet request = new HttpGet(url);
            request.addHeader("Accept","text/json");
            HttpResponse response = httpclient.execute(request);  
            HttpEntity entity = response.getEntity();  
            String json = EntityUtils.toString(entity,"UTF-8");   
            if(json != null){  
                JSONObject jsonObject = new JSONObject(json);  
                result = jsonObject.get("success").toString();  
                System.out.println(result);
                if("success".endsWith(result)){  
                	return true;  
                }
            } 
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (JSONException e) {   
            e.printStackTrace();  
        }
        return false;
	}
}
