package com.s890510.microfilm.util;

import java.io.Closeable;
import java.io.IOException;

import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

import com.s890510.microfilm.R;

public class WeatherQuery {
    private static final String TAG                              = "WeatherQuery";

    private static final String TAKEN_TIME                       = "photo_taken_time";
    private static final String WEATHER_UNIT_URI                 = "content://com.asus.weathertime.provider/widgetcity";
    public final String         PACKAGE_NAME_WEATHER             = "com.asus.weathertime";

    private static final String PhotoCluster_BASE_PATH           = "photoevents";
    private static final String PhotoCluster_AUTHORITY           = "com.asus.photoclusteringservice.contentprovider";
    public static final Uri     PhotoCluster_CONTENT_URI         = Uri.parse("content://" + PhotoCluster_AUTHORITY + "/" + PhotoCluster_BASE_PATH);

    public static final Uri     EventsPKGIntentType_provider_uri = Uri.parse("content://com.asus.pkgservice.dataprovider/single-entry");

    private int                 mPKG_WeatherCode;
    private int                 mPKG_Temperature;
    private String              mPKG_AdminAreaName;
    private String              mPKG_CountryName;
    private String              mPKG_CityName;
    private String              mPKG_WeatherText;
    private String              mPKG_TempUnits;

    private static final String PHOTO_EVENT_BY_TAKEN_TIME_QUERY  = "PREFIX echo: <http://echo.asus.com/pkg/schema#> \n"
                                                                         + "SELECT ?photoEvent ?weather WHERE { \n"
                                                                         + "?photoEvent a echo:PhotoEvent . \n"
                                                                         + "?photoEvent echo:startTime ?start . \n"
                                                                         + "?photoEvent echo:instaWeather ?weather . \n"
                                                                         + "?photoEvent echo:endTime ?end . \n" + "FILTER ( ?end >= " + TAKEN_TIME
                                                                         + " && ?start <= " + TAKEN_TIME + " && ?weather != \"\")" + "}";

    /**
     * This function demo how to query PKG for a photo's weather data.
     **/
    public boolean queryWeather(Context context, String mPhotoTakenTime) {
        boolean flag = true;
        Cursor cur = null;
        // B: Keith 2014/02/19
        try {
            if(isPhotoEventCotentPrvoiderExist(context)) {
                cur = context.getContentResolver().query(PhotoCluster_CONTENT_URI, null,
                        " start <= " + mPhotoTakenTime + " AND " + " end >= " + mPhotoTakenTime, null, null);
            } else {
                String query = PHOTO_EVENT_BY_TAKEN_TIME_QUERY.replace(TAKEN_TIME, mPhotoTakenTime);
                cur = context.getContentResolver().query(EventsPKGIntentType_provider_uri, null, query, null, null);
            }
            if(cur != null && cur.moveToNext()) // Weather data found
            {
                String weatherJSON = cur.getString(cur.getColumnIndex("weather"));
                if(weatherJSON != null && !weatherJSON.isEmpty()) {
                    JSONObject weather;
                    weather = new JSONObject(weatherJSON);
                    // Parse weather data from JSON object.
                    mPKG_AdminAreaName = weather.optString("adminArea");
                    mPKG_CountryName = weather.optString("country");
                    mPKG_CityName = weather.optString("cityname");
                    mPKG_WeatherCode = weather.optInt("weathericon");
                    mPKG_WeatherText = weather.optString("weathertext");
                    mPKG_Temperature = weather.optInt("temperature");
                    mPKG_TempUnits = weather.optString("tempunits");
                    // Normalize Temperature
                    mPKG_Temperature = normalizeTemp(mPKG_Temperature, mPKG_TempUnits);

                    // No internet connection or have not updated weatherTime
                    if(mPKG_AdminAreaName.toLowerCase().matches("null") || mPKG_CountryName.toLowerCase().matches("null")
                            || mPKG_CityName.toLowerCase().matches("null") || mPKG_WeatherText.toLowerCase().matches("null")) {
                        Log.d("WeatherQuery", "Query weather error! (No internet connection or have not updated weatherTime)");
                        flag = false;
                    }
                } else
                    flag = false;
            } else {
                // No weather data found.
                flag = false;
            }
        } catch(Exception e) {
            flag = false;
        } finally {
            closeSilently(cur);
        }
        return flag;
        // E: Keith 2014/02/19
    }

