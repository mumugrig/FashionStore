package com.fashionstore.services;

import com.fashionstore.exceptions.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PageRequestFactoryTest {

    @Test
    void create_whenApiPageIsOneBased_returnsZeroBasedSpringPageRequest() {
        var pageable = PageRequestFactory.create(1, 20);

        assertEquals(0, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
        assertEquals("id: ASC", pageable.getSort().toString());
    }

    @Test
    void create_whenPageOrSizeIsInvalid_throwsValidationException() {
        assertThrows(ValidationException.class, () -> PageRequestFactory.create(0, 20));
        assertThrows(ValidationException.class, () -> PageRequestFactory.create(1, 0));
        assertThrows(ValidationException.class, () -> PageRequestFactory.create(1, 101));
    }
}
