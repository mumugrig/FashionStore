export type FieldType = 'text' | 'email' | 'password' | 'number' | 'textarea' | 'select' | 'color' | 'checkbox';

export interface AdminField {
  key: string;
  label: string;
  type: FieldType;
  required?: boolean;
  options?: string[];
}

export interface AdminColumn {
  key: string;
  label: string;
  defaultVisible?: boolean;
}

export interface AdminResourceConfig {
  key: string;
  label: string;
  endpoint: string;
  paged: boolean;
  columns: AdminColumn[];
  fields: AdminField[];
  create?: boolean;
  update?: boolean;
  delete?: boolean;
  updateMethod?: 'put' | 'patch';
  deletePath?: (id: number) => string;
  bulkDeletePath?: string;
}

const audience = ['MEN', 'WOMEN', 'KIDS', 'UNISEX'];
const sizeSystems = ['ALPHA', 'US', 'UK', 'EU'];
const sizeFit = ['RUNS_SMALL', 'TRUE_TO_SIZE', 'RUNS_LARGE'];
const quality = ['POOR', 'AVERAGE', 'EXCELLENT'];
const comfort = ['UNCOMFORTABLE', 'COMFORTABLE', 'VERY_COMFORTABLE'];

export const adminResources: AdminResourceConfig[] = [
  {
    key: 'items',
    label: 'Items',
    endpoint: '/admin/items',
    paged: true,
    columns: [
      { key: 'id', label: 'ID' },
      { key: 'name', label: 'Name', defaultVisible: true },
      { key: 'price', label: 'Price', defaultVisible: true },
      { key: 'audience', label: 'Audience', defaultVisible: true },
      { key: 'categoryId', label: 'Category ID' },
      { key: 'categoryName', label: 'Category', defaultVisible: true },
      { key: 'variantCount', label: 'Variants' },
      { key: 'description', label: 'Description' },
      { key: 'imageUrl', label: 'Image URL' }
    ],
    fields: [
      { key: 'name', label: 'Name', type: 'text', required: true },
      { key: 'price', label: 'Price', type: 'number', required: true },
      { key: 'description', label: 'Description', type: 'textarea', required: true },
      { key: 'imageUrl', label: 'Image URL', type: 'text' },
      { key: 'audience', label: 'Audience', type: 'select', required: true, options: audience },
      { key: 'categoryId', label: 'Category reference', type: 'number', required: true }
    ],
    create: true,
    update: true,
    delete: true
  },
  {
    key: 'categories',
    label: 'Categories',
    endpoint: '/admin/categories',
    paged: true,
    columns: [
      { key: 'id', label: 'ID' },
      { key: 'name', label: 'Name', defaultVisible: true },
      { key: 'parentId', label: 'Parent ID' },
      { key: 'parentName', label: 'Parent', defaultVisible: true }
    ],
    fields: [
      { key: 'name', label: 'Name', type: 'text', required: true },
      { key: 'parentId', label: 'Parent category reference', type: 'number' }
    ],
    create: true,
    update: true,
    delete: true
  },
  {
    key: 'colors',
    label: 'Colors',
    endpoint: '/admin/colors',
    paged: true,
    columns: [
      { key: 'id', label: 'ID' },
      { key: 'name', label: 'Name', defaultVisible: true },
      { key: 'value', label: 'Hex value', defaultVisible: true },
      { key: 'imageUrl', label: 'Image URL', defaultVisible: true }
    ],
    fields: [
      { key: 'name', label: 'Name', type: 'text', required: true },
      { key: 'value', label: 'Hex value', type: 'color', required: true },
      { key: 'imageUrl', label: 'Image URL', type: 'text', required: true }
    ],
    create: true,
    update: true,
    delete: true
  },
  {
    key: 'sizes',
    label: 'Sizes',
    endpoint: '/admin/sizes',
    paged: true,
    columns: [
      { key: 'id', label: 'ID' },
      { key: 'label', label: 'Label', defaultVisible: true },
      { key: 'sizeSystem', label: 'System', defaultVisible: true }
    ],
    fields: [
      { key: 'label', label: 'Label', type: 'text', required: true },
      { key: 'sizeSystem', label: 'System', type: 'select', required: true, options: sizeSystems }
    ],
    create: true,
    update: true,
    delete: true
  },
  {
    key: 'users',
    label: 'Users',
    endpoint: '/admin/users',
    paged: true,
    columns: [
      { key: 'id', label: 'ID' },
      { key: 'firstName', label: 'First name', defaultVisible: true },
      { key: 'lastName', label: 'Last name', defaultVisible: true },
      { key: 'email', label: 'Email', defaultVisible: true },
      { key: 'phoneNumber', label: 'Phone', defaultVisible: true },
      { key: 'role', label: 'Role', defaultVisible: true }
    ],
    fields: [
      { key: 'firstName', label: 'First name', type: 'text', required: true },
      { key: 'lastName', label: 'Last name', type: 'text', required: true },
      { key: 'email', label: 'Email', type: 'email', required: true },
      { key: 'phoneNumber', label: 'Phone', type: 'text', required: true },
      { key: 'password', label: 'Password', type: 'password', required: true }
    ],
    update: true,
    delete: true,
    updateMethod: 'patch'
  },
  {
    key: 'reviews',
    label: 'Reviews',
    endpoint: '/admin/reviews',
    paged: true,
    columns: [
      { key: 'id', label: 'ID' },
      { key: 'body', label: 'Body', defaultVisible: true },
      { key: 'userId', label: 'User ID' },
      { key: 'userName', label: 'User', defaultVisible: true },
      { key: 'userEmail', label: 'User email', defaultVisible: true },
      { key: 'itemVariantId', label: 'Variant ID' },
      { key: 'itemId', label: 'Item ID' },
      { key: 'itemName', label: 'Item', defaultVisible: true },
      { key: 'sizeFit', label: 'Size fit' },
      { key: 'quality', label: 'Quality' },
      { key: 'comfort', label: 'Comfort' },
      { key: 'sizeLabel', label: 'Size' },
      { key: 'sizeSystem', label: 'Size system' },
      { key: 'colorName', label: 'Color' },
      { key: 'colorValue', label: 'Color value' },
      { key: 'variantActive', label: 'Variant active' },
      { key: 'variantStockLeft', label: 'Variant stock' },
      { key: 'variantImageUrl', label: 'Variant image' }
    ],
    fields: [
      { key: 'body', label: 'Body', type: 'textarea', required: true },
      { key: 'sizeFit', label: 'Size fit', type: 'select', required: true, options: sizeFit },
      { key: 'quality', label: 'Quality', type: 'select', required: true, options: quality },
      { key: 'comfort', label: 'Comfort', type: 'select', required: true, options: comfort },
      { key: 'itemVariantId', label: 'Product option reference', type: 'number', required: true }
    ],
    update: true,
    delete: true
  },
  {
    key: 'addresses',
    label: 'Addresses',
    endpoint: '/admin/addresses',
    paged: true,
    columns: [
      { key: 'id', label: 'ID' },
      { key: 'country', label: 'Country', defaultVisible: true },
      { key: 'region', label: 'Region', defaultVisible: true },
      { key: 'city', label: 'City', defaultVisible: true },
      { key: 'postalCode', label: 'Postal code', defaultVisible: true },
      { key: 'addressLine', label: 'Address line', defaultVisible: true },
      { key: 'userId', label: 'User ID' },
      { key: 'userName', label: 'User', defaultVisible: true },
      { key: 'userEmail', label: 'User email' },
      { key: 'userPhoneNumber', label: 'User phone' }
    ],
    fields: [
      { key: 'country', label: 'Country', type: 'text', required: true },
      { key: 'region', label: 'Region', type: 'text', required: true },
      { key: 'city', label: 'City', type: 'text', required: true },
      { key: 'postalCode', label: 'Postal code', type: 'number', required: true },
      { key: 'addressLine', label: 'Address line', type: 'text', required: true },
      { key: 'userId', label: 'User reference', type: 'number' }
    ],
    create: true,
    update: true,
    delete: true
  },
  {
    key: 'cart',
    label: 'Cart',
    endpoint: '/admin/cart',
    paged: true,
    columns: [
      { key: 'id', label: 'ID' },
      { key: 'quantity', label: 'Quantity', defaultVisible: true },
      { key: 'userId', label: 'User ID' },
      { key: 'userName', label: 'User', defaultVisible: true },
      { key: 'userEmail', label: 'User email', defaultVisible: true },
      { key: 'itemVariantId', label: 'Variant ID' },
      { key: 'itemId', label: 'Item ID' },
      { key: 'itemName', label: 'Item', defaultVisible: true },
      { key: 'sizeLabel', label: 'Size' },
      { key: 'sizeSystem', label: 'Size system' },
      { key: 'colorName', label: 'Color' },
      { key: 'colorValue', label: 'Color value' },
      { key: 'variantActive', label: 'Variant active' },
      { key: 'variantStockLeft', label: 'Variant stock' },
      { key: 'variantImageUrl', label: 'Variant image' }
    ],
    fields: [
      { key: 'quantity', label: 'Quantity', type: 'number', required: true },
      { key: 'itemVariantId', label: 'Product option reference', type: 'number', required: true },
      { key: 'userId', label: 'User reference', type: 'number' }
    ],
    update: true,
    delete: true,
    deletePath: (id) => `/admin/cart/items/${id}`
  },
  {
    key: 'favorites',
    label: 'Favorites',
    endpoint: '/admin/favorites',
    paged: true,
    columns: [
      { key: 'id', label: 'ID' },
      { key: 'userId', label: 'User ID' },
      { key: 'userName', label: 'User', defaultVisible: true },
      { key: 'userEmail', label: 'User email', defaultVisible: true },
      { key: 'itemVariantId', label: 'Variant ID' },
      { key: 'itemId', label: 'Item ID' },
      { key: 'itemName', label: 'Item', defaultVisible: true },
      { key: 'sizeLabel', label: 'Size', defaultVisible: true },
      { key: 'sizeSystem', label: 'Size system' },
      { key: 'colorName', label: 'Color', defaultVisible: true },
      { key: 'colorValue', label: 'Color value' },
      { key: 'variantActive', label: 'Variant active' },
      { key: 'variantStockLeft', label: 'Variant stock' },
      { key: 'variantImageUrl', label: 'Variant image' }
    ],
    fields: [
      { key: 'itemVariantId', label: 'Product option reference', type: 'number', required: true },
      { key: 'userId', label: 'User reference', type: 'number' }
    ],
    create: true,
    delete: true
  },
  {
    key: 'item-variants',
    label: 'Item Variants',
    endpoint: '/admin/item-variants',
    paged: true,
    columns: [
      { key: 'id', label: 'ID' },
      { key: 'active', label: 'Active', defaultVisible: true },
      { key: 'stockLeft', label: 'Stock', defaultVisible: true },
      { key: 'itemId', label: 'Item ID' },
      { key: 'itemName', label: 'Item', defaultVisible: true },
      { key: 'itemPrice', label: 'Item price' },
      { key: 'itemAudience', label: 'Audience' },
      { key: 'sizeId', label: 'Size ID' },
      { key: 'sizeLabel', label: 'Size', defaultVisible: true },
      { key: 'sizeSystem', label: 'Size system' },
      { key: 'colorId', label: 'Color ID' },
      { key: 'colorName', label: 'Color', defaultVisible: true },
      { key: 'colorValue', label: 'Color value' },
      { key: 'colorImageUrl', label: 'Color image' },
      { key: 'imageUrl', label: 'Variant image' }
    ],
    fields: [
      { key: 'active', label: 'Active', type: 'checkbox' },
      { key: 'stockLeft', label: 'Stock left', type: 'number', required: true },
      { key: 'imageUrl', label: 'Image URL', type: 'text' },
      { key: 'itemId', label: 'Item reference', type: 'number', required: true },
      { key: 'sizeId', label: 'Size reference', type: 'number', required: true },
      { key: 'colorId', label: 'Color reference', type: 'number', required: true }
    ],
    create: true,
    update: true,
    delete: true
  }
];

export function adminResourceByKey(key: string | null): AdminResourceConfig {
  return adminResources.find((resource) => resource.key === key) ?? adminResources[0];
}
