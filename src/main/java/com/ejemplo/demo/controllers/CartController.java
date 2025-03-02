package com.ejemplo.demo.controllers;

import com.ejemplo.demo.models.Product;
import com.ejemplo.demo.repositories.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final ProductRepository productRepository;

    public CartController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @GetMapping
    public String index(HttpSession session, Model model) {
        // Asegurar que la sesión es nueva al iniciar el servidor
        if (session.isNew()) {
            session.removeAttribute("cart_product_data");
        }
    
        List<Product> products = productRepository.findAll();
        session.setAttribute("all_products", products);
    
        Map<Long, Long> cartProductData = (Map<Long, Long>) session.getAttribute("cart_product_data");
        Map<Long, Product> cartProducts = new HashMap<>();
    
        if (cartProductData != null) {
            for (Long id : cartProductData.keySet()) {
                productRepository.findById(id).ifPresent(value -> cartProducts.put(id, value));
            }
        }
    
        model.addAttribute("title", "Shopping Cart");
        model.addAttribute("subtitle", "Carrito de compras");
        model.addAttribute("products", products);
        model.addAttribute("cartProducts", cartProducts);
    
        return "cart/index";
    }
    

    @GetMapping("/add/{id}")
    public String add(@PathVariable Long id, HttpSession session) {
        Map<Long, Long> cartProductData = (Map<Long, Long>) session.getAttribute("cart_product_data");
        if (cartProductData == null) {
            cartProductData = new HashMap<>();
        }
    
        cartProductData.put(id, id);
        session.setAttribute("cart_product_data", cartProductData);
    
        return "redirect:/cart";
    }
    

    @GetMapping("/removeAll")
    public String removeAll(HttpSession session) {
        // Eliminar el carrito de la sesión
        session.removeAttribute("cart_product_data");
        return "redirect:/cart";
    }
}
