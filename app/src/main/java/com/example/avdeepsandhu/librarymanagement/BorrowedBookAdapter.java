package com.example.avdeepsandhu.librarymanagement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Vinay on 12/5/2017.
 */

public class BorrowedBookAdapter extends ArrayAdapter<Book> {

    private Context _context;
    private ArrayList<Book> mBooks;

    public SparseBooleanArray mCheckStates;

    public BorrowedBookAdapter(@NonNull Context context, ArrayList<Book> books) {
        super(context, 0, books);
        this._context=context;
        this.mBooks=books;
        mCheckStates = new SparseBooleanArray(books.size());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.borrowed_list_item, parent, false);
        }

        Book currentBook=getItem(position);

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

        return convertView;
    }

    public boolean isChecked(int position) {
        return mCheckStates.get(position, false);
    }

    public void setChecked(int position, boolean isChecked) {
        mCheckStates.put(position, isChecked);
    }
}
