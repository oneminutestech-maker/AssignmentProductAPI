package com.example.product.service;

import com.example.product.dto.ProductRequestDto;
import com.example.product.model.Product;

import java.util.List;

public interface ProductService {

    List<Product> getProductList();
    Product getProductById(Long id);
    Product createProduct(ProductRequestDto productRequestDTO);
    Product updateProduct(Long id, ProductRequestDto productRequestDTO);
    boolean deleteProduct(Long id);
    boolean existsByName(String name);

}
