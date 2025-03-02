package com.ejemplo.demo.controllers;

import com.ejemplo.demo.forms.ProductForm;
import com.ejemplo.demo.models.Comment;
import com.ejemplo.demo.models.Product;
import com.ejemplo.demo.repositories.CommentRepository;
import com.ejemplo.demo.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

     @Autowired
    private CommentRepository commentRepository;

    private static final String DEFAULT_IMAGE_URL = "https://www.fedex.com/content/dam/fedex/us-united-states/shipping/images/2022/FedEx-boxes.jpg";


    @GetMapping
    public String index(Model model) {
        List<Product> dbProducts = productRepository.findAll();
    
        model.addAttribute("title", "Products - Online Store");
        model.addAttribute("subtitle", "List of products");
        model.addAttribute("products", dbProducts);
    
        return "product/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("title", "Create Product");
        model.addAttribute("productForm", new ProductForm());
        return "product/create";
    }

    @PostMapping("/save")
public String saveProduct(@ModelAttribute ProductForm productForm, RedirectAttributes redirectAttributes) {
    if (productForm.getPrice() == null || productForm.getPrice() <= 0) {
        redirectAttributes.addFlashAttribute("errorMessage", "El precio debe ser mayor a cero.");
        return "redirect:/products/create";
    }

    Product newProduct = new Product(
            productForm.getName(),
            productForm.getPrice(),
            DEFAULT_IMAGE_URL,
            ""  
    );

    productRepository.save(newProduct);

    redirectAttributes.addFlashAttribute("successMessage", "¡Producto creado exitosamente!");
    return "redirect:/products";
}

@GetMapping("/{id}")
public String show(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
    Product product = productRepository.findById(id).orElse(null);

    if (product != null) {
        model.addAttribute("title", product.getName() + " - Online Store");
        model.addAttribute("subtitle", product.getName() + " - Product Information");
        model.addAttribute("product", product);
        model.addAttribute("comments", product.getComments());
        return "product/show";
    }

    redirectAttributes.addFlashAttribute("errorMessage", "Producto no encontrado.");
    return "redirect:/products";
}

@PostMapping("/{id}/comments")
public String addComment(@PathVariable Long id, @RequestParam("description") String description, RedirectAttributes redirectAttributes) {
    Product product = productRepository.findById(id).orElse(null);

    if (product == null) {
        redirectAttributes.addFlashAttribute("errorMessage", "Producto no encontrado.");
        return "redirect:/products";
    }

    if (description != null && !description.trim().isEmpty()) {
        Comment newComment = new Comment(description, product);
        commentRepository.save(newComment);
        redirectAttributes.addFlashAttribute("successMessage", "¡Comentario agregado exitosamente!");
    } else {
        redirectAttributes.addFlashAttribute("errorMessage", "El comentario no puede estar vacío.");
    }

    return "redirect:/products/" + id;
}
}