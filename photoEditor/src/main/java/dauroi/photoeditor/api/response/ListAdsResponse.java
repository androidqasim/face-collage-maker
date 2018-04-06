package dauroi.photoeditor.api.response;

import java.util.List;

/**
 * Created by Wisekey on 5/10/2015.
 */
public class ListAdsResponse extends BaseResponse {
    private List<GetAdsResponse> mAds;

    public List<GetAdsResponse> getAds() {
        return mAds;
    }
}
