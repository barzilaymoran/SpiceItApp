package com.moran.spiceitapp.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
public class User implements Serializable {
    @PrimaryKey
    @NonNull
    public String email;
    public String name;
    public String location;
    public String about;
    public String imageUrl;
    public long lastUpdated;

    public User() {}

    public User(String email, String name, String location, String about, String imageUrl) {
        this.email = email;
        this.name = name;
        this.location = location;
        this.about = about;
        this.imageUrl = imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setAbout(String about) {
        this.about = about;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getEmail() {
        return this.email;
    }
    public String getName() {
        return this.name;
    }
    public String getLocation() {
        return this.location;
    }
    public String getAbout() {
        return this.about;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public long getLastUpdated() { return lastUpdated; }


    public Map<String, Object> toJson() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("name", name);
        result.put("location", location);
        result.put("about", about);
        result.put("imageUrl", imageUrl);
        result.put("lastUpdated", FieldValue.serverTimestamp());
        return result;
    }

    static User factory(Map<String, Object> json){
        User user = new User();
        user.email = (String) Objects.requireNonNull(json.get("email"));
        user.name = (String)json.get("name");
        user.location = (String)json.get("location");
        user.about = (String)json.get("about");
        user.imageUrl = (String)json.get("imageUrl");
        Timestamp ts = (Timestamp)json.get("lastUpdated");
        assert ts != null;
        user.lastUpdated = ts.getSeconds();
        return user;
    }
}
