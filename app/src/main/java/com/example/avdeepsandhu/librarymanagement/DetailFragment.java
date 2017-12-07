package com.example.avdeepsandhu.librarymanagement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ganesha on 12/5/2017.
 */

public class DetailFragment extends Fragment {
    ArrayList<Book> bookList_modelArrayList=new ArrayList<Book>();
    TextView tv;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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






        return v;
    }
}
