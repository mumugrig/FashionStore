package com.fashionstore.controllers;

import com.fashionstore.dto.request.ColorRequest;
import com.fashionstore.dto.response.ColorResponse;
import com.fashionstore.services.ColorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colors")
@RequiredArgsConstructor
public class ColorController {
    private final ColorService colorService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ColorResponse>> getColors(){
        return ResponseEntity.ok(colorService.getAllColors());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ColorResponse> createColor(@Valid @RequestBody ColorRequest colorRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(colorService.createColor(colorRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ColorResponse> updateColor(@PathVariable Long id, @Valid @RequestBody ColorRequest colorRequest){
        return ResponseEntity.ok(colorService.updateColor(id, colorRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteColor(@PathVariable Long id){
        colorService.deleteColor(id);
        return ResponseEntity.noContent().build();
    }
}
