package models;


import io.realm.RealmObject;

/**
 * Created by Andrew on 11/7/2016.
 */
public class EpochSSTimes extends RealmObject {
    private long startTime;
    private long endTime;

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
