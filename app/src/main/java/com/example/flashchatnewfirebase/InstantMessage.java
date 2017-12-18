package com.example.flashchatnewfirebase;

/**
 * Created by katrina on 10/17/2017.
 */

public class InstantMessage  {
    private String message;
    private String author;
    private transient String key;

    public InstantMessage(String message, String author) {
        this.message = message;
        this.author = author;

    }

    public InstantMessage() {
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public String getKey() {
        return key;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValues(InstantMessage newMessage) {
        this.message = newMessage.getMessage();
        this.author = newMessage.getAuthor();
    }
}
