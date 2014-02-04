package com.preferanser.server.transformer;

import com.preferanser.server.entity.Entity;

import java.util.List;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;

public abstract class BaseTransformer<T, E extends Entity> implements Transformer<T, E> {

    @Override public List<T> fromEntities(List<E> entities) {
        List<T> types = newArrayListWithExpectedSize(entities.size());
        for (E entity : entities) {
            types.add(fromEntity(entity));
        }
        return types;
    }

    @Override public List<E> toEntities(List<T> types) {
        List<E> entities = newArrayListWithExpectedSize(types.size());
        for (T type : types) {
            entities.add(toEntity(type));
        }
        return entities;
    }

}
