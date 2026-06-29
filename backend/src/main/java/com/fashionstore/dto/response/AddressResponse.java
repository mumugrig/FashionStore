package com.fashionstore.dto.response;

import com.fashionstore.models.Address;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private Long id;
    private String country;
    private String region;
    private String city;
    private int postalCode;
    private String addressLine;
    private List<Long> userIds;

    public static AddressResponse from(Address address){
        AddressResponse result = new AddressResponse();
        result.id = address.getId();
        result.country = address.getCountry();
        result.region = address.getRegion();
        result.city = address.getCity();
        result.postalCode = address.getPostalCode();
        result.addressLine = address.getAddressLine();
        result.userIds = address.getUsers().stream().map(user -> user.getId()).toList();
        return result;
    }
}

