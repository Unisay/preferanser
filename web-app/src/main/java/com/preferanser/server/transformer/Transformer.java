package com.preferanser.server.transformer;

import com.preferanser.server.entity.Entity;

import java.util.List;

public interface Transformer<T, E extends Entity> {

    E toEntity(T type);

    List<E> toEntities(List<T> types);

    T fromEntity(E entity);

    List<T> fromEntities(List<E> entities);

}
