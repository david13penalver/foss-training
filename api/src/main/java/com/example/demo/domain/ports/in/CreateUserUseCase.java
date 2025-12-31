package com.example.demo.domain.ports.in;

import com.example.demo.domain.model.User;

public interface CreateUserUseCase {
    User execute(User user);
}
