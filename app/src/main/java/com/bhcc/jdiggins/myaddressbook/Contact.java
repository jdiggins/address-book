package com.bhcc.jdiggins.myaddressbook;


import java.util.UUID;
/**
 * Created by JCDig on 3/31/2018.
 * John Diggins
 */

public class Contact {
    private UUID mId;

    private String mName;
    private String mPhone;
    private String mEmail;
    private String mStreet;
    private String mCity;
    private String mState;
    private String mZip;

    private boolean isEdit;

    public Contact() {
        this(UUID.randomUUID());
    }

    public Contact(UUID id) {
        setId(id);
        isEdit = false;
        mName = "";
    }

    public Contact(String name, String phone, String email, String street, String city, String state, String zip) {
        mName = name;
        mPhone = phone;
        mEmail = email;
        mStreet = street;
        mCity = city;
        mState = state;
        mZip = zip;
    }

    public boolean isEdit() {return isEdit;}
    public void setIsEdit(boolean isEdit) {this.isEdit = isEdit;}



    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setPhone(String phone) { mPhone = phone;}

    public String getPhone() { return mPhone; }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getStreet() {
        return mStreet;
    }

    public void setStreet(String street) {
        mStreet = street;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getZip() {
        return mZip;
    }

    public void setZip(String zip) {
        mZip = zip;
    }
}
