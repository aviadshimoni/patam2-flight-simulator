package model;

import java.io.Serializable;
import java.util.List;

public class FlightSetting implements Serializable {

    private List<Attribute> attributes;
    private Integer playSpeed;
    private String ip;
    private Double port;

    public FlightSetting() {}

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Integer getPlaySpeed() {
        return playSpeed;
    }

    public void setPlaySpeed(Integer playSpeed) {
        this.playSpeed = playSpeed;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Double getPort() {
        return port;
    }

    public void setPort(Double port) {
        this.port = port;
    }
}
