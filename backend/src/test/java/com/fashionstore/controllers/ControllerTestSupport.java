package com.fashionstore.controllers;

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

    protected ItemVariantRequest itemVariantRequest(long itemId, long sizeId, long colorId) {
        ItemVariantRequest request = new ItemVariantRequest();
        request.setActive(true);
        request.setStockLeft(12);
        request.setImageUrl("https://example.com/variant.png");
        request.setItemId(itemId);
        request.setSizeId(sizeId);
        request.setColorId(colorId);
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

    protected AdminCategoryResponse adminCategoryResponse(long id, String name, Long parentId, String parentName) {
        AdminCategoryResponse response = new AdminCategoryResponse();
        response.setId(id);
        response.setName(name);
        response.setParentId(parentId);
        response.setParentName(parentName);
        return response;
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

    protected AdminItemResponse adminItemResponse(long id, String name, long categoryId) {
        AdminItemResponse response = new AdminItemResponse();
        response.setId(id);
        response.setName(name);
        response.setPrice(BigDecimal.valueOf(19.99));
        response.setDescription("Valid controller item description");
        response.setImageUrl("https://example.com/item.png");
        response.setAudience("MEN");
        response.setCategoryId(categoryId);
        response.setVariants(new ArrayList<>());
        response.setCategoryName("Outerwear");
        response.setVariantCount(0);
        return response;
    }

    protected ItemVariantResponse itemVariantResponse(long id, long itemId, long sizeId, long colorId) {
        return new ItemVariantResponse(id, true, 12, "https://example.com/variant.png", itemId, sizeId, colorId);
    }

    protected AdminItemVariantResponse adminItemVariantResponse(long id, long itemId, long sizeId, long colorId) {
        AdminItemVariantResponse response = new AdminItemVariantResponse();
        response.setId(id);
        response.setActive(true);
        response.setStockLeft(12);
        response.setImageUrl("https://example.com/variant.png");
        response.setItemId(itemId);
        response.setSizeId(sizeId);
        response.setColorId(colorId);
        response.setItemName("Rain Jacket");
        response.setItemPrice(BigDecimal.valueOf(19.99));
        response.setItemAudience("MEN");
        response.setSizeLabel("M");
        response.setSizeSystem("ALPHA");
        response.setColorName("Black");
        response.setColorValue("#000000");
        return response;
    }

    protected AddressResponse addressResponse(long id, long userId, String city) {
        return new AddressResponse(id, "Ukraine", "Region", city, 1000, "123 Test Street", userId);
    }

    protected AdminAddressResponse adminAddressResponse(long id, long userId, String city) {
        AdminAddressResponse response = new AdminAddressResponse();
        response.setId(id);
        response.setCountry("Ukraine");
        response.setRegion("Region");
        response.setCity(city);
        response.setPostalCode(1000);
        response.setAddressLine("123 Test Street");
        response.setUserId(userId);
        response.setUserName("Test User");
        response.setUserEmail("test@example.com");
        response.setUserPhoneNumber("1234567890");
        return response;
    }

    protected FavoriteResponse favoriteResponse(long id, long userId, long variantId) {
        return new FavoriteResponse(id, variantId, userId);
    }

    protected CartItemResponse cartItemResponse(long id, long userId, long variantId, int quantity) {
        return new CartItemResponse(id, quantity, variantId, userId);
    }

    protected AdminCartItemResponse adminCartItemResponse(long id, long userId, long variantId, int quantity) {
        AdminCartItemResponse response = new AdminCartItemResponse();
        response.setId(id);
        response.setQuantity(quantity);
        response.setItemVariantId(variantId);
        response.setUserId(userId);
        response.setUserName("Test User");
        response.setUserEmail("test@example.com");
        response.setItemId(10L);
        response.setItemName("Jacket");
        response.setSizeLabel("M");
        response.setColorName("Black");
        return response;
    }

    protected ReviewResponse reviewResponse(long id, long userId, long variantId, String body) {
        return new ReviewResponse(id, body, "TRUE_TO_SIZE", "EXCELLENT", "VERY_COMFORTABLE", userId, variantId);
    }

    protected UserResponse userResponse(long id, String email) {
        return new UserResponse(id, "Test", "User", email, "1234567890");
    }

    protected AdminUserResponse adminUserResponse(long id, String email) {
        AdminUserResponse response = new AdminUserResponse();
        response.setId(id);
        response.setFirstName("Test");
        response.setLastName("User");
        response.setEmail(email);
        response.setPhoneNumber("1234567890");
        response.setRole("USER");
        return response;
    }

    protected <T> PageResponse<T> pageResponse(T item) {
        return new PageResponse<>(List.of(item), 1, 20, 1, 1, true, true);
    }
}
