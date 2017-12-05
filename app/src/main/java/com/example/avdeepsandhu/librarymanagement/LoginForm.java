package com.example.avdeepsandhu.librarymanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginForm extends AppCompatActivity {

    TextView signup_btn;
    EditText username , password;
    Button signin_btn;
    private static final int REQUEST_SIGNUP = 0;
    private String url ="http://10.0.0.181:3000";
    final Handler handler = new Handler();


    public void init()
    {
        signup_btn = (TextView) findViewById(R.id.signup_btn);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        signin_btn = (Button) findViewById(R.id.signin_btn);

        final RequestQueue queue = Volley.newRequestQueue(this);
        url = url + "/login";

        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //ip address of your computer and connect the android phone to same network to use it.

                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                                try {
                                    JSONObject jObject = new JSONObject(response);
                                    String result = (String) jObject.get("status");
                                    Log.e("Status", result);
                                    if(result.equals("success")){
                                        Toast.makeText(LoginForm.this,"Login Successful",Toast.LENGTH_SHORT).show();
                                        SharedPreferences sp = getSharedPreferences("session", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString("emailid",username.getText().toString() );
                                        editor.commit();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent(getApplicationContext(),Home_Activity.class);
                                                startActivity(intent);
                                                //Do something after 100ms
                                            }
                                        }, 1000);


                                    }
                                    else if(result.equals("failure")) {
                                        Toast.makeText(LoginForm.this,"Sign In Failed! Incorrect userid or password",Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", "Error in response");
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("emailid", String.valueOf(username.getText()));
                        params.put("password", String.valueOf(password.getText()));

                        return params;
                    }
                };
                queue.add(postRequest);

            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(LoginForm.this, SignupForm.class);
//                startActivity(intent);
                Intent intent = new Intent(getApplicationContext(), SignupForm.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        SharedPreferences sp = getSharedPreferences("session", 0);
        if(sp.contains("emailid")){
            Intent intent = new Intent(getApplicationContext(), Home_Activity.class);
            startActivity(intent);
            finish();
        }

        init();
    }
}



