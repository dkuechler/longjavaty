package io.github.dkuechler.jbench.services;
import java.util.List;

import org.springframework.stereotype.Service;

import io.github.dkuechler.jbench.model.Product;

@Service
public class ProductService {
    //TODO replace a database
    private List<Product> products = new java.util.ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public List<Product> getAllProducts() {
        return List.copyOf(products);
    }
}