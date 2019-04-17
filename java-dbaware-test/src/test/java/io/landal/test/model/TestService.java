package io.landal.test.model;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@ApplicationScoped
public class TestService {

    private final List<String> names = new ArrayList<>();
    private final EntityManager entityManager;

    @Inject
    public TestService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<String> getTestEntityNames() {
        return names;
    }

    public void addTestEntityName(String name) {
        names.add(name);
    }
}
