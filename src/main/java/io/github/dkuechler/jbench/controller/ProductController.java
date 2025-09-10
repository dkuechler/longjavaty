package io.github.dkuechler.jbench.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.dkuechler.jbench.model.Product;
import io.github.dkuechler.jbench.services.ProductService;

@Controller
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        var products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "products";
    }

    @PostMapping("/products")
    public String addProduct(
            @RequestParam(defaultValue = "Default Name") String name,
            @RequestParam(defaultValue = "0.0") double price,
            @RequestParam(defaultValue = "0") int quantity,
            @RequestParam(defaultValue = "Default Category") String category,
            Model model) {
        Product p = new Product(name, price, quantity, category);
        productService.addProduct(p);
        var products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "products";
    }
}

