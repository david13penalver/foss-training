package com.david13penalver.foss_training_api.infrastructure.adapters.out.exercise;

import com.david13penalver.foss_training_api.domain.model.exercise.mobility.Joint;
import com.david13penalver.foss_training_api.domain.ports.out.exercise.JointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JointRepositoryImpl implements JointRepository {
    @Override
    public List<Joint> findAll() {
        return List.of();
    }

    @Override
    public Optional<Joint> findById(String name) {
        return Optional.empty();
    }

    @Override
    public Joint save(Joint joint) {
        return null;
    }

    @Override
    public void deleteById(String name) {

    }

    @Override
    public boolean existsById(String name) {
        return false;
    }
}
