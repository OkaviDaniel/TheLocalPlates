package com.example.thelocalplates8.Models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

public class MessageModel {
    private String cid;     // ChatID
    private String from; // The one who sent the message
    @ServerTimestamp
    private Timestamp ts;
    private String text;

    // Default constructor is required for Firestore
    public MessageModel() {}

    // Parameterized constructor
    public MessageModel(String cid, String from, Timestamp ts, String text) {
        this.cid = cid;
        this.from = from;
        this.ts = ts;
        this.text = text;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Timestamp getTs() {
        return ts;
    }

    public void setTs(Timestamp ts) {
        this.ts = ts;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Message{" +
                ", cid='" + cid + '\'' +
                ", from='" + from + '\'' +
                ", ts=" + ts +
                ", text='" + text + '\'' +
                '}';
    }
}
