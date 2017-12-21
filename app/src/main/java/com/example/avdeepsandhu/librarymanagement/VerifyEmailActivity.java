package com.example.avdeepsandhu.librarymanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.goodiebag.pinview.Pinview;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VerifyEmailActivity extends AppCompatActivity {

    Pinview pinview;
    Button submit_btn;
    final Handler handler = new Handler();
    private String url = Config.url;


    public void init()
    {
        pinview = (Pinview) findViewById(R.id.pinview);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        final RequestQueue queue = Volley.newRequestQueue(this);
        url = url + "/verified";
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin = pinview.getValue();
                SharedPreferences sp2 = getSharedPreferences("verification_code", Context.MODE_PRIVATE);
                String actual_pin = sp2.getString("verification_code",null);
                SharedPreferences.Editor editor2 = sp2.edit();

                editor2.commit();


                System.out.println(actual_pin +" is the actual pin");

                if(pin.equals(actual_pin))
                {
                    editor2.remove("verification_code");
                    SharedPreferences sp = getSharedPreferences("session", Context.MODE_PRIVATE);
                    final String emailid = sp.getString("emailid",null);


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
                                            Toast.makeText(VerifyEmailActivity.this,"Verification Complete",Toast.LENGTH_SHORT).show();
                                            String fromEmail = Config.fromEmail;
                                            String fromPassword = Config.fromPassword;
                                            String toEmails = emailid;
                                            List toEmailList = Arrays.asList(toEmails
                                                    .split("\\s*,\\s*"));
                                            String emailSubject = "Email Verification Complete";
                                            String emailBody = "Thank You for verifying your Email id. ";

                                            new SendMailTask(VerifyEmailActivity.this).execute(fromEmail,fromPassword, toEmailList, emailSubject, emailBody);
                                            Log.e("ERROR", "MAIL SENT.");
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
                                            Toast.makeText(getApplicationContext(),"Verification Failed",Toast.LENGTH_SHORT).show();
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
                            params.put("emailid", emailid);
                            return params;
                        }
                    };
                    queue.add(postRequest);
                }
                else{
                    Toast.makeText(VerifyEmailActivity.this,"Verification Failed",Toast.LENGTH_SHORT).show();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                SharedPreferences sp = getSharedPreferences("session", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("emailid");
                editor.commit();
                Intent intent = new Intent(getApplicationContext(),LoginForm.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
