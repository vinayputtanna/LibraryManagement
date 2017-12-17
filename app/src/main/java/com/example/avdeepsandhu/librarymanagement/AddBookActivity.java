package com.example.avdeepsandhu.librarymanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddBookActivity extends AppCompatActivity {

    ImageButton book_pic;
    static final int CAM_REQUEST = 1888;
    EditText book_name, author_name, call_number, publisher, year_published, location, number_of_copies, status, keywords;
    Button save_btn;
    private String url = Config.url;
    ByteArrayOutputStream stream=new ByteArrayOutputStream();
    Bitmap photo;



    public void init()
    {
        book_pic = (ImageButton) findViewById(R.id.book_pic);
        book_name = (EditText) findViewById(R.id.book_name);
        author_name = (EditText) findViewById(R.id.author_name);
        call_number = (EditText) findViewById(R.id.call_number);
        publisher = (EditText) findViewById(R.id.publisher);
        year_published = (EditText) findViewById(R.id.year_published);
        location = (EditText) findViewById(R.id.location);
        number_of_copies = (EditText) findViewById(R.id.number_of_copies);
        status = (EditText) findViewById(R.id.status);
        keywords = (EditText) findViewById(R.id.keywords);
        save_btn = (Button) findViewById(R.id.book_save_btn);
        url = url+"/addbook";




        final RequestQueue queue = Volley.newRequestQueue(this);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bname = book_name.getText().toString();

                if(photo!= null) {
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                }
                else{
                    photo= BitmapFactory.decodeResource(getResources(),R.drawable.profile);
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                }
                byte imagearray[]=stream.toByteArray();
                final String image_string=imagearray.toString();



                String au_name = author_name.getText().toString();
                String call_no = call_number.getText().toString();
                String publish_year = year_published.getText().toString();
                String publiser_name = publisher.getText().toString();
                String loc = location.getText().toString();
                String copies = number_of_copies.getText().toString();
                String statuss = status.getText().toString();
                String regex = "\\d+";
                System.out.println(bname +" : "+ au_name +" : "+call_no+" : "+publish_year+" : "+publiser_name+" : "+loc+" : "+copies+" : "+statuss );
                if(bname .equals("") || au_name .equals("")|| publish_year .equals("") || call_no.equals("") || publiser_name.equals("") || loc.equals("") || copies.equals("") || statuss.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"All the fields are required to add the book",Toast.LENGTH_SHORT).show();
                }
                else if(!publish_year.matches(regex)){
                    Toast.makeText(getApplicationContext(),"published year field can only contain numbers",Toast.LENGTH_SHORT).show();
                }
                else if(!copies.matches(regex))
                {
                    Toast.makeText(getApplicationContext(),"Number of copies field can only contain numbers",Toast.LENGTH_SHORT).show();
                }
                else if(!statuss.matches(regex))
                {
                    Toast.makeText(getApplicationContext(),"Status field can only contain numbers",Toast.LENGTH_SHORT).show();
                }
                else {


                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // response
                                    Log.d("Response", response);
                                    try {
                                        JSONObject jObject = new JSONObject(response);
                                        String result = (String) jObject.get("status");
                                        if (result.equals("added")) {
                                            Toast.makeText(getApplicationContext(), "Book successfully added", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), Home_Activity.class);
                                            startActivity(intent);
                                        }
                                        else if(result.equals("copieserr")){
                                            Toast.makeText(getApplicationContext(), "current status cannot exceed Number of copies", Toast.LENGTH_SHORT).show();
                                        }
                                        else if (result.equals("update")) {
                                            Toast.makeText(getApplicationContext(), "Book successfully added to existing book ", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), Home_Activity.class);
                                            startActivity(intent);
                                        } else if (result.equals("failure")) {
                                            Toast.makeText(getApplicationContext(), "Book could not be added", Toast.LENGTH_SHORT).show();
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
                            params.put("book_name", String.valueOf(book_name.getText()));
                            params.put("author_name", String.valueOf(author_name.getText()));
                            params.put("call_number", String.valueOf(call_number.getText()));
                            params.put("publisher", String.valueOf(publisher.getText()));
                            params.put("year_published", String.valueOf(year_published.getText()));
                            params.put("location", String.valueOf(location.getText()));
                            params.put("number_of_copies", String.valueOf(number_of_copies.getText()));
                            params.put("status", String.valueOf(status.getText()));
                            params.put("keywords", String.valueOf(keywords.getText()));
                            params.put("book_image",image_string);


                            return params;
                        }
                    };
                    queue.add(postRequest);
                }
            }
        });




        book_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent,CAM_REQUEST);

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        init();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAM_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            book_pic.setImageBitmap(photo);
        }

    }
}
