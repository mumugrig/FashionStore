package com.fashionstore.services;

import com.fashionstore.models.CartItem;
import com.fashionstore.models.ItemVariant;
import com.fashionstore.models.User;
import com.fashionstore.dto.request.CartItemRequest;
import com.fashionstore.dto.response.CartItemResponse;
import com.fashionstore.repositories.CartItemRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ItemVariantRepository itemVariantRepository;
    private final UserRepository userRepository;

    public CartItemService(CartItemRepository cartItemRepository,
                          ItemVariantRepository itemVariantRepository,
                          UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.itemVariantRepository = itemVariantRepository;
        this.userRepository = userRepository;
    }

    public CartItemResponse addToCart(CartItemRequest cartItemRequest) {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(cartItemRequest.getQuantity());

        Optional<ItemVariant> itemVariant = itemVariantRepository.findById(cartItemRequest.getItemVariantId());
        itemVariant.ifPresent(cartItem::setItemVariant);

        Optional<User> user = userRepository.findById(cartItemRequest.getUserId());
        user.ifPresent(cartItem::setUser);

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        return CartItemResponse.from(savedCartItem);
    }

    public CartItemResponse updateCartItem(Long id, CartItemRequest cartItemRequest) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            cartItem.setQuantity(cartItemRequest.getQuantity());

            Optional<ItemVariant> itemVariant = itemVariantRepository.findById(cartItemRequest.getItemVariantId());
            itemVariant.ifPresent(cartItem::setItemVariant);

            Optional<User> user = userRepository.findById(cartItemRequest.getUserId());
            user.ifPresent(cartItem::setUser);

            CartItem updatedCartItem = cartItemRepository.save(cartItem);
            return CartItemResponse.from(updatedCartItem);
        }
        return null;
    }

    public CartItemResponse getCartItemById(Long id) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
        return cartItemOptional.map(CartItemResponse::from).orElse(null);
    }

    public List<CartItemResponse> getAllCartItems() {
        return cartItemRepository.findAll()
                .stream()
                .map(CartItemResponse::from)
                .collect(Collectors.toList());
    }

    public List<CartItemResponse> getCartItemsByUser(Long userId) {
        return cartItemRepository.findByUserId(userId)
                .stream()
                .map(CartItemResponse::from)
                .collect(Collectors.toList());
    }

    public void removeFromCart(Long id) {
        cartItemRepository.deleteById(id);
    }

    public void clearUserCart(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        cartItemRepository.deleteAll(cartItems);
    }
}

