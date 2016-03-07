package in.instacard.instacard.models.callback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohakgoyal on 2/13/16.
 */
public class UserContainer {

    @SerializedName("user")
    @Expose
    private User user;


    @SerializedName("errors")
    @Expose
    private List<String> errors = new ArrayList<String>();

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public List<String> getErrors() {
        return errors;
    }

}
