package com.example.prj;

import android.app.Activity;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRCodeAuthManager {
    private final Activity activity;

    // Constructor to initialize the activity
    public QRCodeAuthManager(Activity activity) {
        this.activity = activity;
    }

    // Start the QR Code scanning process
    public void startQRCodeScan() {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);  // Only QR codes
        integrator.setPrompt("Scan QR Code");
        integrator.setCameraId(0);  // Use back camera
        integrator.setBeepEnabled(true);  // Enable beep after scan
        integrator.setOrientationLocked(true);  // Lock screen orientation
        integrator.initiateScan();  // Initiate the scanning process
    }

    // Handle the result of the QR scan
    public void handleActivityResult(int requestCode, int resultCode, IntentResult result) {
        if (result != null && result.getContents() != null) {
            // QR code successfully scanned
            String sessionId = result.getContents();
            verifySessionIdInFirebase(sessionId);  // Verify the scanned session ID
        } else {
            // QR scan was canceled or failed
            Toast.makeText(activity, "Scan cancelled or failed", Toast.LENGTH_SHORT).show();
        }
    }

    // Verify the scanned session ID with Firebase
    private void verifySessionIdInFirebase(String sessionId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("qrCodes");

        // Get the session ID from Firebase and check its status
        ref.child(sessionId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String status = task.getResult().getValue(String.class);

                // If session is waiting for login, mark it as logged in
                if ("waiting_for_login".equals(status)) {
                    ref.child(sessionId).setValue("logged_in");  // Mark the session as used
                    proceedWithLogin();  // Proceed with the login
                } else {
                    // The QR code was already used or is invalid
                    Toast.makeText(activity, "Invalid or already used QR Code", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Session ID not found in Firebase
                Toast.makeText(activity, "Session ID not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Proceed with login after successful QR code verification
    private void proceedWithLogin() {
        Toast.makeText(activity, "Login successful!", Toast.LENGTH_SHORT).show();

        // Example: Redirect to the home page or another activity
        // Intent intent = new Intent(activity, HomeActivity.class);
        // activity.startActivity(intent);
        // activity.finish();  // Optionally finish the current activity
    }
}