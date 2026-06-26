export type FieldType = 'text' | 'email' | 'password' | 'number' | 'textarea' | 'select' | 'color' | 'checkbox';

export interface AdminField {
  key: string;
  label: string;
  type: FieldType;
  required?: boolean;
  options?: string[];
}

export interface AdminResourceConfig {
  key: string;
  label: string;
  endpoint: string;
  paged: boolean;
  columns: string[];
  fields: AdminField[];
  create?: boolean;
  update?: boolean;
  delete?: boolean;
  updateMethod?: 'put' | 'patch';
  deletePath?: (id: number) => string;
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
    columns: ['name', 'price', 'audience', 'imageUrl'],
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
    columns: ['name'],
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
    columns: ['name', 'value', 'imageUrl'],
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
    columns: ['label', 'sizeSystem'],
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
    paged: false,
    columns: ['firstName', 'lastName', 'email', 'phoneNumber'],
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
    columns: ['body', 'sizeFit', 'quality', 'comfort'],
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
    columns: ['country', 'region', 'city', 'postalCode', 'addressLine'],
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
    columns: ['quantity'],
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
    columns: [],
    fields: [
      { key: 'itemVariantId', label: 'Product option reference', type: 'number', required: true },
      { key: 'userId', label: 'User reference', type: 'number' }
    ],
    create: true,
    delete: true
  }
];

export function adminResourceByKey(key: string | null): AdminResourceConfig {
  return adminResources.find((resource) => resource.key === key) ?? adminResources[0];
}
