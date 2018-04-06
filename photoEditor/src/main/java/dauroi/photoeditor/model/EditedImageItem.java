package dauroi.photoeditor.model;

public class EditedImageItem {
	private String mThumbnail;
	private String mImage;

	public void setImage(String image) {
		mImage = image;
	}

	public void setThumbnail(String thumbnail) {
		mThumbnail = thumbnail;
	}

	public String getImage() {
		return mImage;
	}

	public String getThumbnail() {
		return mThumbnail;
	}
}
