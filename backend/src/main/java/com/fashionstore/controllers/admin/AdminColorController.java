package com.fashionstore.controllers.admin;

import com.fashionstore.dto.request.ColorRequest;
import com.fashionstore.dto.response.ColorResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.services.ColorService;
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
@RequestMapping("/api/admin/colors")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminColorController {
    private final ColorService colorService;

    @GetMapping
    public ResponseEntity<PageResponse<ColorResponse>> getPagedColors(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(colorService.getPagedColors(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColorResponse> getColorById(@PathVariable Long id) {
        return ResponseEntity.ok(colorService.getColorById(id));
    }

    @PostMapping
    public ResponseEntity<ColorResponse> createColor(@Valid @RequestBody ColorRequest colorRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(colorService.createColor(colorRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColorResponse> updateColor(@PathVariable Long id, @Valid @RequestBody ColorRequest colorRequest) {
        return ResponseEntity.ok(colorService.updateColor(id, colorRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColor(@PathVariable Long id) {
        colorService.deleteColor(id);
        return ResponseEntity.noContent().build();
    }
}
