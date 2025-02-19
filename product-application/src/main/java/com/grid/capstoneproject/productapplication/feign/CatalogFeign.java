package com.grid.capstoneproject.productapplication.feign;

import com.grid.capstoneproject.productapplication.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "catalog-service")
public interface CatalogFeign {
    @GetMapping("/api/products/{uniq_id}")
    Product getProductById(@PathVariable("uniq_id") String uniqId);

    @GetMapping("/api/products/sku/{sku}")
    List<Product> getProductsBySku(@PathVariable("sku") String sku);
}
