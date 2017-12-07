package com.example.avdeepsandhu.librarymanagement;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ganesha on 12/5/2017.
 */

public class BookList_Adapter extends RecyclerView.Adapter<BookList_Adapter.ViewHolder> {
    ArrayList<Book> bookArrayList=new ArrayList<Book>();
    Context context;
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
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patron_book_item, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int pos=position+1;
        holder.bind(pos);

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

        public void bind(final int pos){
            BookName.setText(bookArrayList.get(pos-1).getTitle());
            AuthorName.setText(bookArrayList.get(pos-1).getAuthor());
//            //DeleteButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    bookArrayList.remove(pos-1);
//                    //write method to delete from server db
//                }
//            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(pos);
                }
            });


        }

    }
}
