package com.javadbappsproject.computerstore.controller;

import com.javadbappsproject.computerstore.model.Category;
import com.javadbappsproject.computerstore.model.Product;
import com.javadbappsproject.computerstore.repository.CategoryRepository;
import com.javadbappsproject.computerstore.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/admin/products")
public class AdminProductsController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String index(Model model, @RequestParam(value = "page", required = false) Integer p) {
        int perPage = 6;
        int page = (p != null) ? p : 0;

        Pageable pageable = PageRequest.of(page, perPage);

        Page<Product> products = productRepository.findAll(pageable);

        List<Category> categories = categoryRepository.findAll();

        HashMap<String, String> productCategories = new HashMap<>();
        for (Category category : categories) {
            productCategories.put(category.getId().toString(), category.getName());
        }

        model.addAttribute("productCategories", productCategories);
        model.addAttribute("products", products);

        long count = productRepository.count();

        double pageCount = Math.ceil((double) count / (double) perPage);

        model.addAttribute("pageCount", (int) pageCount);
        model.addAttribute("perPage", perPage);
        model.addAttribute("count", count);
        model.addAttribute("page", page);

        return "admin/products/index";
    }

    @GetMapping("/add")
    public String add(Product product, Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        return "admin/products/add";
    }

    @PostMapping("/add")
    public String add(@Valid Product product, MultipartFile file, BindingResult bindingResult,
                      RedirectAttributes redirectAttributes, Model model) throws IOException {
        List<Category> categories = categoryRepository.findAll();

        if(bindingResult.hasErrors()) {
            model.addAttribute("categories", categories);

            return "admin/products/add";
        }

        boolean fileOk = false;

        String fileName = file.getOriginalFilename();
        Path path = Path.of("src/main/resources/static/media/" + fileName);

        byte[] bytes = file.getBytes();

        if (fileName != null && (fileName.endsWith(".jpg") || fileName.endsWith(".png"))) {
            fileOk = true;
        }

        redirectAttributes.addFlashAttribute("message", "Product added");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        String slug = product.getName().toLowerCase().replace(" ", "-");

        Product tempProduct = productRepository.findBySlug(slug);

        if(!fileOk) {
            redirectAttributes.addFlashAttribute("message", "Image must be a jpg or png");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("product", product);
        }
        else if(tempProduct != null) {
            redirectAttributes.addFlashAttribute("message", "Product already exists");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("product", product);
        }
        else if( product.getPrice().doubleValue() <= 0 )
        {
            redirectAttributes.addFlashAttribute("message", "Price must be greater than 0");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("product", product);
        }
        else {
            product.setSlug(slug);
            product.setImage(fileName);
            productRepository.save(product);

            Files.write(path, bytes);
        }

        return "redirect:/admin/products/add";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        List<Category> categories = categoryRepository.findAll();
        Product product = productRepository.findById(id).orElse(null);

        model.addAttribute("product", product);
        model.addAttribute("categories", categories);

        return "admin/products/edit";
    }

    @PostMapping("/edit")
    public String edit(@Valid Product product, MultipartFile file, BindingResult bindingResult,
                       RedirectAttributes redirectAttributes, Model model) throws IOException {
        Product currentProduct = productRepository.findById(product.getId()).orElse(null);

        List<Category> categories = categoryRepository.findAll();

        if(currentProduct == null) {
            redirectAttributes.addFlashAttribute("message", "Product does not exist");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/admin/products";
        }

        if(bindingResult.hasErrors()) {
            model.addAttribute("productName", currentProduct.getName());
            model.addAttribute("categories", categories);
            return "admin/products/edit";
        }

        boolean fileOk = false;

        String fileName = file.getOriginalFilename();
        Path path = Path.of("src/main/resources/static/media/" + fileName);
        byte[] bytes = file.getBytes();

        if(!file.isEmpty()) {
            if (fileName != null && (fileName.endsWith(".jpg") || fileName.endsWith(".png"))) {
                fileOk = true;
            }
        }
        else {
            fileOk = true;
        }

        redirectAttributes.addFlashAttribute("message", "Product edited");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        String slug = product.getName().toLowerCase().replace(" ", "-");

        Product tempProduct = productRepository.findBySlugAndIdNot(slug, product.getId());

        if(!fileOk) {
            redirectAttributes.addFlashAttribute("message", "Image must be a jpg or png");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("product", product);
        }
        else if (tempProduct != null) {
            redirectAttributes.addFlashAttribute("message", "Product already exists");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("product", product);
        }
        else {
            product.setSlug(slug);

            if(!file.isEmpty()) {
                Path old_path = Path.of("src/main/resources/static/media/" + currentProduct.getImage());
                Files.delete(old_path);
                product.setImage(fileName);
                Files.write(path, bytes);
            }
            else {
                product.setImage(currentProduct.getImage());
            }
            productRepository.save(product);
        }

        return "redirect:/admin/products/edit/" + product.getId();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) throws IOException {
        Product product = productRepository.findById(id).orElse(null);

        if(product == null) {
            redirectAttributes.addFlashAttribute("message", "Product does not exist");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/admin/products";
        }

        Path path = Path.of("src/main/resources/static/media/" + product.getImage());

        Files.delete(path);

        productRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("message", "Product deleted");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        return "redirect:/admin/products";
    }
}
