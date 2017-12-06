package com.example.avdeepsandhu.librarymanagement;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ganesha on 12/5/2017.
 */

public class Book implements Parcelable {
   String Author;
   String Title;
   String Call_number;
   String Publisher;
   String Year_of_publication;
   String Location_in_library;
   String Number_of_copies;
   String Current_status;
   String Keywords;
   String Coverage_image;


    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCall_number() {
        return Call_number;
    }

    public void setCall_number(String call_number) {
        Call_number = call_number;
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String publisher) {
        Publisher = publisher;
    }

    public String getYear_of_publication() {
        return Year_of_publication;
    }

    public void setYear_of_publication(String year_of_publication) {
        Year_of_publication = year_of_publication;
    }

    public String getLocation_in_library() {
        return Location_in_library;
    }

    public void setLocation_in_library(String location_in_library) {
        Location_in_library = location_in_library;
    }

    public String getNumber_of_copies() {
        return Number_of_copies;
    }

    public void setNumber_of_copies(String number_of_copies) {
        Number_of_copies = number_of_copies;
    }

    public String getCurrent_status() {
        return Current_status;
    }

    public void setCurrent_status(String current_status) {
        Current_status = current_status;
    }

    public String getKeywords() {
        return Keywords;
    }

    public void setKeywords(String keywords) {
        Keywords = keywords;
    }

    public String getCoverage_image() {
        return Coverage_image;
    }

    public void setCoverage_image(String coverage_image) {
        Coverage_image = coverage_image;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
