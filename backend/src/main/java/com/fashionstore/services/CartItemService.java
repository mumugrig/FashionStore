package com.fashionstore.services;

import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.models.CartItem;
import com.fashionstore.models.ItemVariant;
import com.fashionstore.models.User;
import com.fashionstore.dto.request.CartItemRequest;
import com.fashionstore.dto.response.AdminCartItemResponse;
import com.fashionstore.dto.response.CartItemResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.repositories.CartItemRepository;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
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
        assertUniqueCartItem(user.getId(), cartItem.getItemVariant().getId(), null);

        CartItem savedCartItem = cartItemRepository.save(cartItem);
        return CartItemResponse.from(savedCartItem);
    }

    @Transactional
    public CartItemResponse addToCart(Authentication authentication, CartItemRequest cartItemRequest) {
        CartItem cartItem = new CartItem();
        applyCartItemRequest(cartItem, cartItemRequest);
        cartItem.setUser(currentUserService.findCurrentUser(authentication));
        assertUniqueCartItem(cartItem.getUser().getId(), cartItem.getItemVariant().getId(), null);

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
            assertUniqueCartItem(user.getId(), cartItem.getItemVariant().getId(), id);

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
        assertUniqueCartItem(currentUser.getId(), cartItem.getItemVariant().getId(), id);
        CartItem updatedCartItem = cartItemRepository.save(cartItem);
        return CartItemResponse.from(updatedCartItem);
    }

    @Transactional(readOnly = true)
    public CartItemResponse getCartItemById(Long id) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
        return cartItemOptional.map(CartItemResponse::from).orElseThrow(() -> new NotFoundException("CartItem", id));
    }

    @Transactional(readOnly = true)
    public AdminCartItemResponse getAdminCartItemById(Long id) {
        return cartItemRepository.findById(id)
                .map(AdminCartItemResponse::from)
                .orElseThrow(() -> new NotFoundException("CartItem", id));
    }

    @Transactional(readOnly = true)
    public PageResponse<CartItemResponse> getPagedCartItems(int page, int size) {
        return PageResponse.from(cartItemRepository.findAll(PageRequestFactory.create(page, size)), CartItemResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<CartItemResponse> getPagedCartItems(int page, int size, String search, String filterColumn, String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return getPagedCartItems(page, size);
        }
        return PageResponse.from(cartItemRepository.findAll(
                AdminFilterSpecification.create(AdminSearchFields.CART_ITEMS, search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), CartItemResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminCartItemResponse> getPagedAdminCartItems(int page, int size, String search, String filterColumn, String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return PageResponse.from(cartItemRepository.findAll(PageRequestFactory.create(page, size)), AdminCartItemResponse::from);
        }
        return PageResponse.from(cartItemRepository.findAll(
                AdminFilterSpecification.create(AdminSearchFields.CART_ITEMS, search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), AdminCartItemResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<CartItemResponse> getPagedCartItems(Authentication authentication, int page, int size) {
        return getPagedCartItems(authentication, page, size, null);
    }

    @Transactional(readOnly = true)
    public PageResponse<CartItemResponse> getPagedCartItems(Authentication authentication, int page, int size, String search) {
        var currentUser = currentUserService.findCurrentUser(authentication);
        if (!hasText(search)) {
            return PageResponse.from(cartItemRepository.findByUserId(currentUser.getId(), PageRequestFactory.create(page, size)), CartItemResponse::from);
        }
        return PageResponse.from(cartItemRepository.findAll(
                cartSearch(currentUser.getId(), search),
                PageRequestFactory.create(page, size)
        ), CartItemResponse::from);
    }

    private Specification<CartItem> cartSearch(Long userId, String search) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            var item = root.join("itemVariant").join("item");
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("user").get("id"), userId),
                    criteriaBuilder.like(criteriaBuilder.lower(item.get("name")), "%" + normalized(search) + "%")
            );
        };
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String normalized(String value) {
        return value.trim().toLowerCase();
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
    public void removeCartItems(List<Long> ids) {
        ids.forEach(this::removeFromCart);
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
        assertVariantHasStock(itemVariant, cartItemRequest.getQuantity());
        cartItem.setItemVariant(itemVariant);
    }

    private void assertVariantHasStock(ItemVariant itemVariant, int quantity) {
        if (itemVariant.getStockLeft() <= 0) {
            throw new ConflictException("This item variant is out of stock.");
        }
        if (quantity > itemVariant.getStockLeft()) {
            throw new ConflictException("Only " + itemVariant.getStockLeft() + " item(s) left in stock.");
        }
    }

    private void assertUniqueCartItem(Long userId, Long itemVariantId, Long currentCartItemId) {
        boolean duplicate = currentCartItemId == null
                ? cartItemRepository.existsByUserIdAndItemVariantId(userId, itemVariantId)
                : cartItemRepository.existsByUserIdAndItemVariantIdAndIdNot(userId, itemVariantId, currentCartItemId);
        if (duplicate) {
            throw new ConflictException("This item is already in the bag.");
        }
    }
}
