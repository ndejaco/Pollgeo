package ndejaco.pollgeo;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//************************
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ListView;

import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import android.widget.CompoundButton.OnCheckedChangeListener;
import java.util.List;

import ndejaco.pollgeo.Model.Group;

/**
 * Created by Matthew on 10/25/2015.
 * used in MakeGroupActivity for bringing up the friends list of user
 */
public class friendsViewAdapter extends ArrayAdapter<ParseUser> {


    // Private Context that will be used to do an intent
    private Context mContext;
    // Private List of users friends to hold our data
    private List<ParseUser> friends;

    // variable for the current group being created, where the friends will be added and removed from
    private Group currGroup;

    private static final String TAG = friendsViewAdapter.class.getSimpleName();


    // Sets instance variables and calls super class constructor
    public friendsViewAdapter(Context context, List<ParseUser> users, Group group) {
        super(context, R.layout.friends_list_item, users);
        this.mContext = context;
        this.friends = users;
        Log.d(TAG, "FRIENDS VIEW ADAPTER REACHED");
        for(ParseUser pu: friends) {
            Log.d(TAG, "Adding " + pu.get("name") + " In ADAPTER");
        }
        this.currGroup = group;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {

        ViewHolder viewHolder = null;

        // If the view passed is null it inflates the home list item view to create a new one
        if (v == null) {
            v = View.inflate(getContext(), R.layout.friends_list_item, null);
            // set up viewHolder
            viewHolder = new ViewHolder();
            viewHolder.friendName = (TextView) v.findViewById(R.id.friendName);
            viewHolder.fbPhoto = (ProfilePictureView) v.findViewById(R.id.fbPhoto);
            viewHolder.checkBox = (CheckBox) v.findViewById(R.id.checkBox);
            // set tag
            v.setTag(viewHolder);

        } else{
            viewHolder = (ViewHolder) v.getTag();
        }
        Log.d(TAG, "GROUP NAME: " + currGroup.getName());

        // ParseUser variable for current friend
        ParseUser friend;

        // set up elements from layout
        TextView friendName = (TextView) v.findViewById(R.id.friendName);
        ProfilePictureView fbPhoto = (ProfilePictureView) v.findViewById(R.id.fbPhoto);
        fbPhoto.setPresetSize(ProfilePictureView.SMALL);

        //set up check box
        final CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox);
        checkBox.setEnabled(true);
        checkBox.setClickable(true);


        try {
            friend = friends.get(position).fetchIfNeeded();
            if (friend != null) {
                Log.d(TAG, "ADAPTER, friend name: " + friend.getString("name"));
                if (friendName != null) {
                    Log.d(TAG, "ADAPTER, friend name not null");
                    friendName.setText((String) friend.getString("name"));
                }
                String profileId = friend.getString("facebookId");
                if (profileId != null && fbPhoto != null){
                    Log.d(TAG, "ADAPTER, profile Id and fbPhoto not null");
                    fbPhoto.setProfileId((String) friend.getString("facebookId"));
                }
                // set up the checkBox
                checkBox.setTag(position);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag(); // get where the friend is
                        ParseUser user = friends.get(position); // friend being added

                        // check if button has been checked or not
                        boolean checked = checkBox.isChecked();
                        Log.d(TAG, "checked: " + checked);
                        if (checked){
                            // add the member to the group
                            insertMember(user); // insert friend to group
                            // let user know friend has been added by changing color of checkbox
                            checkBox.setHighlightColor(Color.GREEN);

                            // let the user know of change by text
                            checkBox.setText( user.getString("name") + " added (uncheck to take out of group)");
                            Log.d(TAG, user.getString("name") + " added to Group");
                            Log.d(TAG, currGroup.getName() + " is the Group");
                        }
                        else {
                            //friend has been added to group, now remove friend from group

                            Log.d(TAG, user.getString("name") + " removed from Group " + currGroup.getName());
                            checkBox.setText("Add " + user.getString("name"));
                            checkBox.setHighlightColor(Color.BLACK);
                            // need to remove the user from the group in parse database
                            removeMember(user);

                        }


                    }
                });
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return v;
    }

    /*
    addMember adds a ParseUser to the group
     */
    public void insertMember(ParseUser user) {
        Log.d(TAG, user.getString("name") + " added to Group");
        Log.d(TAG, currGroup.getName() + " is the Group");
        currGroup.addMember(user);
        currGroup.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
            }
        });

    }

    /*
    removeMember removes a ParseUser from the group
     */
    public void removeMember(ParseUser user) {
        currGroup.removeMember(user);
        currGroup.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
            }
        });

    }

    /*
    ViewHolder class, helps maintain list items that are not visible are being functioned on by a different items
    position
     */
    private static class ViewHolder {
        public TextView friendName;
        public ProfilePictureView fbPhoto;
        public CheckBox checkBox;
    }

}