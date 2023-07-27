package com.example.affablebeanui.service;

import com.example.affablebeanui.entity.Product;
import com.example.affablebeanui.entity.Products;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final CartService cartService;
    private List<Product> products;

    private RestTemplate restTemplate = new RestTemplate();

    record RequestData(@JsonProperty("from_email") String fromEmail, @JsonProperty("to_email") String toEmail, double amount){}
    record ResponseData(@JsonProperty("from_email") String fromEmail, @JsonProperty("to_email") String toEmail, double amount){}

    public boolean transfer(String fromEmail, String toEmail, double amount) {
        try {
            var result = restTemplate.postForEntity("http://localhost:8091/account/transfer",
                    new RequestData(fromEmail, toEmail, amount), ResponseData.class);
            if(result.getStatusCode().is2xxSuccessful()){
                return true;
            }
        }catch (Exception e) {
            return false;
        }
        return false;
    }

    @Value("${backend.url}")
    private String base_url;

    public ProductService(CartService cartService1){
        this.cartService = cartService1;
        var productResponse = restTemplate.getForEntity("http://localhost:8092/backend/products", Products.class);
        if(productResponse.getStatusCode().is2xxSuccessful()){
            products = Objects.requireNonNull(productResponse.getBody()).getProducts();
            return;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    public List<Product> showAllProducts(){
        return products;
    }

    public List<Product> findProductByCategory(int categoryId){
      return products.stream().filter(p -> p.getCategory().getId() == categoryId).collect(Collectors.toList());
    }

    public Product findProductById(int id){
        return products.stream().filter(p -> p.getId() == id).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public void purchaseProduct(int id){
        cartService.addToCart(findProductById(id));
    }

    public ResponseEntity<?> saveCartItem(){
        return restTemplate.getForEntity("http://localhost:9000/transport/cart/save", String.class);
    }
}
