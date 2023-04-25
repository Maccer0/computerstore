package com.javadbappsproject.computerstore.controller;

import com.javadbappsproject.computerstore.model.Category;
import com.javadbappsproject.computerstore.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoriesController {
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String index(Model model) {
        List<Category> categories = categoryRepository.findAllByOrderBySortingAsc();
        model.addAttribute("categories", categories);

        return "admin/categories/index";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("category") Category category) {
        return "admin/categories/add";
    }


    @PostMapping("/add")
    public String add(@Valid Category category, BindingResult bindingResult,
                      RedirectAttributes redirectAttributes, Model model) {
        if(bindingResult.hasErrors()) {
            return "admin/categories/add";
        }

        redirectAttributes.addFlashAttribute("message", "Category added");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        String slug = category.getName().toLowerCase().replace(" ", "-");

        Category tempCategory = categoryRepository.findByName(category.getName());

        if(tempCategory != null) {
            redirectAttributes.addFlashAttribute("message", "Category already exists");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");

        }
        else {
            category.setSlug(slug);
            categoryRepository.save(category);
        }

        return "redirect:/admin/categories/add";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Category tempCategory = categoryRepository.findById(id).orElse(null);
        model.addAttribute("category", tempCategory);

        return "admin/categories/edit";
    }

    @PostMapping("/edit")
    public String edit(@Valid Category category, BindingResult bindingResult,
                       Model model, RedirectAttributes redirectAttributes) {
        Category tempCategory = categoryRepository.findById(category.getId()).orElse(null);

        if(tempCategory == null) {
            redirectAttributes.addFlashAttribute("message", "Category does not exist");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/admin/categories";
        }

        if(bindingResult.hasErrors()){
            model.addAttribute("categoryName", tempCategory.getName());
            return "admin/categories/edit";
        }

        redirectAttributes.addFlashAttribute("message", "Category edited");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        String slug = category.getName().toLowerCase().replace(" ", "-");

        Category currentCategory = categoryRepository.findByName(category.getName());

        if(currentCategory != null) {
            redirectAttributes.addFlashAttribute("message", "Category already exists");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        else {
            category.setSlug(slug);
            categoryRepository.save(category);
        }

        return "redirect:/admin/categories/edit/" + category.getId();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("message", "Category deleted");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        return "redirect:/admin/categories";
    }

    @PostMapping("/reorder")
    public @ResponseBody String reorder(@RequestParam("id[]") Long[] id) {
        int count = 1;
        Category category;

        for(Long categoryId : id) {
            category = categoryRepository.findById(categoryId).orElse(null);
            if(category == null) {
                continue;
            }
            category.setSorting(count);
            categoryRepository.save(category);
            count++;
        }

        return "ok";
    }
}
