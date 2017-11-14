package tcn.com.models;

import java.io.Serializable;

/**
 * Created by MyPC on 05/09/2017.
 */

public class NoteModels implements Serializable {
    private int id;
    private String noteSource;
    private String langguageSource;
    private String noteMeaning;
    private String langguageMeaning;
    private byte learned;

    public NoteModels(int id, String noteSource, String langguageSource, String noteMeaning, String langguageMeaning, byte learned) {
        this.id = id;
        this.noteSource = noteSource;
        this.langguageSource = langguageSource;
        this.noteMeaning = noteMeaning;
        this.langguageMeaning = langguageMeaning;
        this.learned = learned;
    }

    public byte getLearned() {
        return learned;
    }

    public void setLearned(byte learned) {
        this.learned = learned;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNoteSource() {
        return noteSource;
    }

    public void setNoteSource(String noteSource) {
        this.noteSource = noteSource;
    }

    public String getLangguageSource() {
        return langguageSource;
    }

    public void setLangguageSource(String langguageSource) {
        this.langguageSource = langguageSource;
    }

    public String getNoteMeaning() {
        return noteMeaning;
    }

    public void setNoteMeaning(String noteMeaning) {
        this.noteMeaning = noteMeaning;
    }

    public String getLangguageMeaning() {
        return langguageMeaning;
    }

    public void setLangguageMeaning(String langguageMeaning) {
        this.langguageMeaning = langguageMeaning;
    }
}
