package org.apache.cordova.blurdetect;

import org.apache.cordova.PermissionHelper;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.imgcodecs.Imgcodecs;
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
        if (action.equals("checkImage")) {
            imageProcess();
        } else {
            return false;
        }
        return true;
    }

    private void imageProcess() {
        Mat image = Imgcodecs.imread(_imageUri, Imgcodecs.CV_LOAD_IMAGE_COLOR);
        if (image.empty()) {
            _callbackContext.error("CANNOT OPEN IMAGE!");
        } else {
            Mat destination = new Mat();
            Mat matGray = new Mat();
            Mat kernel = new Mat(3, 3, CvType.CV_32F) {
                {
                    put(0, 0, 0.0);
                    put(0, 1, -1.0);
                    put(0, 2, 0.0);

                    put(1, 0, -1.0);
                    put(1, 1, 4.0);
                    put(1, 2, -1.0);

                    put(2, 0, 0.0);
                    put(2, 1, -1.0);
                    put(2, 2, 0.0);
                }
            };
            Imgproc.cvtColor(image, matGray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.filter2D(matGray, destination, -1, kernel);
            MatOfDouble median = new MatOfDouble();
            MatOfDouble std = new MatOfDouble();
            Core.meanStdDev(destination, median, std);

            double result = Math.pow(std.get(0, 0)[0], 2.0);
            boolean isBlur = Math.round(result) > 85;
            _callbackContext.success(isBlur ? "BLUR" : "NOT BLUR");
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