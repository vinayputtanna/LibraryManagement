package com.example.avdeepsandhu.librarymanagement;

/**
 * Created by Vinay on 12/5/2017.
 */

public class Book {
    private String mAuthor;
    private String mBookName;
    private String mCallNumber;
    private String mPublisher;
    private String mYearOfPublication;
    private String mLocation;
    private String mNumberOfCopies;
    private String mCurrentStatus;
    private String mKeywords;
    private String mCoverImage;
    private String mDueDate;

    public Book(String mAuthor, String mBookName, String mCallNumber, String mPublisher, String mYearOfPublication, String mLocation, String mNumberOfCopies, String mCurrentStatus, String mKeywords, String mCoverImage, String mDueDate) {
        this.mAuthor = mAuthor;
        this.mBookName = mBookName;
        this.mCallNumber = mCallNumber;
        this.mPublisher = mPublisher;
        this.mYearOfPublication = mYearOfPublication;
        this.mLocation = mLocation;
        this.mNumberOfCopies = mNumberOfCopies;
        this.mCurrentStatus = mCurrentStatus;
        this.mKeywords = mKeywords;
        this.mCoverImage = mCoverImage;
        this.mDueDate=mDueDate;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        this.mAuthor = author;
    }

    public String getBookName() {
        return mBookName;
    }

    public void setBookName(String bookName) {
        this.mBookName = bookName;
    }

    public String getCallNumber() {
        return mCallNumber;
    }

    public void setCallNumber(String callNumber) {
        this.mCallNumber = callNumber;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public void setPublisher(String publisher) {
        this.mPublisher = publisher;
    }

    public String getYearOfPublication() {
        return mYearOfPublication;
    }

    public void setYearOfPublication(String yearOfPublication) {
        this.mYearOfPublication=yearOfPublication;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation=location;
    }

    public String getNumberOfCopies() {
        return mNumberOfCopies;
    }

    public void setNumberOfCopies(String numberOfCopies) {
        this.mNumberOfCopies=numberOfCopies;
    }

    public String getCurrentStatus() {
        return mCurrentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.mCurrentStatus=currentStatus;
    }

    public String getKeywords() {
        return mKeywords;
    }

    public void setKeywords(String keywords) {
        this.mKeywords = keywords;
    }

    public String getCoverImage() {
        return mCoverImage;
    }

    public void setCoverImage(String coverImage) {
        this.mCoverImage = coverImage;
    }

    public String getDueDate() {
        return mDueDate;
    }

    public void setDueDate(String dueDate) {
        this.mDueDate= dueDate;
    }
}
