package com.bhcc.jdiggins.myaddressbook.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bhcc.jdiggins.myaddressbook.Contact;
import com.bhcc.jdiggins.myaddressbook.database.ContactDbSchema.ContactTable;

import java.util.UUID;


/**
 * Created by JCDig on 3/31/2018.
 */

public class ContactCursorWrapper extends CursorWrapper {
    public ContactCursorWrapper(Cursor cursor) {super(cursor);}

    public Contact getContact() {
        String id = getString(getColumnIndex(ContactTable.Cols.ID));
        String name = getString(getColumnIndex(ContactTable.Cols.NAME));
        String phone = getString(getColumnIndex(ContactTable.Cols.PHONE));
        String email = getString(getColumnIndex(ContactTable.Cols.EMAIL));
        String street = getString(getColumnIndex(ContactTable.Cols.STREET));
        String city = getString(getColumnIndex(ContactTable.Cols.CITY));
        String state = getString(getColumnIndex(ContactTable.Cols.STATE));
        String zip = getString(getColumnIndex(ContactTable.Cols.ZIP));

        Contact contact = new Contact(UUID.fromString(id));
        contact.setName(name);
        contact.setPhone(phone);
        contact.setEmail(email);
        contact.setStreet(street);
        contact.setCity(city);
        contact.setState(state);
        contact.setZip(zip);
        return contact;

    }
}
