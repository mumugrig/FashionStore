package com.fashionstore.dto.response;

import com.fashionstore.models.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AdminUserResponse extends UserResponse {
    private String role;
    private List<Long> addressIds;

    public AdminUserResponse(UserResponse base) {
        setId(base.getId());
        setFirstName(base.getFirstName());
        setLastName(base.getLastName());
        setEmail(base.getEmail());
        setPhoneNumber(base.getPhoneNumber());
    }

    public static AdminUserResponse from(User user) {
        AdminUserResponse result = new AdminUserResponse(UserResponse.from(user));
        result.role = user.getRole().name();
        result.addressIds = user.getAddresses().stream().map(address -> address.getId()).toList();
        return result;
    }
}
