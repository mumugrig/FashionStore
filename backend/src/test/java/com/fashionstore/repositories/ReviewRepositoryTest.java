package com.fashionstore.repositories;

import com.fashionstore.models.Category;
import com.fashionstore.models.Color;
import com.fashionstore.models.Item;
import com.fashionstore.models.ItemVariant;
import com.fashionstore.models.Review;
import com.fashionstore.models.Size;
import com.fashionstore.models.User;
import com.fashionstore.vo.Audience;
import com.fashionstore.vo.Comfort;
import com.fashionstore.vo.Quality;
import com.fashionstore.vo.SizeFit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemVariantRepository itemVariantRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    private User user;
    private ItemVariant itemVariant;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("Reviewer");
        user.setLastName("Expert");
        user.setEmail("reviewer@example.com");
        user.setPasswordHash("hashedPassword");
        userRepository.save(user);

        Category category = new Category();
        category.setName("Reviews");
        categoryRepository.save(category);

        Item item = new Item();
        item.setName("Review Item");
        item.setPrice(49.99f);
        item.setAudience(Audience.UNISEX);
        item.setCategory(category);
        itemRepository.save(item);

        Color color = new Color();
        color.setName("Gray");
        colorRepository.save(color);

        Size size = new Size();
        sizeRepository.save(size);

        itemVariant = new ItemVariant();
        itemVariant.setItem(item);
        itemVariant.setColor(color);
        itemVariant.setSize(size);
        itemVariant.setActive(true);
        itemVariant.setStockLeft(20);
        itemVariantRepository.save(itemVariant);
    }

    @Test
    void testSaveReview() {
        Review review = new Review();
        review.setBody("Great product!");
        review.setSizeFit(SizeFit.TRUE_TO_SIZE);
        review.setQuality(Quality.EXCELLENT);
        review.setComfort(Comfort.VERY_COMFORTABLE);
        review.setUser(user);
        review.setItemVariant(itemVariant);

        Review savedReview = reviewRepository.save(review);

        assertNotNull(savedReview.getId());
        assertEquals("Great product!", savedReview.getBody());
        assertEquals(SizeFit.TRUE_TO_SIZE, savedReview.getSizeFit());
    }

    @Test
    void testFindReviewById() {
        Review review = new Review();
        review.setBody("Good quality");
        review.setSizeFit(SizeFit.RUNS_SMALL);
        review.setQuality(Quality.AVERAGE);
        review.setComfort(Comfort.COMFORTABLE);
        review.setUser(user);
        review.setItemVariant(itemVariant);
        Review savedReview = reviewRepository.save(review);

        Review foundReview = reviewRepository.findById(savedReview.getId()).orElse(null);

        assertNotNull(foundReview);
        assertEquals("Good quality", foundReview.getBody());
    }

    @Test
    void testFindByItemVariantId() {
        ItemVariant itemVariant2 = new ItemVariant();
        itemVariant2.setItem(itemVariant.getItem());
        itemVariant2.setColor(itemVariant.getColor());
        itemVariant2.setSize(itemVariant.getSize());
        itemVariant2.setActive(true);
        itemVariant2.setStockLeft(30);
        itemVariantRepository.save(itemVariant2);

        Review review1 = new Review();
        review1.setBody("Review 1");
        review1.setSizeFit(SizeFit.TRUE_TO_SIZE);
        review1.setQuality(Quality.EXCELLENT);
        review1.setComfort(Comfort.VERY_COMFORTABLE);
        review1.setUser(user);
        review1.setItemVariant(itemVariant);
        reviewRepository.save(review1);

        Review review2 = new Review();
        review2.setBody("Review 2");
        review2.setSizeFit(SizeFit.RUNS_SMALL);
        review2.setQuality(Quality.AVERAGE);
        review2.setComfort(Comfort.COMFORTABLE);
        review2.setUser(user);
        review2.setItemVariant(itemVariant);
        reviewRepository.save(review2);

        Review review3 = new Review();
        review3.setBody("Review 3");
        review3.setSizeFit(SizeFit.TRUE_TO_SIZE);
        review3.setQuality(Quality.EXCELLENT);
        review3.setComfort(Comfort.VERY_COMFORTABLE);
        review3.setUser(user);
        review3.setItemVariant(itemVariant2);
        reviewRepository.save(review3);

        List<Review> variantReviews = reviewRepository.findByItemVariantId(itemVariant.getId());

        assertEquals(2, variantReviews.size());
        assertTrue(variantReviews.stream().allMatch(r -> r.getItemVariant().getId().equals(itemVariant.getId())));
    }

    @Test
    void testFindByItemVariantIdEmpty() {
        ItemVariant emptyVariant = new ItemVariant();
        emptyVariant.setItem(itemVariant.getItem());
        emptyVariant.setColor(itemVariant.getColor());
        emptyVariant.setSize(itemVariant.getSize());
        emptyVariant.setActive(true);
        emptyVariant.setStockLeft(10);
        itemVariantRepository.save(emptyVariant);

        List<Review> reviews = reviewRepository.findByItemVariantId(emptyVariant.getId());

        assertEquals(0, reviews.size());
    }

    @Test
    void testDeleteReview() {
        Review review = new Review();
        review.setBody("To Delete");
        review.setSizeFit(SizeFit.TRUE_TO_SIZE);
        review.setQuality(Quality.EXCELLENT);
        review.setComfort(Comfort.VERY_COMFORTABLE);
        review.setUser(user);
        review.setItemVariant(itemVariant);
        Review savedReview = reviewRepository.save(review);

        reviewRepository.deleteById(savedReview.getId());

        assertFalse(reviewRepository.existsById(savedReview.getId()));
    }

    @Test
    void testCountReviews() {
        Review review1 = new Review();
        review1.setBody("First");
        review1.setSizeFit(SizeFit.TRUE_TO_SIZE);
        review1.setQuality(Quality.EXCELLENT);
        review1.setComfort(Comfort.VERY_COMFORTABLE);
        review1.setUser(user);
        review1.setItemVariant(itemVariant);

        Review review2 = new Review();
        review2.setBody("Second");
        review2.setSizeFit(SizeFit.RUNS_SMALL);
        review2.setQuality(Quality.AVERAGE);
        review2.setComfort(Comfort.COMFORTABLE);
        review2.setUser(user);
        review2.setItemVariant(itemVariant);

        reviewRepository.save(review1);
        reviewRepository.save(review2);

        long count = reviewRepository.count();
        assertTrue(count >= 2);
    }
}







