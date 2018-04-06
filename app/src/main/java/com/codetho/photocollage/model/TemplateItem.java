package com.codetho.photocollage.model;

import com.codetho.photocollage.template.PhotoItem;
import com.codetho.photocollage.template.PhotoLayout;

import java.util.ArrayList;
import java.util.List;

import dauroi.photoeditor.model.ImageTemplate;

/**
 * Created by vanhu_000 on 3/25/2016.
 */
public class TemplateItem extends ImageTemplate {
    private int mSectionManager;
    private int mSectionFirstPosition;
    private boolean mIsHeader = false;
    private String mHeader;

    private boolean mIsAds = false;
    private List<PhotoItem> mPhotoItemList = new ArrayList<>();

    public TemplateItem() {

    }

    public TemplateItem(ImageTemplate template) {
        setLanguages(template.getLanguages());
        setPackageId(template.getPackageId());
        setPreview(template.getPreview());
        setTemplate(template.getTemplate());
        setChild(template.getChild());
        setTitle(template.getTitle());
        setThumbnail(template.getThumbnail());
        setSelectedThumbnail(template.getSelectedThumbnail());
        setSelected(template.isSelected());
        // To be used to display
        setShowingType(template.getShowingType());
        // To be used in database
        setLastModified(template.getLastModified());
        setStatus(template.getStatus());
        setId(template.getId());
        mPhotoItemList = PhotoLayout.parseImageTemplate(template);
    }

    public void setHeader(String header) {
        mHeader = header;
    }

    public String getHeader() {
        return mHeader;
    }

    public List<PhotoItem> getPhotoItemList() {
        return mPhotoItemList;
    }

    public void setSectionFirstPosition(int sectionFirstPosition) {
        mSectionFirstPosition = sectionFirstPosition;
    }

    public int getSectionFirstPosition() {
        return mSectionFirstPosition;
    }

    public void setSectionManager(int sectionManager) {
        mSectionManager = sectionManager;
    }

    public int getSectionManager() {
        return mSectionManager;
    }

    public boolean isHeader() {
        return mIsHeader;
    }

    public void setIsHeader(boolean isHeader) {
        mIsHeader = isHeader;
    }

    public void setIsAds(boolean isAds) {
        mIsAds = isAds;
    }

    public boolean isAds() {
        return mIsAds;
    }
}
