package com.ecommerceapp.paymentservice.services;

import com.ecommerceapp.paymentservice.dtos.ProductSkuDto;
import com.ecommerceapp.paymentservice.dtos.cartDtos.GetCartItemResponseDto;
import com.ecommerceapp.paymentservice.dtos.cartDtos.GetCartResponseDto;
import com.ecommerceapp.paymentservice.dtos.cartDtos.UpdateCartRequestDto;
import com.ecommerceapp.paymentservice.models.carts_orders.Cart;
import com.ecommerceapp.paymentservice.models.carts_orders.CartItem;
import com.ecommerceapp.paymentservice.repositories.CartItemRepository;
import com.ecommerceapp.paymentservice.repositories.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    private CartRepository cartRepository;
    private CartItemRepository cartItemRepository;
    RestTemplate restTemplate;
    final String productServiceUrl;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.restTemplate = new RestTemplate();
        this.productServiceUrl = System.getenv("product_service_url");
    }
    public GetCartResponseDto getCartDetails(Long userId) {
        GetCartResponseDto cartResponseDto = new GetCartResponseDto();
//        GetCartItemResponseDto cartItemResponseDto = new GetCartItemResponseDto();

        // Fetch carts by user
        List<Cart> carts = cartRepository.findByUserId(userId);

        // For now just select any active cart (later restrict to only one active cart per user)
        if (!carts.isEmpty()) {
            Cart cart = carts.get(0);

            // Fetch cart items for the selected cart
            List<CartItem> cartItems = cartItemRepository.findByCart(cart);

            if (!cartItems.isEmpty()) {
                BigDecimal totalPrice = BigDecimal.ZERO;

                for (CartItem item : cartItems) {
                    GetCartItemResponseDto cartItemResponseDto = new GetCartItemResponseDto();
                    cartItemResponseDto.setSku(item.getSku());
                    cartItemResponseDto.setPrice(item.getPrice());
                    cartItemResponseDto.setQuantity(item.getQuantity());
                    cartResponseDto.getItems().add(cartItemResponseDto);

                    if (item.getPrice() != null) {
                        totalPrice = totalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    }
                }

                cartResponseDto.setTotalPrice(totalPrice);
                cartResponseDto.setCartId(cart.getId());
            } else {
//                S.println("Exception----DEBUG" + e);
                throw new RuntimeException("Cart items not found for cart id: " + cart.getId());
            }
        } else {
            throw new RuntimeException("No cart found for user id: " + userId);
        }

        return cartResponseDto;
    }

    @Transactional
    public void Upsert(Long userId, UpdateCartRequestDto updateCartRequestDto) {
        try {

            List<Cart> carts = cartRepository.findByUserId(userId);
            Cart cart;
            if (carts.isEmpty()) {
                //create new cart
                cart = new Cart();
                cart.setUserId(userId);
                cart = cartRepository.saveAndFlush(cart);
            }
            else{
                cart = carts.get(0);
            }
            List<CartItem> cartItemstoSave = new ArrayList<>();
            //add more items to the cart
            for (GetCartItemResponseDto cartItem : updateCartRequestDto.getItems()) {
                //check if item is already present in the cart
                List<CartItem> existingItems = cartItemRepository.findByCartAndSku(cart, cartItem.getSku());
                if (!existingItems.isEmpty()) {
                    CartItem existingItem = existingItems.get(0);
                    String getProductPriceUrl = UriComponentsBuilder.fromHttpUrl(productServiceUrl + "/products")
                            .queryParam("SKU", cartItem.getSku())
                            .toUriString();

                    ProductSkuDto productSkuDto = restTemplate.getForObject(getProductPriceUrl, ProductSkuDto.class);

                    existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
//                    cartItemRepository.save(existingItem);
                    cartItemstoSave.add(existingItem);

                } else {
                    // add new item
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setSku(cartItem.getSku());
                    String getProductPriceUrl = UriComponentsBuilder.fromHttpUrl(productServiceUrl + "/products")
                            .queryParam("SKU", cartItem.getSku())
                            .toUriString();
                    ProductSkuDto productSkuDto = restTemplate.getForObject(getProductPriceUrl, ProductSkuDto.class);
                    newItem.setPrice(cartItem.getPrice());
                    newItem.setQuantity(cartItem.getQuantity());
//                    cartItemRepository.save(newItem);
                    cartItemstoSave.add(newItem);
                }
            }
            cartItemRepository.saveAll(cartItemstoSave);
        }catch (Exception e) {
            throw new RuntimeException("Failed to upsert cart for user id: " + userId + ". Error: " + e.getMessage());
        }
    }


    public Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));
    }

    public List<CartItem> getCartItemsByCart(Cart cart){
        return cartItemRepository
                .findByCart(cart);
    }

    public BigDecimal getTotalPrice(Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            if (item.getPrice() != null) {
                totalPrice = totalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        return totalPrice;
    }

    public void deleteCartItems(Cart cart) throws Exception {
        try{
            cartItemRepository.deleteByCart(cart);
        }catch (Exception e){
            throw new RuntimeException("Failed to delete cart items for user id: " + cart.getId());
        }
    }
}
