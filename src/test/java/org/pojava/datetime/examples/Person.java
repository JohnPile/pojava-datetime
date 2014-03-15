package org.pojava.datetime.examples;

import org.pojava.datetime.DateTime;

public class Person {

    private int id;
    private String name;
    private DateTime birth;

    public Person() {
    }

    public Person(int id, String name, DateTime birth) {
        this.id = id;
        this.name = name;
        this.birth = birth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getBirth() {
        return birth;
    }

    public void setBirth(DateTime birth) {
        this.birth = birth;
    }

}
