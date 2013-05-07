package polyu.comp.mps.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LocationUtil {
	private double latitude=0.0;  
	private double longitude =0.0;
	private LocationManager lm;
	private String locationInfo;
	private LocationListener locationListener; 
	private Location location;
	private String networkProvider = LocationManager.NETWORK_PROVIDER;  
    private String GpsProvider = LocationManager.GPS_PROVIDER;  
	
	public LocationUtil(Context context) {
		
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);  
        if (startLocation(networkProvider,context)) {  
            updateLocation(location,context);  
        }else if(startLocation(GpsProvider,context)){  
            updateLocation(location,context);  
        }else{  
            Toast.makeText(context, "GPS is not available", 5000).show();  
        }  
	}
	
	private boolean startLocation(String provider,final Context context){  
		Location location = lm.getLastKnownLocation(provider);   
        locationListener = new LocationListener() {   
        	public void onProviderEnabled(String provider) {  
                
            }  
              
            public void onProviderDisabled(String provider) {  
                  
            }  
               
            public void onLocationChanged(Location location) {  
                if (location != null) {     
                	Log.e("Map", "Location changed : Lat: "    
                          + location.getLatitude() + " Lng: "    
                          + location.getLongitude()); 
                }  
            }

			public void onStatusChanged(String provider, int status, Bundle extras) {
				
			}  
        };  

        lm.requestLocationUpdates(provider, 1000, 0, locationListener);  
          
        if (location!= null) {  
        	this.location = location;
            return true;  
        }  
        return false;  
    }  
	
	private void updateLocation(Location location,Context context) {  
        if (location != null) {  
        	latitude = location.getLatitude();  
            longitude = location.getLongitude();  
            locationInfo = latitude+","+longitude;
            lm.removeUpdates(locationListener);  
        } else {  
        	Toast.makeText(context, "Fail to get location information", 5000).show(); 
        }  
    }  
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public void setLocationInfo(String locationInfo) {
		this.locationInfo = locationInfo;
	}
	public String getLocationInfo() {
		return locationInfo;
	}  
	
}