    public String getUserTempUnits(Context context) {
        String unitInHistory = null;
        Cursor weatherUnit = null;
        try {
            weatherUnit = context.getContentResolver().query(Uri.parse(WEATHER_UNIT_URI), null, null, null, null);
            if(weatherUnit != null && weatherUnit.moveToNext()) {
                int tempUnitIndex = weatherUnit.getColumnIndex("tempunits");
                if(tempUnitIndex != -1) {
                    unitInHistory = weatherUnit.getString(tempUnitIndex);
                }
            }
        } catch(Exception e) {
            Log.d("WeatherQuery", "Error in getUserTempUnits");
        } finally {
            weatherUnit.close();
        }

        return unitInHistory;
    }

    public static void closeSilently(Closeable c) {
        if(c == null)
            return;
        try {
            c.close();
        } catch(IOException t) {
            Log.w(TAG, "close fail ", t);
        }
    }

    public static boolean isPhotoEventCotentPrvoiderExist(Context context) {
        try {
            context.getPackageManager().getProviderInfo(
                    new ComponentName("com.asus.sitd.whatsnext", "com.asus.photoclusteringservice.PhotoEventContentProvider"),
                    PackageManager.GET_META_DATA);
            return true;
        } catch(NameNotFoundException e) {
            return false;
        }
    }

