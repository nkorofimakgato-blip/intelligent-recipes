package com.example.intelligent_recipy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CameraActivity extends AppCompatActivity {

    public static final String EXTRA_RESULT = "extra_result";
    private static final String TAG = "CameraActivity";
    private static final int PERMISSION_CODE = 1001;

    private PreviewView previewView;
    private ImageView captureButton;
    private TextView statusText;

    private ImageCapture imageCapture;
    private ImageLabeler labeler;
    private ExecutorService cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView    = findViewById(R.id.previewView);
        captureButton  = findViewById(R.id.captureButton);
        statusText     = findViewById(R.id.statusText);
        ImageView closeButton = findViewById(R.id.closeButton);

        cameraExecutor = Executors.newSingleThreadExecutor();
        labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { takePhoto(); }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Camera permission required",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(this);

        future.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider provider = future.get();

                    Preview preview = new Preview.Builder().build();
                    preview.setSurfaceProvider(previewView.getSurfaceProvider());

                    imageCapture = new ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)

                            .build();

                    CameraSelector selector = CameraSelector.DEFAULT_BACK_CAMERA;

                    provider.unbindAll();
                    provider.bindToLifecycle(
                            CameraActivity.this, selector, preview, imageCapture);

                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, "Camera start failed", e);
                    Toast.makeText(CameraActivity.this,
                            "Couldn't start camera", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        statusText.setText("Analyzing…");
        captureButton.setEnabled(false);

        imageCapture.takePicture(
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        Bitmap bitmap = imageProxyToBitmap(image);
                        image.close();

                        if (bitmap == null) {
                            statusText.setText("Capture failed");
                            captureButton.setEnabled(true);
                            return;
                        }

                        runLabeler(bitmap);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Capture failed", exception);
                        statusText.setText("Capture failed");
                        captureButton.setEnabled(true);
                    }
                });
    }

    /** Convert CameraX ImageProxy to a Bitmap. */
    /** Convert CameraX ImageProxy to a Bitmap using the built-in helper. */
    private Bitmap imageProxyToBitmap(ImageProxy image) {
        try {
            // CameraX 1.3+ provides this directly.
            // It handles YUV → Bitmap conversion and rotation for us.
            return image.toBitmap();
        } catch (Exception e) {
            Log.e(TAG, "imageProxyToBitmap failed", e);
            return null;
        }
    }


    private void runLabeler(Bitmap bitmap) {
        InputImage input = InputImage.fromBitmap(bitmap, 0);

        labeler.process(input)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        if (labels.isEmpty()) {
                            statusText.setText("No match — try again");
                            captureButton.setEnabled(true);
                            return;
                        }

                        java.util.Set<String> allowed = new java.util.HashSet<>(java.util.Arrays.asList(
                                "apple", "banana", "orange", "lemon", "lime", "strawberry",
                                "grape", "watermelon", "pineapple", "peach", "pear",
                                "carrot", "broccoli", "tomato", "potato", "onion", "garlic",
                                "pepper", "cucumber", "lettuce", "spinach", "kale",
                                "mushroom", "corn", "cabbage", "pumpkin", "zucchini",
                                "bread", "cheese", "egg", "milk", "butter", "yogurt",
                                "chicken", "beef", "pork", "fish", "shrimp", "bacon",
                                "rice", "pasta", "noodle", "flour", "sugar", "salt",
                                "food", "fruit", "vegetable", "produce",
                                "bottle", "cup", "bowl", "plate", "jar"
                        ));

                        String bestMatch = null;
                        float bestScore = 0f;

                        for (ImageLabel label : labels) {
                            String lower = label.getText().toLowerCase().trim();

                            if (allowed.contains(lower) && label.getConfidence() > bestScore) {
                                bestMatch = lower;
                                bestScore = label.getConfidence();
                            }

                            for (String word : allowed) {
                                if (lower.contains(word) && label.getConfidence() > bestScore) {
                                    bestMatch = word;
                                    bestScore = label.getConfidence();
                                }
                            }
                        }

                        Log.d(TAG, "Best ingredient match: " + bestMatch
                                + " (score " + bestScore + ")");

                        if (bestMatch == null) {
                            statusText.setText("Couldn't identify an ingredient. Try a clearer photo.");
                            captureButton.setEnabled(true);
                            return;
                        }

                        Intent result = new Intent();
                        result.putExtra(EXTRA_RESULT, bestMatch);
                        setResult(RESULT_OK, result);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Labeler failed", e);
                    statusText.setText("Analysis failed");
                    captureButton.setEnabled(true);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}