package com.example.avdeepsandhu.librarymanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RequestQueue queue;

    private ListView mCartListView;
    private Button mCheckoutBtn;
    private String url = Config.url + "/viewcart";
    ArrayList<Book> cartBooks = new ArrayList<>();
    final Handler handler = new Handler();

    String patron_email_id;
    String checkouturl = Config.url + "/checkout";

    private static final String CHECKOUT_BOOKS_API = "/checkoutbooks";

    public void init() {
        queue = Volley.newRequestQueue(this);
        SharedPreferences sp = getSharedPreferences("session", Context.MODE_PRIVATE);
        patron_email_id = sp.getString("emailid",null);

        mCartListView = (ListView) findViewById(R.id.cart_list_view);
        mCheckoutBtn = (Button) findViewById(R.id.checkout_btn);
        mCheckoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest postRequest = new StringRequest(Request.Method.POST,checkouturl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                                try {
                                    JSONObject jObject = new JSONObject(response);
                                    String result = (String) jObject.get("status");
                                    if(result.equals("success")){
                                        Toast.makeText(getApplicationContext(),"Book Successfully Checkout!",Toast.LENGTH_SHORT).show();
                                        String toEmails = patron_email_id;
                                        List toEmailList = Arrays.asList(toEmails
                                                .split("\\s*,\\s*"));
                                        String emailSubject = "Book Checkout";
                                        String emailBody = "You have successfully checked out the book";
                                        String fromEmail = "Avdeep2802@gmail.com";
                                        String fromPassword = "Avneet0705";
                                        new SendMailTask(CartActivity.this).execute(fromEmail,fromPassword, toEmailList, emailSubject, emailBody);
                                    }
                                    else if(result.equals("failure")){
                                        Toast.makeText(getApplicationContext(),"Book Checkout Failure",Toast.LENGTH_SHORT).show();
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
                        params.put("patron_emailid", patron_email_id);
                        return params;
                    }
                };
                queue.add(postRequest);
            }
        });
    }


    public void beforeinit() {

                RequestQueue queue2 = Volley.newRequestQueue(getApplicationContext());
                StringRequest postRequest2 = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                                try {
                                    JSONObject jObject = new JSONObject(response);
                                    JSONArray jsonarr = jObject.getJSONArray("result");
                                    for (int i = 0; i < jsonarr.length(); i++) {
                                        Book book_obj = new Book();
                                        JSONObject json_obj = jsonarr.getJSONObject(i);
                                        book_obj.setTitle(json_obj.getString("book_name"));
                                        book_obj.setAuthor(json_obj.getString("author"));
                                        cartBooks.add(book_obj);
                                    }
                                    CartBookAdapter cartBookAdapter = new CartBookAdapter(getApplicationContext(), cartBooks, patron_email_id);
                                    mCartListView.setAdapter(cartBookAdapter);

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
                        Log.e("PPPPPPPPPPP", " i sthe book");
                        params.put("patron_emailid", patron_email_id);
                        return params;
                    }
                };
                queue2.add(postRequest2);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        beforeinit();
        init();




    }
}