package io.landal.test.model;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PostPersist;

@ApplicationScoped
public class TestListener {

    @Inject
    private TestService testService;

    @PostPersist
    public void onPostPersist(TestEntity entity) {
        testService.addTestEntityName(entity.name);
    }
}
