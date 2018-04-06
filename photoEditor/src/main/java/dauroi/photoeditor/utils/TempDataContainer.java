package dauroi.photoeditor.utils;

import java.util.ArrayList;
import java.util.List;

import dauroi.photoeditor.api.response.CheckShowingAdsResponse;
import dauroi.photoeditor.listener.OnInstallStoreItemListener;

public class TempDataContainer {
    private static TempDataContainer instance;

    public static TempDataContainer getInstance() {
        if (instance == null) {
            instance = new TempDataContainer();
        }

        return instance;
    }

    private List<OnInstallStoreItemListener> mOnInstallStoreItemListeners = new ArrayList<OnInstallStoreItemListener>();
    //common info
    private CheckShowingAdsResponse mCheckShowingAdsResponse = null;

    private TempDataContainer() {

    }

    public void setCheckShowingAdsResponse(CheckShowingAdsResponse checkShowingAdsResponse) {
        mCheckShowingAdsResponse = checkShowingAdsResponse;
    }

    public CheckShowingAdsResponse getCheckShowingAdsResponse() {
        return mCheckShowingAdsResponse;
    }

    public List<OnInstallStoreItemListener> getOnInstallStoreItemListeners() {
        return mOnInstallStoreItemListeners;
    }

    public void clear() {
        mOnInstallStoreItemListeners.clear();
    }
}
