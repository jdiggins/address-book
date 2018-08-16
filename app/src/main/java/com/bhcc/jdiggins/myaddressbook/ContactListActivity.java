package com.bhcc.jdiggins.myaddressbook;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by JCDig on 3/31/2018.
 */

public class ContactListActivity extends SingleFragmentActivity
        implements ContactListFragment.Callbacks, ContactFragment.Callbacks{

    @Override
    protected Fragment createFragment() {
        return new ContactListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onContactSelected(Contact contact) {
        if(findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = ContactPagerActivity.newIntent(this, contact.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = ContactFragment.newInstance(contact.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    public void onContactUpdated(Contact contact) {
        ContactListFragment listFragment = (ContactListFragment)
                getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }



}
