package com.fashionstore.dto.response;

import com.fashionstore.models.Favorite;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {
    private Long id;
    private Long itemVariantId;
    private Long userId;

    public static FavoriteResponse from(Favorite favorite) {
        FavoriteResponse result = new FavoriteResponse();
        result.id = favorite.getId();
        result.itemVariantId = favorite.getItemVariant().getId();
        result.userId = favorite.getUser().getId();
        return result;
    }
}

