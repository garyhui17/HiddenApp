package gary.hiddenapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.KeyguardManager;
import android.hardware.fingerprint.FingerprintManager;
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

        hKeyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        hFingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        startFingerprintListening();
        Log.v("fingerPrint","startFingerprintListening()");
        if(!hKeyguardManager.isKeyguardSecure()){
            return;
        }

        if(checkSelfPermission(android.Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED){
            Log.v("fingerPrint", String.valueOf(hFingerprintManager.isHardwareDetected()));
            if(hFingerprintManager.hasEnrolledFingerprints()){
                return;
            }
            if(hFingerprintManager.isHardwareDetected()){
                return;
            }

        }
    }

    FingerprintManager.AuthenticationCallback hAuthenticationCallback
            = new FingerprintManager.AuthenticationCallback()
    {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString)
        {
            Log.v("fingerPrint","onAuthenticationError");
            Log.e("fingerPrint", "error " + errorCode + " " + errString);
        }

        @Override
        public void onAuthenticationFailed()
        {
            Log.v("fingerPrint","onAuthenticationFailed");
            Log.e("fingerPrint", "onAuthenticationFailed");
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result)
        {
            Intent intent = new Intent(MainActivity.this,service_class.class);
            startService(intent);
            Log.v("fingerPrint","onAuthenticationSucceeded");
            Log.i("fingerPrint", "onAuthenticationSucceeded");
        }
    };

    private void startFingerprintListening() {
        cancellationSignal = new CancellationSignal();

        if (checkSelfPermission(android.Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED)
        {
            hFingerprintManager.authenticate(null,
                    cancellationSignal,
                    0, //optional flags; should be 0
                    hAuthenticationCallback,
                    null); //optional 的參數，如果有使用，FingerprintManager 會透過它來傳遞訊息
        }
    }

    @Override
    public void onResume(){
        super.onResume();
//        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onPause(){
        super.onPause();
//        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this,service_class.class);
        stopService(intent);
    }
}