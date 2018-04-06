package dauroi.photoeditor.utils;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

import dauroi.photoeditor.R;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.widget.Toast;

public class ImageDecoder {
    public static final int SAMPLER_SIZE = 512;

    public static Bitmap decodeAsset(Context context, String filePath) {
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(filePath);
            return decodeStreamToBitmap(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap decodeResource(Context context, int resId) {
        return decodeSampledBitmapFromResource(context.getResources(), resId,
                SAMPLER_SIZE, SAMPLER_SIZE);
    }

    public static Bitmap decodeUriToBitmap(Context context, Uri uri) throws OutOfMemoryError {
        if (uri == null) {
            return null;
        }
        try {
            String file = FileUtils.getPath(context, uri);
            //ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            //FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file, options);
            //BitmapFactory.decodeFileDescriptor(fileDescriptor, new Rect(), options);
            // Find the correct scale value. It should be the power of 2.
            int width_tmp = options.outWidth, height_tmp = options.outHeight;
            int scale = 1;
            int requiredSize = SAMPLER_SIZE;
            while (true) {
                if (width_tmp / 2 <= requiredSize
                        || height_tmp / 2 <= requiredSize)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            // decode with inSampleSize
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            Bitmap bm = BitmapFactory.decodeFile(file, options);//BitmapFactory.decodeFileDescriptor(fileDescriptor, new Rect(), options);
            return bm;
        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (OutOfMemoryError e) {
            throw e;
        }

        return null;
    }

    public static Bitmap decodeBlobToBitmap(byte[] data)
            throws OutOfMemoryError {
        try {
            if (data != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(data, 0, data.length, options);

                // Find the correct scale value. It should be the power of 2.
                int width_tmp = options.outWidth, height_tmp = options.outHeight;
                int scale = 1;
                int requiredSize = SAMPLER_SIZE;
                while (true) {
                    if (width_tmp / 2 <= requiredSize
                            || height_tmp / 2 <= requiredSize)
                        break;
                    width_tmp /= 2;
                    height_tmp /= 2;
                    scale *= 2;
                }

                // decode with inSampleSize
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length,
                        options);
                return bm;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
            throw err;
        }

        return null;
    }

    public static Drawable decodeBlobToDrawable(byte[] data, int reqWidth,
                                                int reqHeight, Resources res) throws OutOfMemoryError {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return new BitmapDrawable(res, BitmapFactory.decodeByteArray(data,
                    0, data.length, options));
        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
            throw err;
        }
        return null;
    }

    public static Bitmap decodeBlobToBitmap(byte[] data, int reqWidth,
                                            int reqHeight, Resources res) throws OutOfMemoryError {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
            throw err;
        }

        return null;
    }

    public static Drawable decodeBlobToDrawble(byte[] data, Resources res)
            throws OutOfMemoryError {
        try {
            // decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = options.outWidth, height_tmp = options.outHeight;
            int scale = 1;
            int requiredSize = SAMPLER_SIZE;
            while (true) {
                if (width_tmp / 2 <= requiredSize
                        || height_tmp / 2 <= requiredSize)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length,
                    options);

            return new BitmapDrawable(res, bm);
        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
            throw err;
        }

        return null;
    }

    public static Drawable decodeStreamToDrawble(InputStream is, Resources res)
            throws OutOfMemoryError {
        try {
            // decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Rect outPadding = new Rect();
            BitmapFactory.decodeStream(is, outPadding, options);

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = options.outWidth, height_tmp = options.outHeight;
            int scale = 1;
            int requiredSize = SAMPLER_SIZE;
            while (true) {
                if (width_tmp / 2 <= requiredSize
                        || height_tmp / 2 <= requiredSize)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;

            return new BitmapDrawable(res, BitmapFactory.decodeStream(is,
                    outPadding, options));
        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
            throw err;
        }

        return null;
    }

    public static Bitmap decodeFileToBitmap(String pathName)
            throws OutOfMemoryError {
        try {
            // decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathName, options);
            // Find the correct scale value. It should be the power of 2.
            int width_tmp = options.outWidth, height_tmp = options.outHeight;
            int scale = 1;
            int requiredSize = SAMPLER_SIZE;
            while (true) {
                if (width_tmp / 2 <= requiredSize
                        || height_tmp / 2 <= requiredSize)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;

            return BitmapFactory.decodeFile(pathName, options);
        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
            throw err;
        }

        return null;
    }

    public static Bitmap decodeStreamToBitmap(InputStream is)
            throws OutOfMemoryError {
        try {
            // decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Rect outPadding = new Rect();
            BitmapFactory.decodeStream(is, outPadding, options);

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = options.outWidth, height_tmp = options.outHeight;
            int scale = 1;
            int requiredSize = SAMPLER_SIZE;
            while (true) {
                if (width_tmp / 2 <= requiredSize
                        || height_tmp / 2 <= requiredSize)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;

            return BitmapFactory.decodeStream(is, outPadding, options);
        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
            throw err;
        }

        return null;
    }

    public static Bitmap decodeStreamToBitmap(InputStream is, int reqWidth,
                                              int reqHeight) throws OutOfMemoryError {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Rect outPadding = new Rect();
            BitmapFactory.decodeStream(is, outPadding, options);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(is, outPadding, options);
        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
            throw err;
        }

        return null;
    }

    public static Drawable decodeStreamToDrawable(InputStream is, int reqWidth,
                                                  int reqHeight, Resources res) throws OutOfMemoryError {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Rect outPadding = new Rect();
            BitmapFactory.decodeStream(is, outPadding, options);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return new BitmapDrawable(res, BitmapFactory.decodeStream(is,
                    outPadding, options));
        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
            throw err;
        }

        return null;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId, int reqWidth, int reqHeight) throws OutOfMemoryError {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(res, resId, options);
        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
            throw err;
        }

        return null;
    }

    public static Drawable decodeSampledDrawableFromResource(Resources res,
                                                             int resId, int reqWidth, int reqHeight) throws OutOfMemoryError {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return new BitmapDrawable(res, BitmapFactory.decodeResource(res,
                    resId, options));
        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            throw e;
        }

        return null;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
}
