package ndejaco.pollgeo;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import ndejaco.pollgeo.Model.Poll;
import ndejaco.pollgeo.Model.PollActivity;

/**
 * Created by Nicholas on 9/25/2015.
 */
// Application initializes parse sdk usage and registers subclasses.Need to register subclass here everytime create a new
// Parse Object type that we want to store on Parse
public class PollgeoApplication extends Application {

    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);

        ParseObject.registerSubclass(Poll.class);
        ParseObject.registerSubclass(PollActivity.class);

        Parse.initialize(this, "efCVhlYGapbFMccQ3dEk8V02KewoO74TGZaaVoiQ", "ymQXFhW4m96V6RV3l8Ymrg3oDsab2ZLKYSv4MZPu");

    }
}
