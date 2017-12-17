package com.example.avdeepsandhu.librarymanagement;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ganesha on 12/5/2017.
 */

public class BookList_Adapter extends RecyclerView.Adapter<BookList_Adapter.ViewHolder> {
    ArrayList<Book> bookArrayList=new ArrayList<Book>();
    Context context;
    final Handler handler = new Handler();
    public static String  xurl = Config.url +"/deletebook";



    public String user_mode;
    public interface OnItemClickListener{
        void onItemClick(int position);

    }
    private OnItemClickListener listener;
    public BookList_Adapter(Context context, ArrayList<Book> bookArrayList, String user_mode, OnItemClickListener listener) {
        this.bookArrayList=bookArrayList;
        this.user_mode=user_mode;
        this.listener=listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(user_mode.equals("patron")) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patron_book_item, parent, false);
        }
        else
        {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.librarian_book_item, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int pos=position+1;
        holder.bind(pos, holder);
    }

    @Override
    public int getItemCount() {
        Log.e("SIZEEEEEEE : ", String.valueOf(bookArrayList.size()));
        return bookArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView Image;
        public TextView BookName;
        public TextView AuthorName;
        public Button UpdateButton;
        public Button DeleteButton;


        public ViewHolder(View itemView) {
            super(itemView);
            Image=(ImageView)itemView.findViewById(R.id.book_image);
            BookName=(TextView)itemView.findViewById(R.id.book_name);
            AuthorName=(TextView)itemView.findViewById(R.id.author_name);
            UpdateButton=(Button) itemView.findViewById(R.id.update_book);
            DeleteButton=(Button)itemView.findViewById(R.id.delete_book);

        }

        public void bind(final int pos, final ViewHolder holder){
            BookName.setText(bookArrayList.get(pos-1).getTitle());
            AuthorName.setText(bookArrayList.get(pos-1).getAuthor());
            final String id = bookArrayList.get(pos-1).getBookid();
            final String book_name = bookArrayList.get(pos-1).getTitle();
            final String author = bookArrayList.get(pos-1).getAuthor();
            final String call_number = bookArrayList.get(pos-1).getCall_number();
            final String publisher = bookArrayList.get(pos-1).getPublisher();
            final String year_of_publication = bookArrayList.get(pos-1).getYear_of_publication();
            final String location = bookArrayList.get(pos-1).getLocation_in_library();
            final String number_of_copies = bookArrayList.get(pos-1).getNumber_of_copies();
            final String status = bookArrayList.get(pos-1).getCurrent_status();
            final String keywords = bookArrayList.get(pos-1).getKeywords();

//            String image=bookArrayList.get(pos-1).getCoverage_image();
//            byte[] outImage= image.getBytes(StandardCharsets.UTF_8);
//            Log.e("???????", bookArrayList.get(pos-1).getCoverage_image());
//            ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
//            final Bitmap theImage = BitmapFactory.decodeStream(imageStream);
//            Image.setImageBitmap(theImage);

            if(user_mode.equals("librarian")) {
                DeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.e("DELETE BUTTON : ", "Clicked on " + String.valueOf(pos));
                        Log.e("Book ID : ", id);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                RequestQueue queue2 = Volley.newRequestQueue(context);
                                StringRequest postRequest2 = new StringRequest(Request.Method.POST, xurl,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                // response
                                                Log.d("Response", response);
                                                try {
                                                    JSONObject jObject = new JSONObject(response);
                                                    String result = (String) jObject.get("status");
                                                    if (result.equals("borrowed")) {
                                                        Toast.makeText(context, "Book is borrowed so cannot be deleted !", Toast.LENGTH_LONG).show();
                                                    } else if (result.equals("{{{{{{{{{{{{{failure}}}}}}}}}}}}}}}")) {
                                                        Toast.makeText(context, "Book cannot be deleted ! Server error", Toast.LENGTH_LONG).show();
                                                    } else if (result.equals("success")) {
                                                        Toast.makeText(context, "Book successfully deleted !", Toast.LENGTH_LONG).show();
                                                        System.out.println("{{{{{{{{{{{{{deleted}}}}}}}}}}}}}}}");
                                                        bookArrayList.remove(pos - 1);
                                                        notifyDataSetChanged();

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
                                        params.put("id", id);
                                        return params;
                                    }
                                };
                                queue2.add(postRequest2);

                            }
                        }, 2000);


                        //write method to delete from server db
                    }
                });
            }
            if(user_mode.equals("librarian")){
                UpdateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, EditBookActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("id",id);
                        bundle.putString("book_name",book_name);
                        bundle.putString("author",author);
                        bundle.putString("call_number",call_number);
                        bundle.putString("publisher",publisher);
                        bundle.putString("year_of_publication",year_of_publication);
                        bundle.putString("location",location);
                        bundle.putString("number_of_copies",number_of_copies);
                        bundle.putString("status",status);
                        bundle.putString("keywords",keywords);
                        //bundle.putString("book_image",  bookArrayList.get(pos-1).getCoverage_image());

                        intent.putExtras(bundle);
                        context.startActivity(intent);

                    }
                });
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(pos);
                }
            });


        }

    }
}
