package com.changami.app.batteryisready.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;

import com.changami.app.batteryisready.R;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private final int MAX = 100;
    private final int MIN = 0;

    @InjectView(R.id.switchOnOff)
    Switch switchOnOff;
    @InjectView(R.id.editValue)
    TextView textView;

    SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preference = getSharedPreferences(getString(R.string.preference_name), Context.MODE_PRIVATE);
        switchOnOff.setChecked(preference.getBoolean(getString(R.string.preference_available), false));
        switchOnOff.setOnCheckedChangeListener(this);
        textView.setText(String.valueOf(preference.getInt(getString(R.string.preference_target_volume), MAX)));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences.Editor prefEditor = preference.edit();
        prefEditor.putBoolean(getString(R.string.preference_available), isChecked);
        prefEditor.apply();
    }

    // TODO: 以下のクリックイベントをまとめる
    @OnClick(R.id.plusButton)
    void pushPlusButton(View v) {
        int value = Integer.parseInt(textView.getText().toString());
        if (value < MAX) {
            value++;
        }
        textView.setText(String.valueOf(value));
        preserveTargetVolume(value);
    }

    @OnLongClick(R.id.plusButton)
    boolean longPushPlusButton(View v) {
        int value = Integer.parseInt(textView.getText().toString());
        if (value + 10 <= MAX) {
            value += 10;
        } else {
            value = 100;
        }
        textView.setText(String.valueOf(value));
        preserveTargetVolume(value);

        return true;
    }

    @OnClick(R.id.minusButton)
    void pushMinusButton(View v) {
        int value = Integer.parseInt(textView.getText().toString());
        if (value > MIN) {
            value--;
        }
        textView.setText(String.valueOf(value));
        preserveTargetVolume(value);
    }

    @OnLongClick(R.id.minusButton)
    boolean longPushMinusButton(View v) {
        int value = Integer.parseInt(textView.getText().toString());
        if (value - 10 >= MIN) {
            value -= 10;
        } else {
            value = 0;
        }
        textView.setText(String.valueOf(value));
        preserveTargetVolume(value);

        return true;
    }

    private void preserveTargetVolume(int volume) {
        SharedPreferences.Editor prefEditor = preference.edit();
        prefEditor.putInt(getString(R.string.preference_target_volume), volume);
        prefEditor.apply();
    }
}
