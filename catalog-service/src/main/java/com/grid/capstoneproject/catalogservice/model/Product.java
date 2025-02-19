package com.grid.capstoneproject.catalogservice.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Product {
    @CsvBindByName(column = "uniq_id", required = true)
    private String uniqId;

    @CsvBindByName(column = "sku", required = false)
    private String sku;

    @CsvBindByName(column = "name_title", required = false)
    private String nameTitle;

    @CsvBindByName(column = "description", required = false)
    private String description;

    @CsvBindByName(column = "list_price", required = false)
    private String listPrice;

    @CsvBindByName(column = "sale_price", required = false)
    private String salePrice;

    @CsvBindByName(column = "category", required = false)
    private String category;

    @CsvBindByName(column = "category_tree", required = false)
    private String categoryTree;

    @CsvBindByName(column = "average_product_rating", required = false)
    private String averageProductRating;

    @CsvBindByName(column = "product_url", required = false)
    private String productUrl;

    @CsvBindByName(column = "product_image_urls", required = false)
    private String productImageUrls;

    @CsvBindByName(column = "brand", required = false)
    private String brand;

    @CsvBindByName(column = "total_number_reviews", required = false)
    private String totalNumberReviews;

    @CsvBindByName(column = "Reviews", required = false)
    private String reviews;

}
