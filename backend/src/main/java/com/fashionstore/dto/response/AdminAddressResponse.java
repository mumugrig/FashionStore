package com.fashionstore.dto.response;

import com.fashionstore.models.Address;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AdminAddressResponse extends AddressResponse {
    private List<UserSummary> users;
    private List<String> userNames;
    private List<String> userEmails;
    private List<String> userPhoneNumbers;

    public AdminAddressResponse(AddressResponse base) {
        setId(base.getId());
        setCountry(base.getCountry());
        setRegion(base.getRegion());
        setCity(base.getCity());
        setPostalCode(base.getPostalCode());
        setAddressLine(base.getAddressLine());
        setUserIds(base.getUserIds());
    }

    public static AdminAddressResponse from(Address address) {
        AdminAddressResponse result = new AdminAddressResponse(AddressResponse.from(address));
        result.users = address.getUsers().stream()
                .map(user -> new UserSummary(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        fullName(user.getFirstName(), user.getLastName()),
                        user.getEmail(),
                        user.getPhoneNumber()))
                .toList();
        result.userNames = result.users.stream().map(UserSummary::getName).toList();
        result.userEmails = result.users.stream().map(UserSummary::getEmail).toList();
        result.userPhoneNumbers = result.users.stream().map(UserSummary::getPhoneNumber).toList();
        return result;
    }

    private static String fullName(String firstName, String lastName) {
        return ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
    }

    @Getter
    @Setter
    public static class UserSummary {
        private Long id;
        private String firstName;
        private String lastName;
        private String name;
        private String email;
        private String phoneNumber;

        public UserSummary(Long id, String firstName, String lastName, String name, String email, String phoneNumber) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
        }
    }
}
