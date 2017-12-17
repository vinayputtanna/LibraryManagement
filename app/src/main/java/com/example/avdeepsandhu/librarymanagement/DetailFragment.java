package com.example.avdeepsandhu.librarymanagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ganesha on 12/5/2017.
 */

@SuppressLint("ValidFragment")
public class DetailFragment extends Fragment {
    ArrayList<Book> bookList_modelArrayList=new ArrayList<Book>();
    TextView tv;
    Context context;
    String url = Config.url + "/addtocart";

    @SuppressLint("ValidFragment")
    public DetailFragment(Context context){
        this.context = context;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
//        bookList_modelArrayList=getArguments().getParcelableArrayList("Books");


        View v=inflater.inflate(R.layout.book_detailpager,container,false);
        TextView bookname = (TextView) v.findViewById(R.id.book_name);
        bookname.setText(getArguments().getString("bookname"));
        Log.d("here",getArguments().getString("bookname"));
        TextView author = (TextView) v.findViewById(R.id.author_name);
        author.setText(getArguments().getString("author"));

        TextView callno = (TextView) v.findViewById(R.id.call_number);
        callno.setText(getArguments().getString("callno"));

        TextView publisher = (TextView) v.findViewById(R.id.publisher);
        publisher.setText(getArguments().getString("publisher"));

        TextView yop = (TextView) v.findViewById(R.id.year_published);
        yop.setText(getArguments().getString("year"));

        TextView location = (TextView) v.findViewById(R.id.location);
        location.setText(getArguments().getString("location"));

        TextView copies = (TextView) v.findViewById(R.id.number_of_copies);
        copies.setText(getArguments().getString("copies"));
        TextView status = (TextView) v.findViewById(R.id.status);
        status.setText(getArguments().getString("status"));
        TextView keyword = (TextView) v.findViewById(R.id.keywords);
        keyword.setText(getArguments().getString("keyword"));

        Button add_to_cart = (Button) v.findViewById(R.id.add_to_cart);


        final RequestQueue queue = Volley.newRequestQueue(context);
        add_to_cart.setOnClickListener(new View.OnClickListener() {
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
                                    Log.e("Status", result);
                                    if(result.equals("limit")){
                                        Toast.makeText(context,"Remove an item from cart to add new item! Max 3 items can be added to cart",Toast.LENGTH_SHORT).show();
                                    }
                                    else if(result.equals("repeat")){
                                        Toast.makeText(context,"Book Already Exists in Cart",Toast.LENGTH_SHORT).show();
                                    }
                                    else if(result.equals("success")){
                                        Toast.makeText(context,"Successfully Added to Cart",Toast.LENGTH_SHORT).show();
                                    }
                                    else if(result.equals("failure")) {
                                        Toast.makeText(context,"Failed to Add to Cart",Toast.LENGTH_SHORT).show();
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

                        params.put("book_name",getArguments().getString("bookname") );
                        params.put("author_name",getArguments().getString("author") );
                        params.put("call_number", getArguments().getString("callno"));
                        params.put("publisher", getArguments().getString("publisher"));
                        params.put("year_published", getArguments().getString("year"));
                        params.put("location", getArguments().getString("location"));
                        params.put("number_of_copies",getArguments().getString("copies") );
                        params.put("status", getArguments().getString("status"));
                        params.put("keywords", getArguments().getString("keyword"));
                        params.put("patron_emailid", getArguments().getString("patron_email_id"));


                        return params;
                    }
                };
                queue.add(postRequest);
            }
        });



        return v;
    }
}
