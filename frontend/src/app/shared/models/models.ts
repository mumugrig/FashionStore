export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  accessTokenExpiresAt: string;
  user: User;
}

export interface Item {
  id: number;
  name: string;
  price: number;
  description: string;
  imageUrl: string | null;
  audience: string;
  categoryId: number;
  variants: ItemVariant[];
}

export interface ItemVariant {
  id: number;
  active: boolean;
  isActive?: boolean;
  stockLeft: number;
  imageUrl: string | null;
  itemId: number;
  sizeId: number;
  colorId: number;
}

export interface Category {
  id: number;
  name: string;
  parentId: number | null;
}

export interface Color {
  id: number;
  name: string;
  value: string;
  imageUrl: string | null;
}

export interface Size {
  id: number;
  label: string;
  sizeSystem: string;
}

export interface Address {
  id: number;
  country: string;
  region: string;
  city: string;
  postalCode: number;
  addressLine: string;
  userId: number;
}

export interface CartItem {
  id: number;
  quantity: number;
  itemVariantId: number;
  userId: number;
}

export interface Favorite {
  id: number;
  itemVariantId: number;
  userId: number;
}

export interface Review {
  id: number;
  body: string;
  sizeFit: string;
  quality: string;
  comfort: string;
  userId: number;
  itemVariantId: number;
}

export type ApiCollection<T> = PageResponse<T> | T[];
