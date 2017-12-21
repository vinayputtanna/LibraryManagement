package com.example.avdeepsandhu.librarymanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class TestActivity extends AppCompatActivity {

    Button btn;
    EditText number_of_days;
    private String url = Config.url + "/test";


    public void init(){
        btn = (Button) findViewById(R.id.add_days);
        number_of_days = (EditText)findViewById(R.id.days);
        final RequestQueue queue = Volley.newRequestQueue(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                                try {
                                    JSONObject jObject = new JSONObject(response);
                                    String result = (String) jObject.get("status");
                                    String fromEmail = Config.fromEmail;
                                    String fromPassword = Config.fromPassword;

                                    String toEmails = "avdeep280294@gmail.com";
                                    List toEmailList = Arrays.asList(toEmails
                                            .split("\\s*,\\s*"));
                                    String emailSubject = "Book Return Date Close";
                                    String emailBody ="You have "+result + " number of days to return the book. Please return it on time or you will be charged with fine of 1$ for each extra day passed your due date.";
                                    new SendMailTask(TestActivity.this).execute(fromEmail,fromPassword, toEmailList, emailSubject, emailBody);
                                    Log.e("ERROR", "MAIL SENT.");




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
                        params.put("number", String.valueOf(number_of_days.getText()));



                        return params;
                    }
                };
                queue.add(postRequest);

            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        init();
    }
}
