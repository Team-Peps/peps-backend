package fr.teampeps.model;

public enum Bucket {

    MEMBERS("members"),
    ROSTERS("rosters"),
    HEROES("heroes"),
    MAPS("maps");

    private final String bucketName;

    Bucket(String bucketName) {
        this.bucketName = bucketName;
    }
}
