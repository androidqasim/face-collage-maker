package dauroi.photoeditor.api.response;

import java.util.List;

/**
 * Created by Wisekey on 4/23/2015.
 */
public class LogoutResponse extends BaseResponse{
    private List<TokenInfo> mOtherDevices;

    public List<TokenInfo> getOtherDevices() {
        return mOtherDevices;
    }
}
