package com.ahmedalraziki.vote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class Dashboard extends AppCompatActivity {

    Button btnReg, btnSign, btnVote;
    ProgressBar pbResults;
    TextView txtCan1, txtCan2;
    SharedPreferences preferences ;
    boolean vu = false;
    int can1, can2;
    double dCan1, dCan2;
    int qrResult = 0;
    DatabaseReference refMain ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        //1-
        Assigner();
        Clicker();
        RequestPermissions();

        //2-
        preferences = this.getSharedPreferences("prefs1", Context.MODE_PRIVATE);
        refMain = FirebaseDatabase.getInstance().getReference();

        //3-
        setVoteBtn();

        //4-
        ReadProgress();
    }

    private void Assigner(){
        btnReg    = findViewById(R.id.vd_btnReg);
        btnSign   = findViewById(R.id.vd_btnSign);
        btnVote   = findViewById(R.id.vd_btnVote);
        pbResults = findViewById(R.id.vd_pbResults);
        txtCan1   = findViewById(R.id.vd_txtCan1);
        txtCan2   = findViewById(R.id.vd_txtCan2);
    }

    private void Clicker(){
        btnReg.setOnClickListener(v -> {
            goReg();
        });

        btnSign.setOnClickListener(v -> {
            goSign();
        });

        btnVote.setOnClickListener(v -> {
            goVote();
        });
    }

    private void RequestPermissions(){
        int check1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (check1 != PackageManager.PERMISSION_GRANTED){
            String [] permissions ={Manifest.permission.CAMERA};
            requestPermissions(permissions,1);
        }
    }

    private void goReg(){
        Intent intent = new Intent(Dashboard.this, Register.class);
        startActivity(intent);
    }

    private void goSign(){
        Intent intent = new Intent(Dashboard.this, SignIn.class);
        startActivity(intent);
    }

    private void goVote(){
        Intent intent = new Intent(Dashboard.this, Scanner.class);
        startActivity(intent);
    }

    private void setVoteBtn(){
        if (preferences.getBoolean("vu", false)){ vu = true; }
        else {
            vu = false;
        }
        btnVote.setBackgroundTintList(this.getResources().getColorStateList(R.color.btn_back_color));
        if (vu){
            btnVote.setEnabled(true);
        } else {
            btnVote.setEnabled(false);
        }
    }

    private void ReadProgress(){
        DatabaseReference refMain  = FirebaseDatabase.getInstance().getReference();
        DatabaseReference refVotes = refMain.child("Votes");

        refVotes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d1 : snapshot.getChildren()){
                    int cTemp = Integer.parseInt(d1.getValue().toString());
                    AddVote(cTemp);
                }
                SetUpPB();
                refVotes.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void AddVote(int candidate){
        if (candidate == 1){
            can1++;
            dCan1 = dCan1 + 1;
        } else if (candidate == 2){
            can2++;
            dCan2 = dCan2 + 1;
        }
    }

    private void SetUpPB(){

        //Set Progress Bar
        int tot = can1 + can2;
        pbResults.setMax(tot);
        pbResults.setProgress(can1);

        //Set TextViews
        double dTot = dCan1 + dCan2;
        double perCan1 = dCan1/dTot * 100;
        double perCan2 = dCan2/dTot * 100;

        DecimalFormat dfm = new DecimalFormat("##");

        txtCan1.setText(dfm.format(perCan1) + "%");
        txtCan2.setText(dfm.format(perCan2) + "%");

    }





    @Override
    protected void onResume() {
        super.onResume();
        setVoteBtn();
        ReadProgress();
    }
}