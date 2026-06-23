package com.fashionstore.controllers;

import com.fashionstore.dto.request.SizeRequest;
import com.fashionstore.dto.response.ItemResponse;
import com.fashionstore.dto.response.SizeResponse;
import com.fashionstore.services.SizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sizes")
@RequiredArgsConstructor
public class SizeController {
    private final SizeService sizeService;

    @GetMapping
    public ResponseEntity<List<SizeResponse>> getAllSizes() {
        return ResponseEntity.ok(sizeService.getAllSizes());
    }

    @PostMapping
    public ResponseEntity<SizeResponse> createSize(@RequestBody SizeRequest sizeRequest) {
        return ResponseEntity.ok(sizeService.createSize(sizeRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SizeResponse> updateSize(@PathVariable Long id, @RequestBody SizeRequest sizeRequest){
        return ResponseEntity.ok(sizeService.updateSize(id, sizeRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SizeResponse> deleteSize(@PathVariable Long id){
        sizeService.deleteSize(id);
        return ResponseEntity.noContent().build();
    }
}
