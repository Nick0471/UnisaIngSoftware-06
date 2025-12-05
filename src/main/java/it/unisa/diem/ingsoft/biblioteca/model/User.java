package it.unisa.diem.ingsoft.biblioteca.model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String email;
    private String name;
    private String surname;

    public User() {}

    public User(String id, String email, String name, String surname) {
        this.email = email;
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
