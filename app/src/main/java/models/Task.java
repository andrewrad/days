package models;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Andrew on 10/26/2016.
 */

public class Task extends RealmObject{
    private String name;
    private RealmList<SubTask> subTasks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(RealmList<SubTask> subTasks) {
        this.subTasks = subTasks;
    }
}
