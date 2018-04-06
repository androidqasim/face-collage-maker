package dauroi.photoeditor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import dauroi.photoeditor.api.AdsService;
import dauroi.photoeditor.api.response.CheckShowingAdsResponse;
import dauroi.photoeditor.config.ALog;
import dauroi.photoeditor.utils.InterstitialAdCreator;
import dauroi.photoeditor.utils.NetworkUtils;
import dauroi.photoeditor.utils.TempDataContainer;

public class NetworkStateReceiver extends BroadcastReceiver {
    public static interface NetworkStateReceiverListener {
        void onNetworkAvailable();

        void onNetworkUnavailable();
    }

    private static List<NetworkStateReceiverListener> networkStateListeners = new ArrayList<NetworkStateReceiverListener>();
    private static boolean connected;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getExtras() == null) {
            connected = false;
            return;
        } else {
            connected = NetworkUtils.checkNetworkAvailable(context);
            notifyStateToAll();
        }
    }

    private void notifyStateToAll() {
        for (NetworkStateReceiverListener listener : networkStateListeners)
            notifyState(listener);
    }

    private static void notifyState(NetworkStateReceiverListener listener) {
        if (connected == false || listener == null)
            return;

        if (connected == true)
            listener.onNetworkAvailable();
        else
            listener.onNetworkUnavailable();
    }

    public static void addListener(NetworkStateReceiverListener l) {
        networkStateListeners.add(l);
        notifyState(l);
    }

    public static void removeListener(NetworkStateReceiverListener l) {
        networkStateListeners.remove(l);
    }

    public static void clear() {
        connected = false;
        networkStateListeners.clear();
    }

}
