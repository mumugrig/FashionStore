package com.fashionstore.controllers;

import com.fashionstore.dto.request.ColorRequest;
import com.fashionstore.dto.response.ColorResponse;
import com.fashionstore.services.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colors")
@RequiredArgsConstructor
public class ColorController {
    private final ColorService colorService;

    @GetMapping
    public ResponseEntity<List<ColorResponse>> getColors(){
        return ResponseEntity.ok(colorService.getAllColors());
    }

    @PostMapping
    public ResponseEntity<ColorResponse> createColor(@RequestBody ColorRequest colorRequest){
        return ResponseEntity.ok(colorService.createColor(colorRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColorResponse> updateColor(@PathVariable Long id, @RequestBody ColorRequest colorRequest){
        return ResponseEntity.ok(colorService.updateColor(id, colorRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColor(@PathVariable Long id){
        colorService.deleteColor(id);
        return ResponseEntity.noContent().build();
    }
}
