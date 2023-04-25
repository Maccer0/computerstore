package com.javadbappsproject.computerstore.controller;

import com.javadbappsproject.computerstore.model.Cart;
import com.javadbappsproject.computerstore.model.Product;
import com.javadbappsproject.computerstore.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
@RequestMapping("/cart")
@SuppressWarnings("unchecked")
public class CartController {
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/add/{id}")
    public String add(@PathVariable Long id, HttpSession session, Model model,
                      @RequestParam(value = "cartPage", required = false) String cartPage) {
        Product product = productRepository.findById(id).orElse(null);

        if(session.getAttribute("cart") == null) {
            HashMap<Long, Cart> cart = new HashMap<>();

            cart.put(id, new Cart(id, product.getName(), product.getPrice(), 1, product.getImage()));
            session.setAttribute("cart", cart);
        }
        else {
            HashMap<Long, Cart> cart = (HashMap<Long, Cart>) session.getAttribute("cart");
            if(cart.containsKey(id)) {
                int quantity = cart.get(id).getQuantity();
                cart.put(id, new Cart(id, product.getName(), product.getPrice(), ++quantity, product.getImage()));
            }
            else {
                cart.put(id, new Cart(id, product.getName(), product.getPrice(), 1, product.getImage()));
                session.setAttribute("cart", cart);
            }
        }

        HashMap<Long, Cart> cart = (HashMap<Long, Cart>) session.getAttribute("cart");

        int size = 0;
        double total = 0;

        for(Cart value : cart.values()) {
            size += value.getQuantity();
            total += value.getQuantity() * value.getPrice().doubleValue();
        }

        model.addAttribute("size", size);
        model.addAttribute("total", total);

        if(cartPage != null) {
            return "redirect:/cart/view";
        }

        return "cart_view";
    }

    @GetMapping("/view")
    public String view(HttpSession session, Model model) {
        if(session.getAttribute("cart") == null) {
            return "redirect:/";
        }

        HashMap<Long, Cart> cart = (HashMap<Long, Cart>) session.getAttribute("cart");

        model.addAttribute("cart", cart);
        model.addAttribute("notCartPage", true);

        return "cart";
    }

    @GetMapping("/subtract/{id}")
    public String subtract(@PathVariable Long id, HttpSession session, Model model,
                           @RequestParam(value = "cartPage", required = false) String cartPage,
                           HttpServletRequest request) {
        HashMap<Long, Cart> cart = (HashMap<Long, Cart>) session.getAttribute("cart");

        Product product = productRepository.findById(id).orElse(null);

        if(product == null) {
            return "cart_view";
        }

        int quantity = cart.get(id).getQuantity();

        if(quantity == 1) {
            cart.remove(id);
            if(cart.size() == 0) {
                session.removeAttribute("cart");
            }
        }
        else {
            cart.put(id, new Cart(id, product.getName(), product.getPrice(), --quantity, product.getImage()));
        }

        String referer = request.getHeader("referer");

        return "redirect:" + referer;
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable Long id, Model model, HttpSession session, HttpServletRequest request) {
        HashMap<Long, Cart> cart = (HashMap<Long, Cart>) session.getAttribute("cart");

        cart.remove(id);
        if(cart.size() == 0) {
            session.removeAttribute("cart");
        }

        String referer = request.getHeader("referer");

        return "redirect:" + referer;
    }

    @GetMapping("/clear")
    public String clear(HttpSession session, HttpServletRequest request) {
        session.removeAttribute("cart");

        String referer = request.getHeader("referer");

        return "redirect:" + referer;
    }
}
