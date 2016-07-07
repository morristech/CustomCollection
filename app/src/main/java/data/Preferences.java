package data;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    //TODO savepath preference
    //TODO image size preference
    private static final String COLLECTION_PREFERENCES = "Collection_Preferences";

    public enum COMMONPREFERENCES {
        IMAGESIZE, PREFERREDEXPORTFORMAT;
    }

    public static SharedPreferences getInstance(Context context) {
        return context.getSharedPreferences(COLLECTION_PREFERENCES, Context.MODE_PRIVATE);
    }
}
