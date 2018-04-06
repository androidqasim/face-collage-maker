package dauroi.photoeditor.api.response;

import java.util.List;

import dauroi.photoeditor.model.AppInfo;

/**
 * Created by Wisekey on 5/10/2015.
 */
public class GetAppInfoResponse extends BaseResponse{
    private List<AppInfo> mApps;

    public List<AppInfo> getApps() {
        return mApps;
    }
}
