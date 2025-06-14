package fr.teampeps.models;

public interface Translation {
    String getLang();
    void setLang(String lang);
    void setParent(TranslatableEntity<?> parent);
}
