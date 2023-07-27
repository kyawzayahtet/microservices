package com.example.affablebeanui.controller;

import com.example.affablebeanui.entity.Product;
import com.example.affablebeanui.model.CartItem;
import com.example.affablebeanui.service.CartService;
import com.example.affablebeanui.service.ProductService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/webui")
public class ProductController {

    private final ProductService productService;
    private final CartService cartService;

    @GetMapping("/transport")
    public String transport(){
        try {
            ResponseEntity<?> responseEntity = productService.saveCartItem();
            if(responseEntity.getStatusCode().is2xxSuccessful()){
                return "redirect:/";
            }
            return "redirect:/ui/checkoutView";
        }catch (Exception e){
            return "redirect:/ui/checkoutView";
        }
    }

    @GetMapping("/transfer")
    public String checkoutTransfer(@ModelAttribute("total") double total, RedirectAttributes redirectAttributes) {
        var responseEntity = productService.transfer("marry@gmail.com", "john@gmail.com", total);
        if (responseEntity) {
            cartService.clear();
            return "redirect:/webui/?transfer=true";
        }
        return "redirect:/webui/checkoutView?transferError=true";
    }


    @GetMapping("/products/{id}")
    @ResponseBody
    public List<Product> showAll(@PathVariable int id) {
        return productService.findProductByCategory(id);
    }

    @GetMapping("/products/category")
    public String showProduct(@RequestParam int id, Model model) {
        model.addAttribute("products", productService.findProductByCategory(id));
        return "products";
    }

    @GetMapping("/product/purchase")
    public String addToCarts(@RequestParam int id) {
        productService.purchaseProduct(id);
        return "redirect:/webui/products/category?id=" + productService.findProductById(id).getCategory().getId();
    }

    @GetMapping("/")
    public String home(@RequestParam(value = "transfer", defaultValue = "false") boolean transfer, Model model) {
        model.addAttribute("transfer", transfer);
        return "home";
    }

    @ModelAttribute("cartSize")
    public int cartSize() {
        return cartService.cartSize();
    }

    @GetMapping("/cart-view")
    public String viewCart(Model model) {
        model.addAttribute("cartItems", cartService.getAllProduct());
        model.addAttribute("product", new Product());
        return "cartView";
    }

    @GetMapping("/clear-cart")
    public String clearCart() {
        cartService.clear();
        return "redirect:/";
    }

    @PostMapping("/checkout")
    public String checkout(Product product) {
        int i = 0;
        for (Product product1 : cartService.getAllProduct()) {
            product1.setQuantity(product.getQuantities().get(i));
            i++;
        }
        return "redirect:/webui/checkoutView";
    }

    @GetMapping("/checkoutView")
    public String checkoutView(@RequestParam(value = "transferError", defaultValue = "false") boolean transferError, Model model) {
        model.addAttribute("transferError", transferError);
        return "checkOutView";
    }

    @ModelAttribute("total")
    public double total() {
        return cartService.getAllProduct().stream().map(p -> p.getQuantity() * p.getPrice()).mapToDouble(p -> p).sum();
    }

    @QueryMapping
    public List<CartItem> cartItems() {
        return cartService.getAllProduct().stream().map(p -> new CartItem(p.getId(),
                p.getName(),
                p.getPrice(),
                p.getDescription(),
                p.getQuantity(),
                p.getLastUpdate())).collect(Collectors.toList());
    }

}
