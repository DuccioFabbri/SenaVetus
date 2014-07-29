package it.duccius.musicplayer;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class _GeoActivity extends Activity implements LocationListener {
  private TextView latituteField;
  private TextView longitudeField;
  private LocationManager locationManager;
  private String provider;

  
/** Called when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.download_selection);
    latituteField = (TextView) findViewById(R.id.TextView02);
    longitudeField = (TextView) findViewById(R.id.TextView04);

    // Get the location manager
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L,
            500.0f, this);
    Location location1 = locationManager
            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
    if (location1 != null) {
        double latitude = location1.getLatitude();
        double longitude = location1.getLongitude();
    }
    // Define the criteria how to select the locatioin provider -> use
    // default
    //https://code.google.com/p/android/issues/detail?id=7849
    Criteria criteria = new Criteria();
    criteria.setAccuracy(Criteria.ACCURACY_FINE);
    provider = locationManager.getBestProvider(criteria, true);
    Location location = locationManager.getLastKnownLocation(provider);
    locationManager.requestLocationUpdates(                
            LocationManager.GPS_PROVIDER,0,0,(LocationListener) this);
   
//   
//    // Initialize the location fields
//    if (location != null) {
//      System.out.println("Provider " + provider + " has been selected.");
//      onLocationChanged(location);
//    } else {
//      latituteField.setText("Location not available");
//      longitudeField.setText("Location not available");
//    }
  }

  /* Request updates at startup */
  @Override
  protected void onResume() {
    super.onResume();
    locationManager.requestLocationUpdates(provider, 400, 1, this);
  }

  /* Remove the locationlistener updates when Activity is paused */
  @Override
  protected void onPause() {
    super.onPause();
    locationManager.removeUpdates(this);
  }

  @Override
  public void onLocationChanged(Location location) {
    int lat = (int) (location.getLatitude());
    int lng = (int) (location.getLongitude());
    latituteField.setText(String.valueOf(lat));
    longitudeField.setText(String.valueOf(lng));
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onProviderEnabled(String provider) {
    Toast.makeText(this, "Enabled new provider " + provider,
        Toast.LENGTH_SHORT).show();

  }

  @Override
  public void onProviderDisabled(String provider) {
    Toast.makeText(this, "Disabled provider " + provider,
        Toast.LENGTH_SHORT).show();
  }
} 
