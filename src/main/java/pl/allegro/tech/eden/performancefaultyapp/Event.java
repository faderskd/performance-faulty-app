package pl.allegro.tech.eden.performancefaultyapp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Event(String content) {

    @JsonCreator
    public Event(@JsonProperty("content") String content) {
        this.content = content;
    }
}
