package com.bhcc.jdiggins.myaddressbook;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.UUID;

import static android.icu.lang.UProperty.INT_START;

/**
 * Created by JCDig on 3/31/2018.
 * John Diggins
 */

public class ContactFragment extends Fragment {
    private static final String TAG = "contactFragment";
    private static final String ARG_CONTACT_ID = "contact_id";

    private Contact mContact;

    private EditText mNameField;
    private EditText mPhoneField;
    private EditText mEmailField;
    private EditText mStreetField;
    private EditText mCityField;
    private EditText mStateField;
    private EditText mZipField;

    // used to enable / disable the EditTexts
    private KeyListener mNameListener;
    private KeyListener mPhoneListener;
    private KeyListener mEmailListener;
    private KeyListener mStreetListener;
    private KeyListener mCityListener;
    private KeyListener mStateListener;
    private KeyListener mZipListener;

    private Callbacks mCallbacks;


    public interface Callbacks {
        void onContactUpdated(Contact contact);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID contactId = (UUID) getArguments().getSerializable(ARG_CONTACT_ID);
        mContact = ContactManager.get(getActivity()).getContact(contactId);

        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        if(mContact.getName().compareTo("") == 0) {
            ContactManager.get(getActivity()).fixNoNameContact(mContact);
            updateContact();
        }
        super.onPause();

        closeKeyboard();
        ContactManager.get(getActivity()).updateContact(mContact);
    }

    @Override
    public void onDestroy() {
        if(mContact.getName().compareTo("") == 0) {

            ContactManager.get(getActivity()).fixNoNameContact(mContact);
            updateContact();
        }
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact, container, false);

        // ensure edit button set to white
        Drawable edit = ContextCompat.getDrawable(getContext(), R.drawable.ic_edit_24dp);
        edit.clearColorFilter();

        createNameField((EditText)v.findViewById(R.id.editName));
        createPhoneField((EditText) v.findViewById(R.id.editPhone));
        createEmailField((EditText) v.findViewById(R.id.editEmail));
        createStreetField((EditText) v.findViewById(R.id.editStreet));
        createCityField((EditText) v.findViewById(R.id.editCity));
        createStateField((EditText) v.findViewById(R.id.editState));
        createZipField((EditText) v.findViewById(R.id.editZip));

