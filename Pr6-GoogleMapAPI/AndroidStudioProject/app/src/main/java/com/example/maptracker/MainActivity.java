package com.example.maptracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Experiment 6 - Google Maps location tracking.
 * Shows the user's live position on a Google Map, draws the travelled path
 * (Polyline) and displays latitude, longitude, accuracy and distance walked.
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private FusedLocationProviderClient fusedClient;
    private LocationCallback locationCallback;

    private TextView tvInfo;
    private Button btnStart, btnStop;

    private final List<LatLng> pathPoints = new ArrayList<>();
    private Polyline pathLine;
    private Marker currentMarker;
    private Location previousLocation;
    private float totalDistanceMeters = 0f;
    private boolean tracking = false;
    private boolean centerOnlyOnce = false;

    private ActivityResultLauncher<String[]> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvInfo = findViewById(R.id.tvInfo);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        Button btnMyLocation = findViewById(R.id.btnMyLocation);
        Button btnClear = findViewById(R.id.btnClear);

        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Runtime permission result handler
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                (Map<String, Boolean> result) -> {
                    if (hasLocationPermission()) {
                        enableMyLocationLayer();
                        if (centerOnlyOnce) {
                            showLastKnownLocation();
                        } else {
                            startTracking();
                        }
                    } else {
                        Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
                    }
                });

        btnStart.setOnClickListener(v -> {
            centerOnlyOnce = false;
            if (hasLocationPermission()) {
                startTracking();
            } else {
                requestLocationPermission();
            }
        });
        btnStop.setOnClickListener(v -> stopTracking());
        btnMyLocation.setOnClickListener(v -> {
            centerOnlyOnce = true;
            if (hasLocationPermission()) {
                showLastKnownLocation();
            } else {
                requestLocationPermission();
            }
        });
        btnClear.setOnClickListener(v -> clearPath());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        // Default camera position (centre of India) until a real fix arrives
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(20.5937, 78.9629), 4f));
        if (hasLocationPermission()) {
            enableMyLocationLayer();
        }
    }

    // ---------------------------------------------------------------- permissions
    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        permissionLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION});
    }

    @SuppressWarnings("MissingPermission")
    private void enableMyLocationLayer() {
        if (map != null && hasLocationPermission()) {
            map.setMyLocationEnabled(true);   // blue dot
        }
    }

    // ---------------------------------------------------------------- one-time location
    @SuppressWarnings("MissingPermission")
    private void showLastKnownLocation() {
        if (!hasLocationPermission() || map == null) return;
        fusedClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng p = new LatLng(location.getLatitude(), location.getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(p, 17f));
                updateInfo(location);
            } else {
                Toast.makeText(this, "No last location yet. Turn on GPS or use Start Tracking.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // ---------------------------------------------------------------- tracking
    @SuppressWarnings("MissingPermission")
    private void startTracking() {
        if (tracking || map == null || !hasLocationPermission()) return;

        // Update every 3 s (fastest 2 s) but only if user moved at least 2 m
        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
                .setMinUpdateIntervalMillis(2000L)
                .setMinUpdateDistanceMeters(2f)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                for (Location location : result.getLocations()) {
                    onNewLocation(location);
                }
            }
        };

        fusedClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
        tracking = true;
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        tvInfo.setText("Tracking started. Waiting for GPS fix...");
    }

    private void stopTracking() {
        if (locationCallback != null) {
            fusedClient.removeLocationUpdates(locationCallback);
            locationCallback = null;
        }
        tracking = false;
        previousLocation = null;
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        tvInfo.append("\nTracking stopped.");
    }

    private void onNewLocation(Location location) {
        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());

        if (previousLocation != null) {
            totalDistanceMeters += previousLocation.distanceTo(location);
        }
        previousLocation = location;

        pathPoints.add(point);
        if (pathLine == null) {
            pathLine = map.addPolyline(new PolylineOptions().color(Color.BLUE).width(10f));
        }
        pathLine.setPoints(pathPoints);

        if (currentMarker == null) {
            currentMarker = map.addMarker(new MarkerOptions().position(point).title("You are here"));
        } else {
            currentMarker.setPosition(point);
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 17f));
        updateInfo(location);
    }

    private void updateInfo(Location location) {
        tvInfo.setText(String.format(Locale.getDefault(),
                "Lat: %.6f   Lng: %.6f%nAccuracy: %.1f m   Points: %d%nDistance travelled: %.1f m",
                location.getLatitude(), location.getLongitude(), location.getAccuracy(),
                pathPoints.size(), totalDistanceMeters));
    }

    private void clearPath() {
        pathPoints.clear();
        totalDistanceMeters = 0f;
        previousLocation = null;
        if (pathLine != null) {
            pathLine.remove();
            pathLine = null;
        }
        if (currentMarker != null) {
            currentMarker.remove();
            currentMarker = null;
        }
        tvInfo.setText(tracking ? "Path cleared. Tracking continues..." : getString(R.string.status_idle));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationCallback != null) {
            fusedClient.removeLocationUpdates(locationCallback);
        }
    }
}
