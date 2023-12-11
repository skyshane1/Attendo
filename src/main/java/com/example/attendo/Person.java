package com.example.attendo;

public class Person {
    private String name;
    private String banner_id;
    private int swipes;

    public Person() {
        this.name = "";
        this.banner_id = "";
        this.swipes = 0;
    }

    public Person(String name, String banner_id, int swipes) {
        this.name = name;
        this.banner_id = banner_id;
        this.swipes = swipes;
    }

    public int getSwipes() {
        return swipes;
    }

    public void setSwipes(int swipes) {
        this.swipes = swipes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBanner_id() {
        return banner_id;
    }

    public void setBanner_id(String banner_id) {
        this.banner_id = banner_id;
    }
}
