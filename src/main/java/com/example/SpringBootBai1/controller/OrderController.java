package com.example.SpringBootBai1.controller;


import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.SpringBootBai1.model.entity.Order;
import com.example.SpringBootBai1.model.entity.Product;
import com.example.SpringBootBai1.model.entity.User;
import com.example.SpringBootBai1.model.repo.OrderRepo;
import com.example.SpringBootBai1.model.repo.ProductRepo;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {
    @Autowired
    OrderRepo orderRepo;
    @Autowired
    ProductRepo productRepo;
    @Autowired
    ArrayList<Product> cartList = new ArrayList<>();

    //--------------------------------------------------------------//
    //Order product
    @GetMapping("/ShowOrder/{id}")
    public String ShowOrder(@PathVariable("id") int id, Model model)throws Exception {
        Product product = productRepo.getProductBypid(id);
        model.addAttribute("ProductOrder", product);
        return "Order/ShowOrder";
    }

    @PostMapping("/OrderProduct")
    public String OrderProduct(@RequestParam("pid") int pid, @RequestParam("Quantity") int quantity, HttpSession httpSession) throws Exception {
        User user = (User) httpSession.getAttribute("UserAfterLogin");
        System.out.println(pid);
        Product product = productRepo.getProductBypid(pid);
        double totalPrice = product.getPrice() * quantity;
        int newQuantity = product.getQuantity() - quantity;
        Order order = new Order(0, user, product, quantity, totalPrice);
        productRepo.updateQuantity(pid, newQuantity);
        orderRepo.addNewOrder(order);
        return "redirect:/";
    }
    //--------------------------------------------------------------//
    //View Order
    @GetMapping("/ShowOrderByUserId")
    public String showOrderByUserId(HttpSession httpSession,Model model)throws Exception{
        User user = (User) httpSession.getAttribute("UserAfterLogin");
        ArrayList<Order> ordList = orderRepo.getAllOrderByUserId(user.getUid());
        model.addAttribute("OrderList", ordList);
        return "Order/showOrderByUserId";
    }

    //--------------------------------------------------------------//
    //ADD TO CARD
    @GetMapping("/AddToCard/{id}")
    public String addToCard(@PathVariable("id") int id, HttpSession httpSession)throws Exception{
        Product product = productRepo.getProductBypid(id);
        for (Product p : cartList) {
            if (p.getPid()==product.getPid()) {
                p.setQuantity(p.getQuantity()+1);
                return "redirect:/";
            }
        }
        product.setQuantity(1);
        cartList.add(product);
        httpSession.setAttribute("CartList", cartList);
        return "redirect:/";
    }
    //--------------------------------------------------------------//
    // SHOW CARD
    @GetMapping("ShowCard")
    public String showCard(HttpSession httpSession, Model model) throws Exception{
        ArrayList<Product> proList = (ArrayList<Product>)httpSession.getAttribute("CartList");
        model.addAttribute("CartListModel", proList);
        return "Order/showCard";
    }
}
