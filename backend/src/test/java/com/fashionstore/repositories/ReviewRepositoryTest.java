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
import com.fashionstore.vo.SizeSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        item.setPrice(BigDecimal.valueOf(49.99f));
        item.setDescription("Review item description");
        item.setAudience(Audience.UNISEX);
        item.setCategory(category);
        itemRepository.save(item);

        Color color = new Color();
        color.setName("Gray");
        color.setValue("#808080");
        colorRepository.save(color);

        Size size = new Size();
        size.setLabel("M");
        size.setSizeSystem(SizeSystem.ALPHA);
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
    void save_whenReviewIsValid_persistsReview() {
        Review review = new Review();
        review.setBody("Great product!");
        review.setSizeFit(SizeFit.TRUE_TO_SIZE);
        review.setQuality(Quality.EXCELLENT);
        review.setComfort(Comfort.VERY_COMFORTABLE);
        review.setUser(user);
        review.setItem(itemVariant.getItem());

        Review savedReview = reviewRepository.save(review);

        assertNotNull(savedReview.getId());
        assertEquals("Great product!", savedReview.getBody());
        assertEquals(SizeFit.TRUE_TO_SIZE, savedReview.getSizeFit());
    }

    @Test
    void findById_whenReviewExists_returnsReview() {
        Review review = new Review();
        review.setBody("Good quality");
        review.setSizeFit(SizeFit.RUNS_SMALL);
        review.setQuality(Quality.AVERAGE);
        review.setComfort(Comfort.COMFORTABLE);
        review.setUser(user);
        review.setItem(itemVariant.getItem());
        Review savedReview = reviewRepository.save(review);

        Review foundReview = reviewRepository.findById(savedReview.getId()).orElse(null);

        assertNotNull(foundReview);
        assertEquals("Good quality", foundReview.getBody());
    }

    @Test
    void findByItemId_whenReviewsExist_returnsItemReviews() {
        Review review1 = new Review();
        review1.setBody("Review 1");
        review1.setSizeFit(SizeFit.TRUE_TO_SIZE);
        review1.setQuality(Quality.EXCELLENT);
        review1.setComfort(Comfort.VERY_COMFORTABLE);
        review1.setUser(user);
        review1.setItem(itemVariant.getItem());
        reviewRepository.save(review1);

        Review review2 = new Review();
        review2.setBody("Review 2");
        review2.setSizeFit(SizeFit.RUNS_SMALL);
        review2.setQuality(Quality.AVERAGE);
        review2.setComfort(Comfort.COMFORTABLE);
        review2.setUser(user);
        review2.setItem(itemVariant.getItem());
        reviewRepository.save(review2);

        Review review3 = new Review();
        review3.setBody("Review 3");
        review3.setSizeFit(SizeFit.TRUE_TO_SIZE);
        review3.setQuality(Quality.EXCELLENT);
        review3.setComfort(Comfort.VERY_COMFORTABLE);
        review3.setUser(user);
        review3.setItem(itemVariant.getItem());
        reviewRepository.save(review3);

        List<Review> itemReviews = reviewRepository.findByItemId(itemVariant.getItem().getId());

        assertEquals(3, itemReviews.size());
        assertEquals(3, reviewRepository.findByItemId(itemVariant.getItem().getId(), PageRequest.of(0, 20)).getTotalElements());
        assertTrue(itemReviews.stream().allMatch(r -> r.getItem().getId().equals(itemVariant.getItem().getId())));
        assertTrue(reviewRepository.existsByItemId(itemVariant.getItem().getId()));
    }

    @Test
    void findByItemId_whenReviewsAreMissing_returnsEmptyList() {
        Item emptyItem = new Item();
        emptyItem.setName("Empty Review Item");
        emptyItem.setPrice(BigDecimal.valueOf(39.99));
        emptyItem.setDescription("Item without reviews");
        emptyItem.setAudience(Audience.UNISEX);
        emptyItem.setCategory(itemVariant.getItem().getCategory());
        itemRepository.save(emptyItem);

        List<Review> reviews = reviewRepository.findByItemId(emptyItem.getId());

        assertEquals(0, reviews.size());
    }

    @Test
    void deleteById_whenReviewExists_removesReview() {
        Review review = new Review();
        review.setBody("To Delete");
        review.setSizeFit(SizeFit.TRUE_TO_SIZE);
        review.setQuality(Quality.EXCELLENT);
        review.setComfort(Comfort.VERY_COMFORTABLE);
        review.setUser(user);
        review.setItem(itemVariant.getItem());
        Review savedReview = reviewRepository.save(review);

        reviewRepository.deleteById(savedReview.getId());

        assertFalse(reviewRepository.existsById(savedReview.getId()));
    }

    @Test
    void deleteByUserId_whenUserHasRecords_removesUserRecords() {
        Review review = new Review();
        review.setBody("To Delete By User");
        review.setSizeFit(SizeFit.TRUE_TO_SIZE);
        review.setQuality(Quality.EXCELLENT);
        review.setComfort(Comfort.VERY_COMFORTABLE);
        review.setUser(user);
        review.setItem(itemVariant.getItem());
        reviewRepository.save(review);

        reviewRepository.deleteByUserId(user.getId());

        assertTrue(reviewRepository.findAll().stream().noneMatch(r -> r.getUser().getId().equals(user.getId())));
    }

    @Test
    void count_whenReviewsExist_returnsReviewCount() {
        Review review1 = new Review();
        review1.setBody("First");
        review1.setSizeFit(SizeFit.TRUE_TO_SIZE);
        review1.setQuality(Quality.EXCELLENT);
        review1.setComfort(Comfort.VERY_COMFORTABLE);
        review1.setUser(user);
        review1.setItem(itemVariant.getItem());

        Review review2 = new Review();
        review2.setBody("Second");
        review2.setSizeFit(SizeFit.RUNS_SMALL);
        review2.setQuality(Quality.AVERAGE);
        review2.setComfort(Comfort.COMFORTABLE);
        review2.setUser(user);
        review2.setItem(itemVariant.getItem());

        reviewRepository.save(review1);
        reviewRepository.save(review2);

        long count = reviewRepository.count();
        assertTrue(count >= 2);
    }
}







