package com.myousufkhan360.opencvapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.hardware.Camera.PictureCallback;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d("TAG", "OpenCV not loaded");
        } else {
            Log.d("TAG", "OpenCV loaded");
        }
    }

    int iLowH = 45;
    int iHighH = 75;
    int iLowS = 20;
    int iHighS = 255;
    int iLowV = 10;
    int iHighV = 255;

    Mat imgHSV, imgThresholded;

    Scalar sc1, sc2;

    JavaCameraView cameraView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //added code
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main);

        sc1 = new Scalar(iLowH, iLowS, iLowV);

        sc2 = new Scalar(iHighH, iHighS, iHighV);
        // settings for camera
        cameraView = (JavaCameraView) findViewById(R.id.cameraview);
        cameraView.setCameraIndex(0); // 0 for rear and 1 for front
        cameraView.setCvCameraViewListener(this);
        cameraView.enableView();


        Button ba = (Button) findViewById(R.id.mybutton); // your Button

        ba.setOnClickListener(new View.OnClickListener() {

                                  @Override
                                  public void onClick(View v) {
                                      Intent img = new Intent(); //Your Intent
                                      img.setAction(MediaStore.ACTION_IMAGE_CAPTURE); //the intents action to capture the image
                                      startActivityForResult(img, 1);
                                  }

                              }
        );

    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraView.disableView();
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        imgHSV = new Mat(width, height, CvType.CV_16UC4);
        imgThresholded = new Mat(width, height, CvType.CV_16UC4);
    }

    @Override
    public void onCameraViewStopped() {

    }


    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Imgproc.cvtColor(inputFrame.rgba(), imgHSV, Imgproc.COLOR_BGR2HSV);
        Core.inRange(imgHSV, sc1, sc2, imgThresholded);
        return imgThresholded;
    }

    ImageView im = (ImageView) findViewById(R.id.myImage); //Your image View

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);//method retrieves the requestCode , its result and the data containing the pic from system
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data"); //get data and casts it into Bitmap photo
            im.setImageBitmap(photo);// set photo to imageView
        }
    }
}
