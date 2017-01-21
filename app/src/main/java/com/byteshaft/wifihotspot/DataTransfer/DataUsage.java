package com.byteshaft.wifihotspot.DataTransfer;

/**
 * Created by shahid on 20/01/2017.
 */
import android.net.TrafficStats;

class DataUsage {
    long tx=0;
    long rx=0;
    String tag=null;

    DataUsage() {
        tx=TrafficStats.getTotalTxBytes();
        rx=TrafficStats.getTotalRxBytes();
    }

    DataUsage(int uid, String tag) {
        tx=TrafficStats.getUidTxBytes(uid);
        rx=TrafficStats.getUidRxBytes(uid);
        this.tag=tag;
    }
}