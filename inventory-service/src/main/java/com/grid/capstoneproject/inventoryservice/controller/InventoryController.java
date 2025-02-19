package com.grid.capstoneproject.inventoryservice.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("api/inventory")
public class InventoryController {
    private final List<Boolean> availabilityList = List.of(true, false);
    private final Map<String, Boolean> inventory = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("data.csv").getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(";");
                if (fields.length == 14) {
                    inventory.put(fields[0], availabilityList.get(ThreadLocalRandom.current().nextInt(availabilityList.size())));
                }
            }
            System.out.println("ADDED INVENTORY, COUNT: " + inventory.size());
        } catch (Exception e) {
            throw new RuntimeException("Error loading dataset", e);
        }
    }

    @GetMapping("/{uniq_id}")
    public Boolean getAvailability(@PathVariable String uniq_id) {
        return inventory.getOrDefault(uniq_id, false);
    }

    @PostMapping("/availability")
    public Map<String, Boolean> getAvailabilities(@RequestBody List<String> uniq_ids) {
        Map<String, Boolean> availabilityMap = new ConcurrentHashMap<>();
        for (String uniq_id : uniq_ids) {
            availabilityMap.put(uniq_id, inventory.getOrDefault(uniq_id, false));
        }
        return availabilityMap;
    }

}
