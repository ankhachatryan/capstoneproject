package com.grid.capstoneproject.catalogservice.controller;

import jakarta.annotation.PostConstruct;
import com.grid.capstoneproject.catalogservice.model.Product;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
class ProductController {
    private final Map<String, Product> productMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("data.csv").getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(";");
                if (fields.length == 14) {
                    Product product = getProduct(fields);
                    productMap.put(fields[0], product);
                }
            }
            System.out.println("ADDED Products, COUNT: " + productMap.size());

        } catch (Exception e) {
            throw new RuntimeException("Error loading dataset", e);

        }
    }

    @GetMapping("/{uniq_id}")
    public Product getProductById(@PathVariable("uniq_id") String uniqId) {
        return productMap.get(uniqId);
    }

    @GetMapping("/sku/{sku}")
    public List<Product> getProductsBySku(@PathVariable("sku") String sku) {
        return productMap.values().stream()
                .filter(product -> sku.equalsIgnoreCase(product.getSku()))
                .collect(Collectors.toList());
    }

    private static Product getProduct(String[] fields) {
        Product product = new Product();
        product.setUniqId(fields[0].trim());
        product.setSku(fields[1].trim());
        product.setNameTitle(fields[2].trim());
        product.setDescription(fields[3].trim());
        product.setListPrice(fields[4].trim());
        product.setSalePrice(fields[5].trim());
        product.setCategory(fields[6].trim());
        product.setCategoryTree(fields[7].trim());
        product.setAverageProductRating(fields[8].trim());
        product.setProductUrl(fields[9].trim());
        product.setProductImageUrls(fields[10].trim());
        product.setBrand(fields[11].trim());
        product.setTotalNumberReviews(fields[12].trim());
        product.setReviews(fields[13].trim());
        return product;
    }
}