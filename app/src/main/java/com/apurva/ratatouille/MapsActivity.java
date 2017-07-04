package com.apurva.ratatouille;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    //Google Map object
    private GoogleMap mMap;
    //Specified location, nearby whose food trucks are queried for.
    private Location mLocation = new Location("manual");
    private LatLng mLatLng;
    //Map Fragment;
    private SupportMapFragment mapFragment;
    //Layout of the Truck Details View
    private LinearLayout linearLayout;
    //Layout of the activity
    private FrameLayout frameLayout;
    //Progress bar for AsyncTask
    private ProgressBar mProgressBar;
    //Card View holding the autoplace completion widget
    private CardView mCardView;
    //Floating button leading to the Filter Activity
    private ImageButton mFilterButton;
    //Initialization of the text field in the truck details view
    private TextView mTextName;
    private TextView mTextAddress;
    private TextView mTextMenu;
    private TextView mTextTimings;
    private EditText textPlace;
    //Marker showing the location whose nearby the food trucks are to be located
    private Marker searchMarker;
    //Marker when a user clicks a food truck
    private Marker truckMarker;
    //JSON Array containing the dataset
    private JSONArray mJsonArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //content view to current layout
        setContentView(R.layout.activity_maps);
        //set linear layout to be invisible until the user clicks on a food truck
        askPermission();
        linearLayout = findViewById(R.id.truck_details);
        linearLayout.setVisibility(View.INVISIBLE);
        frameLayout = findViewById(R.id.frame_layout);
        //initializing google map fragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //setting up on click event for the filter button
        mFilterButton = findViewById(R.id.filter_button);
        mFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
                startActivity(intent);
            }
        });

        //initializing the autoplace completion widget and text views for the truck details
        initializePlaceFragment();
        initializeTextView();
    }

    private void initializeTextView() {
        mTextName = findViewById(R.id.text_name);
        mTextAddress = findViewById(R.id.text_address);
        mTextMenu = findViewById(R.id.text_menu);
        mTextTimings = findViewById(R.id.text_timings);
        mProgressBar = findViewById(R.id.progressBar);
        mCardView = findViewById(R.id.card_view);
    }

    private void initializePlaceFragment() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        //setting hint for the widget
        if(autocompleteFragment.getView()!=null) {
            textPlace = autocompleteFragment.getView().
                    findViewById(R.id.place_autocomplete_search_input);
            textPlace.setHint(R.string.specify_location);
        }
        //since we only want to search in the bounds of the San Francisco city and nearby areas
        autocompleteFragment.setBoundsBias(new LatLngBounds(
                new LatLng(37.674824, -122.508123),
                new LatLng(37.808944, -122.364199)
        ));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                //when the user selects a place, initialize the search
                initiateSearch(place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                //Toast to notify of the failure to auto find the place
                createToast("Error occurred, please wait");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        //search again when resumed, it will apply the recent filter settings
        if(mLocation.getLatitude()!= 0) {

           initiateSearch(mLatLng);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void createToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT ).show();
    }

    //to check if the location permission has been granted.
    private void askPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Parameters.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setPadding(20, 20, 20, 20);
        //create toast for giving the direction when the map is displayed
        createToast("Specify a location in the box above or by long pressing on the map");
        //set the onclick listeners for the map.
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        //set the starting position to San Francisco city
        LatLng startingPoint = new LatLng(37.771943, -122.416337);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(startingPoint).zoom(12).build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        //set the current location button, if the location permission is granted
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            //reposition the location button
            if (mapFragment.getView() != null) {
                View locationButton = ((View) mapFragment.getView().findViewById(1).getParent()).findViewById(2);
                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                rlp.setMargins(0, 0, 30, 30);
            }
        }
    }

    //mark trucks depending of the food type
    private void markTrucks(HashMap<String, LatLng> latLongList, int index) {
        for (Map.Entry<String, LatLng> entry : latLongList.entrySet()) {
            createMarker(entry.getValue(), entry.getKey(), index);
        }
    }

    protected Marker createMarker(LatLng truckLocations, String cartName, int index) {
        Marker marker = mMap.addMarker(new MarkerOptions()
                             .position(truckLocations));
        marker.setTag(cartName);
        switch(index) {
            case 0:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.stand));
                break;
            case 1:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.tacos));
                break;
            case 2:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icecream));
                break;
            case 3:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.indian));
                break;
            case 4:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.coldtruck));
                break;
            case 5:
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.fastfood));
                break;
            default:
                    break;
        }
        return marker;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Parameters.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();

                }

            }
        }
    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        /*
        if the user click for long on a map point,
        reset the search location and initiate the search again
        */
        textPlace.setText("");
        initiateSearch(latLng);
    }

    //to send the auto completetion to the back of the details view
    public static void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup)child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }

    private void initiateSearch(LatLng latLng) {
        /*
        Initialize the search, clear previous markers
        set the camera position to the search location
        execute AsyncTask.
         */
        mMap.clear();
        mLatLng = latLng;
        mLocation.setLatitude(latLng.latitude);
        mLocation.setLongitude(latLng.longitude);

        searchMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng));

        int zoomNumber = (int) (15-(0.0003)*Parameters.radius);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(zoomNumber).build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        try {
            new JsonParser().execute(mLocation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        /*
        When the marker of a food truck is clicked
        it's details are displayed on the left hand side
        by executing AsyncTask.
         */
        searchMarker.remove();
        if(truckMarker!= null) {
            truckMarker.remove();
        }
        truckMarker = mMap.addMarker(new MarkerOptions().position(marker.getPosition()));
        new LoadTruckDetails().execute(marker.getTag());
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        /*
        Make the details of the truck invisible when the map is clicked
        */
        linearLayout.setVisibility(View.INVISIBLE);
        linearLayout.setAlpha(0.0f);
        linearLayout.animate()
                .translationX(linearLayout.getWidth())
                .alpha(1.0f);
        mCardView.setAlpha(1f);

    }

    private class JsonParser extends AsyncTask<Location, Void, ArrayList<HashMap<String, LatLng>>> {
        private byte[] buffer;
        private Location spLocation;

        public JsonParser() throws JSONException {
            InputStream mStream;

            try {
                //Parse the JSON file
                mStream = getApplicationContext().getAssets().open("foodTrucks.json");
                //check if it is available
                int size = mStream.available();
                //create a new buffer to load json
                buffer = new byte[size];
                mStream.read(buffer);
                mStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public String loadJSONFromAsset() {
            //load json from the assets folder
            String json = null;
            try {
                json = new String(buffer, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            return json;
        }

        @Override
        protected void onPreExecute() {
            //display a progress bar while background process is executed
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<HashMap<String, LatLng>> doInBackground(Location... locations) {

            spLocation = locations[0];
            /*
            Array List of Hash Maps of different food trucks.
            Different strings were searched to identify the type of food trucks
            However it is an initial attempt.
            This requires more complex string search query
             */
            ArrayList<HashMap<String, LatLng>> list = new ArrayList<>();
            HashMap<String, LatLng> otherList = new HashMap<>();
            HashMap<String, LatLng> mexicanFoodList = new HashMap<>();
            HashMap<String, LatLng> iceCreamList = new HashMap<>();
            HashMap<String, LatLng> indianList = new HashMap<>();
            HashMap<String, LatLng> fastFoodList = new HashMap<>();
            HashMap<String, LatLng> coldTrucksList = new HashMap<>();

            try {
                JSONObject mJsonObject = new JSONObject(loadJSONFromAsset());
                mJsonArray = mJsonObject.getJSONArray("TruckDB");

                for (int i = 0; i < mJsonArray.length(); i++) {
                    JSONObject jsonInside = mJsonArray.getJSONObject(i);
                    if (jsonInside.has("Latitude") && jsonInside.has("Longitude") && jsonInside.has("ID")) {
                        Double latitudeValue = jsonInside.getDouble("Latitude");
                        Double longitudeValue = jsonInside.getDouble("Longitude");

                        String name = jsonInside.getString("ID");
                        String foodItem = jsonInside.optString("FoodItems");
                        Location tLocation = new Location("manual");
                        tLocation.setLatitude(latitudeValue);
                        tLocation.setLongitude(longitudeValue);
                        LatLng tLatLng = new LatLng(latitudeValue, longitudeValue);

                        if (insideBuffer(tLocation)) {
                            if(containsString("taco", foodItem)){
                                if(Parameters.isMexican) {
                                    mexicanFoodList.put(name, tLatLng);
                                }
                            }
                            else if((containsString("indian", foodItem)
                                    || containsString("masala", foodItem))){
                                if(Parameters.isIndian) {
                                    indianList.put(name, tLatLng);
                                }
                            }

                            else if(containsString("cold truck",
                                    foodItem)) {
                                if (Parameters.isColdTrucks) {
                                    coldTrucksList.put(name, tLatLng);
                                }
                            }
                            else if((containsString("burger", foodItem)
                                    || containsString("pizza", foodItem)
                                    || containsString("hot dog", foodItem)
                                    || containsString("sandwich", foodItem))){
                                if(Parameters.isFastFood){
                                fastFoodList.put(name, tLatLng);
                            }
                            }

                            else if(containsString("ice cream", foodItem)){
                                if(Parameters.isIcecream) {
                                    iceCreamList.put(name, tLatLng);
                                }
                            }

                            else if(Parameters.isOthers){
                                otherList.put(name, tLatLng);
                            }

                        }
                    }
                }
                list.add(otherList);
                list.add(mexicanFoodList);
                list.add(iceCreamList);
                list.add(indianList);
                list.add(coldTrucksList);
                list.add(fastFoodList);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return list;
        }

        private boolean insideBuffer(Location tLocation) {
            return spLocation.distanceTo(tLocation) < Parameters.radius;
        }

        private boolean containsString(String baby, String mother){
           return Pattern.compile(Pattern.quote(baby), Pattern.CASE_INSENSITIVE).matcher(mother).find();
        }


        @Override
        protected void onPostExecute(ArrayList<HashMap<String, LatLng>> result) {
            //create markers for the parsed truck locations
            mProgressBar.setVisibility(View.INVISIBLE);
            if(result.get(0).isEmpty() && result.get(1).isEmpty() && result.get(2).isEmpty()
                    && result.get(3).isEmpty() && result.get(4).isEmpty()&&
                    result.get(5).isEmpty()){
                createToast("No results found");
            }
            for(int i = 0; i < result.size(); i++) {
                if(!result.get(i).isEmpty())
                markTrucks(result.get(i), i);
            }
        }
    }

    private class LoadTruckDetails extends AsyncTask<Object, Void, ArrayList<String>> {
        /*
        Set the address, name , menu and opening timings of the food trucks
         */
        @Override
        protected ArrayList<String> doInBackground(Object... objects) {
            Object id = objects[0];
            ArrayList<String> truckAttr = new ArrayList<>();

            for (int i = 0; i < mJsonArray.length(); i++) {

                JSONObject jsonInside = mJsonArray.optJSONObject(i);
                if(jsonInside.optString("ID").equals(id)){

                    truckAttr.add(jsonInside.optString("Applicant"));
                    truckAttr.add(jsonInside.optString("Address"));
                    truckAttr.add(jsonInside.optString("FoodItems"));
                    truckAttr.add(jsonInside.optString("Timings"));
                }

            }
            return truckAttr;
        }



        @Override
        protected void onPostExecute(ArrayList<String> truckAttr) {
            if (!truckAttr.isEmpty()) {
                mTextName.setText(truckAttr.get(0));
                mTextAddress.setText(truckAttr.get(1));
                mTextMenu.setText(truckAttr.get(2));
                mTextTimings.setText(truckAttr.get(3));

                linearLayout.setVisibility(View.VISIBLE);
                mCardView.setAlpha(0.0f);
                linearLayout.setAlpha(0.0f);
                linearLayout.animate()
                        .translationX(linearLayout.getWidth()-frameLayout.getWidth() )
                        .alpha(1.0f);

                linearLayout.invalidate();

            }
        }


    }
}
