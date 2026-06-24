package com.fashionstore.controllers;

import com.fashionstore.dto.request.AddressRequest;
import com.fashionstore.dto.response.AddressResponse;
import com.fashionstore.services.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AddressResponse>> getAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Long id) {
        AddressResponse addressResponse = addressService.getAddressById(id);
        return ResponseEntity.ok(addressResponse);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AddressResponse> createAddress(@Valid @RequestBody AddressRequest addressRequest) {
        AddressResponse addressResponse = addressService.addAddress(addressRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressRequest addressRequest) {
        AddressResponse addressResponse = addressService.updateAddress(id, addressRequest);
        return ResponseEntity.ok(addressResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id){
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
