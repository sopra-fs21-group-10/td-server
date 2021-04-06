package ch.uzh.ifi.hase.soprafs21.rest.dto;

import java.util.List;

public class WeatherDTO {
    private String name;
    private List weather;//array with 1 entry => map  == [{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03d"}]

    public List getWeather() {
        return weather;
    }

    public void setWeather(List weather) {
        this.weather = weather;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
