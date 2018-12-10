package com.example.darshanh.photoserver;

import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.multidots.fingerprintauth.AuthErrorCodes;
import com.multidots.fingerprintauth.FingerPrintAuthCallback;
import com.multidots.fingerprintauth.FingerPrintAuthHelper;


public class FingerPrintAuthentication extends AppCompatActivity implements FingerPrintAuthCallback {
    FingerPrintAuthHelper mFingerPrintAuthHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print_authentication);
        mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //start finger print authentication
        mFingerPrintAuthHelper.startAuth();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFingerPrintAuthHelper.stopAuth();
    }

    @Override
    public void onNoFingerPrintHardwareFound() {
        Toast.makeText(this,"No Fingerprint scanner hardware found",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoFingerPrintRegistered() {
        Toast.makeText(this,"No Fingerprint registered, Add fingerprint and try again!!",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBelowMarshmallow() {
        Toast.makeText(this,"Your device doesn't support fingerprint authentication. Sorry!!",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {
        Toast.makeText(this,"Authentication Successful!!",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {
        switch (errorCode) {    //Parse the error code for recoverable/non recoverable error.
            case AuthErrorCodes.CANNOT_RECOGNIZE_ERROR:
                Toast.makeText(this,"Fingerprint cannot be recognized. Please try again!!",Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
