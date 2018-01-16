package com.msh7.game.tictactoe;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Respect on 4/10/2017.
 */

public class ConnectionUtils {
    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        return (ni != null && ni.isConnected() && ni.getType() == ConnectivityManager.TYPE_WIFI);
    }
}
