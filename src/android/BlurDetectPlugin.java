package org.apache.cordova.blurdetect;

import org.apache.cordova.PermissionHelper;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

/**
 * This class exposes methods in Cordova that can be called from JavaScript.
 */
public class BlurDetectPlugin extends CordovaPlugin {

    public String _imageUri;
    public CallbackContext _callbackContext;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 0;

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action          The action to execute.
     * @param args            JSONArry of arguments for the plugin.
     * @param callbackContext The callback context from which we were invoked.
     */
    @SuppressLint("NewApi")
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        OpenCVLoader.initDebug();
        this.cordova.requestPermissions(this, 0, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
        String imageUri = args.getString(0);
        _imageUri = imageUri;
        _callbackContext = callbackContext;
        if (action.equals("chcekImage")) {
            imageProcess();
        } else {
            return false;
        }
        return true;
    }

    private void imageProcess() {
        try {
            File imgFile = new File(_imageUri);
            if (!imgFile.exists()) {
                _callbackContext.error("File not found : " + _imageUri);
                return;
            }
            boolean writeExternalPermission = PermissionHelper.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writeExternalPermission) {
                Bitmap image = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inDither = true;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                int l = CvType.CV_8UC1; //8-bit grey scale image
                Mat matImage = new Mat();
                Utils.bitmapToMat(image, matImage);
                Mat matImageGrey = new Mat();
                Imgproc.cvtColor(matImage, matImageGrey, Imgproc.COLOR_BGR2GRAY);
                Bitmap destImage;
                destImage = Bitmap.createBitmap(image);
                Mat dst2 = new Mat();
                Utils.bitmapToMat(destImage, dst2);
                Mat laplacianImage = new Mat();
                dst2.convertTo(laplacianImage, l);
                Imgproc.Laplacian(matImageGrey, laplacianImage, CvType.CV_8U);
                Mat laplacianImage8bit = new Mat();
                laplacianImage.convertTo(laplacianImage8bit, l);

                Bitmap bmp = Bitmap.createBitmap(laplacianImage8bit.cols(), laplacianImage8bit.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(laplacianImage8bit, bmp);
                int[] pixels = new int[bmp.getHeight() * bmp.getWidth()];
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
                int maxLap = -16777216; // 16m

                for (int pixel : pixels) {
                    if (pixel > maxLap)
                        maxLap = pixel;
                }

                int soglia = -6118750;
                boolean isBlur = maxLap <= soglia;
                _callbackContext.success(isBlur ? "BLUR" : "NOT BLUR");
            }

            if (!writeExternalPermission) {
                PermissionHelper.requestPermission(this, WRITE_EXTERNAL_STORAGE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        } catch (Exception ex) {
            _callbackContext.error(ex.toString());
        }
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                _callbackContext.error("PERMISSION DENIED");
                return;
            }
        }

        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_PERMISSION:
                imageProcess();
                break;
        }
    }
}