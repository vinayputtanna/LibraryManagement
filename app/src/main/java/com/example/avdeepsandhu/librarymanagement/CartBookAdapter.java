package com.example.avdeepsandhu.librarymanagement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Vinay on 12/5/2017.
 */

public class CartBookAdapter extends ArrayAdapter<Book> {

    public CartBookAdapter(@NonNull Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.cart_list_item, parent, false);
        }

        Book currentBook=getItem(position);

        TextView cartBookName=(TextView)convertView.findViewById(R.id.cart_book_name);
        cartBookName.setText(currentBook.getBookName());

        TextView cartAuthor=(TextView)convertView.findViewById(R.id.cart_author_name);
        cartAuthor.setText(currentBook.getAuthor());

        return convertView;
    }
}
