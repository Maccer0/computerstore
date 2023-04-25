package com.javadbappsproject.computerstore.controller;

import com.javadbappsproject.computerstore.model.Category;
import com.javadbappsproject.computerstore.model.Product;
import com.javadbappsproject.computerstore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @GetMapping("/{id}")
    public String index(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        model.addAttribute("product", product);

        return "product";
    }

    @GetMapping("/search")
    public String search(String search, Model model,
                        @RequestParam(value = "page", required = false) Integer p) {
        int perPage = 6;
        int page = (p != null) ? p : 0;

        Pageable pageable = PageRequest.of(page, perPage);

        long count = 0;

        if (!search.isEmpty()) {
            Page<Product> products = productRepository.findAllByNameContaining(search, pageable);

            count = productRepository.countAllByNameContaining(search);
            model.addAttribute("products", products);
            model.addAttribute("categoryName", "Search results for: " + search);
        }
        else {
            return "redirect:/";
        }

        double pageCount = Math.ceil((double) count / (double) perPage);

        model.addAttribute("pageCount", (int) pageCount);
        model.addAttribute("perPage", perPage);
        model.addAttribute("count", count);
        model.addAttribute("page", page);

        return "products";
    }

}
