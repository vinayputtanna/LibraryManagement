package com.example.avdeepsandhu.librarymanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignupForm extends AppCompatActivity{

    private TextView signin, signup_btn;
    private EditText emailid, password, re_password, pin;
    private String url = Config.url;
    final Handler handler = new Handler();
    String verification_code = "";

    public void init()
    {
        signin = (TextView) findViewById(R.id.signin);
        emailid = (EditText) findViewById(R.id.emailid);
        password = (EditText) findViewById(R.id.password);
        re_password = (EditText) findViewById(R.id.re_password);
        pin = (EditText) findViewById(R.id.pin);
        signup_btn = (Button) findViewById(R.id.signup_btn);

        final RequestQueue queue = Volley.newRequestQueue(this);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginForm.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        url = url + "/signup";


        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CharSequence temp_emilID = emailid.getText().toString();//here username is the your edittext object...
                if (!isValidEmail(temp_emilID)) {
                    emailid.requestFocus();
                    emailid.setError("Enter Correct Email Id");

                }
                else {
                    String pwd = password.getText().toString();
                    String re_pwd = re_password.getText().toString();

                    if (!pwd.equals(re_pwd)) {
                        password.setText("");
                        re_password.setText("");
                        password.requestFocus();
                        password.setError("Passwords Do not Match");
                    }
                    else if(pwd.length()<5){
                        password.setText("");
                        re_password.setText("");
                        password.requestFocus();
                        password.setError("Passwords must be of atleast 5 characters");
                    }
                    else if(pin.getText().toString().length() < 6){
                        pin.setText("");
                        pin.requestFocus();
                        pin.setError("Pin must be of 6 characters");
                    }
                    else{

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
                                            if(result.equals("emailnotunique")){
                                                Toast.makeText(SignupForm.this,"Email already taken!",Toast.LENGTH_SHORT).show();
                                            }
                                            else if  (result.equals("pinnotunique")){
                                                Toast.makeText(SignupForm.this,"Pin already taken! Pin has to be unique",Toast.LENGTH_SHORT).show();
                                            }
                                            else if (result.equals("success")){
                                                String email = emailid.getText().toString();
                                                SharedPreferences sp = getSharedPreferences("session", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sp.edit();
                                                editor.putString("emailid",email );
                                                editor.commit();
                                                Toast.makeText(SignupForm.this,"Sign Up Successful",Toast.LENGTH_SHORT).show();
                                                String fromEmail = "Avdeep2802@gmail.com";
                                                String fromPassword = "Avneet0705";
                                                verification_code = VerificationCodeGenerator();
                                                SharedPreferences sp2 = getSharedPreferences("verification_code", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor2 = sp2.edit();
                                                editor2.putString("verification_code",verification_code );
                                                editor2.commit();
                                                String toEmails = email;
                                                List toEmailList = Arrays.asList(toEmails
                                                        .split("\\s*,\\s*"));
                                                String emailSubject = "Email Verification for Library Managemant App";
                                                String emailBody = "You email vefification code is : "+verification_code+"\n Thank You for signing up in Library Managemant App.";

                                                new SendMailTask(SignupForm.this).execute(fromEmail,fromPassword, toEmailList, emailSubject, emailBody);
                                                Log.e("ERROR", "MAIL SENT.");

                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent intent = new Intent(getApplicationContext(),VerifyEmailActivity.class);
                                                        startActivity(intent);
                                                        //Do something after 100ms
                                                    }
                                                }, 3000);


                                            }
                                            else if(result.equals("failure")) {
                                                Toast.makeText(SignupForm.this,"Sign Up Failure",Toast.LENGTH_SHORT).show();
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
                                params.put("emailid", String.valueOf(emailid.getText()));
                                params.put("password", String.valueOf(password.getText()));
                                params.put("pin", String.valueOf(pin.getText()));


                                return params;
                            }
                        };
                        queue.add(postRequest);
                    }
                }

            }
        });
    }



    public String VerificationCodeGenerator()
    {
        return String.valueOf(Math.round((Math.random() * 89999) + 10000));
    }

    public final static boolean isValidEmail(CharSequence target)
    {
        if (TextUtils.isEmpty(target))
        {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        init();
    }
}
