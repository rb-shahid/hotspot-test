package com.byteshaft.wifihotspot;

import android.content.BroadcastReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.Button;
import android.widget.Toast;


import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button invite;
    private Button join;
    wifiHotSpots hotutil;
    WifiStatus wifiStatus;
    BroadcastReceiver receiver;
    WifiAPController wifiAPController;
    WifiManager wifiManager;
    WifiStatus wu;
    private static int result_lavel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiAPController = new WifiAPController();
        wu = new WifiStatus(this);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        hotutil = new wifiHotSpots(getApplicationContext());
        wifiStatus = new WifiStatus(getApplicationContext());
        invite = (Button) findViewById(R.id.Invite);
        join = (Button) findViewById(R.id.Join);
        invite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (wu.checkWifi(wu.SUPPORT_WIFI_DIRECT)) {
                    Toast.makeText(getApplicationContext(), "Yes Device Support Wifi Direct", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "No Device Not Support Wifi Wifi Direct", Toast.LENGTH_LONG).show();
                }


            }
        });
        join.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hotutil.addWifiNetwork("ssid22", "pass", "OPEN");
            }
        });
    }

    public void inviteFriend(wifiHotSpots hotutil) {

        if (hotutil.setHotSpot("SSID", "")) {
            hotutil.startHotSpot(true);
        }
        hotutil.setAndStartHotSpot(true, "Test");
    }

    public void joinFriend(final WifiStatus wifiStatus, final wifiHotSpots hotutil) {
        if (wifiStatus.checkWifi(wifiStatus.IS_WIFI_ON)) {
            hotutil.scanNetworks();
            List<ScanResult> results = hotutil.getHotspotsList();
            for (ScanResult result : results) {
                //Toast.makeText(getApplicationContext(), result.SSID + " " + result.level,
                //        Toast.LENGTH_SHORT).show();
                if (result.SSID.equalsIgnoreCase("SSID")) {

                    Toast.makeText(getApplicationContext(), result.SSID + " Found SSID" + result.level,
                            Toast.LENGTH_SHORT).show();
                    hotutil.connectToHotspot("SSID", "");
                    try {
                        unregisterReceiver(receiver);
                        break;
                    } catch (Exception e) {
                        //error as trying to do unregistering twice?
                    }
                    hotutil.stopScan();
                }
            }

        } else {
            if (hotutil.isWifiApEnabled())
                hotutil.startHotSpot(false);
            //start wifi.
            wifiStatus.checkWifi(wifiStatus.WIFI_ON);

            receiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    final String action = intent.getAction();
                    if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                        List<ScanResult> results = hotutil.getHotspotsList();
                        for (ScanResult result : results) {
                            Toast.makeText(getApplicationContext(), result.SSID + " " + result.level,
                                    Toast.LENGTH_SHORT).show();
                            if (result.SSID.equalsIgnoreCase("SSID")) {
                                Toast.makeText(getApplicationContext(), "Found SSID", Toast.LENGTH_SHORT).show();
                                if (!wifiHotSpots.isConnectToHotSpotRunning)
                                    hotutil.connectToHotspot("SSID", "");
                                try {
                                    unregisterReceiver(receiver);
                                    break;
                                } catch (Exception e) {
                                    //trying to unregister twice? need vary careful about this.
                                }

                            }
                        }
                    }
                }

            };
            IntentFilter mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            registerReceiver(receiver, mIntentFilter);
        }

    }
}
