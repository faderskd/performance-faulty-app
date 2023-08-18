package pl.allegro.tech.eden.performancefaultyapp;

public interface EventLog {

    StoreEventResult store(Event event, boolean force);

    Event get(long offset);
}
