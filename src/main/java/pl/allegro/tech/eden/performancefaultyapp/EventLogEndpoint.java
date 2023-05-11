package pl.allegro.tech.eden.performancefaultyapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventLogEndpoint {

    @PostMapping
    public StoreEventResult store(@RequestBody Event event) {
        return new StoreEventResult(123);
    }

    @GetMapping("/{offset}")
    public Event get(@PathVariable("offset") long offset) {
        return new Event("" + offset);
    }
}
