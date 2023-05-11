package pl.allegro.tech.eden.performancefaultyapp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record StoreEventResult(long offset) {

    @JsonCreator
    public StoreEventResult(@JsonProperty("offset") long offset) {
        this.offset = offset;
    }
}
