package pl.allegro.tech.eden.performancefaultyapp;

public interface EventLog {

    StoreEventResult store(Event event);

    Event get(long offset);
}
