package com.fashionstore.controllers;

import com.fashionstore.dto.request.SizeRequest;
import com.fashionstore.dto.response.SizeResponse;
import com.fashionstore.services.SizeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
public class SizeController {
    private final SizeService sizeService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SizeResponse>> getAllSizes() {
        return ResponseEntity.ok(sizeService.getAllSizes());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SizeResponse> createSize(@Valid @RequestBody SizeRequest sizeRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sizeService.createSize(sizeRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SizeResponse> updateSize(@PathVariable Long id, @Valid @RequestBody SizeRequest sizeRequest){
        return ResponseEntity.ok(sizeService.updateSize(id, sizeRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSize(@PathVariable Long id){
        sizeService.deleteSize(id);
        return ResponseEntity.noContent().build();
    }
}
