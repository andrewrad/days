package models;

import io.realm.RealmObject;

/**
 * Created by Andrew on 10/26/2016.
 */

public class SubTask extends RealmObject{
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
