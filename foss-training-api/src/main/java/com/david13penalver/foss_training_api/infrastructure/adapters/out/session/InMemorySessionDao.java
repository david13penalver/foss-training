package com.david13penalver.foss_training_api.infrastructure.adapters.out.session;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import com.david13penalver.foss_training_api.domain.model.session.Session;

@Component
public class InMemorySessionDao {

    private final Map<Integer, Session> store = new LinkedHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(0);

    public synchronized List<Session> findAll() {
        return new ArrayList<>(store.values());
    }

    public synchronized Optional<Session> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id));
    }

    public synchronized Session save(Session session) {
        if (session.getId() == null) {
            session.setId(sequence.incrementAndGet());
        } else {
            sequence.accumulateAndGet(session.getId(), Math::max);
        }
        store.put(session.getId(), session);
        return session;
    }

    public synchronized void deleteById(Integer id) {
        if (id != null) {
            store.remove(id);
        }
    }

    public synchronized boolean existsById(Integer id) {
        return id != null && store.containsKey(id);
    }

    public synchronized void clear() {
        store.clear();
        sequence.set(0);
    }
}