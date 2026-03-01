package com.devflow.user.service;

import com.devflow.user.dto.UserRegistrationRequest;
import com.devflow.user.dto.UserResponse;

public interface UserService {

    UserResponse registerUser(UserRegistrationRequest request);

}