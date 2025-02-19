package com.grid.capstoneproject.productapplication.controller;

import com.grid.capstoneproject.productapplication.feign.CatalogFeign;
import com.grid.capstoneproject.productapplication.feign.InventoryFeign;
import com.grid.capstoneproject.productapplication.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/store")
public class ProductController {
    private final CatalogFeign catalogFeign;
    private final InventoryFeign inventoryFeign;

    @Autowired
    public ProductController(CatalogFeign catalogFeign, InventoryFeign inventoryFeign) {
        this.catalogFeign = catalogFeign;
        this.inventoryFeign = inventoryFeign;
    }

    @GetMapping("/{uniq_id}")
    public Product getAvailableProductById(@PathVariable("uniq_id") String uniqId) {
        Product product = catalogFeign.getProductById(uniqId);
        boolean isAvailable = inventoryFeign.getAvailabilityById(uniqId);
        return isAvailable ? product : null;
    }

    @GetMapping("/sku/{sku}")
    public List<Product> getAvailableProductsBySku(@PathVariable("sku") String sku) {
        List<Product> products = catalogFeign.getProductsBySku(sku);
        List<String> productIds = products.stream().map(Product::getUniqId).collect(Collectors.toList());

        Map<String, Boolean> availabilityMap = inventoryFeign.getAvailabilityByIds(productIds);

        return products.stream()
                .filter(product -> availabilityMap.getOrDefault(product.getUniqId(), false))
                .collect(Collectors.toList());
    }

}
