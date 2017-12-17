package com.example.avdeepsandhu.librarymanagement;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Ganesha on 12/5/2017.
 */

class GridDetailAdapter extends FragmentPagerAdapter {
    ArrayList<Book> bookarrayList=new ArrayList<Book>();
    Context context;
    String patron_email_id;

    public GridDetailAdapter(ArrayList<Book> bookarrayList, FragmentManager supportFragmentManager, Context context, String patron_email_id) {
        super(supportFragmentManager);
        System.out.println(bookarrayList+" &&&&&&&&&&&&&&&&&&&&&&&&&&&");
        this.bookarrayList=bookarrayList;
        this.context = context;
        this.patron_email_id = patron_email_id;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fa=new DetailFragment(context);
        Bundle ba=new Bundle();
        ba.putInt("Position",position);
        ba.putString("patron_email_id",patron_email_id);



       // ba.putParcelableArrayList("Books",bookarrayList);
        ba.putString("bookname", bookarrayList.get(position).getTitle());
        ba.putString("author", bookarrayList.get(position).getAuthor());
        ba.putString("callno", bookarrayList.get(position).getCall_number());
        ba.putString("publisher", bookarrayList.get(position).getPublisher());
        ba.putString("year", bookarrayList.get(position).getYear_of_publication());
        ba.putString("location", bookarrayList.get(position).getLocation_in_library());
        ba.putString("copies", bookarrayList.get(position).getNumber_of_copies());
        ba.putString("status", bookarrayList.get(position).getCurrent_status());
        ba.putString("keyword", bookarrayList.get(position).getKeywords());
        fa.setArguments(ba);
        return fa;
    }

    @Override
    public int getCount() {
        return bookarrayList.size();
    }
}
