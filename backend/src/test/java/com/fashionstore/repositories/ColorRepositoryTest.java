package com.fashionstore.repositories;

import com.fashionstore.models.Color;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ColorRepositoryTest {

    @Autowired
    private ColorRepository colorRepository;

    @Test
    void testSaveColor() {
        Color color = new Color();
        color.setName("Blue");
        color.setValue("#0000FF");
        color.setImageUrl("https://example.com/blue.png");

        Color savedColor = colorRepository.save(color);

        assertNotNull(savedColor.getId());
        assertEquals("Blue", savedColor.getName());
        assertEquals("#0000FF", savedColor.getValue());
    }

    @Test
    void testFindColorById() {
        Color color = new Color();
        color.setName("Red");
        color.setValue("#FF0000");
        Color savedColor = colorRepository.save(color);

        Color foundColor = colorRepository.findById(savedColor.getId()).orElse(null);

        assertNotNull(foundColor);
        assertEquals("Red", foundColor.getName());
    }

    @Test
    void testDeleteColor() {
        Color color = new Color();
        color.setName("Green");
        color.setValue("#00FF00");
        Color savedColor = colorRepository.save(color);

        colorRepository.deleteById(savedColor.getId());

        assertFalse(colorRepository.existsById(savedColor.getId()));
    }

    @Test
    void testCountColors() {
        Color color1 = new Color();
        color1.setName("Color1");
        color1.setValue("#111111");

        Color color2 = new Color();
        color2.setName("Color2");
        color2.setValue("#222222");

        colorRepository.save(color1);
        colorRepository.save(color2);

        long count = colorRepository.count();
        assertTrue(count >= 2);
    }

    @Test
    void testFindAllColors() {
        Color color1 = new Color();
        color1.setName("Yellow");
        colorRepository.save(color1);

        Color color2 = new Color();
        color2.setName("Purple");
        colorRepository.save(color2);

        long count = colorRepository.findAll().size();
        assertTrue(count >= 2);
    }
}




