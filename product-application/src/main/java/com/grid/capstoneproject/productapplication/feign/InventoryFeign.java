package com.grid.capstoneproject.productapplication.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "inventory-service")
public interface InventoryFeign {
    @GetMapping("/api/inventory/{uniq_id}")
    boolean getAvailabilityById(@PathVariable("uniq_id") String uniqId);

    @PostMapping("/api/inventory/availability")
    Map<String, Boolean> getAvailabilityByIds(@RequestBody List<String> uniqIds);
}
