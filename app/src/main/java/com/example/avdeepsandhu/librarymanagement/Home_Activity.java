package com.example.avdeepsandhu.librarymanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class Home_Activity extends AppCompatActivity {



    ViewPager viewpager;
    PagerAdapter pageradapter;
    String url = Config.url;
    RecyclerView.Adapter BookAdapter;
    RecyclerView BookList;
    RequestQueue queue;
    String myurl= Config.url;
    ArrayList<Book> BookArrayList=new ArrayList<Book>();
    final Handler handler = new Handler();
    public void init()
    {
        final RequestQueue queue = Volley.newRequestQueue(this);
        SharedPreferences sp = getSharedPreferences("session", Context.MODE_PRIVATE);
        final String emailid = sp.getString("emailid",null);
        url = url+"/checkifverified";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        try {
                            JSONObject jObject = new JSONObject(response);
                            String result = (String) jObject.get("status").toString();
                            Log.e("Status", result);
                            if(result.equals("success")){

                            }
                            else if(result.equals("0")) {
                                Intent intent = new Intent(getApplicationContext(), VerifyEmailActivity.class);
                                startActivity(intent);
                                finish();
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
                        Log.d("Error.Response", "Error in responsejj");
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_home_);
        init();
        Button btn = (Button)findViewById(R.id.searchbutton) ;
        queue = Volley.newRequestQueue(this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookList=(RecyclerView)findViewById(R.id.recyclerview);
                TextView searchstringtv=(TextView)findViewById(R.id.searchbook);
                final String searchstring = searchstringtv.getText().toString();
                execute(searchstring);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        System.out.println("I am ahere :::::::::::::::::::::::::::::::::::::" + BookArrayList);
                        BookAdapter = new BookList_Adapter(getApplicationContext(),BookArrayList, "patron", new BookList_Adapter.OnItemClickListener(){
                            @Override
                            public void onItemClick(int position) {
                                setContentView(R.layout.detailfragment);
                                viewpager= (ViewPager) findViewById(R.id.bookdetailview);
                                pageradapter = new GridDetailAdapter(BookArrayList,getSupportFragmentManager());
                                viewpager.setAdapter(pageradapter);
                                viewpager.setCurrentItem(position);
                            }
                        });
                        BookList.setAdapter(BookAdapter);
                        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
                        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
                        BookList.setLayoutManager(linearLayoutManager);
                        BookList.setHasFixedSize(true);


                        //Do something after 100ms
                    }
                }, 1000);




            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_librarian, menu);
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


            case R.id.action_addbook:

                Intent intent1 = new Intent(getApplicationContext(), AddBookActivity.class);
                startActivity(intent1);
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void execute(final String searchstring) {
        myurl = myurl+"/searchbybookname";
        Log.e("MyURL : ", myurl);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest jsonObject = new StringRequest(Request.Method.POST, myurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonresponseobject = new JSONObject(response);
                    JSONArray jsonArray = jsonresponseobject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject bookObject = jsonArray.getJSONObject(i);
                        Log.e("RESPONSE : ", bookObject.getString("book_name") +" : " + bookObject.getString("author"));
                        Book bookList_model = new Book();
                        bookList_model.setBookName(bookObject.getString("book_name"));
                        bookList_model.setAuthor(bookObject.getString("author"));
                        bookList_model.setCallNumber(bookObject.getString("call_number"));
                        bookList_model.setPublisher(bookObject.getString("publisher"));
                        bookList_model.setYearOfPublication(bookObject.getString("year_of_publication"));
                        bookList_model.setLocation(bookObject.getString("location"));
                        bookList_model.setNumberOfCopies(bookObject.getString("number_of_copies"));
                        bookList_model.setCurrentStatus(bookObject.getString("current_status"));
                        bookList_model.setKeywords(bookObject.getString("keywords"));
                        BookArrayList.add(bookList_model);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("book_name", searchstring);
                return params;
            }


        };
        queue.add(jsonObject);


    }
}