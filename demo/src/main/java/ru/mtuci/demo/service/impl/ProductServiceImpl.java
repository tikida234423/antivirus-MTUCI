package ru.mtuci.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.Product;
import ru.mtuci.demo.repository.ProductRepository;
import ru.mtuci.demo.service.ProductService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Boolean updateProduct(Long id,
                                String name,
                                Boolean isBlocked) {

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return false;
        }

        product.setName(name);
        product.setBlocked(isBlocked);
        productRepository.save(product);

        return true;

    }

    public Long createProduct(String name,
                              Boolean isBlocked) {

        Product product = new Product();
        product.setName(name);
        product.setBlocked(isBlocked);
        productRepository.save(product);

        return productRepository.findTopByOrderByIdDesc().get().getId();

    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

}
