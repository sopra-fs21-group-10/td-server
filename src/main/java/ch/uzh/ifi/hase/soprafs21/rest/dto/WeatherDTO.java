package ch.uzh.ifi.hase.soprafs21.rest.dto;

import java.util.HashMap;
import java.util.List;

public class WeatherDTO {
    private String name;
    private List<HashMap<String, String>> weather;//array with 1 entry => map  == [{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03d"}]

    public List<HashMap<String, String>> getWeather() {
        return weather;
    }

    public void setWeather(List<HashMap<String, String>> weather) {
        this.weather = weather;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
