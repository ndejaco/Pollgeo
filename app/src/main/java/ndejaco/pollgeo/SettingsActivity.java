package ndejaco.pollgeo;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class SettingsActivity extends AppCompatActivity {

    protected ParseUser currentUser;
    private ListView mDrawerList;
    private String[] mSections;
    private DrawerLayout mDrawerLayout;
    protected SeekBar searchRadiusBar;
    protected TextView searchRadius;
    protected TextView uName;
    protected Button submitButton;
    protected String currName;
    protected int currRadius;
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        currentUser = ParseUser.getCurrentUser();

        mContext = this;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ProfilePictureView fbThumb = (ProfilePictureView) findViewById(R.id.thumbnail);

        if (currentUser != null) {
            fbThumb.setPresetSize(ProfilePictureView.LARGE);
            String profileId = ParseUser.getCurrentUser().getString("facebookId");
            if (profileId != null) {
                fbThumb.setProfileId(profileId);
            } else {

            }
        }

        // set up the drawer's list view with items and click listener
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mSections = getResources().getStringArray(R.array.sections_array);
        mDrawerList.setAdapter(new DrawerAdapter(this, mSections));

        searchRadiusBar = (SeekBar)findViewById(R.id.discoveryRadius);
        searchRadius = (TextView)findViewById(R.id.distance);
        uName = (TextView)findViewById(R.id.changeName);
        currentUser = ParseUser.getCurrentUser();
        try {
            currentUser.fetchIfNeeded();
        }catch(Exception e){
            return;
        }


        currName = (String)currentUser.get("name");
        uName.setText(currName);
        if(currentUser.get("searchRadius") == null){
            currRadius = 10;
            currentUser.put("searchRadius",10);
            currentUser.saveEventually();
        }
        else{
            currRadius = (int)currentUser.get("searchRadius");
        }

        searchRadius.setText(Integer.toString(currRadius));

        searchRadiusBar.setProgress(currRadius);
        searchRadiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                searchRadius.setText(Integer.toString(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        submitButton = (Button)findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             submitButton.setOnClickListener(null);
             boolean changed = false;
             if(!uName.getText().equals("") && !uName.getText().equals(currentUser.get("name"))) {
                 currentUser.put("name", uName.getText().toString());
                 changed = true;
             }
             try{
                 int r = Integer.parseInt(searchRadius.getText().toString());
                 if(r != currRadius){
                     if(r == 0)
                         r = 1;
                     changed = true;
                     currentUser.put("searchRadius",r);
                 }
             }catch(Exception e){

             }
             if(changed) {
                 currentUser.saveInBackground(new SaveCallback() {
                     @Override
                     public void done(ParseException e) {
                         Intent intent0 = new Intent(mContext, LocalHomeListActivity.class);
                         intent0.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                         intent0.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         mContext.startActivity(intent0);
                     }
                 });
             }else{
                 Intent intent0 = new Intent(mContext, LocalHomeListActivity.class);
                 intent0.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 intent0.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                 mContext.startActivity(intent0);
             }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}