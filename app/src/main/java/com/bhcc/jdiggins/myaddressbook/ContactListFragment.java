package com.bhcc.jdiggins.myaddressbook;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by JCDig on 3/31/2018.
 */

public class ContactListFragment extends Fragment {
    private RecyclerView mContactRecyclerView;
    private ContactAdapter mAdapter;
    private Callbacks mCallbacks;
    private TextView mCopyView;

    public interface Callbacks {
        void onContactSelected(Contact contact);
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        mContactRecyclerView = (RecyclerView) view.findViewById(R.id.contact_recycler_view);
        mContactRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // set api version in textview, visible bottom of contact list
        mCopyView = (TextView) view.findViewById(R.id.copyright_text);
        mCopyView.append(" API: ");
        mCopyView.append(Integer.toString(android.os.Build.VERSION.SDK_INT));

        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_contact_list, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            case R.id.new_contact:
                Contact contact = new Contact();
                ContactManager.get(getActivity()).addContact(contact);

                updateUI();
                mCallbacks.onContactSelected(contact);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateUI() {
        ContactManager contactManager = ContactManager.get(getActivity());
        List<Contact> contacts = contactManager.getContacts();

        if(mAdapter == null) {
            mAdapter = new ContactAdapter(contacts);
            mContactRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setContacts(contacts);
            mAdapter.notifyDataSetChanged();
        }
    }

    public class ContactHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Contact mContact;
        private TextView mNameTextView;

        public ContactHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_contact, parent, false));
            itemView.setOnClickListener(this);
            mNameTextView = (TextView) itemView.findViewById(R.id.contact_name);

        }
        public void bind(Contact contact) {
            mContact = contact;
            mNameTextView.setText(mContact.getName());
           // mNameEditText.setText(mContact.getName());
        }

        @Override
        public void onClick(View view) { mCallbacks.onContactSelected(mContact); }

    }

    private class ContactAdapter extends RecyclerView.Adapter<ContactHolder> {
        private List<Contact> mContacts;

        public ContactAdapter(List<Contact> contacts) {
            mContacts = contacts;
        }

        @Override
        public ContactHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ContactHolder(layoutInflater, parent);
        }
        @Override
        public void onBindViewHolder(ContactHolder holder, int position) {
            Contact contact = mContacts.get(position);
            holder.bind(contact);
        }
        @Override
        public int getItemCount() {
            return mContacts.size();
        }

        public void setContacts(List<Contact> contacts) {
            mContacts = contacts;
        }


    }

}
