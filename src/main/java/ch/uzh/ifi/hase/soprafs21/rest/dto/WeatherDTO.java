package ch.uzh.ifi.hase.soprafs21.rest.dto;

import java.util.ArrayList;

public class WeatherDTO {
    private String name;
    private ArrayList weather;//array with 1 entry => map  == [{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03d"}]

    public ArrayList getWeather() {
        return weather;
    }

    public void setWeather(ArrayList weather) {
        this.weather = weather;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
