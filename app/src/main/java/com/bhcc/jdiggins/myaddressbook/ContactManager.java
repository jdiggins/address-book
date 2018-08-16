package com.bhcc.jdiggins.myaddressbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bhcc.jdiggins.myaddressbook.database.ContactCursorWrapper;
import com.bhcc.jdiggins.myaddressbook.database.ContactDbHelper;
import com.bhcc.jdiggins.myaddressbook.database.ContactDbSchema.ContactTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by JCDig on 3/31/2018.
 * John Diggins
 */

public class ContactManager {
    private static ContactManager sContactManager;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static ContactManager get(Context context) {
        if(sContactManager == null) {
            sContactManager = new ContactManager(context);
        }
        return sContactManager;
    }

    private ContactManager(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ContactDbHelper(mContext).getWritableDatabase();
    }

    public void addContact(Contact c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(ContactTable.NAME, null, values);
    }

    private static ContentValues getContentValues(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(ContactTable.Cols.ID, contact.getId().toString());
        values.put(ContactTable.Cols.NAME, contact.getName());
        values.put(ContactTable.Cols.PHONE, contact.getPhone());
        values.put(ContactTable.Cols.EMAIL, contact.getEmail());
        values.put(ContactTable.Cols.STREET, contact.getStreet());
        values.put(ContactTable.Cols.CITY, contact.getCity());
        values.put(ContactTable.Cols.STATE, contact.getState());
        values.put(ContactTable.Cols.ZIP, contact.getZip());
        return values;
    }

    public List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        ContactCursorWrapper cursor = queryContacts(null, null);
        try {

            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                contacts.add(cursor.getContact());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        // Sort list in alphabetical order using first name
        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact a, Contact b) {
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
        return contacts;
    }

    public Contact getContact(UUID id) {
        ContactCursorWrapper cursor = queryContacts(
                ContactTable.Cols.ID + " = ?",
                new String[] { id.toString() }
        );
        try {
            if(cursor.getCount() == 0)
                return null;
            cursor.moveToFirst();
            return cursor.getContact();
        } finally {
            cursor.close();
        }
    }

    public void deleteContact(Contact contact) {
        String idString = contact.getId().toString();
        mDatabase.delete(ContactTable.NAME, ContactTable.Cols.ID + " = ?",
                new String[] { idString });
    }

    public void updateContact(Contact contact) {
        String idString = contact.getId().toString();
        ContentValues values = getContentValues(contact);
        mDatabase.update(ContactTable.NAME, values,
                ContactTable.Cols.ID + " = ?",
                new String[] { idString });
    }

    public void fixNoNameContact(Contact contact) {
        List<Contact> list = getContacts();
        int count = 0;
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i).getName().contains("New Contact"))
                count++;
        }
        contact.setName("New Contact" + (count > 0 ? " " + count : ""));
        updateContact(contact);
    }

    private ContactCursorWrapper queryContacts(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ContactTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new ContactCursorWrapper(cursor);
    }
}
