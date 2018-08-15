package br.com.home.maildeliveryjfsteel;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import br.com.home.maildeliveryjfsteel.utils.PermissionUtils;

import static br.com.home.maildeliveryjfsteel.utils.PermissionUtils.GPS_PERMISSION;

/**
 * Created by Admin on 04/12/2017.
 */

public class MyLocation {
    public static final String TAG = MyLocation.class.getCanonicalName().toUpperCase();

    Timer timer1;
    LocationManager lm;
    LocationResult locationResult;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    private Context mContext;
    private AppCompatActivity mActivity;
    private Toast mToast;

    public boolean getLocation(Context context, AppCompatActivity activity, LocationResult result) {
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult = result;
        if (lm == null) {
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        if (mActivity == null) {
            mActivity = activity;
        }
        if (mContext == null) {
            mContext = context;
        }
        //exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            showExceptionToast(context, ex, R.string.msg_falha_recuperar_localizacao_gps);
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            showExceptionToast(context, ex, R.string.msg_falha_recuperar_localizacao_network);
        }

        //don't start listeners if no provider is enabled
        if (!gps_enabled && !network_enabled) {
            showToast(context, context.getResources().getString(R.string.msg_falha_recuperar_localizacao_geral));
            return false;
        }

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermissions(activity, GPS_PERMISSION, Arrays.asList(android.Manifest.permission.ACCESS_FINE_LOCATION));
            showToast(context, context.getResources().getString(R.string.msg_permissao_localizacao_pendente));
            return false;
        }

        if (gps_enabled) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
            showToast(context, context.getResources().getString(R.string.msg_localizacao_gps_solicitada));
        }
        if (network_enabled) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
            showToast(context, context.getResources().getString(R.string.msg_localizacao_network_solicitada));
        }
//        timer1 = new Timer();
//        timer1.schedule(new GetLastLocation(), 20000);
        return true;
    }

    private void showExceptionToast(Context context, Exception ex, int msg_falha_recuperar_localizacao_gps) {
        String msg = "";
        if (!ex.getMessage().isEmpty()) {
            msg = ex.getMessage();
        }
        Log.e(TAG, msg);
        showToast(context, String.format("%s\n%s", context.getResources().getString(msg_falha_recuperar_localizacao_gps), msg));
    }

    private void showToast(Context context, String msg) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        mToast.show();
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
//            timer1.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerNetwork);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
//            timer1.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerNetwork);

            Location net_loc = null, gps_loc = null;
            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                PermissionUtils.requestPermissions(mActivity, GPS_PERMISSION, Arrays.asList(android.Manifest.permission.ACCESS_FINE_LOCATION));
                return;
            }
            if (gps_enabled) {
                gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (network_enabled) {
                net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            //if there are both values use the latest one
            if (gps_loc != null && net_loc != null) {
                if (gps_loc.getTime() > net_loc.getTime()) {
                    locationResult.gotLocation(gps_loc);
                } else {
                    locationResult.gotLocation(net_loc);
                }
                return;
            }

            if (gps_loc != null) {
                locationResult.gotLocation(gps_loc);
                return;
            }
            if (net_loc != null) {
                locationResult.gotLocation(net_loc);
                return;
            }
            locationResult.gotLocation(null);
        }
    }

    public static abstract class LocationResult {
        public abstract void gotLocation(Location location);
    }
}
