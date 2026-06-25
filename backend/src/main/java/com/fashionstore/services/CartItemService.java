package com.fashionstore.services;

import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.CartItem;
import com.fashionstore.models.ItemVariant;
import com.fashionstore.models.User;
import com.fashionstore.dto.request.CartItemRequest;
import com.fashionstore.dto.response.CartItemResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.repositories.CartItemRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ItemVariantRepository itemVariantRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public CartItemResponse addToCart(CartItemRequest cartItemRequest) {
        CartItem cartItem = new CartItem();
        applyCartItemRequest(cartItem, cartItemRequest);
        User user = userRepository.findById(cartItemRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("User", cartItemRequest.getUserId()));
        cartItem.setUser(user);

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        return CartItemResponse.from(savedCartItem);
    }

    @Transactional
    public CartItemResponse addToCart(Authentication authentication, CartItemRequest cartItemRequest) {
        CartItem cartItem = new CartItem();
        applyCartItemRequest(cartItem, cartItemRequest);
        cartItem.setUser(currentUserService.findCurrentUser(authentication));

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        return CartItemResponse.from(savedCartItem);
    }

    @Transactional
    public CartItemResponse updateCartItem(Long id, CartItemRequest cartItemRequest) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            applyCartItemRequest(cartItem, cartItemRequest);
            User user = userRepository.findById(cartItemRequest.getUserId())
                    .orElseThrow(() -> new NotFoundException("User", cartItemRequest.getUserId()));
            cartItem.setUser(user);

            CartItem updatedCartItem = cartItemRepository.save(cartItem);
            return CartItemResponse.from(updatedCartItem);
        }
        throw new NotFoundException("CartItem", id);
    }

    @Transactional
    public CartItemResponse updateCartItem(Authentication authentication, Long id, CartItemRequest cartItemRequest) {
        var currentUser = currentUserService.findCurrentUser(authentication);
        CartItem cartItem = cartItemRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new NotFoundException("CartItem", id));
        applyCartItemRequest(cartItem, cartItemRequest);
        CartItem updatedCartItem = cartItemRepository.save(cartItem);
        return CartItemResponse.from(updatedCartItem);
    }

    @Transactional(readOnly = true)
    public CartItemResponse getCartItemById(Long id) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
        return cartItemOptional.map(CartItemResponse::from).orElseThrow(() -> new NotFoundException("CartItem", id));
    }

    @Transactional(readOnly = true)
    public PageResponse<CartItemResponse> getPagedCartItems(int page, int size) {
        return PageResponse.from(cartItemRepository.findAll(PageRequestFactory.create(page, size)), CartItemResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<CartItemResponse> getPagedCartItems(Authentication authentication, int page, int size) {
        var currentUser = currentUserService.findCurrentUser(authentication);
        return PageResponse.from(cartItemRepository.findByUserId(currentUser.getId(), PageRequestFactory.create(page, size)), CartItemResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<CartItemResponse> getPagedCartItemsByUser(Long userId, int page, int size) {
        return PageResponse.from(cartItemRepository.findByUserId(userId, PageRequestFactory.create(page, size)), CartItemResponse::from);
    }

    @Transactional
    public void removeFromCart(Long id) {
        if (!cartItemRepository.existsById(id)) {
            throw new NotFoundException("CartItem", id);
        }
        cartItemRepository.deleteById(id);
    }

    @Transactional
    public void removeFromCart(Authentication authentication, Long id) {
        var currentUser = currentUserService.findCurrentUser(authentication);
        CartItem cartItem = cartItemRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new NotFoundException("CartItem", id));
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearUserCart(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User", userId);
        }
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        cartItemRepository.deleteAll(cartItems);
    }

    private void applyCartItemRequest(CartItem cartItem, CartItemRequest cartItemRequest) {
        cartItem.setQuantity(cartItemRequest.getQuantity());

        ItemVariant itemVariant = itemVariantRepository.findById(cartItemRequest.getItemVariantId())
                .orElseThrow(() -> new NotFoundException("ItemVariant", cartItemRequest.getItemVariantId()));
        cartItem.setItemVariant(itemVariant);
    }
}

