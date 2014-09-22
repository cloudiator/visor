package de.uniulm.omi.monitoring.metric;

/**
 * Created by daniel on 21.09.14.
 */
public class Tag {

    private String name;

    private String value;

    public Tag(String tag, String value) {
        this.name = tag;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
