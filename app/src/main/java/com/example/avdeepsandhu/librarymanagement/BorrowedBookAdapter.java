package com.example.avdeepsandhu.librarymanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class BorrowedBookAdapter extends ArrayAdapter<Book> {

    private Context _context;
    private ArrayList<Book> mBooks;
    final Handler handler = new Handler();
    private RequestQueue requestQue;
    private SharedPreferences mSharedPref;

    public SparseBooleanArray mCheckStates;


    public BorrowedBookAdapter(@NonNull Context context, ArrayList<Book> books, SharedPreferences sp) {
        super(context, 0, books);
        this._context=context;
        this.mBooks=books;
        mCheckStates = new SparseBooleanArray(books.size());
        requestQue = Volley.newRequestQueue(context);
        mSharedPref = sp;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.borrowed_list_item, parent, false);
        }

        final Book currentBook=getItem(position);

        TextView borrowedBookName=(TextView)convertView.findViewById(R.id.borrowed_book_name);
        borrowedBookName.setText(currentBook.getTitle());

        ((TextView)convertView.findViewById(R.id.due_date)).setText("Due date: ");

        TextView borrowedBookDueDate=(TextView)convertView.findViewById(R.id.borrowed_book_due_date);
        borrowedBookDueDate.setText(currentBook.getDueDate());

        CheckBox borrowedBookChkBox = (CheckBox) convertView.findViewById(R.id.borrowed_book_chkbox);
        borrowedBookChkBox.setTag(position);
        borrowedBookChkBox.setChecked(mCheckStates.get(position, false));
        borrowedBookChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCheckStates.put((Integer) compoundButton.getTag(), isChecked);
            }
        });
        if(currentBook.ismChecked())
            borrowedBookChkBox.setChecked(true);
        else
            borrowedBookChkBox.setChecked(false);

        Button renewButton = (Button) convertView.findViewById(R.id.renew_btn);
        renewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        StringRequest postRequest = new StringRequest(Request.Method.POST, Config.url.concat("/renewBook"),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // response
                                        Log.d("Response", response);
                                        try {
                                            JSONObject jObject = new JSONObject(response);
                                            String result = (String) jObject.get("status");
                                            if (result.equals("waitlisted")) {
                                                Toast.makeText(_context, "Book has been waitlisted by another patron. Renewal not possible.", Toast.LENGTH_SHORT).show();
                                            } else if (result.equals("overdue")) {
                                                Toast.makeText(_context, "Book has been overdue. Renewal not possible.", Toast.LENGTH_SHORT).show();
                                            } else if (result.equals("non-renewable")) {
                                                Toast.makeText(_context, "Book has been renewed twice. Renewal not possible.", Toast.LENGTH_SHORT).show();
                                            }
                                            else if(result.equals("renewed")){
                                                Toast.makeText(_context, "Book renewed successfully.", Toast.LENGTH_SHORT).show();
                                                Intent borrowedActivityIntent=new Intent(_context, BorrowedActivity.class);
                                                _context.startActivity(borrowedActivityIntent);
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
                                params.put("emailId", mSharedPref.getString("emailid", null));
                                params.put("bookId", currentBook.getBookid());
                                return params;
                            }
                        };
                        requestQue.add(postRequest);
                    }
                }, 2000);
            }
        });

        return convertView;
    }

    public boolean isChecked(int position) {
        return mCheckStates.get(position, false);
    }

    public void setChecked(int position, boolean isChecked) {
        mCheckStates.put(position, isChecked);
    }
}
