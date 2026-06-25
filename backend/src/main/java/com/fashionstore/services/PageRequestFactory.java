package com.fashionstore.services;

import com.fashionstore.exceptions.ValidationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageRequestFactory {
    private static final int MAX_PAGE_SIZE = 100;

    private PageRequestFactory() {}

    public static Pageable create(int page, int size) {
        if (page < 1) {
            throw new ValidationException("Page must be greater than or equal to 1");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new ValidationException("Size must be between 1 and " + MAX_PAGE_SIZE);
        }
        return PageRequest.of(page - 1, size, Sort.by("id").ascending());
    }
}
