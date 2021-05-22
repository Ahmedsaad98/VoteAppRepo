package com.ahmedalraziki.vote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

public class Scanner extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    SharedPreferences preferences ;
    DatabaseReference refMain ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        preferences = this.getSharedPreferences("prefs1", Context.MODE_PRIVATE);
        refMain = FirebaseDatabase.getInstance().getReference();
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int res = Integer.parseInt(result.getText());
                        Vote(res);
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    private void Vote(int qrResult){
        String ecn = preferences.getString("ecn", "error");

        if (qrResult != 0 && !ecn.equals("error")){
            DatabaseReference refVote = refMain.child("Votes");
            refVote.child(ecn).setValue(qrResult);

            DatabaseReference refUser = refMain.child("User").child(ecn);
            refUser.child("vu").setValue(false);
            preferences.edit().putBoolean("vu", false).apply();
            Toast.makeText(Scanner.this, "تم التصويت للمرشح" + qrResult, Toast.LENGTH_SHORT).show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 200);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }


}