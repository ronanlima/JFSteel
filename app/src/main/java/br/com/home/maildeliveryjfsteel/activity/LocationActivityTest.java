package br.com.home.maildeliveryjfsteel.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.utils.PermissionUtils;

import static br.com.home.maildeliveryjfsteel.utils.PermissionUtils.GPS_PERMISSION;

/**
 * Created by Admin on 02/12/2017.
 */

public class LocationActivityTest extends AppCompatActivity implements LocationListener {
    private TextView textView;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private boolean isNegouAlgumaPermissao = false;
    private LocationManager locationManager;
    private int countPermission = 0;
    private Location location, netLocation, gpsLocation;
    private Context mContext = this;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        textView = (TextView) findViewById(R.id.text_location);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager.removeUpdates(LocationActivityTest.this);
            }
        });
        verifyProviderLocation();
    }

    private void getLastKnowLocation(String provider) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (countPermission == 0) {
                PermissionUtils.requestPermissions(this, GPS_PERMISSION, Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION));
            }
            countPermission++;
            return;
        }
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 15, 0, this);
        } else {
            netLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 15, 0, this);
        }
        getBestLocation();
    }

    private void getBestLocation() {
        if (gpsLocation != null && netLocation != null) {
            if (gpsLocation.getAccuracy() > netLocation.getAccuracy()) {
                setLocation(netLocation);
            } else {
                setLocation(gpsLocation);
            }
        } else {
            if (gpsLocation != null) {
                setLocation(gpsLocation);
            } else if (netLocation != null) {
                setLocation(netLocation);
            }
        }
    }

    private void verifyProviderLocation() {
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps_enabled) {
            getLastKnowLocation(LocationManager.GPS_PROVIDER);
        }
        if (network_enabled) {
            getLastKnowLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (!gps_enabled && !network_enabled) {
            setLocation(null);
            showToast("Nenhuma forma de rastreamento habilitada!");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        showToast(location.toString());
        Log.d("onLocationChanged ", String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()));
//        setLocation(location);
        if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            netLocation = location;
        } else {
            gpsLocation = location;
        }
//        removeLocationUpdates();
        getBestLocation();
        textView.setText(textView.getText().toString() + "\n" + location.getProvider() + ", " + location.getLatitude() + ", " + location.getLongitude() + "\n");
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorBackgroundDialog));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        showToast("O " + provider + " foi atualizado!\nStatus = " + status);
        Log.d("statusChanged = ", extras.toString());
        textView.setText(textView.getText().toString() + "\n" + "O " + provider + " foi atualizado!Status = " + status + "\n");
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorForeground));
    }

    @Override
    public void onProviderEnabled(String provider) {
        showToast("O " + provider + " foi ligado!");
        textView.setText(textView.getText().toString() + "\n" + "O " + provider + " foi ligado!\n");
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @Override
    public void onProviderDisabled(String provider) {
        showToast("O " + provider + " foi desligado!");
        setLocation(null);
        textView.setText(textView.getText().toString() + "\n" + "O " + provider + " foi desligado!\n");
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorAlertErro));
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void showToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }
}
