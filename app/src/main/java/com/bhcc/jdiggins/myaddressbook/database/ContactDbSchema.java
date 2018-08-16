package com.bhcc.jdiggins.myaddressbook.database;

/**
 * Created by JCDig on 3/31/2018.
 */

public class ContactDbSchema {
    public static final class ContactTable {
        public static final String NAME = "addressbook";

        public static final class Cols {
            public static final String ID = "id";
            public static final String NAME = "name";
            public static final String PHONE = "phone";
            public static final String EMAIL = "email";
            public static final String STREET = "street";
            public static final String CITY = "city";
            public static final String STATE = "state";
            public static final String ZIP = "zip";
        }
    }
}
