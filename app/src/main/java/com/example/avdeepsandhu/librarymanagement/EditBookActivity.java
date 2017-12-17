package com.example.avdeepsandhu.librarymanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.avdeepsandhu.librarymanagement.AddBookActivity.CAM_REQUEST;

public class EditBookActivity extends AppCompatActivity {

    EditText book_name,author_name,call_number,publisher,year_published, location, number_of_copies ,status, keywords;
    Button update_btn;
    private String url = Config.url + "/updatebook";
    ByteArrayOutputStream stream=new ByteArrayOutputStream();
    Bitmap photo;
    ImageButton book_pic;

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
        update_btn = (Button) findViewById(R.id.book_update_btn) ;





        Bundle bundle = getIntent().getExtras();
        final String book_id = bundle.getString("id");
        String title = bundle.getString("book_name");
        String author = bundle.getString("author");
        String call = bundle.getString("call_number");
        String publisher_name = bundle.getString("publisher");
        String publication_year = bundle.getString("year_of_publication");
        String location_lib = bundle.getString("location");
        String copies = bundle.getString("number_of_copies");
        String current_status = bundle.getString("status");
        String keyword = bundle.getString("keywords");
        //String book_image = bundle.getString("book_image");

        book_name.setText(title);
        author_name.setText(author);
        call_number.setText(call);
        publisher.setText(publisher_name);
        year_published.setText(publication_year);
        location.setText(location_lib);
        number_of_copies.setText(copies);
        status.setText(current_status);
        keywords.setText(keyword);


//        byte[] outImage=book_image.getBytes();
//        ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
//        final Bitmap theImage = BitmapFactory.decodeStream(imageStream);
//        book_pic.setImageBitmap(theImage);

        final RequestQueue queue = Volley.newRequestQueue(this);
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(photo!= null) {
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                }
                else{
                    photo= BitmapFactory.decodeResource(getResources(),R.drawable.profile);
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                }
                byte imagearray[]=stream.toByteArray();
                final String image_string=imagearray.toString();

                final String title = book_name.getText().toString();
                final String author = author_name.getText().toString();
                final String call = call_number.getText().toString();
                final String publisher_name = publisher.getText().toString();
                final String publication_year = year_published.getText().toString();
                final String location_lib = location.getText().toString();
                final String copies = number_of_copies.getText().toString();
                final String current_status = status.getText().toString();
                final String keyword = keywords.getText().toString();

                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                                try {
                                    JSONObject jObject = new JSONObject(response);
                                    String result = (String) jObject.get("status");
                                    if(result.equals("success")){
                                        Toast.makeText(getApplicationContext(),"Book Updated Successfully!",Toast.LENGTH_SHORT).show();
                                    }
                                    else if(result.equals("copieserr")){
                                        Toast.makeText(getApplicationContext(),"Status Cannot exceed number of copies",Toast.LENGTH_SHORT).show();
                                    }
                                    else if(result.equals("failure")){
                                        Toast.makeText(getApplicationContext(),"Book could not be updated!",Toast.LENGTH_SHORT).show();
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
                        params.put("id",book_id);
                        params.put("book_name",title);
                        params.put("author_name",author);
                        params.put("call_number", call);
                        params.put("publisher", publisher_name);
                        params.put("year_published",publication_year);
                        params.put("location", location_lib);
                        params.put("number_of_copies",copies);
                        params.put("status",current_status);
                        params.put("keywords",keyword);
                        params.put("book_image",image_string);
                        return params;
                    }
                };
                queue.add(postRequest);

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
        setContentView(R.layout.activity_edit_book);
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
