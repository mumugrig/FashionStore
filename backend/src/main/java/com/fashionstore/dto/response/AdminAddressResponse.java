package com.fashionstore.dto.response;

import com.fashionstore.models.Address;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminAddressResponse extends AddressResponse {
    private String userFirstName;
    private String userLastName;
    private String userName;
    private String userEmail;
    private String userPhoneNumber;

    public AdminAddressResponse(AddressResponse base) {
        setId(base.getId());
        setCountry(base.getCountry());
        setRegion(base.getRegion());
        setCity(base.getCity());
        setPostalCode(base.getPostalCode());
        setAddressLine(base.getAddressLine());
        setUserId(base.getUserId());
    }

    public static AdminAddressResponse from(Address address) {
        AdminAddressResponse result = new AdminAddressResponse(AddressResponse.from(address));
        result.userFirstName = address.getUser().getFirstName();
        result.userLastName = address.getUser().getLastName();
        result.userName = fullName(result.userFirstName, result.userLastName);
        result.userEmail = address.getUser().getEmail();
        result.userPhoneNumber = address.getUser().getPhoneNumber();
        return result;
    }

    private static String fullName(String firstName, String lastName) {
        return ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
    }
}
