package com.fashionstore.services;

import java.util.Map;

public class AdminSearchFields {
    private AdminSearchFields() {}

    public static Map<String, String> ADDRESSES = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("country", "country"),
            Map.entry("region", "region"),
            Map.entry("city", "city"),
            Map.entry("postalCode", "postalCode"),
            Map.entry("addressLine", "addressLine"),
            Map.entry("userIds", "users.id"),
            Map.entry("userId", "users.id"),
            Map.entry("userFirstName", "users.firstName"),
            Map.entry("userLastName", "users.lastName"),
            Map.entry("userNames", "users.firstName"),
            Map.entry("userName", "users.firstName"),
            Map.entry("userEmails", "users.email"),
            Map.entry("userEmail", "users.email"),
            Map.entry("userPhoneNumbers", "users.phoneNumber"),
            Map.entry("userPhoneNumber", "users.phoneNumber")
    );

    public static Map<String, String> CART_ITEMS = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("quantity", "quantity"),
            Map.entry("itemVariantId", "itemVariant.id"),
            Map.entry("userId", "user.id"),
            Map.entry("userFirstName", "user.firstName"),
            Map.entry("userLastName", "user.lastName"),
            Map.entry("userName", "user.firstName"),
            Map.entry("userEmail", "user.email"),
            Map.entry("itemId", "itemVariant.item.id"),
            Map.entry("itemName", "itemVariant.item.name"),
            Map.entry("sizeLabel", "itemVariant.size.label"),
            Map.entry("sizeSystem", "itemVariant.size.sizeSystem"),
            Map.entry("colorName", "itemVariant.color.name"),
            Map.entry("colorValue", "itemVariant.color.value"),
            Map.entry("variantActive", "itemVariant.isActive"),
            Map.entry("variantStockLeft", "itemVariant.stockLeft")
    );

    public static Map<String, String> CATEGORIES = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("name", "name"),
            Map.entry("parentId", "parent.id"),
            Map.entry("parentName", "parent.name")
    );

    public static Map<String, String> COLORS = Map.of(
            "id", "id",
            "name", "name",
            "value", "value",
            "imageUrl", "imageUrl"
    );

    public static Map<String, String> FAVORITES = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("itemVariantId", "itemVariant.id"),
            Map.entry("userId", "user.id"),
            Map.entry("userFirstName", "user.firstName"),
            Map.entry("userLastName", "user.lastName"),
            Map.entry("userName", "user.firstName"),
            Map.entry("userEmail", "user.email"),
            Map.entry("itemId", "itemVariant.item.id"),
            Map.entry("itemName", "itemVariant.item.name"),
            Map.entry("sizeLabel", "itemVariant.size.label"),
            Map.entry("sizeSystem", "itemVariant.size.sizeSystem"),
            Map.entry("colorName", "itemVariant.color.name"),
            Map.entry("colorValue", "itemVariant.color.value"),
            Map.entry("variantActive", "itemVariant.isActive"),
            Map.entry("variantStockLeft", "itemVariant.stockLeft")
    );

    public static Map<String, String> ITEMS = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("name", "name"),
            Map.entry("price", "price"),
            Map.entry("description", "description"),
            Map.entry("imageUrl", "imageUrl"),
            Map.entry("audience", "audience"),
            Map.entry("categoryId", "category.id"),
            Map.entry("categoryName", "category.name")
    );

    public static Map<String, String> ITEM_VARIANTS = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("active", "isActive"),
            Map.entry("isActive", "isActive"),
            Map.entry("stockLeft", "stockLeft"),
            Map.entry("imageUrl", "imageUrl"),
            Map.entry("itemId", "item.id"),
            Map.entry("itemName", "item.name"),
            Map.entry("itemPrice", "item.price"),
            Map.entry("itemAudience", "item.audience"),
            Map.entry("sizeId", "size.id"),
            Map.entry("sizeLabel", "size.label"),
            Map.entry("sizeSystem", "size.sizeSystem"),
            Map.entry("colorId", "color.id"),
            Map.entry("colorName", "color.name"),
            Map.entry("colorValue", "color.value")
    );

    public static Map<String, String> REVIEWS = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("body", "body"),
            Map.entry("sizeFit", "sizeFit"),
            Map.entry("quality", "quality"),
            Map.entry("comfort", "comfort"),
            Map.entry("userId", "user.id"),
            Map.entry("userFirstName", "user.firstName"),
            Map.entry("userLastName", "user.lastName"),
            Map.entry("userName", "user.firstName"),
            Map.entry("userEmail", "user.email"),
            Map.entry("itemId", "item.id"),
            Map.entry("itemName", "item.name")
    );

    public static Map<String, String> SIZES = Map.of(
            "id", "id",
            "label", "label",
            "sizeSystem", "sizeSystem"
    );

    public static Map<String, String> USERS = Map.ofEntries(
            Map.entry("id", "id"),
            Map.entry("firstName", "firstName"),
            Map.entry("lastName", "lastName"),
            Map.entry("email", "email"),
            Map.entry("phoneNumber", "phoneNumber"),
            Map.entry("role", "role"),
            Map.entry("addressIds", "addresses.id")
    );
}
