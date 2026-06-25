package com.fashionstore.controllers.user;

import com.fashionstore.dto.response.ColorResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.services.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/colors")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ColorController {
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
}
