package dauroi.photoeditor.api.response;

import java.util.List;

public class ListStoreItemResponse extends BaseResponse {
	private int mTotal;
	private int mResultCount;
	private List<StoreItem> mItems;

	public int getTotal() {
		return mTotal;
	}

	public int getResultCount() {
		return mResultCount;
	}

	public List<StoreItem> getItems() {
		return mItems;
	}
}