        return v;
    }

    public static ContactFragment newInstance(UUID contactId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONTACT_ID, contactId);

        ContactFragment fragment = new ContactFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_contact_page, menu);
        MenuItem button = menu.findItem(R.id.edit_contact);
        if(mContact.getName().compareTo("") == 0) {
            turnEditOn(button.getIcon());

            mNameField.requestFocus();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Drawable button = item.getIcon();

        switch(item.getItemId()) {
            case R.id.edit_contact:

                if(mContact.isEdit() == false) {
                    turnEditOn(button);
                } else {
                   turnEditOff(button);
                }
                return true;

            case R.id.delete_contact:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                deleteContact();

                                if(getSmallestDp() > 600) {
                                    removeFragment();
                                } else {
                                    getActivity().onBackPressed();
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delete Contact")
                        .setMessage("Are you sure you want to delete this contact?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show();

                return true;
            default:

                mContact.setIsEdit(false);
                return false;
        }
    }

    private void removeFragment() {
        Fragment me = this;
        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    private void turnEditOn(Drawable icon) {

        mContact.setIsEdit(true);
        icon.setColorFilter(Color.rgb(255, 76, 0), PorterDuff.Mode.SRC_IN);
        mNameField.setKeyListener(mNameListener);
        mPhoneField.setKeyListener(mPhoneListener);
        mEmailField.setKeyListener(mEmailListener);
        mStreetField.setKeyListener(mStreetListener);
        mCityField.setKeyListener(mCityListener);
        mStateField.setKeyListener(mStateListener);
        mZipField.setKeyListener(mZipListener);
    }

    private void turnEditOff(Drawable icon) {
        mContact.setIsEdit(false);
        icon.clearColorFilter();
        closeKeyboard();

        mNameField.setKeyListener(null);
        mPhoneField.setKeyListener(null);
        mEmailField.setKeyListener(null);
        mStreetField.setKeyListener(null);
        mCityField.setKeyListener(null);
        mStateField.setKeyListener(null);
        mZipField.setKeyListener(null);
    }

    public void updateContact() {
        ContactManager.get(getActivity()).updateContact(mContact);
        mCallbacks.onContactUpdated(mContact);
    }

    public void deleteContact() {
        ContactManager.get(getActivity()).deleteContact(mContact);
        mCallbacks.onContactUpdated(mContact);
    }

    public void closeKeyboard() {

        InputMethodManager inputManager =
                (InputMethodManager) getContext().
                        getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

    }

    public void openKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  BEGINNING EDIT TEXT CREATION FUNCTIONS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void createNameField(EditText nameField) {
        mNameField = nameField;
        mNameField.setText(mContact.getName());
        mNameListener = mNameField.getKeyListener();
        mNameField.setKeyListener(null);
        mNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mContact.setName(s.toString());
                updateContact();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void createPhoneField(EditText phoneField) {
        mPhoneField = phoneField;
        mPhoneField.setText(mContact.getPhone());
        mPhoneListener = mPhoneField.getKeyListener();
        mPhoneField.setKeyListener(null);
        mPhoneField.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mPhoneField.setSelection(mPhoneField.getText().length());
            }
        });

        mPhoneField.addTextChangedListener(new TextWatcher() {

            boolean editing = false;
            boolean deleting = false;
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                if( after == 0)
                    deleting = true;
                else
                    deleting = false;
            }
            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mContact.setPhone(s.toString());
                updateContact();
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (!editing) {
                    editing = true;
                    String currentText = s.toString();
                    if(deleting) {
                        if (currentText.matches("^\\d{3}[-]\\d{3}[-]\\d$"))
                            s.delete(7, 8);
                    } else {
                        if (currentText.matches("^\\d{3}$"))
                            s.append("-");
                        else if (currentText.matches("^\\d{3}[-]\\d{5}$"))
                            s.insert(7, "-");
                        else if (currentText.matches("^\\d{3}[-]\\d{3}[-]\\d{4}\\w+$"))
                            s.delete(s.length() - 1, s.length());
                    }


                    editing = false;
                }
            }
        });
    }

    private void createEmailField(EditText emailField) {
        mEmailField = emailField;
        mEmailField.setText(mContact.getEmail());

        mEmailListener = mEmailField.getKeyListener();
        mEmailField.setKeyListener(null);
        mEmailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mContact.setEmail(s.toString());
                updateContact();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void createStreetField(EditText streetField) {
        mStreetField = streetField;
        mStreetField.setText(mContact.getStreet());
        mStreetListener = mStreetField.getKeyListener();
        mStreetField.setKeyListener(null);
        mStreetField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mContact.setStreet(s.toString());
                updateContact();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void createCityField(EditText cityField) {
        mCityField = cityField;
        mCityField.setText(mContact.getCity());
        mCityListener = mCityField.getKeyListener();
        mCityField.setKeyListener(null);
        mCityField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mContact.setCity(s.toString());
                updateContact();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void createStateField(EditText stateField) {
        mStateField = stateField;
        mStateField.setText(mContact.getState());
        mStateListener = mStateField.getKeyListener();
        mStateField.setKeyListener(null);
        mStateField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mContact.setState(s.toString());
                updateContact();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void createZipField(EditText zipField) {
        mZipField = zipField;
        mZipField.setText(mContact.getZip());
        mZipListener = mZipField.getKeyListener();
        mZipField.setKeyListener(null);
        mZipField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mContact.setZip(s.toString());
                updateContact();
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().matches("^\\d{6}$"))
                    s.delete(s.length()-1,s.length());
            }
        });
    }
    /////////////////////// END EDIT TEXT CREATION FUNCTIONS ///////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  BEGINNING FUNCTIONS FOR FINDING SCREEN SIZE
    //
    //  This was part of my solution to handling deleting a contact
    //
    //  original code from:
    //      https://androidknowledgeblog.wordpress.com
    //              /2016/04/04/how-to-detect-mobile-screen-size-programatically/
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private DisplayMetrics getDisplayMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }
    private float getWidthDp(DisplayMetrics metrics) {
        int widthPixels = metrics.widthPixels;
        float scaleFactor = metrics.density;
        float widthDp = widthPixels / scaleFactor;
        return widthDp;
    }
    private float getHeightDp(DisplayMetrics metrics) {
        int heightPixels = metrics.heightPixels;
        float scaleFactor = metrics.density;
        float heightDp = heightPixels / scaleFactor;
        return heightDp;
    }
    private float getSmallestDp() {
        DisplayMetrics metrics = getDisplayMetrics();
        return Math.min(getWidthDp(metrics), getHeightDp(metrics));
    }
    /////////////////////////// END SCREEN SIZE FUNCTIONS //////////////////////////////////////////

}
