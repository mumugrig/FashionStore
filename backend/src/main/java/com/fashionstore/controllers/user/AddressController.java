package com.fashionstore.controllers.user;

import com.fashionstore.dto.request.AddressRequest;
import com.fashionstore.dto.response.AddressResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.services.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Addresses", description = "Current user's address book")
public class AddressController {
    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "Get current user's paged addresses")
    public ResponseEntity<PageResponse<AddressResponse>> getPagedAddresses(
            Authentication authentication,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(addressService.getPagedAddresses(authentication, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get current user's address by id")
    public ResponseEntity<AddressResponse> getAddressById(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(addressService.getAddressById(authentication, id));
    }

    @PostMapping
    @Operation(summary = "Create address for current user")
    public ResponseEntity<AddressResponse> createAddress(Authentication authentication, @Valid @RequestBody AddressRequest addressRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.addAddress(authentication, addressRequest));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update current user's address")
    public ResponseEntity<AddressResponse> updateAddress(Authentication authentication, @PathVariable Long id, @Valid @RequestBody AddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.updateAddress(authentication, id, addressRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete current user's address")
    public ResponseEntity<Void> deleteAddress(Authentication authentication, @PathVariable Long id) {
        addressService.deleteAddress(authentication, id);
        return ResponseEntity.noContent().build();
    }
}
