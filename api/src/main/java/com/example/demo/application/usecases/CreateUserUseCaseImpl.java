package com.example.demo.application.usecases;

import com.example.demo.domain.model.User;
import com.example.demo.domain.ports.in.CreateUserUseCase;
import com.example.demo.domain.ports.out.UserRepositoryPort;
import com.example.demo.domain.services.UserValidationService;
import org.springframework.stereotype.Service;

@Service
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserValidationService validationService;
    private final UserRepositoryPort userRepositoryPort;

    public CreateUserUseCaseImpl(UserValidationService validationService, UserRepositoryPort userRepositoryPort) {
        this.validationService = validationService;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User execute(User user) {
        // 1. Validate user
        validationService.validate(user);

        // 2. Save user
        return userRepositoryPort.save(user);
    }
}
