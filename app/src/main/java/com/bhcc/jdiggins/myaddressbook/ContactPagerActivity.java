package com.bhcc.jdiggins.myaddressbook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.List;
import java.util.UUID;

/**
 * Created by JCDig on 3/31/2018.
 * John Diggins
 */

public class ContactPagerActivity extends AppCompatActivity
        implements ContactFragment.Callbacks{
    private static final String TAG = "contactPagerActivity";

    private static final String EXTRA_CONTACT_ID = "" +
            "com.bhcc.jdiggins.myaddressbook.contact_id";
    @Override
    public void onContactUpdated(Contact contact) { }
    private ViewPager mViewPager;
    private List<Contact> mContacts;

    public static Intent newIntent(Context packageContext, UUID contactId) {
        Intent intent = new Intent(packageContext, ContactPagerActivity.class);
        intent.putExtra(EXTRA_CONTACT_ID, contactId);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_pager);

        UUID contactId = (UUID) getIntent().getSerializableExtra(EXTRA_CONTACT_ID);

        mViewPager = (ViewPager) findViewById(R.id.contact_view_pager);

        mContacts = ContactManager.get(this).getContacts();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Contact contact = mContacts.get(position);
                return ContactFragment.newInstance(contact.getId());
            }
            @Override
            public int getCount() {return mContacts.size(); }
        });

        for(int i = 0; i < mContacts.size(); i++) {
            if(mContacts.get(i).getId().equals(contactId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }


    }



}
