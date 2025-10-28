package ru.mtuci.demo.service;

import ru.mtuci.demo.model.Product;

import java.util.List;

public interface ProductService {

    public Product getProductById(Long id);

    public Boolean updateProduct(Long id,
                                 String name,
                                 Boolean isBlocked);

    public Long createProduct(String name,
                              Boolean isBlocked);

    public List<Product> getAllProducts();

    void deleteProductById(Long id);

}
