package in.instacard.instacard.models.storeage;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by mohakgoyal on 2/4/16.
 */

@SharedPref(value= SharedPref.Scope.UNIQUE)
public interface UserData {

    String first_name();
    String last_name();
    String full_name();
    int id();
    String qr();
    String phone();
    String email();
    String auth_token();
    String fb_auth();
    int points();
    int sign_in_count();

}
