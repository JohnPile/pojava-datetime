package org.pojava.datetime.examples;

import java.util.ArrayList;
import java.util.List;

public class People {

    private List<Person> people = new ArrayList<Person>();
    private Person leader = null;

    public List<Person> getPeople() {
        return people;
    }

    public void addPerson(Person person) {
        people.add(person);
    }

    public Person getLeader() {
        return leader;
    }

    public void setLeader(Person leader) {
        this.leader = leader;
    }

}
