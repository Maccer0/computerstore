package com.javadbappsproject.computerstore;

import com.javadbappsproject.computerstore.model.Cart;
import com.javadbappsproject.computerstore.model.Category;
import com.javadbappsproject.computerstore.model.Page;
import com.javadbappsproject.computerstore.repository.CategoryRepository;
import com.javadbappsproject.computerstore.repository.PageRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

@ControllerAdvice
@SuppressWarnings("unchecked")
public class Common {
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @ModelAttribute("servletPath")
    String getRequestServletPath(HttpServletRequest request) {
        return request.getServletPath();
  }

    @ModelAttribute
    public void sharedData(Model model, HttpSession session, Principal principal) {
        if(principal != null) {
            model.addAttribute("principal", principal.getName());
            model.addAttribute("role", principal.getName().equals("admin") ? "admin" : "user");
        }

        List<Page> pages = pageRepository.findAllByOrderBySortingAsc();

        List<Category> categories = categoryRepository.findAllByOrderBySortingAsc();

        boolean cartActive = false;

        if(session.getAttribute("cart") != null) {
            HashMap<Long, Cart> cart = (HashMap<Long, Cart>) session.getAttribute("cart");
            int size = 0;
            double total = 0;

            for (Cart value : cart.values()) {
                size += value.getQuantity();
                total += value.getQuantity() * value.getPrice().doubleValue();
            }

            model.addAttribute("csize", size);
            model.addAttribute("ctotal", total);
            cartActive = true;
        }

        model.addAttribute("categories", categories);
        model.addAttribute("cpages", pages);
        model.addAttribute("cartActive", cartActive);
    }
}
