package model;

import java.io.Serializable;

public class Attribute implements Serializable{
    public String name;
    public Integer associativeName;
    public Integer min, max;

    public Attribute() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAssociativeName(Integer associativeName) {
        this.associativeName = associativeName;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }
}