package com.codetho.photocollage.template;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vanhu_000 on 3/11/2016.
 * Parcelable is not complete. It can not save Path object.
 */
public class PhotoItem {
    public static final int SHRINK_METHOD_DEFAULT = 0;
    public static final int SHRINK_METHOD_3_3 = 1;
    public static final int SHRINK_METHOD_USING_MAP = 2;
    public static final int SHRINK_METHOD_3_6 = 3;
    public static final int SHRINK_METHOD_3_8 = 4;
    public static final int SHRINK_METHOD_COMMON = 5;
    public static final int CORNER_METHOD_DEFAULT = 0;
    public static final int CORNER_METHOD_3_6 = 1;
    public static final int CORNER_METHOD_3_13 = 2;
    //Primary info
    public float x = 0;
    public float y = 0;
    public int index = 0;
    public String imagePath;
    public String maskPath;
    //Using point list to construct view. All points and width, height are in [0, 1] range.
    public ArrayList<PointF> pointList = new ArrayList<>();
    public RectF bound = new RectF();
    //Using path to create
    public Path path = null;
    public RectF pathRatioBound = null;
    public boolean pathInCenterHorizontal = false;
    public boolean pathInCenterVertical = false;
    public boolean pathAlignParentRight = false;
    public float pathScaleRatio = 1;
    public boolean fitBound = false;
    //other info
    public boolean hasBackground = false;
    public int shrinkMethod = SHRINK_METHOD_DEFAULT;
    public int cornerMethod = CORNER_METHOD_DEFAULT;
    public boolean disableShrink = false;
    public HashMap<PointF, PointF> shrinkMap;
    //Clear polygon or arc area
    public ArrayList<PointF> clearAreaPoints;
    //Clear an area using path
    public Path clearPath = null;
    public RectF clearPathRatioBound = null;
    public boolean clearPathInCenterHorizontal = false;
    public boolean clearPathInCenterVertical = false;
    public boolean clearPathAlignParentRight = false;
    public float clearPathScaleRatio = 1;
    public boolean centerInClearBound = false;

//    public PhotoItem(){
//
//    }

//    public static final Parcelable.Creator<PhotoItem> CREATOR
//            = new Parcelable.Creator<PhotoItem>() {
//        public PhotoItem createFromParcel(Parcel in) {
//            return new PhotoItem(in);
//        }
//
//        public PhotoItem[] newArray(int size) {
//            return new PhotoItem[size];
//        }
//    };
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        //Primary info
//        dest.writeFloat(x);
//        dest.writeFloat(y);
//        dest.writeInt(index);
//        dest.writeString(imagePath);
//        dest.writeString(maskPath);
//        //Using point list to construct view. All points and width, height are in [0, 1] range.
//        dest.writeTypedList(pointList);
//        dest.writeParcelable(bound, flags);
//        //Using path to create
//        dest.writeParcelable(pathRatioBound, flags);
//        dest.writeBooleanArray(new boolean[]{pathInCenterHorizontal, pathInCenterVertical, pathAlignParentRight, fitBound,
//                hasBackground, disableShrink, clearPathInCenterHorizontal,
//                clearPathInCenterVertical, clearPathAlignParentRight, centerInClearBound});
//        dest.writeFloat(pathScaleRatio);
//        //other info
//        dest.writeInt(shrinkMethod);
//        dest.writeInt(cornerMethod);
//        ArrayList<PointF> shrinkKeys = new ArrayList<>();
//        ArrayList<PointF> shrinkValues = new ArrayList<>();
//        if (shrinkMap != null) {
//            for (PointF p : shrinkMap.keySet())
//                shrinkKeys.add(p);
//            for (PointF p : shrinkMap.values())
//                shrinkValues.add(p);
//        }
//        dest.writeTypedList(shrinkKeys);
//        dest.writeTypedList(shrinkValues);
//        //Clear polygon or arc area
//        if (clearAreaPoints != null && clearAreaPoints.size() > 0) {
//            dest.writeTypedList(clearAreaPoints);
//        } else {
//            dest.writeTypedList(new ArrayList<PointF>());
//        }
//        //Clear an area using path
//        dest.writeParcelable(clearPathRatioBound, flags);
//        dest.writeFloat(clearPathScaleRatio);
//    }
//
//    protected PhotoItem(Parcel in) {
//        //Primary info
//        x = in.readFloat();
//        y = in.readFloat();
//        index = in.readInt();
//        imagePath = in.readString();
//        maskPath = in.readString();
//        //Using point list to construct view. All points and width, height are in [0, 1] range.
//        pointList = new ArrayList<>();
//        in.readTypedList(pointList, PointF.CREATOR);
//        bound = in.readParcelable(RectF.class.getClassLoader());
//        //Using path to create
//        pathRatioBound = in.readParcelable(RectF.class.getClassLoader());
//        boolean[] b = new boolean[10];
//        in.readBooleanArray(b);
//        pathInCenterHorizontal = b[0];
//        pathInCenterVertical = b[1];
//        pathAlignParentRight = b[2];
//        fitBound = b[3];
//        hasBackground = b[4];
//        disableShrink = b[5];
//        clearPathInCenterHorizontal = b[6];
//        clearPathInCenterVertical = b[7];
//        clearPathAlignParentRight = b[8];
//        centerInClearBound = b[9];
//        pathScaleRatio = in.readFloat();
//        //other info
//        shrinkMethod = in.readInt();
//        cornerMethod = in.readInt();
//        ArrayList<PointF> shrinkKeys = new ArrayList<>();
//        ArrayList<PointF> shrinkValues = new ArrayList<>();
//        in.readTypedList(shrinkKeys, PointF.CREATOR);
//        in.readTypedList(shrinkValues, PointF.CREATOR);
//        if (shrinkKeys.size() > 0 && shrinkKeys.size() == shrinkValues.size()) {
//            shrinkMap = new HashMap<>();
//            final int size = shrinkKeys.size();
//            for (int idx = 0; idx < size; idx++)
//                shrinkMap.put(shrinkKeys.get(idx), shrinkValues.get(idx));
//        }
//        //Clear polygon or arc area
//        ArrayList<PointF> clearPs = new ArrayList<>();
//        in.readTypedList(clearPs, PointF.CREATOR);
//        if (clearPs.size() > 0) {
//            clearAreaPoints = new ArrayList<>();
//            clearAreaPoints.addAll(clearPs);
//        }
//        //Clear an area using path
//        clearPathRatioBound = in.readParcelable(RectF.class.getClassLoader());
//        clearPathScaleRatio = in.readFloat();
//    }
}
