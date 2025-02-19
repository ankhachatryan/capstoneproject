package com.grid.capstoneproject.productapplication.controller;

import com.grid.capstoneproject.productapplication.feign.CatalogFeign;
import com.grid.capstoneproject.productapplication.feign.InventoryFeign;
import com.grid.capstoneproject.productapplication.model.Product;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/store")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final CatalogFeign catalogFeign;
    private final InventoryFeign inventoryFeign;

    @Autowired
    public ProductController(CatalogFeign catalogFeign, InventoryFeign inventoryFeign) {
        this.catalogFeign = catalogFeign;
        this.inventoryFeign = inventoryFeign;
    }

    @GetMapping("/{uniq_id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackGetProductById")
    public Product getAvailableProductById(@PathVariable("uniq_id") String uniqId) {
        logger.info("Fetching product with ID: {}", uniqId);

        Product product = catalogFeign.getProductById(uniqId);
        if (product == null) {
            logger.warn("Product not found in catalog for ID: {}", uniqId);
            throw new RuntimeException("Product not found");
        }

        boolean isAvailable = inventoryFeign.getAvailabilityById(uniqId);
        if (!isAvailable) {
            logger.warn("Product {} is out of stock.", uniqId);
            throw new RuntimeException("Product is out of stock");
        }

        logger.info("Product {} is available.", uniqId);
        return product;
    }

    @GetMapping("/sku/{sku}")
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackGetProductsBySku")
    public List<Product> getAvailableProductsBySku(@PathVariable("sku") String sku) {
        logger.info("Fetching products with SKU: {}", sku);

        List<Product> products = catalogFeign.getProductsBySku(sku);
        if (products == null || products.isEmpty()) {
            logger.warn("No products found for SKU: {}", sku);
            throw new RuntimeException("No products found for SKU");
        }

        List<String> productIds = products.stream().map(Product::getUniqId).collect(Collectors.toList());
        Map<String, Boolean> availabilityMap = inventoryFeign.getAvailabilityByIds(productIds);

        List<Product> availableProducts = products.stream()
                .filter(product -> availabilityMap.getOrDefault(product.getUniqId(), false))
                .collect(Collectors.toList());

        if (availableProducts.isEmpty()) {
            logger.warn("No available products for SKU: {}", sku);
            throw new RuntimeException("No available products for SKU");
        }

        return availableProducts;
    }

    public Product fallbackGetProductById(String uniqId, Throwable throwable) {
        logger.error("Circuit Breaker triggered for getAvailableProductById. Reason: {}", throwable.getMessage());
        throw new CustomServiceUnavailableException("Service is temporarily unavailable. Please try again later.");
    }

    public List<Product> fallbackGetProductsBySku(String sku, Throwable throwable) {
        logger.error("Circuit Breaker triggered for getAvailableProductsBySku. Reason: {}", throwable.getMessage());
        throw new CustomServiceUnavailableException("Service is temporarily unavailable. Please try again later.");
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public static class CustomServiceUnavailableException extends RuntimeException {
        public CustomServiceUnavailableException(String message) {
            super(message);
        }
    }
}