package com.example.avdeepsandhu.librarymanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RequestQueue queue;

    private ListView mCartListView;
    private Button mCheckoutBtn;

    private SharedPreferences mSharedPref;

    private static final String CHECKOUT_BOOKS_API = "/returnBooks";

    public void init() {
        queue = Volley.newRequestQueue(this);
        mSharedPref = getSharedPreferences("session", Context.MODE_PRIVATE);

        mCartListView = (ListView) findViewById(R.id.cart_list_view);
        mCheckoutBtn = (Button) findViewById(R.id.checkout_btn);

        mCheckoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest postRequest = new StringRequest(Request.Method.POST, Config.url.concat(CHECKOUT_BOOKS_API),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                                try {
                                    JSONObject jObject = new JSONObject(response);

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
        setContentView(R.layout.activity_cart);

        init();
    }
}
