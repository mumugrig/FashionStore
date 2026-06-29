package com.fashionstore.services;

import com.fashionstore.exceptions.NotFoundException;
import com.fashionstore.exceptions.ConflictException;
import com.fashionstore.models.Size;
import com.fashionstore.dto.request.SizeRequest;
import com.fashionstore.dto.response.SizeResponse;
import com.fashionstore.dto.response.PageResponse;
import com.fashionstore.repositories.ItemVariantRepository;
import com.fashionstore.repositories.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SizeService {
    private final SizeRepository sizeRepository;
    private final ItemVariantRepository itemVariantRepository;

    @Transactional
    public SizeResponse createSize(SizeRequest sizeRequest) {
        Size size = new Size();
        size.setLabel(sizeRequest.getLabel());
        size.setSizeSystem(sizeRequest.getSizeSystem());

        Size savedSize = sizeRepository.save(size);
        return SizeResponse.from(savedSize);
    }

    @Transactional
    public SizeResponse updateSize(Long id, SizeRequest sizeRequest) {
        Optional<Size> sizeOptional = sizeRepository.findById(id);
        if (sizeOptional.isPresent()) {
            Size size = sizeOptional.get();
            size.setLabel(sizeRequest.getLabel());
            size.setSizeSystem(sizeRequest.getSizeSystem());

            Size updatedSize = sizeRepository.save(size);
            return SizeResponse.from(updatedSize);
        }
        throw new NotFoundException("Size", id);
    }

    @Transactional(readOnly = true)
    public SizeResponse getSizeById(Long id) {
        Optional<Size> sizeOptional = sizeRepository.findById(id);
        return sizeOptional.map(SizeResponse::from).orElseThrow(() -> new NotFoundException("Size", id));
    }

    @Transactional(readOnly = true)
    public PageResponse<SizeResponse> getPagedSizes(int page, int size) {
        return PageResponse.from(sizeRepository.findAll(PageRequestFactory.create(page, size)), SizeResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResponse<SizeResponse> getPagedSizes(int page, int size, String search, String filterColumn, String filterValue) {
        if (!AdminFilterSpecification.hasFilters(search, filterColumn, filterValue)) {
            return getPagedSizes(page, size);
        }
        return PageResponse.from(sizeRepository.findAll(
                AdminFilterSpecification.create(AdminSearchFields.SIZES, search, filterColumn, filterValue),
                PageRequestFactory.create(page, size)
        ), SizeResponse::from);
    }

    @Transactional
    public void deleteSize(Long id) {
        if (!sizeRepository.existsById(id)) {
            throw new NotFoundException("Size", id);
        }
        if (itemVariantRepository.existsBySizeId(id)) {
            throw new ConflictException("Cannot delete size because item variants use it. Reassign or delete them first.");
        }
        sizeRepository.deleteById(id);
    }

    @Transactional
    public void deleteSizes(List<Long> ids) {
        ids.forEach(this::deleteSize);
    }
}