    private String findLastTakenPhoto(Context context) {
        String mPhotoLastTakenTime;
        Cursor photoCur = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[] { Images.ImageColumns.DATE_TAKEN },
                Images.ImageColumns.BUCKET_ID + " = -1739773001", null, Images.ImageColumns.DATE_TAKEN + " DESC limit 1");
        // B: Keith 2014/02/19
        try {
            DatabaseUtils.dumpCursor(photoCur);
            if(photoCur != null & photoCur.moveToNext()) {
                return mPhotoLastTakenTime = new String(String.valueOf(photoCur.getLong(photoCur.getColumnIndex(Images.ImageColumns.DATE_TAKEN))));
            } else
                return mPhotoLastTakenTime = null;
        } finally {
            closeSilently(photoCur);
        }
        // E: Keith 2014/02/19
    }

    public int getWeatherCode() {
        return mPKG_WeatherCode;
    }

    public String getWeatherText() {
        return mPKG_WeatherText;
    }

    public int getTemperature() {
        return mPKG_Temperature;
    }

    public String getAdminAreaName() {
        return mPKG_AdminAreaName;
    }

    public String getCountryName() {
        return mPKG_CountryName;
    }

    public String getCityName() {
        return mPKG_CityName;
    }

    public String getTempUnits() {
        return mPKG_TempUnits;
    }

    public int normalizeTemp(int temp, String unit) {
        if(!unit.equalsIgnoreCase("c"))
            return (int) (temp * 1.8) + 32;
        else
            return temp;
    }

    public Drawable getWeatherIcon(Context context, int options) {
        // Get weather pictures
        Context weatherCtx;
        Drawable drawable;
        try {
            switch (options) {
                case 0:
                    drawable = context.getResources().getDrawableForDensity(
                            context.getResources().getIdentifier(getWeatherName(mPKG_WeatherCode, 0), "drawable", context.getPackageName()),
                            context.getResources().getDisplayMetrics().densityDpi);
                    return drawable;
                case 1:
                    drawable = context.getResources().getDrawableForDensity(
                            context.getResources().getIdentifier(getWeatherName(mPKG_WeatherCode, 1), "drawable", context.getPackageName()),
                            context.getResources().getDisplayMetrics().densityDpi);
                    return drawable;
                default:
                    weatherCtx = getTargetContext(context, PACKAGE_NAME_WEATHER);
                    drawable = getTargetDrawable(context, weatherCtx.getResources(), PACKAGE_NAME_WEATHER, getWeatherName(mPKG_WeatherCode, 3));
                    return drawable;
            }
        } catch(NameNotFoundException e) {
        }
        return null;
    }

    // Get target context, eg: weather context
    private Context getTargetContext(Context context, String pkg) throws NameNotFoundException {
        return context.createPackageContext(pkg, Context.CONTEXT_IGNORE_SECURITY);
    }

    // @targetRs: targetRes, not your res.
    // @defPackage: weather package name
    // @name: picture name which you want to get.
    private Drawable getTargetDrawable(Context context, Resources targetRs, String defPackage, String name) {
        if(name == null) {
            Log.d("WeatherQuery", "Query weather error! (No weather name)");
            return null;
        }
        int id = targetRs.getIdentifier(name, "drawable", defPackage);
        if(id != 0) {
            Drawable drawable = targetRs.getDrawableForDensity(id, context.getResources().getDisplayMetrics().densityDpi);
            if(drawable != null)
                return drawable;
            else {
                Log.d("WeatherQuery", "Query weather error! (No weather drawable)");
                return null;
            }
        } else {
            Log.d("WeatherQuery", "Query weather error! (No weather id)");
            return null;
        }
    }

    public int getWeatherResId(int weatherCode) { // Use for MicroFilm
        switch (weatherCode) {
            case 1:
                return R.drawable.asus_ic_weather_04_sunny;
            case 2:
                return R.drawable.asus_ic_weather_07_mostlysunny;
            case 3:
                return R.drawable.asus_ic_weather_24_partlysunny;
            case 4:
                return R.drawable.asus_ic_weather_25_intermittentclouds;
            case 5:
                return R.drawable.asus_ic_weather_05_hazysunshine;
            case 6:
                return R.drawable.asus_ic_weather_09_mostlycloudy;
            case 7:
                return R.drawable.asus_ic_weather_02_cloudy;
            case 8:
                return R.drawable.asus_ic_weather_08_dreary;
            case 11:
                return R.drawable.asus_ic_weather_14_fog;
            case 12:
                return R.drawable.asus_ic_weather_22_shower;
            case 13:
                return R.drawable.asus_ic_weather_17_mostlycloudyshower;
            case 14:
                return R.drawable.asus_ic_weather_20_partlysunnyshowers;
            case 15:
                return R.drawable.asus_ic_weather_12_thunderstorm;
            case 16:
                return R.drawable.asus_ic_weather_23_mostlycloudythundershowers;
            case 17:
                return R.drawable.asus_ic_weather_21_partlysunnythundershowers;
            case 18:
                return R.drawable.asus_ic_weather_10_rain;
            case 19:
                return R.drawable.asus_ic_weather_15_flurries;
            case 20:
                return R.drawable.asus_ic_weather_27_mostlycloudywithflurries;
            case 21:
                return R.drawable.asus_ic_weather_18_partlysunnyflurries;
            case 22:
                return R.drawable.asus_ic_weather_06_snow;
            case 23:
                return R.drawable.asus_ic_weather_19_mostlycloudsnow;
            case 24:
                return R.drawable.asus_ic_weather_03_ice;
            case 25:
                return R.drawable.asus_ic_weather_16_sleet;
            case 26:
                return R.drawable.asus_ic_weather_11_freezingrain;
            case 29:
                return R.drawable.asus_ic_weather_13_rainsnow;
            case 32:
                return R.drawable.asus_ic_weather_26_windy;
            case 33:
                return R.drawable.asus_ic_weather_04_sunny;
            case 34:
                return R.drawable.asus_ic_weather_07_mostlysunny;
            case 35:
                return R.drawable.asus_ic_weather_24_partlysunny;
            case 36:
                return R.drawable.asus_ic_weather_25_intermittentclouds;
            case 37:
                return R.drawable.asus_ic_weather_05_hazysunshine;
            case 38:
                return R.drawable.asus_ic_weather_09_mostlycloudy;
            case 39:
                return R.drawable.asus_ic_weather_20_partlysunnyshowers;
            case 40:
                return R.drawable.asus_ic_weather_17_mostlycloudyshower;
            case 41:
                return R.drawable.asus_ic_weather_21_partlysunnythundershowers;
            case 42:
                return R.drawable.asus_ic_weather_23_mostlycloudythundershowers;
            case 43:
                return R.drawable.asus_ic_weather_27_mostlycloudywithflurries;
            case 44:
                return R.drawable.asus_ic_weather_19_mostlycloudsnow;
            default:
                return R.drawable.asus_ic_weather_01_unknown;
        }
    }

    public String getWeatherName(int weatherCode, int options) {
        switch (options) {
            case 0:
                switch (weatherCode) {
                    case 1:
                        return "asus_ic_weather_04_sunny";
                    case 2:
                        return "asus_ic_weather_07_mostlysunny";
                    case 3:
                        return "asus_ic_weather_24_partlysunny";
                    case 4:
                        return "asus_ic_weather_25_intermittentclouds";
                    case 5:
                        return "asus_ic_weather_05_hazysunshine";
                    case 6:
                        return "asus_ic_weather_09_mostlycloudy";
                    case 7:
                        return "asus_ic_weather_02_cloudy";
                    case 8:
                        return "asus_ic_weather_08_dreary";
                    case 11:
                        return "asus_ic_weather_14_fog";
                    case 12:
                        return "asus_ic_weather_22_shower";
                    case 13:
                        return "asus_ic_weather_17_mostlycloudyshower";
                    case 14:
                        return "asus_ic_weather_20_partlysunnyshowers";
                    case 15:
                        return "asus_ic_weather_12_thunderstorm";
                    case 16:
                        return "asus_ic_weather_23_mostlycloudythundershowers";
                    case 17:
                        return "asus_ic_weather_21_partlysunnythundershowers";
                    case 18:
                        return "asus_ic_weather_10_rain";
                    case 19:
                        return "asus_ic_weather_15_flurries";
                    case 20:
                        return "asus_ic_weather_27_mostlycloudywithflurries";
                    case 21:
                        return "asus_ic_weather_18_partlysunnyflurries";
                    case 22:
                        return "asus_ic_weather_06_snow";
                    case 23:
                        return "asus_ic_weather_19_mostlycloudsnow";
                    case 24:
                        return "asus_ic_weather_03_ice";
                    case 25:
                        return "asus_ic_weather_16_sleet";
                    case 26:
                        return "asus_ic_weather_11_freezingrain";
                    case 29:
                        return "asus_ic_weather_13_rainsnow";
                    case 32:
                        return "asus_ic_weather_26_windy";
                    case 33:
                        return "asus_ic_weather_04_sunny";
                    case 34:
                        return "asus_ic_weather_07_mostlysunny";
                    case 35:
                        return "asus_ic_weather_24_partlysunny";
                    case 36:
                        return "asus_ic_weather_25_intermittentclouds";
                    case 37:
                        return "asus_ic_weather_05_hazysunshine";
                    case 38:
                        return "asus_ic_weather_09_mostlycloudy";
                    case 39:
                        return "asus_ic_weather_20_partlysunnyshowers";
                    case 40:
                        return "asus_ic_weather_17_mostlycloudyshower";
                    case 41:
                        return "asus_ic_weather_21_partlysunnythundershowers";
                    case 42:
                        return "asus_ic_weather_23_mostlycloudythundershowers";
                    case 43:
                        return "asus_ic_weather_27_mostlycloudywithflurries";
                    case 44:
                        return "asus_ic_weather_19_mostlycloudsnow";
                    default:
                        return "asus_ic_weather_01_unknown";
                }
            case 1:
                switch (weatherCode) {
                    case 1:
                        return "asus_ic_weather_04_sunny_dark";
                    case 2:
                        return "asus_ic_weather_07_mostlysunny_dark";
                    case 3:
                        return "asus_ic_weather_24_partlysunny_dark";
                    case 4:
                        return "asus_ic_weather_25_intermittentclouds_dark";
                    case 5:
                        return "asus_ic_weather_05_hazysunshine_dark";
                    case 6:
                        return "asus_ic_weather_09_mostlycloudy_dark";
                    case 7:
                        return "asus_ic_weather_02_cloudy_dark";
                    case 8:
                        return "asus_ic_weather_08_dreary_dark";
                    case 11:
                        return "asus_ic_weather_14_fog_dark";
                    case 12:
                        return "asus_ic_weather_22_shower_dark";
                    case 13:
                        return "asus_ic_weather_17_mostlycloudyshower_dark";
                    case 14:
                        return "asus_ic_weather_20_partlysunnyshowers_dark";
                    case 15:
                        return "asus_ic_weather_12_thunderstorm_dark";
                    case 16:
                        return "asus_ic_weather_23_mostlycloudythundershowers_dark";
                    case 17:
                        return "asus_ic_weather_21_partlysunnythundershowers_dark";
                    case 18:
                        return "asus_ic_weather_10_rain_dark";
                    case 19:
                        return "asus_ic_weather_15_flurries_dark";
                    case 20:
                        return "asus_ic_weather_27_mostlycloudywithflurries_dark";
                    case 21:
                        return "asus_ic_weather_18_partlysunnyflurries_dark";
                    case 22:
                        return "asus_ic_weather_06_snow_dark";
                    case 23:
                        return "asus_ic_weather_19_mostlycloudsnow_dark";
                    case 24:
                        return "asus_ic_weather_03_ice_dark";
                    case 25:
                        return "asus_ic_weather_16_sleet_dark";
                    case 26:
                        return "asus_ic_weather_11_freezingrain_dark";
                    case 29:
                        return "asus_ic_weather_13_rainsnow_dark";
                    case 32:
                        return "asus_ic_weather_26_windy_dark";
                    case 33:
                        return "asus_ic_weather_04_sunny_dark";
                    case 34:
                        return "asus_ic_weather_07_mostlysunny_dark";
                    case 35:
                        return "asus_ic_weather_24_partlysunny_dark";
                    case 36:
                        return "asus_ic_weather_25_intermittentclouds_dark";
                    case 37:
                        return "asus_ic_weather_05_hazysunshine_dark";
                    case 38:
                        return "asus_ic_weather_09_mostlycloudy_dark";
                    case 39:
                        return "asus_ic_weather_20_partlysunnyshowers_dark";
                    case 40:
                        return "asus_ic_weather_17_mostlycloudyshower_dark";
                    case 41:
                        return "asus_ic_weather_21_partlysunnythundershowers_dark";
                    case 42:
                        return "asus_ic_weather_23_mostlycloudythundershowers_dark";
                    case 43:
                        return "asus_ic_weather_27_mostlycloudywithflurries_dark";
                    case 44:
                        return "asus_ic_weather_19_mostlycloudsnow_dark";
                    default:
                        return "asus_ic_weather_01_unknown_dark";
                }

            default:
                switch (weatherCode) {
                    case 1:
                        return "asus_ep_weather_sunny_list";
                    case 2:
                        return "asus_ep_weather_mostlysunny_list";
                    case 3:
                        return "asus_ep_weather_partlysunny_list";
                    case 4:
                        return "asus_ep_weather_intermittentclouds_list";
                    case 5:
                        return "asus_ep_weather_hazysunshine_list";
                    case 6:
                        return "asus_ep_weather_mostlycloudy_list";
                    case 7:
                        return "asus_ep_weather_cloudy_list";
                    case 8:
                        return "asus_ep_weather_dreary_list";
                    case 11:
                        return "asus_ep_weather_fog_list";
                    case 12:
                        return "asus_ep_weather_shower_list";
                    case 13:
                        return "asus_ep_weather_mostlycloudywithshowers_list";
                    case 14:
                        return "asus_ep_weather_partlysunnywithshowers_list";
                    case 15:
                        return "asus_ep_weather_thunderstorm_list";
                    case 16:
                        return "asus_ep_weather_mostlycloudywiththundershowers_list";
                    case 17:
                        return "asus_ep_weather_partlysunnywiththundershowers_list";
                    case 18:
                        return "asus_ep_weather_rain_list";
                    case 19:
                        return "asus_ep_weather_flurries_list";
                    case 20:
                        return "asus_ep_weather_mostlycloudywithflurries_list";
                    case 21:
                        return "asus_ep_weather_partlysunnywithflurries_list";
                    case 22:
                        return "asus_ep_weather_snow_list";
                    case 23:
                        return "asus_ep_weather_mostlycloudywithsnow_list";
                    case 24:
                        return "asus_ep_weather_ice_list";
                    case 25:
                        return "asus_ep_weather_sleet_list";
                    case 26:
                        return "asus_ep_weather_freezingrain_list";
                    case 29:
                        return "asus_ep_weather_rainandsnowmixed_list";
                    case 32:
                        return "asus_ep_weather_windy_list";
                    case 33:
                        return "asus_ep_weather_sunny_list";
                    case 34:
                        return "asus_ep_weather_mostlysunny_list";
                    case 35:
                        return "asus_ep_weather_partlysunny_list";
                    case 36:
                        return "asus_ep_weather_intermittentclouds_list";
                    case 37:
                        return "asus_ep_weather_hazysunshine_list";
                    case 38:
                        return "asus_ep_weather_mostlycloudy_list";
                    case 39:
                        return "asus_ep_weather_partlysunnywithshowers_list";
                    case 40:
                        return "asus_ep_weather_mostlycloudywithshowers_list";
                    case 41:
                        return "asus_ep_weather_partlysunnywiththundershowers_list";
                    case 42:
                        return "asus_ep_weather_mostlycloudywiththundershowers_list";
                    case 43:
                        return "asus_ep_weather_mostlycloudywithflurries_list";
                    case 44:
                        return "asus_ep_weather_mostlycloudywithsnow_list";
                    default:
                        return "asus_ic_weather_01_unknown";
                }
        }
    }
}