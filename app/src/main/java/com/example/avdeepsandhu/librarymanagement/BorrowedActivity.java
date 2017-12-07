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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BorrowedActivity extends AppCompatActivity {

    private RequestQueue queue;

    private ListView mBorrowedListView;
    private Button mReturnBtn;

    private SharedPreferences mSharedPref;

    private static final String GET_BORROWED_BOOKS_API = "/getBorrowedBooks";
    private static final String RETURN_BOOKS_API = "/returnBooks";


    public void init() {
        queue = Volley.newRequestQueue(this);
        mSharedPref = getSharedPreferences("session", Context.MODE_PRIVATE);

        mBorrowedListView = (ListView) findViewById(R.id.borrowed_list_view);
        mReturnBtn = (Button) findViewById(R.id.return_btn);


        mReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest postRequest = new StringRequest(Request.Method.POST, Config.url.concat(RETURN_BOOKS_API),
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
        setContentView(R.layout.activity_borrowed);

        init();

        getCheckouts();
    }

    private void getCheckouts() {
        final ArrayList<Book> borrowedBooks = new ArrayList<>();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Config.url.concat(GET_BORROWED_BOOKS_API),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        try {
                            JSONObject jsonObjectResponse=new JSONObject(response);
                            JSONArray jsonArrayResponse = jsonObjectResponse.getJSONArray("result");

                            for(int i=0;i<jsonArrayResponse.length();i++){
                                JSONObject bookObject=jsonArrayResponse.getJSONObject(i);
                                Book checkoutBook=new Book("", bookObject.getString("book_name"), "",
                                        "", "", "", "", "", "",
                                        "", bookObject.getString("due_date").substring(0, bookObject.getString("due_date").indexOf('T')));
                                borrowedBooks.add(checkoutBook);
                            }

                            BorrowedBookAdapter borrowedBookAdapter = new BorrowedBookAdapter(BorrowedActivity.this, borrowedBooks);
                            mBorrowedListView.setAdapter(borrowedBookAdapter);

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
                params.put("emailid", mSharedPref.getString("emailid", null));
                return params;
            }
        };
        queue.add(postRequest);
    }
}
