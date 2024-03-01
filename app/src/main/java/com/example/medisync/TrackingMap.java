package com.example.medisync;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TrackingMap extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {

    private GoogleMap mMap;
    private Location myLocation = null;
    private LatLng start = null;
    private LatLng start1 = null;
    private LatLng end = null;
    private LatLng end1 = null;

    double latitude, longitude;
    private loading loadingDialog;
    private List<Polyline> polylines = null;
    private final static int LOCATION_REQUEST_CODE = 23;
    private boolean locationPermission = false;

    private static final double DEFAULT_LATITUDE = 0.0;  // Replace with your default latitude
    private static final double DEFAULT_LONGITUDE = 0.0; // Replace with your default longitude

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracking_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Request location permission.
        requestPermission();

        // Initialize Google Map fragment to show the map.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        loadingDialog = new loading(getContext());
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            locationPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If permission granted.
                    locationPermission = true;
                    getMyLocation();

                } else {
                    // Permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
        }
    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                myLocation = location;
                LatLng ltlng = new LatLng(location.getLatitude(), location.getLongitude());
                start1 = ltlng;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ltlng, 16f);
                mMap.animateCamera(cameraUpdate);
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                end = latLng;
                mMap.clear();
                start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                start1 = start;
                FindRoutes(start, end1);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (locationPermission) {
            loadingDialog.show();
            getMyLocation();

            GlobalData globalData = GlobalData.getInstance();
            String hospitalName = globalData.getSelectedHospital();

            FirebaseFirestore.getInstance().collection("Ambulance")
                    .whereEqualTo("Hospital_Name", hospitalName)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            // Handle the query result here
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                // Access document data using document.getData()
                                latitude = document.getDouble("latitude");
                                longitude = document.getDouble("longitude");
                                LatLng ltlngw = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                                Toast.makeText(getActivity(), "" + ltlngw, Toast.LENGTH_SHORT).show();

                                // Set the destination (end1) based on the retrieved latitude and longitude
                                end1 = new LatLng(latitude, longitude);
                                FindRoutes(ltlngw, end1);


                                // Check if start1 is not null before finding routes
                                if (start1 != null) {
                                    // Find the route from the current location to the destination
                                    //FindRoutes(myLocation, end1);
                                } else {
                                    Toast.makeText(requireContext(), "Unable to get the current location", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failures
                            Toast.makeText(requireContext(), "Error fetching data from Firestore", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        }
                    });
        }

    }

    public void FindRoutes(LatLng Start, LatLng End) {
        if (Start == null || End == null) {
            Toast.makeText(requireContext(), "Unable to get location" + start + "END:" + End, Toast.LENGTH_LONG).show();
            System.out.println("Uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuunable to get location " + start + "  END: " + End);
        } else {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key("AIzaSyC7ZQ5LDflovXKny22vY_f7SLtTdfwPs_0")
                    .build();
            routing.execute();
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = requireActivity().findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
        loadingDialog.dismiss();
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(requireContext(), "Finding Route...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (end1 != null) {
            CameraUpdate center;
            if (start != null) {
                center = CameraUpdateFactory.newLatLng(start);
            } else {
                // Provide a default location if start is null
                center = CameraUpdateFactory.newLatLng(new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE));
            }

            CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
            if (polylines != null) {
                polylines.clear();
            }
            PolylineOptions polyOptions = new PolylineOptions();
            LatLng polylineStartLatLng = null;
            LatLng polylineEndLatLng = null;
            polylines = new ArrayList<>();

            for (int i = 0; i < route.size(); i++) {
                if (i == shortestRouteIndex) {
                    polyOptions.color(ContextCompat.getColor(requireContext(), R.color.black));
                    polyOptions.width(7);
                    polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                    Polyline polyline = mMap.addPolyline(polyOptions);

                    List<LatLng> points = polyline.getPoints();
                    if (!points.isEmpty()) {
                        polylineStartLatLng = points.get(0);
                        polylineEndLatLng = points.get(points.size() - 1);
                    }

                    polylines.add(polyline);
                }
            }

            if (polylineStartLatLng != null && polylineEndLatLng != null) {
                // Add marker for the destination with direction
                float bearing = (float) getBearing(polylineStartLatLng, polylineEndLatLng);
                MarkerOptions endMarker = new MarkerOptions();
                endMarker.position(polylineEndLatLng);
                endMarker.title("Destination");
                endMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance_icon));
                endMarker.rotation(bearing); // Set the marker rotation based on the bearing
                mMap.addMarker(endMarker);

                // Move the camera to the specified location
                mMap.animateCamera(center);
                mMap.animateCamera(zoom);
            } else {
                // Handle the case where polylineStartLatLng or polylineEndLatLng is null
                Toast.makeText(requireContext(), "Error finding route", Toast.LENGTH_SHORT).show();
            }
            loadingDialog.dismiss();
        }
        else {
            loadingDialog.dismiss();
            // Handle the case where start or end1 is null
            Toast.makeText(requireContext(), "Error finding route. Start or end location is null.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled() {
        FindRoutes(start, end);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        FindRoutes(start, end);
    }

    // Method to calculate bearing between two LatLng points
    private double getBearing(LatLng start, LatLng end) {
        double deltaLongitude = end.longitude - start.longitude;
        double deltaLatitude = end.latitude - start.latitude;

        double angle = Math.atan2(deltaLongitude, deltaLatitude) * (180 / Math.PI);

        return (angle + 360) % 360;
    }
}
