# API Endpoints

### Authentication
| Method | Endpoint       | Description          | Permissions   |
|--------|----------------|----------------------|---------------|
| POST   | /api/auth/register | Register new user    | Everyone      |
| POST   | /api/auth/login    | Log in existing user | Everyone      | 
| POST   | /api/auth/logout   | Logout current user  | User          |
| POST   | /api/auth/refresh  | Refresh access token | Refresh token |

### User
| Method | Endpoint    | Description                        | Permissions|
|--------|-------------|------------------------------------|------------|
| GET    | /api/users/me   | Get current user profile data      | User       |
| PATCH  | /api/users/me   | Update current user profile data   | User       |
| GET    | /api/users/{id} | Get user profile data with `id`    | Admin      |
| PATCH  | /api/users/{id} | Update user profile data with `id` | Admin      |
| DELETE | /api/users/{id} | Remove user with `id`              | Admin      |

### Items
| Method | Endpoint             | Description                                       | Permissions|
|--------|----------------------|---------------------------------------------------|------------|
| GET    | /api/items               | Get all items                                     | User       |
| POST   | /api/items               | List new item                                     | Admin      |
| GET    | /api/items/{id}          | Get info about item with `id`                     | User       |
| PUT    | /api/items/{id}          | Update item with `id`                             | Admin      |
| DELETE | /api/items/{id}          | Remove item with `id`                             | Admin      |
| GET    | /api/items/{id}/variants | Get info about available sizes and colors of item | User       |

### Carts
| Method | Endpoint             | Description                                       | Permissions|
|--------|----------------------|---------------------------------------------------|------------|
| GET    | /api/cart                | Get current user's cart                           | User       |
| POST   | /api/cart/items          | Add item to current user's cart                   | User       |
| PUT    | /api/cart/items/{itemId} | Update the quantity of item with `itemId` in cart | User       |
| DELETE | /api/cart/items/{itemId} | Delete item with `itemId` from current user's cart| User       |

### Reviews
| Method | Endpoint            | Description                                           | Permissions |
|--------|---------------------|-------------------------------------------------------|-------------|
| GET    | /api/items/{id}/reviews | Fetch the reviews for the item with `id`              | User        |
| POST   | /api/items/{id}/reviews | Add a review for the item with `id` from current user | User        |
| PUT    | /api/reviews/{id}       | Update the review with `id`                           | User        |
| DELETE | /api/reviews/{id}       | Remove the review with `id`                           | User/Admin  |

### Favorites
| Method | Endpoint                  | Description                                | Permissions|
|--------|---------------------------|--------------------------------------------|------------|
| GET    | /api/favorites                | Gets the favorite items of the current user| User       |
| POST   | /api/favorites/items          | Add item to favorites                      | User       |
| DELETE | /api/favorites/items/{itemId} | Remove item with `itemId` from favorites   | User       |

### Categories
| Method | Endpoint         | Description               | Permissions|
|--------|------------------|---------------------------|------------|
| GET    | /api/categories      | Get all item categories   | User       |
| POST   | /api/categories      | Add new category          | Admin      |
| PUT    | /api/categories/{id} | Update category with `id` | Admin      |
| DELETE | /api/categories/{id} | Remove category with `id` | Admin      |

### Audiences
| Method | Endpoint        | Description                        | Permissions|
|--------|-----------------|------------------------------------|------------|
| GET    | /api/audiences      | Get all audience categories        | User       |
| POST   | /api/audiences      | Create new audience category       | Admin      |
| PUT    | /api/audiences/{id} | Update audience category with `id` | Admin      |
| DELETE | /api/audiences/{id} | Remove audience category with `id` | Admin      |

### Sizes
| Method | Endpoint    | Description           | Permissions|
|--------|-------------|-----------------------|------------|
| GET    | /api/sizes      | Get all item sizes    | User       |
| POST   | /api/sizes      | Create new size       | Admin      |
| PUT    | /api/sizes/{id} | Update size with `id` | Admin      |
| DELETE | /api/sizes/{id} | Remove size with `id` | Admin      |

### Colors
| Method | Endpoint     | Description            | Permissions|
|--------|--------------|------------------------|------------|
| GET    | /api/colors      | Get all item colors    | User       |
| POST   | /api/colors      | Create new color       | Admin      |
| PUT    | /api/colors/{id} | Update color with `id` | Admin      |
| DELETE | /api/colors/{id} | Remove color with `id` | Admin      |

---

# Query parameters

Collection endpoints return a page object instead of a raw array:

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

Pagination uses one-based page numbers. If omitted, `page` defaults to `1` and `size` defaults to `20`. `size` must be between `1` and `100`.

### GET /api/items
| Parameter        | Type   | Description               | Required |
|------------------|--------|---------------------------|----------|
| category=`cat`   | String | Filter items by category  | No       |
| search=`query`   | String | Filter items by name      | No       |
| itemSize=`size`  | String | Filter items by size      | No       |
| color=`col`      | String | Filter items by color     | No       |
| audience=`aud`   | String | Filter items by audience  | No       |
| pricemin=`price` | Number | Set lower bound for price | No       |
| pricemax=`price` | Number | Set upper bound for price | No       |
| page=`num`       | Number | Get items on one-based page `num` | No |
| size=`num`       | Number | Number of items per page  | No       |

### GET /api/items/{id}/reviews
| Parameter     | Type   | Description                                     | Required |
|---------------|--------|-------------------------------------------------|----------|
| stars=`stars` | Number | Get reviews with stars more or equal to `stars` | No       |
| page=`num`    | Number | Get reviews on one-based page `num`             | No       |
| size=`num`    | Number | Number of reviews per page                      | No       |

### GET /api/favorites
| Parameter      | Type   | Description                | Required |
|----------------|--------|----------------------------|----------|
| search=`query` | String | Filter favorites by name   | No       |
| page=`num`     | Number | Get favorites on one-based page `num` | No |
| size=`num`     | Number | Number of favorites per page | No     |

### GET /api/cart
| Parameter      | Type   | Description                  | Required |
|----------------|--------|------------------------------|----------|
| search=`query` | String | Filter cart items by name    | No       |
| page=`num`     | Number | Get cart items on one-based page `num` | No |
| size=`num`     | Number | Number of cart items per page | No     |

### Other collection endpoints
The following endpoints also support `page` and `size` with the same defaults and limits:

| Endpoint       | Description                 |
|----------------|-----------------------------|
| GET /api/categories| Get paged item categories   |
| GET /api/colors    | Get paged item colors       |
| GET /api/sizes     | Get paged item sizes        |
| GET /api/addresses | Get paged addresses         |
