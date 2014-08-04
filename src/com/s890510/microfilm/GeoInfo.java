package com.s890510.microfilm;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.content.Context;
import android.location.Address;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.s890510.microfilm.util.Future;
import com.s890510.microfilm.util.FutureListener;
import com.s890510.microfilm.util.ThreadPool.Job;
import com.s890510.microfilm.util.ThreadPool.JobContext;

public class GeoInfo {
    private static String TAG = "GeoInfo";
    private double[] mlatlng;
    private final Handler mHandler;
    private boolean mHAddress = false;
    private Context mContext;
    private MicroFilmActivity mActivity;
    private Future<Address> mAddressLookupJob;
    private ArrayList<String> mLocation;

    public GeoInfo(Context context, MicroFilmActivity activity, double[] latlng) {
        mHandler = new Handler(Looper.getMainLooper());
        mContext = context;
        mlatlng = latlng;
        mActivity = activity;
    }

    public static boolean isCNSku() {
	    String sku = AsusSystemProperties.get("ro.build.asus.sku").toLowerCase();
	    String productName = AsusSystemProperties.get("ro.product.name").toLowerCase();
	    if(sku == null || productName == null)
	        return false;
	    return sku.startsWith("cn") || sku.startsWith("cucc") ||
	            productName.startsWith("cn") || productName.startsWith("cucc") ? true : false;
	}
 
	public static boolean isCTA() {
	    String cta = AsusSystemProperties.get("ro.asus.cta");
	    String ctaKK = AsusSystemProperties.get("persist.sys.cta.security");
	    return TextUtils.equals(cta, "1") || TextUtils.equals(ctaKK, "1");
	}
    
    public void LoadAddress() {
        if(!isCNSku() && !isCTA()) {
	        mAddressLookupJob = mActivity.getLocationThreadPool().submit(
	            new AddressLookupJob(mlatlng),
	            new FutureListener<Address>() {
	                @Override
	                public void onFutureDone(final Future<Address> future) {
	                    mAddressLookupJob = null;
	                    if (!future.isCancelled()) {
	                        mHandler.post(new Runnable() {
	                            @Override
	                            public void run() {
	                                updateLocation(future.get());
	                            }
	                        });
	                    }
	                }
	            });
        }
    }

    private void updateLocation(Address address) {
        if (address != null) {
            mLocation = new ArrayList<String>();
            mHAddress = true;
            if(address.getAdminArea() != null) {
                mLocation.add(address.getAdminArea());
                if(address.getCountryName() != null) {
                    mLocation.add(address.getCountryName());
                }
            } else if(address.getCountryName() != null) {
                mLocation.add(address.getCountryName());
            }
        }
    }

    public ArrayList<String> getLocation() {
        return mLocation;
    }

    public boolean haveAddress() {
        return mHAddress;
    }

    public void cancel() {
        if (mAddressLookupJob != null) {
            mAddressLookupJob.cancel();
            mAddressLookupJob = null;
        }
    }

    private class AddressLookupJob implements Job<Address> {
        private double[] mLatlng;

        protected AddressLookupJob(double[] latlng) {
            mLatlng = latlng;
        }

        @Override
        public Address run(JobContext jc) {
            MicroFilmGeocoder geocoder = new MicroFilmGeocoder(mContext.getApplicationContext());
            return geocoder.lookupAddress(mLatlng[0], mLatlng[1], true);
        }
    }

    public static class AsusSystemProperties{
    	public static String get(String key){
    		Class clazz = null;
    		String prop = null;
    		try{
    			clazz = Class.forName("android.os.SystemProperties");
    			Method method = clazz.getDeclaredMethod("get", String.class);
    			prop = (String)method.invoke(null, key);
    		}catch(java.lang.Exception e){
    		}
    		if(prop == null)
    			return "";
    		else return prop;
    	}
    }
}
