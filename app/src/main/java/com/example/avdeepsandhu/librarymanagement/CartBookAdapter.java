package com.example.avdeepsandhu.librarymanagement;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vinay on 12/5/2017.
 */

public class CartBookAdapter extends ArrayAdapter<Book> {
    final Handler handler = new Handler();
    final String url = Config.url + "/deletefromcart";
    Context context;
    ArrayList<Book> cartBooks = new ArrayList<>();
    String patron_email_id;

    public CartBookAdapter(@NonNull Context context, ArrayList<Book> books, String patron_email_id) {
        super(context, 0, books);
        this.context = context;
        this.cartBooks = books;
        this.patron_email_id = patron_email_id;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.cart_list_item, parent, false);
        }

        final Book currentBook=getItem(position);

        TextView cartBookName=(TextView)convertView.findViewById(R.id.cart_book_name);
        cartBookName.setText(currentBook.getTitle());

        TextView cartAuthor=(TextView)convertView.findViewById(R.id.cart_author_name);
        cartAuthor.setText(currentBook.getAuthor());

        Button delete_btn = (Button) convertView.findViewById(R.id.delete_cart_book);
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        RequestQueue queue2 = Volley.newRequestQueue(context);
                        StringRequest postRequest2 = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // response
                                        Log.d("Response", response);
                                        try {
                                            JSONObject jObject = new JSONObject(response);
                                            String result = (String) jObject.get("status");
                                            if (result.equals("success")) {
                                                Toast.makeText(context, "Book successfully deleted from Cart !", Toast.LENGTH_LONG).show();
                                                System.out.println("{{{{{{{{{{{{{deleted}}}}}}}}}}}}}}}");
                                                cartBooks.remove(position);
                                                notifyDataSetChanged();
                                            }
                                            else if(result.equals("failure")) {
                                                Toast.makeText(context, "Book could not be deleted from Cart !", Toast.LENGTH_LONG).show();

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
                                        Log.d("Error.Response", "Error in response in cart : "+ error);
                                    }
                                }
                        ) {

                            @Override
                            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                                if (response.headers == null) {
                                    // cant just set a new empty map because the member is final.
                                    response = new NetworkResponse(
                                            response.statusCode,
                                            response.data,
                                            Collections.<String, String>emptyMap(), // this is the important line, set an empty but non-null map.
                                            response.notModified,
                                            response.networkTimeMs);
                                }
                                return super.parseNetworkResponse(response);
                            }

                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                Log.e("PPPPPPPPPPP", " i sthe book");
                                params.put("book_name", currentBook.getTitle());
                                params.put("author", currentBook.getAuthor());
                                params.put("patron_emailid", patron_email_id);

                                return params;
                            }
                        };
                        queue2.add(postRequest2);

                    }
                }, 1000);
            }
        });

        return convertView;
    }
}