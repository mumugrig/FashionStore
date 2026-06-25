package com.fashionstore.services;

import com.fashionstore.dto.request.AddressRequest;
import com.fashionstore.dto.request.CartItemRequest;
import com.fashionstore.dto.request.CategoryRequest;
import com.fashionstore.dto.request.ColorRequest;
import com.fashionstore.dto.request.FavoriteRequest;
import com.fashionstore.dto.request.ItemRequest;
import com.fashionstore.dto.request.ItemVariantRequest;
import com.fashionstore.dto.request.ReviewRequest;
import com.fashionstore.dto.request.SizeRequest;
import com.fashionstore.dto.request.UserRequest;
import com.fashionstore.models.Address;
import com.fashionstore.models.CartItem;
import com.fashionstore.models.Category;
import com.fashionstore.models.Color;
import com.fashionstore.models.Favorite;
import com.fashionstore.models.Item;
import com.fashionstore.models.ItemVariant;
import com.fashionstore.models.Review;
import com.fashionstore.models.Size;
import com.fashionstore.models.User;
import com.fashionstore.vo.Audience;
import com.fashionstore.vo.Comfort;
import com.fashionstore.vo.Quality;
import com.fashionstore.vo.SizeFit;
import com.fashionstore.vo.SizeSystem;
import com.fashionstore.vo.UserRole;

import java.math.BigDecimal;

abstract class ServiceTestSupport {
    protected UserRequest userRequest(String email, String firstName) {
        UserRequest request = new UserRequest();
        request.setFirstName(firstName);
        request.setLastName("User");
        request.setEmail(email);
        request.setPhoneNumber("1234567890");
        request.setPassword("password");
        return request;
    }

    protected CategoryRequest categoryRequest(String name, Long parentId) {
        CategoryRequest request = new CategoryRequest();
        request.setName(name);
        request.setParentId(parentId);
        return request;
    }

    protected ColorRequest colorRequest(String name, String value) {
        ColorRequest request = new ColorRequest();
        request.setName(name);
        request.setValue(value);
        request.setImageUrl("https://example.com/image.png");
        return request;
    }

    protected SizeRequest sizeRequest(String label) {
        SizeRequest request = new SizeRequest();
        request.setLabel(label);
        request.setSizeSystem(SizeSystem.US);
        return request;
    }

    protected ItemRequest itemRequest(String name, Long categoryId) {
        ItemRequest request = new ItemRequest();
        request.setName(name);
        request.setPrice(BigDecimal.valueOf(19.99));
        request.setDescription("A valid test item description");
        request.setAudience(Audience.MEN);
        request.setCategoryId(categoryId);
        return request;
    }

    protected ItemVariantRequest itemVariantRequest(Long itemId, Long sizeId, Long colorId) {
        return itemVariantRequest(itemId, sizeId, colorId, true, 10);
    }

    protected ItemVariantRequest itemVariantRequest(Long itemId, Long sizeId, Long colorId, boolean active, int stockLeft) {
        ItemVariantRequest request = new ItemVariantRequest();
        request.setItemId(itemId);
        request.setSizeId(sizeId);
        request.setColorId(colorId);
        request.setActive(active);
        request.setStockLeft(stockLeft);
        return request;
    }

    protected CartItemRequest cartItemRequest(Long userId, Long itemVariantId, int quantity) {
        CartItemRequest request = new CartItemRequest();
        request.setUserId(userId);
        request.setItemVariantId(itemVariantId);
        request.setQuantity(quantity);
        return request;
    }

    protected FavoriteRequest favoriteRequest(Long userId, Long itemVariantId) {
        FavoriteRequest request = new FavoriteRequest();
        request.setUserId(userId);
        request.setItemVariantId(itemVariantId);
        return request;
    }

    protected ReviewRequest reviewRequest(Long userId, Long itemVariantId, String body) {
        ReviewRequest request = new ReviewRequest();
        request.setItemVariantId(itemVariantId);
        request.setBody(body);
        request.setSizeFit(SizeFit.TRUE_TO_SIZE);
        request.setQuality(Quality.EXCELLENT);
        request.setComfort(Comfort.VERY_COMFORTABLE);
        return request;
    }

    protected AddressRequest addressRequest(Long userId, String city) {
        AddressRequest request = new AddressRequest();
        request.setUserId(userId);
        request.setCountry("Ukraine");
        request.setRegion("Region");
        request.setCity(city);
        request.setPostalCode(1000);
        request.setAddressLine("123 Test Street");
        return request;
    }

    protected User user(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setPhoneNumber("1234567890");
        user.setPasswordHash("hashedPassword");
        user.setRole(UserRole.USER);
        return user;
    }

    protected Category category(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }

    protected Color color(Long id, String name, String value) {
        Color color = new Color();
        color.setId(id);
        color.setName(name);
        color.setValue(value);
        color.setImageUrl("https://example.com/image.png");
        return color;
    }

    protected Size size(Long id, String label) {
        Size size = new Size();
        size.setId(id);
        size.setLabel(label);
        size.setSizeSystem(SizeSystem.US);
        return size;
    }

    protected Item item(Long id, String name, Category category) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setPrice(BigDecimal.valueOf(19.99));
        item.setDescription("A valid test item description");
        item.setAudience(Audience.MEN);
        item.setCategory(category);
        return item;
    }

    protected ItemVariant itemVariant(Long id, Item item, Size size, Color color) {
        ItemVariant variant = new ItemVariant();
        variant.setId(id);
        variant.setActive(true);
        variant.setStockLeft(10);
        variant.setItem(item);
        variant.setSize(size);
        variant.setColor(color);
        return variant;
    }

    protected CartItem cartItem(Long id, User user, ItemVariant variant, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setId(id);
        cartItem.setUser(user);
        cartItem.setItemVariant(variant);
        cartItem.setQuantity(quantity);
        return cartItem;
    }

    protected Favorite favorite(Long id, User user, ItemVariant variant) {
        Favorite favorite = new Favorite();
        favorite.setId(id);
        favorite.setUser(user);
        favorite.setItemVariant(variant);
        return favorite;
    }

    protected Review review(Long id, User user, ItemVariant variant, String body) {
        Review review = new Review();
        review.setId(id);
        review.setUser(user);
        review.setItemVariant(variant);
        review.setBody(body);
        review.setSizeFit(SizeFit.TRUE_TO_SIZE);
        review.setQuality(Quality.EXCELLENT);
        review.setComfort(Comfort.VERY_COMFORTABLE);
        return review;
    }

    protected Address address(Long id, User user, String city) {
        Address address = new Address();
        address.setId(id);
        address.setUser(user);
        address.setCountry("Ukraine");
        address.setRegion("Region");
        address.setCity(city);
        address.setPostalCode(1000);
        address.setAddressLine("123 Test Street");
        return address;
    }
}
