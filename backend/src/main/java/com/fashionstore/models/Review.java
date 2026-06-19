package com.fashionstore.models;

import com.fashionstore.vo.Comfort;
import com.fashionstore.vo.Quality;
import com.fashionstore.vo.SizeFit;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reviews")
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String body;

    @Enumerated(value = EnumType.STRING)
    private SizeFit sizeFit;

    @Enumerated(value = EnumType.STRING)
    private Quality quality;

    @Enumerated(value = EnumType.STRING)
    private Comfort comfort;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_variant_id")
    private ItemVariant itemVariant;
}
