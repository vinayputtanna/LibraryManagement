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

public class BorrowedBookAdapter extends ArrayAdapter<Book> {

    public BorrowedBookAdapter(@NonNull Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.borrowed_list_item, parent, false);
        }

        Book currentBook=getItem(position);

        TextView borrowedBookName=(TextView)convertView.findViewById(R.id.borrowed_book_name);
        borrowedBookName.setText(currentBook.getBookName());

        ((TextView)convertView.findViewById(R.id.due_date)).setText("Due date: ");

        TextView borrowedBookDueDate=(TextView)convertView.findViewById(R.id.borrowed_book_due_date);
        borrowedBookDueDate.setText(currentBook.getDueDate());

        return convertView;
    }
}
