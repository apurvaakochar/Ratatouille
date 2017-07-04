package com.apurva.ratatouille;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Created by apurv on 04-07-2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class MapsActivityTest {
    LatLng latLng;
    MapsActivity mapsActivity;

    @Before
    public void setUp() throws Exception {
        mapsActivity = Mockito.mock(MapsActivity.class);
        latLng = new LatLng(122, -32);
    }

    @Test
    public void tester() throws Exception {

    }
}