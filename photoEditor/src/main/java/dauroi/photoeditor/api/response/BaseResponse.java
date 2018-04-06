package dauroi.photoeditor.api.response;

public class BaseResponse{
	private int mCode = -1;
	private String mMsgCode;
	private String mMessage;

	public int getCode() {
		return mCode;
	}
	
	public String getMessageCode() {
		return mMsgCode;
	}
	
	public String getMessage() {
		return mMessage;
	}
	public void setMessage(String message) {
		this.mMessage = message;
	}

	@Override
	public String toString() {
		return "code:" + mCode + "\n" + "msg_code:" + mMsgCode + "\n" + "message:" + mMessage;
	}
}
