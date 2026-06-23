package com.fashionstore.services;

import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.models.Size;
import com.fashionstore.vo.SizeSystem;
import com.fashionstore.dto.request.SizeRequest;
import com.fashionstore.dto.response.SizeResponse;
import com.fashionstore.repositories.SizeRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SizeService {
    private final SizeRepository sizeRepository;

    public SizeService(SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    public SizeResponse createSize(SizeRequest sizeRequest) {
        Size size = new Size();
        size.setLabel(sizeRequest.getLabel());
        size.setSizeSystem(SizeSystem.valueOf(sizeRequest.getSizeSystem()));

        Size savedSize = sizeRepository.save(size);
        return SizeResponse.from(savedSize);
    }

    public SizeResponse updateSize(Long id, SizeRequest sizeRequest) {
        Optional<Size> sizeOptional = sizeRepository.findById(id);
        if (sizeOptional.isPresent()) {
            Size size = sizeOptional.get();
            size.setLabel(sizeRequest.getLabel());
            size.setSizeSystem(SizeSystem.valueOf(sizeRequest.getSizeSystem()));

            Size updatedSize = sizeRepository.save(size);
            return SizeResponse.from(updatedSize);
        }
        throw new NotFoundException("Size", id);
    }

    public SizeResponse getSizeById(Long id) {
        Optional<Size> sizeOptional = sizeRepository.findById(id);
        return sizeOptional.map(SizeResponse::from).orElseThrow(() -> new NotFoundException("Size", id));
    }

    public List<SizeResponse> getAllSizes() {
        return sizeRepository.findAll()
                .stream()
                .map(SizeResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteSize(Long id) {
        sizeRepository.deleteById(id);
    }
}

