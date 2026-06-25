package com.fashionstore.repositories;

import com.fashionstore.models.Size;
import com.fashionstore.vo.SizeSystem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SizeRepositoryTest {

    @Autowired
    private SizeRepository sizeRepository;

    @Test
    void save_whenSizeIsValid_persistsSize() {
        Size size = new Size();
        size.setLabel("M");
        size.setSizeSystem(SizeSystem.US);

        Size savedSize = sizeRepository.save(size);

        assertNotNull(savedSize.getId());
        assertEquals("M", savedSize.getLabel());
        assertEquals(SizeSystem.US, savedSize.getSizeSystem());
    }

    @Test
    void findById_whenSizeExists_returnsSize() {
        Size size = new Size();
        size.setLabel("L");
        size.setSizeSystem(SizeSystem.US);
        Size savedSize = sizeRepository.save(size);

        Size foundSize = sizeRepository.findById(savedSize.getId()).orElse(null);

        assertNotNull(foundSize);
        assertEquals("L", foundSize.getLabel());
    }

    @Test
    void deleteById_whenSizeExists_removesSize() {
        Size size = new Size();
        size.setLabel("XL");
        size.setSizeSystem(SizeSystem.EU);
        Size savedSize = sizeRepository.save(size);

        sizeRepository.deleteById(savedSize.getId());

        assertFalse(sizeRepository.existsById(savedSize.getId()));
    }

    @Test
    void count_whenSizesExist_returnsSizeCount() {
        Size size1 = new Size();
        size1.setLabel("S");
        size1.setSizeSystem(SizeSystem.US);

        Size size2 = new Size();
        size2.setLabel("M");
        size2.setSizeSystem(SizeSystem.US);

        sizeRepository.save(size1);
        sizeRepository.save(size2);

        long count = sizeRepository.count();
        assertTrue(count >= 2);
    }

    @Test
    void findAll_whenSizesExist_returnsSizes() {
        Size size1 = new Size();
        size1.setLabel("XS");
        size1.setSizeSystem(SizeSystem.EU);
        sizeRepository.save(size1);

        Size size2 = new Size();
        size2.setLabel("XXL");
        size2.setSizeSystem(SizeSystem.EU);
        sizeRepository.save(size2);

        long count = sizeRepository.findAll().size();
        assertTrue(count >= 2);
    }

    @Test
    void existsById_whenSizeExists_returnsTrue() {
        Size size = new Size();
        size.setLabel("2XL");
        size.setSizeSystem(SizeSystem.US);
        Size savedSize = sizeRepository.save(size);

        assertTrue(sizeRepository.existsById(savedSize.getId()));
        assertFalse(sizeRepository.existsById(99999L));
    }
}




