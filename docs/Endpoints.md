# API Endpoints

All endpoints are prefixed with `/api`. User endpoints require an authenticated user unless marked `Everyone`. Admin endpoints require `ADMIN`.

## Authentication
| Method | Endpoint | Description | Permissions |
|--------|----------|-------------|-------------|
| POST | `/api/auth/register` | Register a new user | Everyone |
| POST | `/api/auth/login` | Log in an existing user | Everyone |
| POST | `/api/auth/refresh` | Refresh an access token | Refresh token |
| POST | `/api/auth/logout` | Log out the current user | User |

## User Profile
| Method | Endpoint | Description | Permissions |
|--------|----------|-------------|-------------|
| GET | `/api/users/me` | Get current user profile | User |
| PATCH | `/api/users/me` | Update current user profile | User |
| DELETE | `/api/users/me` | Delete current user account | User |

## Catalog
| Method | Endpoint | Description | Permissions |
|--------|----------|-------------|-------------|
| GET | `/api/items` | Get paged items | User |
| GET | `/api/items/{id}` | Get item by id | User |
| GET | `/api/categories` | Get paged categories | User |
| GET | `/api/categories/{id}` | Get category by id | User |
| GET | `/api/colors` | Get paged colors | User |
| GET | `/api/colors/{id}` | Get color by id | User |
| GET | `/api/sizes` | Get paged sizes | User |
| GET | `/api/sizes/{id}` | Get size by id | User |

## Addresses
| Method | Endpoint | Description | Permissions |
|--------|----------|-------------|-------------|
| GET | `/api/addresses` | Get current user's paged addresses | User |
| GET | `/api/addresses/{id}` | Get current user's address by id | User |
| POST | `/api/addresses` | Create address for current user | User |
| PUT | `/api/addresses/{id}` | Update current user's address | User |
| DELETE | `/api/addresses/{id}` | Delete current user's address | User |

## Cart
| Method | Endpoint | Description | Permissions |
|--------|----------|-------------|-------------|
| GET | `/api/cart` | Get current user's paged cart items | User |
| POST | `/api/cart/items` | Add item variant to current user's cart | User |
| PUT | `/api/cart/items/{id}` | Update current user's cart item by cart item id | User |
| DELETE | `/api/cart/items/{id}` | Delete current user's cart item by cart item id | User |

## Favorites
| Method | Endpoint | Description | Permissions |
|--------|----------|-------------|-------------|
| GET | `/api/favorites` | Get current user's paged favorites | User |
| POST | `/api/favorites/items` | Add item variant to current user's favorites | User |
| DELETE | `/api/favorites/items/{id}` | Delete current user's favorite by favorite id | User |

## Reviews
| Method | Endpoint | Description | Permissions |
|--------|----------|-------------|-------------|
| GET | `/api/items/{itemId}/reviews` | Get paged reviews for an item | User |
| POST | `/api/items/{itemId}/reviews` | Create review for an item variant | User |
| PUT | `/api/items/{itemId}/reviews/{id}` | Update current user's review | User |
| DELETE | `/api/items/{itemId}/reviews/{id}` | Delete current user's review | User |

## Admin Users
| Method | Endpoint | Description | Permissions |
|--------|----------|-------------|-------------|
| GET | `/api/admin/users` | Get paged users | Admin |
| GET | `/api/admin/users/{id}` | Get user by id | Admin |
| PATCH | `/api/admin/users/{id}` | Update user by id | Admin |
| DELETE | `/api/admin/users/{id}` | Delete user by id | Admin |
| POST | `/api/admin/users/bulk-delete` | Delete multiple users | Admin |

## Admin Items
| Method | Endpoint | Description | Permissions |
|--------|----------|-------------|-------------|
| GET | `/api/admin/items` | Get paged items with admin details | Admin |
| GET | `/api/admin/items/{id}` | Get item by id | Admin |
| POST | `/api/admin/items` | Create item | Admin |
| PUT | `/api/admin/items/{id}` | Update item by id | Admin |
| DELETE | `/api/admin/items/{id}` | Delete item by id | Admin |
| POST | `/api/admin/items/bulk-delete` | Delete multiple items | Admin |

## Admin Item Variants
| Method | Endpoint | Description | Permissions |
|--------|----------|-------------|-------------|
| GET | `/api/admin/item-variants` | Get paged item variants with admin details | Admin |
| GET | `/api/admin/item-variants/{id}` | Get item variant by id | Admin |
| POST | `/api/admin/item-variants` | Create item variant | Admin |
| PUT | `/api/admin/item-variants/{id}` | Update item variant by id | Admin |
| DELETE | `/api/admin/item-variants/{id}` | Delete item variant by id | Admin |
| POST | `/api/admin/item-variants/bulk-delete` | Delete multiple item variants | Admin |

