package com.example.demo.domain.ports.out;

import com.example.demo.domain.model.User;

public interface UserRepositoryPort {
    User save(User user);

    User findById(Long id);
}
