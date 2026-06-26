package com.fashionstore.controllers;

import com.fashionstore.dto.request.AddressRequest;
import com.fashionstore.dto.request.CartItemRequest;
import com.fashionstore.dto.request.CategoryRequest;
import com.fashionstore.dto.request.ColorRequest;
import com.fashionstore.dto.request.FavoriteRequest;
import com.fashionstore.dto.request.ItemRequest;
import com.fashionstore.dto.request.ReviewRequest;
import com.fashionstore.dto.request.SizeRequest;
import com.fashionstore.dto.request.UserRequest;
import com.fashionstore.dto.response.*;
import com.fashionstore.vo.Audience;
import com.fashionstore.vo.Comfort;
import com.fashionstore.vo.Quality;
import com.fashionstore.vo.SizeFit;
import com.fashionstore.vo.SizeSystem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

abstract class ControllerTestSupport {
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
        request.setImageUrl("https://example.com/color.png");
        return request;
    }

    protected SizeRequest sizeRequest(String label) {
        SizeRequest request = new SizeRequest();
        request.setLabel(label);
        request.setSizeSystem(SizeSystem.ALPHA);
        return request;
    }

    protected ItemRequest itemRequest(String name, long categoryId) {
        ItemRequest request = new ItemRequest();
        request.setName(name);
        request.setPrice(BigDecimal.valueOf(19.99));
        request.setDescription("Valid controller item description");
        request.setImageUrl("https://example.com/item.png");
        request.setAudience(Audience.MEN);
        request.setCategoryId(categoryId);
        return request;
    }

    protected AddressRequest addressRequest(long userId, String city) {
        AddressRequest request = new AddressRequest();
        request.setUserId(userId);
        request.setCountry("Ukraine");
        request.setRegion("Region");
        request.setCity(city);
        request.setPostalCode(1000);
        request.setAddressLine("123 Test Street");
        return request;
    }

    protected FavoriteRequest favoriteRequest(long userId, long variantId) {
        FavoriteRequest request = new FavoriteRequest();
        request.setUserId(userId);
        request.setItemVariantId(variantId);
        return request;
    }

    protected CartItemRequest cartItemRequest(long userId, long variantId, int quantity) {
        CartItemRequest request = new CartItemRequest();
        request.setUserId(userId);
        request.setItemVariantId(variantId);
        request.setQuantity(quantity);
        return request;
    }

    protected ReviewRequest reviewRequest(long userId, long variantId, String body) {
        ReviewRequest request = new ReviewRequest();
        request.setItemVariantId(variantId);
        request.setBody(body);
        request.setSizeFit(SizeFit.TRUE_TO_SIZE);
        request.setQuality(Quality.EXCELLENT);
        request.setComfort(Comfort.VERY_COMFORTABLE);
        return request;
    }

    protected UserRequest userRequest(String email, String firstName) {
        UserRequest request = new UserRequest();
        request.setFirstName(firstName);
        request.setLastName("User");
        request.setEmail(email);
        request.setPhoneNumber("1234567890");
        request.setPassword("password");
        return request;
    }

    protected CategoryResponse categoryResponse(long id, String name, Long parentId) {
        return new CategoryResponse(id, name, parentId);
    }

    protected ColorResponse colorResponse(long id, String name, String value) {
        return new ColorResponse(id, name, value, "https://example.com/color.png");
    }

    protected SizeResponse sizeResponse(long id, String label) {
        return new SizeResponse(id, label, "ALPHA");
    }

    protected ItemResponse itemResponse(long id, String name, long categoryId) {
        return new ItemResponse(id, name, BigDecimal.valueOf(19.99), "Valid controller item description",
                "https://example.com/item.png", "MEN", categoryId, new ArrayList<ItemVariantResponse>());
    }

    protected AddressResponse addressResponse(long id, long userId, String city) {
        return new AddressResponse(id, "Ukraine", "Region", city, 1000, "123 Test Street", userId);
    }

    protected FavoriteResponse favoriteResponse(long id, long userId, long variantId) {
        return new FavoriteResponse(id, variantId, userId);
    }

    protected CartItemResponse cartItemResponse(long id, long userId, long variantId, int quantity) {
        return new CartItemResponse(id, quantity, variantId, userId);
    }

    protected ReviewResponse reviewResponse(long id, long userId, long variantId, String body) {
        return new ReviewResponse(id, body, "TRUE_TO_SIZE", "EXCELLENT", "VERY_COMFORTABLE", userId, variantId);
    }

    protected UserResponse userResponse(long id, String email) {
        return new UserResponse(id, "Test", "User", email, "1234567890");
    }

    protected <T> PageResponse<T> pageResponse(T item) {
        return new PageResponse<>(List.of(item), 1, 20, 1, 1, true, true);
    }
}
