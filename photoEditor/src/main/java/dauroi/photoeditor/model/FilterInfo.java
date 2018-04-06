package dauroi.photoeditor.model;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dauroi.com.imageprocessing.filter.ImageFilter;
import dauroi.com.imageprocessing.filter.ImageFilterGroup;
import dauroi.com.imageprocessing.filter.TwoInputFilter;
import dauroi.com.imageprocessing.filter.blend.AlphaBlendFilter;
import dauroi.com.imageprocessing.filter.blend.DestinationOverBlendFilter;
import dauroi.com.imageprocessing.filter.blend.SoftLightBlendFilter;
import dauroi.com.imageprocessing.filter.blend.SourceOverBlendFilter;
import dauroi.com.imageprocessing.filter.colour.ChannelLookupFilter;
import dauroi.com.imageprocessing.filter.colour.GrayscaleFilter;
import dauroi.com.imageprocessing.filter.colour.HueFilter;
import dauroi.com.imageprocessing.filter.colour.LookupFilter;
import dauroi.com.imageprocessing.filter.colour.MonochromeFilter;
import dauroi.com.imageprocessing.filter.colour.OneDimenLookupFilter;
import dauroi.com.imageprocessing.filter.colour.OpacityFilter;
import dauroi.com.imageprocessing.filter.colour.SaturationFilter;
import dauroi.com.imageprocessing.filter.colour.SepiaFilter;
import dauroi.com.imageprocessing.filter.effect.DilationFilter;
import dauroi.com.imageprocessing.filter.effect.EmbossFilter;
import dauroi.com.imageprocessing.filter.effect.HalftoneFilter;
import dauroi.com.imageprocessing.filter.effect.KuwaharaFilter;
import dauroi.com.imageprocessing.filter.effect.SketchFilter;
import dauroi.com.imageprocessing.filter.effect.SmoothToonFilter;
import dauroi.com.imageprocessing.filter.effect.ToneCurveFilter;
import dauroi.com.imageprocessing.filter.effect.VignetteFilter;
import dauroi.photoeditor.PhotoEditorApp;
import dauroi.photoeditor.config.ALog;
import dauroi.photoeditor.utils.PhotoUtils;
import dauroi.photoeditor.utils.Utils;

/**
 * Field names are used in Gson. So Don't rename if file 'package.json' doesn't
 * change field names.
 *
 * @author vanhu_000
 */
@SuppressLint("DefaultLocale")
public class FilterInfo extends ItemInfo {
    // H - HueFilter
    // S - SepiaFilter
    // Sat - SaturationFilter
    // M - MonochromeFilter
    // V - VignetteFilter
    // O - OpacityFilter
    // T - ToneCurveFilter
    // C - ChannelLookupFilter
    // G - GrayscaleFilter
    // L1 - OneDimenLookupFilter
    // L2 - LookupFilter
    // Ba - Blend Alpha
    // Bs - Blend Soft
    // Bd - Blend Destination Over
    // Bo - Blend Source Over
    //Sk - Sketch
    //K - Kuwahara
    //E - Emboss
    //St - Smooth toon
    //D - Dilation
    //Ha - Halftone
    private static final String TAG = FilterInfo.class.getSimpleName();
    private Language[] mNames;
    private long mPackageId;
    private ImageFilter mImageFilter;
    private String mCmd;
    private String mPackageFolder;
    // cache
    private Map<String, ArrayList<String>> mParamsMap = new HashMap<String, ArrayList<String>>();
    private String[] mCommands;

    public ImageFilter getImageFilter() {
        if (mImageFilter == null || !mImageFilter.isInitialized()) {
            buildImageFilter();
        }
        return mImageFilter;
    }

    public void setLanguages(Language[] lang) {
        mNames = lang;
    }
    
    public Language[] getLanguages() {
        return mNames;
    }

    public void setCmd(String cmd) {
        mCmd = cmd;
    }

    public String getCmd() {
        return mCmd;
    }

