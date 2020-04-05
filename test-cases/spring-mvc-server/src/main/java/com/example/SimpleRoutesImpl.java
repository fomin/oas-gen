package com.example;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

@Controller
public class SimpleRoutesImpl implements SimpleRoutes {

    @Override
    public ResponseEntity<String> create(Item item) {
        return ResponseEntity.ok("id1");
    }

    @Override
    public ResponseEntity<Item> get(String id) {
        Item item = new Item(
                "common property 1",
                "property 1",
                new ItemProperty2("common property 1", "property 21", ItemProperty2Property22.VALUE1),
                BigDecimal.TEN,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Arrays.asList("item1", "item2"),
                Collections.singletonMap("k1", BigDecimal.ONE)
        );
        return ResponseEntity.ok(item);
    }

    @Override
    public ResponseEntity<Item> find(String param1, String param2) {
        Item item = new Item(
                "common property 1",
                "property 1",
                new ItemProperty2("common property 1", "property 21", ItemProperty2Property22.VALUE1),
                BigDecimal.TEN,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0),
                Arrays.asList("item1", "item2"),
                Collections.singletonMap("k1", BigDecimal.ONE)
        );
        return ResponseEntity.ok(item);
    }
}
