package asu.reach;

/**
 * Created by mandar on 13-10-2015.
 */
public class AdminFeaturesMetadata {
    private String activityName;
    private String activityStatus;

    public AdminFeaturesMetadata(String activityName,String activityStatus){
        this.activityName=activityName;
        this.activityStatus=activityStatus;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
    }
}
