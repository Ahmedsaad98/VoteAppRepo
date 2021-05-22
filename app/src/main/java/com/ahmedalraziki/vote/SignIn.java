package com.ahmedalraziki.vote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SignIn extends AppCompatActivity {

    TextView tvStatus;
    EditText edEcn, edPa;
    Button btnSign;
    DatabaseReference mainRef = FirebaseDatabase.getInstance().getReference();
    boolean vu = false;
    SharedPreferences preferences ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //1-
        Assigner();
        Clicker();

        //2-
        preferences = this.getSharedPreferences("prefs1", Context.MODE_PRIVATE);

    }

    private void Assigner(){
        edEcn = findViewById(R.id.vs_edEcn);
        edPa = findViewById(R.id.vs_edPa);
        btnSign = findViewById(R.id.vs_btnSign);
        tvStatus = findViewById(R.id.vs_tvStatus);
    }

    private void Clicker(){
        btnSign.setOnClickListener(v -> {
            SignIn();
        });
    }


    private void SignIn() {
        String id = edEcn.getText().toString();
        String pa = edPa.getText().toString();

        DatabaseReference useRef = mainRef.child("User").child(id);

        useRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tmpId = "", tmpPa = "", tmpVu = "";
                for (DataSnapshot d1 : snapshot.getChildren()){
                    if (Objects.equals(d1.getKey(), "id")){ tmpId = Objects.requireNonNull(d1.getValue()).toString(); }
                    if (Objects.equals(d1.getKey(), "pass")){ tmpPa = Objects.requireNonNull(d1.getValue()).toString(); }
                    if (d1.getKey().equals("vu")){tmpVu = Objects.requireNonNull(d1.getValue()).toString();}
                }
                if (tmpId.equals(id) && tmpPa.equals(pa) && tmpVu.equals("true")){
                    setValidUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setValidUser(){
        vu = true;
        preferences.edit().putBoolean("vu", true).apply();
        preferences.edit().putString("ecn", edEcn.getText().toString()).apply();
        tvStatus.setTextColor(Color.GREEN);
        tvStatus.setText("تم تسجيل الدخول");
    }

}