package com.apurva.ratatouille;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class FilterActivity extends AppCompatActivity {

    private Switch mexican, fastFood, iceCream, coldTrucks, indian, others;
    private SeekBar radius;
    private TextView textSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        //initialize the switches and the seek bar.
        initialize();
    }

    private void initialize() {
        mexican = (Switch) findViewById(R.id.switch1);
        fastFood = (Switch) findViewById(R.id.switch2);
        iceCream = (Switch) findViewById(R.id.switch3);
        coldTrucks = (Switch) findViewById(R.id.switch4);
        indian = (Switch) findViewById(R.id.switch5);
        others = (Switch) findViewById(R.id.switch6);
        radius = (SeekBar)findViewById(R.id.seekBar);
        textSeekBar = (TextView) findViewById(R.id.text_seek_bar);
        //assigning click events to these views.
        initializeClickEvents();
    }

    private void initializeClickEvents() {
        //set the values of these views to the static value in the Parameters.java class.
        textSeekBar.setText("" + Parameters.radius);
        mexican.setChecked(Parameters.isMexican);
        fastFood.setChecked(Parameters.isFastFood);
        coldTrucks.setChecked(Parameters.isColdTrucks);
        iceCream.setChecked(Parameters.isIcecream);
        others.setChecked(Parameters.isOthers);
        indian.setChecked(Parameters.isIndian);

        //default settings of the seek bar(radius)
        if(Parameters.radius == 1000){
            radius.setProgress(0);
        }
        else {
            radius.setProgress(Parameters.radius/200);
        }

        //to listen to the changes when the user changes the seek bar progress.
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int bufferRadius;
                if(progress < 10){
                    bufferRadius = 1000;
                }
                else {
                    bufferRadius = progress * 200;
                }
                textSeekBar.setText(""+bufferRadius);
                Parameters.radius = bufferRadius;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //listening to the toggling of the switches
        mexican.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Parameters.isMexican = isChecked;
                if(isChecked) {
                    fastFood.setChecked(false);
                    indian.setChecked(false);
                    coldTrucks.setChecked(false);
                }
            }
        });

       fastFood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Parameters.isFastFood = isChecked;
                if(isChecked){
                    indian.setChecked(false);
                    coldTrucks.setChecked(false);
                    mexican.setChecked(false);
                }
            }
        });

        iceCream.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Parameters.isIcecream = isChecked;
            }
        });

        coldTrucks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Parameters.isColdTrucks = isChecked;
                if(isChecked){
                    indian.setChecked(false);
                    mexican.setChecked(false);
                    fastFood.setChecked(false);
                }
            }
        });

        indian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Parameters.isIndian = isChecked;
                if(isChecked){
                    coldTrucks.setChecked(false);
                    mexican.setChecked(false);
                    fastFood.setChecked(false);
                }
            }
        });

        others.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Parameters.isOthers = isChecked;
            }
        });
    }


}
