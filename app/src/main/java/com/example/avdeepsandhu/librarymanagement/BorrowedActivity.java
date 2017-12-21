package com.example.avdeepsandhu.librarymanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BorrowedActivity extends AppCompatActivity {

    private RequestQueue queue;

    private ListView mBorrowedListView;
    private Button mReturnBtn;

    private SharedPreferences mSharedPref;

    private static final String GET_BORROWED_BOOKS_API = "/getBorrowedBooks";
    private static final String RETURN_BOOKS_API = "/returnBooks";

    private BorrowedBookAdapter mBorrowedBookAdapter;
    final Handler handler = new Handler();
    private int returnCount=0;
    private ArrayList<Book> mBorrowedBooks;


    public void init() {
        queue = Volley.newRequestQueue(this);
        mSharedPref = getSharedPreferences("session", Context.MODE_PRIVATE);

        mBorrowedListView = (ListView) findViewById(R.id.borrowed_list_view);
        mReturnBtn = (Button) findViewById(R.id.return_btn);



        mReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final StringBuilder bookIds=new StringBuilder();
                boolean isFirst=true;

                if(mBorrowedBookAdapter.mCheckStates.size()>9){
                    Toast.makeText(BorrowedActivity.this, "You can return upto 9 books at a time.", Toast.LENGTH_SHORT).show();
                    return;
                }


                for(int i=0;i<mBorrowedBookAdapter.mCheckStates.size();i++)
                {
                    int key=mBorrowedBookAdapter.mCheckStates.keyAt(i);

                    if(mBorrowedBookAdapter.mCheckStates.get(key)==true)
                    {
                        if(!isFirst)
                            bookIds.append(",");
                        bookIds.append(mBorrowedBooks.get(key).getBookid());
                    }
                    isFirst=false;
                }

                StringRequest postRequest = new StringRequest(Request.Method.POST, Config.url.concat(RETURN_BOOKS_API),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                                try {
                                    JSONObject jsonObjectResponse = new JSONObject(response);
                                    String result = (String) jsonObjectResponse.get("status");
                                    if(result.equals("success")){
                                        Toast.makeText(BorrowedActivity.this, "Books returned successfully.", Toast.LENGTH_SHORT).show();
                                        String toEmails = mSharedPref.getString("emailid", null);
                                        List toEmailList = Arrays.asList(toEmails
                                                .split("\\s*,\\s*"));
                                        String emailSubject = "Book Return";
                                        String emailBody = "You have successfully returned the book";
                                        String fromEmail = "Avdeep2802@gmail.com";
                                        String fromPassword = "Avneet0705";
                                        new SendMailTask(BorrowedActivity.this).execute(fromEmail,fromPassword, toEmailList, emailSubject, emailBody);
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent homeActivityIntent=new Intent(BorrowedActivity.this, Home_Activity.class);
                                                startActivity(homeActivityIntent);
                                                finish();
                                            }
                                        }, 3000);


//                                        String[] booksToRemove=bookIds.toString().split(",");
//
//                                        mBorrowedBookAdapter.remove(mBorrowedBooks.get());
//                                        mBorrowedBookAdapter.notifyDataSetChanged();
                                    }
                                    else if(result.equals("failure")){
                                        Toast.makeText(BorrowedActivity.this, "Books return Failure", Toast.LENGTH_SHORT).show();
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

                        Map<String, String> params = new HashMap<>();
                        params.put("emailid", mSharedPref.getString("emailid", null));
                        params.put("bookIds", bookIds.toString());
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
        mBorrowedBooks = new ArrayList<>();

        StringRequest postRequest = new StringRequest(Request.Method.POST, Config.url.concat(GET_BORROWED_BOOKS_API),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        try {
                            JSONObject jsonObjectResponse=new JSONObject(response);
                            JSONArray jsonArrayResponse = jsonObjectResponse.getJSONArray("result");

                            if(jsonArrayResponse.length()>0){
                                for(int i=0;i<jsonArrayResponse.length();i++){
                                    JSONObject bookObject=jsonArrayResponse.getJSONObject(i);
                                    Book checkoutBook=new Book();
                                    checkoutBook.setBookid(bookObject.getString("book_id"));
                                    checkoutBook.setTitle(bookObject.getString("book_name"));
                                    checkoutBook.setDueDate(bookObject.getString("due_date").substring(0, bookObject.getString("due_date").indexOf('T')));
                                    mBorrowedBooks.add(checkoutBook);
                                }

                                mBorrowedBookAdapter = new BorrowedBookAdapter(BorrowedActivity.this, mBorrowedBooks, mSharedPref);
                                mBorrowedListView.setAdapter(mBorrowedBookAdapter);
                            }
                            else{
                                ((TextView)findViewById(R.id.no_books_borrowed_txt)).setVisibility(View.VISIBLE);
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
                params.put("emailid", mSharedPref.getString("emailid", null));
                return params;
            }
        };
        queue.add(postRequest);
    }
}
