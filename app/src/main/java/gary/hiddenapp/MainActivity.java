package gary.hiddenapp;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.jar.Manifest;
import java.util.logging.Handler;

import static java.util.jar.Manifest.*;

public class MainActivity extends AppCompatActivity {

    private KeyguardManager hKeyguardManager;
    private FingerprintManager hFingerprintManager;
    private CancellationSignal cancellationSignal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hKeyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
        hFingerprintManager = (FingerprintManager)getSystemService(Activity.FINGERPRINT_SERVICE);

        if(!hKeyguardManager.isKeyguardSecure()){
            return;
        }

        if(checkSelfPermission(android.Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED){
            if(hFingerprintManager.isHardwareDetected()){
                return;
            }
            Toast.makeText(this, "Hardware Support", Toast.LENGTH_SHORT).show();

            if(hFingerprintManager.hasEnrolledFingerprints()){
                return;
            }
            Toast.makeText(this, "Has Enrolled Fingerprint", Toast.LENGTH_SHORT).show();
        }
        startFingerprintListening();
    }

    private void startFingerprintListening() {
        cancellationSignal = new CancellationSignal();

        if (checkSelfPermission(android.Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) //In SDK 23, we need to check the permission before we call FingerprintManager API functionality.
        {
            hFingerprintManager.authenticate(null, //crypto objects 的 wrapper class，可以透過它讓 authenticate 過程更為安全，但也可以不使用。
                    cancellationSignal, //用來取消 authenticate 的 object
                    0, //optional flags; should be 0
                    hAuthenticationCallback, //callback 用來接收 authenticate 成功與否，有三個 callback method
                    null); //optional 的參數，如果有使用，FingerprintManager 會透過它來傳遞訊息
        }
    }

    FingerprintManager.AuthenticationCallback hAuthenticationCallback
            = new FingerprintManager.AuthenticationCallback()
    {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString)
        {
            Log.e("", "error " + errorCode + " " + errString);
        }

        @Override
        public void onAuthenticationFailed()
        {
            Log.e("", "onAuthenticationFailed");
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result)
        {
            Log.i("", "onAuthenticationSucceeded");
        }
    };


    @Override
    public void onResume(){
        super.onResume();
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this,service_class.class);
        stopService(intent);
    }
    @Override
    public void onPause(){
        super.onPause();
        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this,service_class.class);
        startService(intent);
    }
}