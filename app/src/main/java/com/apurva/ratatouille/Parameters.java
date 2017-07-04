package com.apurva.ratatouille;

/**
 * Created by apurv on 03-07-2017.
 */
public class Parameters {
    //set the radius of the region for the search of food trucks
    public static int radius = 2000;
    //Boolean parameters changing as per the selection by the users.
    public static boolean isMexican = true;
    public static boolean isIndian = false;
    public static boolean isIcecream = true;
    public static boolean isOthers = false;
    public static boolean isFastFood = false;
    public static boolean isColdTrucks = false;
    //to check if the location permission has been granted.
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final String TAG = "MapActivity";
}
