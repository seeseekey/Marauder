package net.seeseekey.marauder.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EntityTest {

    // execute core -> build -> testclasses
//    @org.junit.jupiter.api.Test
//    void getRotation() {
//    }
//
    @Test
    void destroy() {
        Entity entity = new Entity(0, 0, 0, 0, 0, 1, 1, 1, 1, 0);
        assertEquals(false, entity.isDestroyed());

        entity.destroy();
        assertEquals(true, entity.isDestroyed());
    }
}