## Admin Categories, Colors, And Sizes
| Method | Endpoint | Description | Permissions |
|--------|----------|-------------|-------------|
| GET | `/api/admin/categories` | Get paged categories with admin details | Admin |
| GET | `/api/admin/categories/{id}` | Get category by id | Admin |
| POST | `/api/admin/categories` | Create category | Admin |
| PUT | `/api/admin/categories/{id}` | Update category by id | Admin |
| DELETE | `/api/admin/categories/{id}` | Delete category by id | Admin |
| POST | `/api/admin/categories/bulk-delete` | Delete multiple categories | Admin |
| GET | `/api/admin/colors` | Get paged colors | Admin |
| GET | `/api/admin/colors/{id}` | Get color by id | Admin |
| POST | `/api/admin/colors` | Create color | Admin |
| PUT | `/api/admin/colors/{id}` | Update color by id | Admin |
| DELETE | `/api/admin/colors/{id}` | Delete color by id | Admin |
| POST | `/api/admin/colors/bulk-delete` | Delete multiple colors | Admin |
| GET | `/api/admin/sizes` | Get paged sizes | Admin |
| GET | `/api/admin/sizes/{id}` | Get size by id | Admin |
| POST | `/api/admin/sizes` | Create size | Admin |
| PUT | `/api/admin/sizes/{id}` | Update size by id | Admin |
| DELETE | `/api/admin/sizes/{id}` | Delete size by id | Admin |
| POST | `/api/admin/sizes/bulk-delete` | Delete multiple sizes | Admin |

## Admin Addresses
| Method | Endpoint | Description | Permissions |
|--------|----------|-------------|-------------|
| GET | `/api/admin/addresses` | Get paged addresses with admin details | Admin |
| GET | `/api/admin/users/{userId}/addresses` | Get paged addresses for a user | Admin |
| GET | `/api/admin/addresses/{id}` | Get address by id | Admin |
| POST | `/api/admin/addresses` | Create address | Admin |
| PUT | `/api/admin/addresses/{id}` | Update address by id | Admin |
| DELETE | `/api/admin/addresses/{id}` | Delete address by id | Admin |
| POST | `/api/admin/addresses/bulk-delete` | Delete multiple addresses | Admin |

## Admin Cart
| Method | Endpoint | Description | Permissions |
|--------|----------|-------------|-------------|
| GET | `/api/admin/cart` | Get paged cart items with admin details | Admin |
| GET | `/api/admin/users/{userId}/cart` | Get paged cart items for a user | Admin |
| GET | `/api/admin/cart/items/{id}` | Get cart item by id | Admin |
| POST | `/api/admin/cart` | Create cart item | Admin |
| PUT | `/api/admin/cart/items/{id}` | Update cart item by id | Admin |
| DELETE | `/api/admin/cart/items/{id}` | Delete cart item by id | Admin |
| POST | `/api/admin/cart/bulk-delete` | Delete multiple cart items | Admin |

## Admin Favorites
| Method | Endpoint | Description | Permissions |
|--------|----------|-------------|-------------|
| GET | `/api/admin/favorites` | Get paged favorites with admin details | Admin |
| GET | `/api/admin/users/{userId}/favorites` | Get paged favorites for a user | Admin |
| GET | `/api/admin/favorites/{id}` | Get favorite by id | Admin |
| POST | `/api/admin/favorites` | Create favorite | Admin |
| PUT | `/api/admin/favorites/{id}` | Update favorite by id | Admin |
| DELETE | `/api/admin/favorites/{id}` | Delete favorite by id | Admin |
| POST | `/api/admin/favorites/bulk-delete` | Delete multiple favorites | Admin |

## Admin Reviews
| Method | Endpoint | Description | Permissions |
|--------|----------|-------------|-------------|
| GET | `/api/admin/reviews` | Get paged reviews with admin details | Admin |
| GET | `/api/admin/reviews/{id}` | Get review by id | Admin |
| PUT | `/api/admin/reviews/{id}` | Update review by id | Admin |
| DELETE | `/api/admin/reviews/{id}` | Delete review by id | Admin |
| POST | `/api/admin/reviews/bulk-delete` | Delete multiple reviews | Admin |

---

# Query Parameters

Paged collection endpoints return:

```json
{
  "content": [],
  "page": 1,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0,
  "first": true,
  "last": true
}
```

Pagination uses one-based page numbers. If omitted, `page` defaults to `1` and `size` defaults to `20`.

## Catalog Filters

### GET `/api/items`
| Parameter | Type | Description | Required |
|-----------|------|-------------|----------|
| `page` | Number | One-based page number | No |
| `size` | Number | Items per page | No |
| `category` | String | Filter by category | No |
| `search` | String | Filter by item name | No |
| `itemSize` | String | Filter by size label | No |
| `color` | String | Filter by color | No |
| `audience` | String | Filter by audience | No |
| `pricemin` | Number | Minimum price | No |
| `pricemax` | Number | Maximum price | No |

### User Paged Collections
| Endpoint | Extra parameters |
|----------|------------------|
| `GET /api/cart` | `search` filters cart items by item name |
| `GET /api/favorites` | `search` filters favorites by item name |
| `GET /api/addresses` | Pagination only |
| `GET /api/categories` | Pagination only |
| `GET /api/colors` | Pagination only |
| `GET /api/sizes` | Pagination only |
| `GET /api/items/{itemId}/reviews` | Pagination only |

## Admin Filters

Admin paged collection endpoints support:

| Parameter | Type | Description | Required |
|-----------|------|-------------|----------|
| `page` | Number | One-based page number | No |
| `size` | Number | Items per page | No |
| `search` | String | Search across configured admin fields | No |
| `filterColumn` | String | Restrict filtering to one field | No |
| `filterValue` | String | Value to match for `filterColumn` | No |

Bulk delete endpoints accept:

```json
{
  "ids": [1, 2, 3]
}
```
