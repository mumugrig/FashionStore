package com.fashionstore.services;

import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.CartItem;
import com.fashionstore.models.ItemVariant;
import com.fashionstore.models.User;
import com.fashionstore.dto.request.CartItemRequest;
import com.fashionstore.dto.response.CartItemResponse;
import com.fashionstore.repositories.CartItemRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public CartItemResponse addToCart(CartItemRequest cartItemRequest) {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(cartItemRequest.getQuantity());

        ItemVariant itemVariant = itemVariantRepository.findById(cartItemRequest.getItemVariantId())
                .orElseThrow(() -> new NotFoundException("ItemVariant", cartItemRequest.getItemVariantId()));
        cartItem.setItemVariant(itemVariant);

        User user = userRepository.findById(cartItemRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("User", cartItemRequest.getUserId()));
        cartItem.setUser(user);

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        return CartItemResponse.from(savedCartItem);
    }

    @Transactional
    public CartItemResponse updateCartItem(Long id, CartItemRequest cartItemRequest) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            cartItem.setQuantity(cartItemRequest.getQuantity());

            ItemVariant itemVariant = itemVariantRepository.findById(cartItemRequest.getItemVariantId())
                    .orElseThrow(() -> new NotFoundException("ItemVariant", cartItemRequest.getItemVariantId()));
            cartItem.setItemVariant(itemVariant);

            User user = userRepository.findById(cartItemRequest.getUserId())
                    .orElseThrow(() -> new NotFoundException("User", cartItemRequest.getUserId()));
            cartItem.setUser(user);

            CartItem updatedCartItem = cartItemRepository.save(cartItem);
            return CartItemResponse.from(updatedCartItem);
        }
        throw new NotFoundException("CartItem", id);
    }

    @Transactional(readOnly = true)
    public CartItemResponse getCartItemById(Long id) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
        return cartItemOptional.map(CartItemResponse::from).orElseThrow(() -> new NotFoundException("CartItem", id));
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> getAllCartItems() {
        return cartItemRepository.findAll()
                .stream()
                .map(CartItemResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCartItemsByUser(Long userId) {
        return cartItemRepository.findByUserId(userId)
                .stream()
                .map(CartItemResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeFromCart(Long id) {
        if (!cartItemRepository.existsById(id)) {
            throw new NotFoundException("CartItem", id);
        }
        cartItemRepository.deleteById(id);
    }

    @Transactional
    public void clearUserCart(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User", userId);
        }
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        cartItemRepository.deleteAll(cartItems);
    }
}

