package org.songs.intunestopsongsdemo.model.helper;


public class Constants {

    public static final class HTTP {
        public static final String BASE_URL = "https://rss.itunes.apple.com/api/v1/us/";
    }

    public static final class DATABASE {

        public static final String DB_NAME = "isongs";
        public static final int DB_VERSION = 1;
        public static final String TABLE_NAME = "isong";

        public static final String DROP_QUERY = "DROP TABLE IF EXIST " + TABLE_NAME;

        public static final String GET_ITUNES_QUERY = "SELECT * FROM " + TABLE_NAME;

        public static final String NAME = "name";
        public static final String PHOTO_URL = "photo_url";
        public static final String PHOTO = "photo";



        public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "" +
                "(" +
                NAME + " TEXT not null," +
                PHOTO_URL + " TEXT not null," +
                PHOTO + " blob not null)" ;
    }

    public static final class REFERENCE {
        public static final String ISONG = Config.PACKAGE_NAME + "isong";
    }

    public static final class Config {
        public static final String PACKAGE_NAME = "org.songs.intunestopsongs.";
    }


}
