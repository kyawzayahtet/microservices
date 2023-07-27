package com.example.transportionservice.service;

import com.example.transportionservice.entity.CartItem;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CartItemService {

    private final HttpGraphQlClient graphQlClient;

    public CartItemService() {
        WebClient client = WebClient
                .builder()
                .baseUrl("http://localhost:8080/graphql")
                .build();
        graphQlClient = HttpGraphQlClient.builder(client).build();
    }

    public Mono<List<CartItem>> getAllCartItem(){
        return graphQlClient.document("""
                {
                  cartItems{
                    id
                    name
                    price
                    description
                    quantity
                    lastUpdate
                  }
                }
                """).retrieve("cartItems").toEntityList(CartItem.class);
    }
}
