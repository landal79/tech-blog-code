package io.landal.test.model;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;

@Entity
@EntityListeners(TestListener.class)
public class TestEntity {

    @Id
    public UUID id;

    public String name;
}
