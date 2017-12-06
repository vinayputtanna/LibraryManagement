package com.example.avdeepsandhu.librarymanagement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Vinay on 12/5/2017.
 */

public class CheckoutBookAdapter extends ArrayAdapter<Book> {

    public CheckoutBookAdapter(@NonNull Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.checkout_list_item, parent, false);
        }

        Book currentBook=getItem(position);

        TextView checkoutBookName=(TextView)convertView.findViewById(R.id.checkout_book_name);
        checkoutBookName.setText(currentBook.getBookName());

        ((TextView)convertView.findViewById(R.id.due_date)).setText("Due date: ");

        TextView checkoutBookDueDate=(TextView)convertView.findViewById(R.id.checkout_book_due_date);
        checkoutBookDueDate.setText(currentBook.getDueDate());

        return convertView;
    }
}
