package com.example.avdeepsandhu.librarymanagement;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;


public class VerifyEmailActivity extends AppCompatActivity {

    Pinview pinview;
    Button submit_btn;
    final Handler handler = new Handler();


    public void init()
    {
        pinview = (Pinview) findViewById(R.id.pinview);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin = pinview.getValue();
                Intent in= getIntent();
                Bundle b = in.getExtras();
                String actual_pin="";
                if(b!=null)
                {
                    actual_pin = String.valueOf(b.get("verification_code"));
                }
                System.out.println(actual_pin +" is the actual pin");

                if(pin.equals(actual_pin))
                {
                    Toast.makeText(VerifyEmailActivity.this,"Verification Complete",Toast.LENGTH_SHORT).show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(VerifyEmailActivity.this, Home_Activity.class);
                            startActivity(i);
                        }
                    }, 1000);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        init();
    }
}
