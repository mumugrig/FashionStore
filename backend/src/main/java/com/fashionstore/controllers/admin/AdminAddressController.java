package com.fashionstore.controllers.admin;

import com.fashionstore.dto.request.AddressRequest;
import com.fashionstore.dto.request.BulkDeleteRequest;
import com.fashionstore.dto.response.AdminAddressResponse;
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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Addresses", description = "Admin address management")
public class AdminAddressController {
    private final AddressService addressService;

    @GetMapping("/addresses")
    @Operation(summary = "Get paged addresses with admin details")
    public ResponseEntity<PageResponse<AdminAddressResponse>> getPagedAddresses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filterColumn,
            @RequestParam(required = false) String filterValue) {
        return ResponseEntity.ok(addressService.getPagedAdminAddresses(page, size, search, filterColumn, filterValue));
    }

    @GetMapping("/users/{userId}/addresses")
    @Operation(summary = "Get paged addresses for a user")
    public ResponseEntity<PageResponse<AddressResponse>> getPagedAddressesByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(addressService.getPagedAddressesByUserId(userId, page, size));
    }

    @GetMapping("/addresses/{id}")
    @Operation(summary = "Get address by id")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getAddressById(id));
    }

    @PostMapping("/addresses")
    @Operation(summary = "Create address")
    public ResponseEntity<AddressResponse> createAddress(@Valid @RequestBody AddressRequest addressRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.addAddress(addressRequest));
    }

    @PutMapping("/addresses/{id}")
    @Operation(summary = "Update address by id")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.updateAddress(id, addressRequest));
    }

    @DeleteMapping("/addresses/{id}")
    @Operation(summary = "Delete address by id")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/addresses/bulk-delete")
    @Operation(summary = "Delete multiple addresses")
    public ResponseEntity<Void> deleteAddresses(@Valid @RequestBody BulkDeleteRequest request) {
        addressService.deleteAddresses(request.getIds());
        return ResponseEntity.noContent().build();
    }
}
