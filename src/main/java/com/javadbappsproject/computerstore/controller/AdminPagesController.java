package com.javadbappsproject.computerstore.controller;

import com.javadbappsproject.computerstore.model.Page;
import com.javadbappsproject.computerstore.repository.PageRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin/pages")
public class AdminPagesController {
    @Autowired
    private PageRepository pageRepository;

    @GetMapping
    public String index(Model model){
        List<Page> pages = pageRepository.findAllByOrderBySortingAsc();
        model.addAttribute("pages", pages);

        return "admin/pages/index";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("page") Page page) {
        return "admin/pages/add";
    }

    @PostMapping("/add")
    public String add(@Valid Page page, BindingResult bindingresult,
                      RedirectAttributes redirectAttributes, Model model) {
        if(bindingresult.hasErrors()) {
            return "admin/pages/add";
        }

        redirectAttributes.addFlashAttribute("message", "Page added");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        String slug = Objects.equals(page.getSlug(), "") ?
                page.getTitle().toLowerCase().replace(" ", "-") :
                page.getSlug().toLowerCase().replace(" ", "-");

        Page slugExists = pageRepository.findBySlug(slug);

        if(slugExists != null) {
            redirectAttributes.addFlashAttribute("message", "Slug already exists, choose another");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        else {
            page.setSlug(slug);
            page.setSorting(100);
            pageRepository.save(page);
        }

        return "redirect:/admin/pages/add";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Page page = pageRepository.findById(id).orElse(null);
        model.addAttribute("page", page);

        return "admin/pages/edit";
    }

    @PostMapping("/edit")
    public String edit(@Valid Page page, BindingResult bindingResult,
                       RedirectAttributes redirectAttributes, Model model) {
        Page pageCurrent = pageRepository.findById(page.getId()).orElse(null);
        if (pageCurrent == null) {
            redirectAttributes.addFlashAttribute("message", "Page does not exist");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/admin/pages";
        }

        if(bindingResult.hasErrors()){
            model.addAttribute("pageTitle", pageCurrent.getTitle());
            return "admin/pages/edit";
        }

        redirectAttributes.addFlashAttribute("message", "Page edited");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        String slug = Objects.equals(page.getSlug(), "") ?
                page.getTitle().toLowerCase().replace(" ", "-") :
                page.getSlug().toLowerCase().replace(" ", "-");

        Page slugExists = pageRepository.findBySlugAndIdNot(slug, page.getId());

        if(slugExists != null) {
            redirectAttributes.addFlashAttribute("message", "Slug already exists, choose another");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        else {
            page.setSlug(slug);
            pageRepository.save(page);
        }

        return "redirect:/admin/pages/edit/" + page.getId();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        pageRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("message", "Page deleted");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        return "redirect:/admin/pages";
    }

    @PostMapping("/reorder")
    public @ResponseBody String reorder(@RequestParam("id[]") Long[] id) {
        int count = 1;
        Page page;

        for(Long pageId : id) {
            page = pageRepository.findById(pageId).orElse(null);
            if(page == null) {
                continue;
            }
            page.setSorting(count);
            pageRepository.save(page);
            count++;
        }

        return "ok";
    }
}
