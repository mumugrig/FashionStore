package com.fashionstore.dto.response;

import com.fashionstore.models.User;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    public static UserResponse from(User user){
        UserResponse result = new UserResponse();
        result.id = user.getId();
        result.firstName = user.getFirstName();
        result.lastName = user.getLastName();
        result.email = user.getEmail();
        result.phoneNumber = user.getPhoneNumber();
        return result;
    }
}

