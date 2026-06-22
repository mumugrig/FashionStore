package com.fashionstore.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    private String country;
    private String region;
    private String city;
    private int postalCode;
    private String addressLine;
    private Long userId;
}