    public void setPackageFolder(String packageFolder) {
        mPackageFolder = packageFolder;
    }

    public String getPackageFolder() {
        return mPackageFolder;
    }

    public void setPackageId(long packageId) {
        mPackageId = packageId;
    }

    public long getPackageId() {
        return mPackageId;
    }

    private void buildImageFilter() {
        ALog.d(TAG, "buildImageFilter, name=" + getTitle());
        if (mCmd == null || mCmd.length() < 1) {
            mImageFilter = new ImageFilter();
            return;
        }

        if (mCommands == null) {
            mCommands = mCmd.split(",");
        }

        List<ImageFilter> imageFilters = new ArrayList<ImageFilter>();
        if (mCommands != null) {
            for (String cmd : mCommands) {
                imageFilters.add(createFilter(cmd));
            }
        }

        if (imageFilters.size() > 1) {
            ImageFilterGroup filters = new ImageFilterGroup(imageFilters);
            mImageFilter = filters;
        } else if (imageFilters.size() > 0) {
            mImageFilter = imageFilters.get(0);
        } else {
            mImageFilter = new ImageFilter();
        }
    }

    private ImageFilter createFilter(String cmd) {
        ArrayList<String> paramsList = mParamsMap.get(cmd);
        if (paramsList == null) {
            String[] params = cmd.trim().split("\\ ");
            paramsList = new ArrayList<String>();
            if (params != null && params.length > 0) {
                for (String str : params) {
                    String tmp = str.trim();
                    if (tmp.length() > 0) {
                        paramsList.add(tmp);
                    }
                }
            }

            mParamsMap.put(cmd, paramsList);
        }

        if (paramsList.size() > 0) {
            final String filterType = paramsList.get(0);
            if (filterType.equalsIgnoreCase("H")) {
                HueFilter hueFilter = new HueFilter();
                if (paramsList.size() > 1) {
                    try {
                        float hue = Float.parseFloat(paramsList.get(1));
                        hueFilter.setHue(hue);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                return hueFilter;
            } else if (filterType.equalsIgnoreCase("S")) {
                SepiaFilter sepiaFilter = new SepiaFilter();
                if (paramsList.size() > 1) {
                    try {
                        float intensity = Float.parseFloat(paramsList.get(1));
                        sepiaFilter.setIntensity(intensity);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                return sepiaFilter;
            } else if (filterType.equalsIgnoreCase("E")) {
                EmbossFilter embossFilter = new EmbossFilter();
                if (paramsList.size() > 1) {
                    try {
                        float intensity = Float.parseFloat(paramsList.get(1));
                        embossFilter.setIntensity(intensity);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                return embossFilter;
            } else if (filterType.equalsIgnoreCase("Sk")) {
                SketchFilter sketchFilter = new SketchFilter();
                if (paramsList.size() > 1) {
                    try {
                        float lineSize = Float.parseFloat(paramsList.get(1));
                        sketchFilter.setLineSize(lineSize);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return sketchFilter;
            } else if (filterType.equalsIgnoreCase("K")) {
                KuwaharaFilter kuwaharaFilter = new KuwaharaFilter();
                if (paramsList.size() > 1) {
                    try {
                        int radius = Integer.parseInt(paramsList.get(1));
                        kuwaharaFilter.setRadius(radius);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return kuwaharaFilter;
            } else if (filterType.equalsIgnoreCase("D")) {
                if (paramsList.size() > 1) {
                    try {
                        int radius = Integer.parseInt(paramsList.get(1));
                        return new DilationFilter(radius);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return new DilationFilter();
            } else if (filterType.equalsIgnoreCase("St")) {
                SmoothToonFilter smoothToonFilter = new SmoothToonFilter();
                if (paramsList.size() > 1) {
                    try {
                        float quantization = Float.parseFloat(paramsList.get(1));
                        smoothToonFilter.setQuantizationLevels(quantization);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    if (paramsList.size() > 2) {
                        try {
                            float threshold = Float.parseFloat(paramsList.get(2));
                            smoothToonFilter.setThreshold(threshold);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (paramsList.size() > 3) {
                        try {
                            float blurSize = Float.parseFloat(paramsList.get(3));
                            smoothToonFilter.setBlurSize(blurSize);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (paramsList.size() > 4) {
                        try {
                            float texelWidth = Float.parseFloat(paramsList.get(4));
                            smoothToonFilter.setTexelWidth(texelWidth);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (paramsList.size() > 5) {
                        try {
                            float texelHeight = Float.parseFloat(paramsList.get(5));
                            smoothToonFilter.setTexelHeight(texelHeight);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                return smoothToonFilter;
            } else if (filterType.equalsIgnoreCase("Ha")) {
                HalftoneFilter halftoneFilter = new HalftoneFilter();
                if (paramsList.size() > 1) {
                    try {
                        float fractionalWidth = Float.parseFloat(paramsList.get(1));
                        halftoneFilter.setFractionalWidthOfAPixel(fractionalWidth);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    if (paramsList.size() > 2) {
                        try {
                            float aspect = Float.parseFloat(paramsList.get(2));
                            halftoneFilter.setAspectRatio(aspect);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                return halftoneFilter;
            } else if (filterType.equalsIgnoreCase("Sat")) {
                SaturationFilter saturationFilter = new SaturationFilter();
                if (paramsList.size() > 1) {
                    try {
                        float saturation = Float.parseFloat(paramsList.get(1));
                        saturationFilter.setSaturation(saturation);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                return saturationFilter;
            } else if (filterType.equalsIgnoreCase("M")) {
                MonochromeFilter monochromeFilter = new MonochromeFilter();
                if (paramsList.size() > 1) {
                    try {
                        float intensity = Float.parseFloat(paramsList.get(1));
                        monochromeFilter.setIntensity(intensity);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                if (paramsList.size() > 2) {
                    try {
                        float r = Float.parseFloat(paramsList.get(2));
                        float g = Float.parseFloat(paramsList.get(3));
                        float b = Float.parseFloat(paramsList.get(4));
                        monochromeFilter.setColorRed(r, g, b);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                return monochromeFilter;
            } else if (filterType.equalsIgnoreCase("V")) {
                PointF center = new PointF(0.5f, 0.5f);
                float[] color = new float[3];
                float start = 0.25f;
                float end = 0.75f;
                try {
                    if (paramsList.size() > 1) {
                        center = new PointF(Float.parseFloat(paramsList.get(1)),
                                Float.parseFloat(paramsList.get(2)));
                    }

                    if (paramsList.size() > 3) {
                        color[0] = Float.parseFloat(paramsList.get(3));
                        color[1] = Float.parseFloat(paramsList.get(4));
                        color[2] = Float.parseFloat(paramsList.get(5));
                    }

                    if (paramsList.size() > 6) {
                        start = Float.parseFloat(paramsList.get(6));
                    }

                    if (paramsList.size() > 7) {
                        end = Float.parseFloat(paramsList.get(7));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return new VignetteFilter(center, color, start, end);
            } else if (filterType.equalsIgnoreCase("O")) {
                OpacityFilter opacityFilter = new OpacityFilter();
                try {
                    if (paramsList.size() > 1) {
                        float opacity = Float.parseFloat(paramsList.get(1));
                        opacityFilter.setOpacity(opacity);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return opacityFilter;
            } else if (filterType.equalsIgnoreCase("T")) {
                ToneCurveFilter toneCurveFilter = new ToneCurveFilter();
                if (paramsList.size() > 1) {
                    if (paramsList.get(1).startsWith(PhotoUtils.ASSET_PREFIX)) {
                        try {
                            InputStream is = PhotoEditorApp.getAppContext().getAssets()
                                    .open(paramsList.get(1).substring(PhotoUtils.ASSET_PREFIX.length()));
                            toneCurveFilter.setFromCurveFileInputStream(is);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            InputStream is = new FileInputStream(paramsList.get(1));
                            toneCurveFilter.setFromCurveFileInputStream(is);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }

                return toneCurveFilter;
            } else if (filterType.equalsIgnoreCase("C")) {
                try {
                    float[] redCoeff = new float[4];
                    float[] greenCoeff = new float[4];
                    float[] blueCoeff = new float[4];
                    float[] alphaCoeff = new float[4];

                    redCoeff[0] = Float.parseFloat(paramsList.get(1));
                    redCoeff[1] = Float.parseFloat(paramsList.get(2));
                    redCoeff[2] = Float.parseFloat(paramsList.get(3));
                    redCoeff[3] = Float.parseFloat(paramsList.get(4));

                    greenCoeff[0] = Float.parseFloat(paramsList.get(5));
                    greenCoeff[1] = Float.parseFloat(paramsList.get(6));
                    greenCoeff[2] = Float.parseFloat(paramsList.get(7));
                    greenCoeff[3] = Float.parseFloat(paramsList.get(8));

                    blueCoeff[0] = Float.parseFloat(paramsList.get(9));
                    blueCoeff[1] = Float.parseFloat(paramsList.get(10));
                    blueCoeff[2] = Float.parseFloat(paramsList.get(11));
                    blueCoeff[3] = Float.parseFloat(paramsList.get(12));

                    alphaCoeff[0] = Float.parseFloat(paramsList.get(13));
                    alphaCoeff[1] = Float.parseFloat(paramsList.get(14));
                    alphaCoeff[2] = Float.parseFloat(paramsList.get(15));
                    alphaCoeff[3] = Float.parseFloat(paramsList.get(16));

                    return new ChannelLookupFilter(redCoeff, greenCoeff, blueCoeff, alphaCoeff);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (filterType.equalsIgnoreCase("G")) {
                return new GrayscaleFilter();
            } else if (filterType.equalsIgnoreCase("L1") || filterType.equalsIgnoreCase("L2")
                    || filterType.equalsIgnoreCase("Ba") || filterType.equalsIgnoreCase("Bs")
                    || filterType.equalsIgnoreCase("Bd")) {
                TwoInputFilter filter = null;
                if (filterType.equalsIgnoreCase("L1")) {
                    filter = new OneDimenLookupFilter();
                } else if (filterType.equalsIgnoreCase("L2")) {
                    filter = new LookupFilter();
                } else if (filterType.equalsIgnoreCase("Ba")) {
                    AlphaBlendFilter alphaBlendFilter = new AlphaBlendFilter();
                    if (paramsList.size() > 2) {
                        try {
                            float mix = Float.parseFloat(paramsList.get(2));
                            alphaBlendFilter.setMix(mix);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    filter = alphaBlendFilter;
                } else if (filterType.equalsIgnoreCase("Bd")) {
                    DestinationOverBlendFilter destinationOverBlendFilter = new DestinationOverBlendFilter();
                    filter = destinationOverBlendFilter;
                } else if (filterType.equalsIgnoreCase("Bo")) {
                    SourceOverBlendFilter sourceOverBlendFilter = new SourceOverBlendFilter();
                    filter = sourceOverBlendFilter;
                } else {
                    filter = new SoftLightBlendFilter();
                }

                filter.setRecycleBitmap(true);

                if (paramsList.size() > 1) {
                    if (paramsList.get(1).startsWith(PhotoUtils.ASSET_PREFIX)) {
                        try {
                            InputStream is = PhotoEditorApp.getAppContext().getAssets()
                                    .open(paramsList.get(1).substring(PhotoUtils.ASSET_PREFIX.length()));
                            filter.setBitmap(BitmapFactory.decodeStream(is));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (mPackageFolder != null && mPackageFolder.length() > 0) {
                        try {
                            final String mapFile = Utils.FILTER_FOLDER.concat("/").concat(mPackageFolder).concat("/")
                                    .concat(paramsList.get(1));
                            filter.setBitmap(BitmapFactory.decodeFile(mapFile));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                return filter;
            }
        }

        return new ImageFilter();
    }
}
