package com.contents.stg.fermagente.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.contents.stg.fermagente.R;
import com.contents.stg.fermagente.ctrl.Observer;
import com.contents.stg.fermagente.ctrl.PostBuilder;
import com.contents.stg.fermagente.ctrl.Subject;
import com.contents.stg.fermagente.model.PostCollection;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostActivity extends AppCompatActivity implements LocationListener, Observer<Address> {

    private EditText editComment;
    private LinearLayout positionLayout;
    private TextView locationTextView;
    private ImageButton placeButton;
    private ProgressBar progress;
    private LocationManager manager;
    private Snackbar gpsSnackbar;
    private PostBuilder builder = new PostBuilder();
    private boolean wasFound = false, wasAccepted = false;
    private Handler timeoutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            timeout();
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null)
            toolbar.setNavigationIcon(R.mipmap.clear);

        editComment = (EditText) findViewById(R.id.edit_comment);

        positionLayout = (LinearLayout) findViewById(R.id.layout_position);
        locationTextView = new TextView(this);
        locationTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
        locationTextView.setText(R.string.gps_do_recover);

        gpsSnackbar = Snackbar.make(positionLayout, R.string.gps_snackbar_alert, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.gps_snackbar_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setActionTextColor(ColorStateList.valueOf(Color.GREEN));

        float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (24 * scale + 0.5f);

        placeButton = (ImageButton) findViewById(R.id.place_button);
        placeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!wasFound || !wasAccepted)
                    registerForLocationUpdates();
            }
        });

        progress = new ProgressBar(this, null, android.R.attr.progressBarStyle);
        progress.setLayoutParams(new LinearLayout.LayoutParams(placeButton.getWidth(), placeButton.getHeight(), Gravity.CENTER_VERTICAL));
        progress.setIndeterminate(true);
        progress.setBackground(null);
        progress.setIndeterminateTintMode(PorterDuff.Mode.SRC_ATOP);

        positionLayout.addView(locationTextView);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        registerForLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterForLocationUpdates();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;
        int id = item.getItemId();
        if (id == R.id.action_confirm) {
            if (builder.isReady()) {
                builder.buildComment(editComment.getText().toString());
                builder.buildDate(new Date());
                builder.buildRating(4);

                PostCollection.instance().add(builder.retrieveObject());
            } else {
                Snackbar.make(positionLayout, R.string.gps_snackbar_wait, Snackbar.LENGTH_SHORT).show();
                return false;
            }
        } else
            result = super.onOptionsItemSelected(item);

        returnToMainActivity();
        return result;
    }

    private void returnToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void registerForLocationUpdates() {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsSnackbar.show();
            return;
        }

        positionLayout.removeView(placeButton);
        if (progress.getParent() == null)
            positionLayout.addView(progress, 0);
        locationTextView.setText(R.string.gps_recovering);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        prevLocation = null;
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        new TimerThread(timeoutHandler, 30000l).start();
    }

    public void unregisterForLocationUpdates() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        manager.removeUpdates(this);
    }

    private Location prevLocation;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onLocationChanged(Location location) {
        System.out.println("LOCATION CHANGED; ACCURACY: " + location.getAccuracy());
        if (location.getAccuracy() < 15f) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;

            manager.removeUpdates(this);
            new AsyncGeocodingTask(this).execute(location);
        }
        else if (prevLocation == null || prevLocation.getAccuracy() > location.getAccuracy()) {
            prevLocation = location;
        }
    }

    @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
    @Override public void onProviderEnabled(String provider) { }
    @Override public void onProviderDisabled(String provider) { }

    @Override
    public void alert(Address address) {
        positionLayout.removeView(progress);
        positionLayout.addView(placeButton, 0);
        String place = address.getAddressLine(0) + ", " + address.getLocality();
        locationTextView.setText(place);
        builder.buildPosition(address);
        wasAccepted = true;
    }

    @Override
    public void alertFailed() {
        locationTextView.setText(R.string.gps_failed);
        positionLayout.removeView(progress);
        positionLayout.addView(placeButton, 0);
    }

    void timeout() {
        synchronized (this) {
            unregisterForLocationUpdates();
            if (!wasFound) {
                if (prevLocation == null)
                    alertFailed();
                else
                    new AsyncGeocodingTask(this).execute(prevLocation);
            }
        }
    }

    private class AsyncGeocodingTask extends AsyncTask<Location, Void, Address> implements Subject<Address> {
        private Observer<Address> observer;
        private Geocoder geocoder;
        private boolean failed = false;

        private AsyncGeocodingTask(PostActivity activity) {
            geocoder = new Geocoder(activity, Locale.ITALY);
            subscribe(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            synchronized (observer) {
                wasFound = true;
            }
        }

        @Override
        protected Address doInBackground(Location... params) {
            if (params.length == 0)
                return null;
            Location location = params[0];
            try {
                List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                System.out.println(addressList.size());
                if (addressList.size() > 0)
                    return addressList.get(0);
                else return null;
            }
            catch (Exception e) {
                e.printStackTrace();
                failed = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Address address) {
            super.onPostExecute(address);

            synchronized (observer) {
                if (failed || address == null)
                    notifyFailed();
                else
                    notifyObservers(address);

                unsubscribe(null);
            }
        }

        @Override
        public void subscribe(Observer<Address> observer) {
            this.observer = observer;
        }

        @Override
        public void unsubscribe(Observer observer) {
            this.observer = null;
        }

        @Override
        public void notifyObservers(Address address) {
            observer.alert(address);
        }

        @Override
        public void notifyFailed() {
            observer.alertFailed();
        }
    }

    private class TimerThread extends Thread {

        private Handler handler;
        private long nap;

        TimerThread(Handler handler, long nap) {
            this.handler = handler;
            this.nap = nap;
            setDaemon(true);
        }

        @Override
        public void run() {
            super.run();
            try { sleep(nap); }
            catch (InterruptedException ignore) { }

            handler.sendEmptyMessage(0);
        }
    }
}
