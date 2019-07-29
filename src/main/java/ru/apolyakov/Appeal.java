package ru.apolyakov;

import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Модель обращения
 */
public class Appeal {
    private int cypher;
    private String email;
    private String subject;
    private String region;
    private String author;
    private Date date;
    private String description;

    private final int startPage;

    private List<PDXObjectImage> images = new ArrayList<>();

    public Appeal(int startPage) {
        this.startPage = startPage;
    }

    public Appeal(int cypher, String email, String subject, String region, String author, Date date, String description, int startPage) {
        this.startPage = startPage;
        this.cypher = cypher;
        this.email = email;
        this.subject = subject;
        this.region = region;
        this.author = author;
        this.date = date;
        this.description = description;
    }

    public int getCypher() {
        return cypher;
    }

    public void setCypher(int cypher) {
        this.cypher = cypher;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getStartPage() {
        return startPage;
    }

    public List<PDXObjectImage> getImages() {
        return images;
    }

    public void setImages(List<PDXObjectImage> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Appeal{" +
                "cypher=" + cypher +
                ", email='" + email + '\'' +
                ", subject='" + subject + '\'' +
                ", region='" + region + '\'' +
                ", author='" + author + '\'' +
                ", date=" + date +
                ", description='" + description + '\'' +
                '}';
    }
}
