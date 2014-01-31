/*
 * Preferanser is a program to simulate and calculate Russian Preferans Card game deals.
 *
 *     Copyright (C) 2013  Yuriy Lazarev <Yuriy.Lazarev@gmail.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see [http://www.gnu.org/licenses/].
 */

package com.preferanser.server.dao;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.preferanser.server.dao.objectify.Ofy;
import com.preferanser.server.dao.objectify.OfyFactory;
import com.preferanser.server.exception.ValidationException;
import com.preferanser.shared.domain.entity.BaseEntity;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

public abstract class BaseDao<T extends BaseEntity> {

    private static Logger logger = LoggerFactory.getLogger(BaseDao.class);

    private final Class<T> clazz;
    private OfyFactory ofyFactory;
    private Validator validator;
    private Ofy lazyOfy;

    protected BaseDao(final Class<T> clazz, OfyFactory ofyFactory, Validator validator) {
        this.clazz = clazz;
        this.ofyFactory = ofyFactory;
        this.validator = validator;
    }

    public List<T> getAll() {
        return ofy().query(clazz).list();
    }

    // TODO: save with user as parent
    public T save(T object) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(object);
        if (CollectionUtils.isEmpty(constraintViolations)) {
            ofy().save().entity(object).now();
            return object;
        } else {
            for (ConstraintViolation<T> constraintViolation : constraintViolations)
                logger.warn(constraintViolation.getMessage());
            String message = String.format("Entity of type '%s' failed validation on save()", clazz.getSimpleName());
            throw new ValidationException(message);
        }
    }

    public Collection<T> save(T... entities) {
        return save(newArrayList(entities));
    }

    public Collection<T> save(Iterable<T> entities) {
        return ofy().save().entities(entities).now().values();
    }

    public T get(Key<T> key) {
        return ofy().get(key);
    }

    public T get(Long id) {
        // work around for objectify caching and new query not having the latest data
        ofy().clear();

        return ofy().get(clazz, id);
    }

    public Boolean exists(Key<T> key) {
        return get(key) != null;
    }

    public Boolean exists(Long id) {
        return get(id) != null;
    }

    public List<T> getSubset(List<Long> ids) {
        return new ArrayList<T>(ofy().query(clazz).ids(ids).values());
    }

    public Map<Long, T> getSubsetMap(List<Long> ids) {
        return new HashMap<Long, T>(ofy().query(clazz).ids(ids));
    }

    public void delete(T object) {
        ofy().delete().entity(object);
    }

    public void delete(Long id) {
        Key<T> key = Key.create(clazz, id);
        ofy().delete().entity(key);
    }

    public void delete(List<T> objects) {
        ofy().delete().entities(objects);
    }

    public List<T> get(List<Key<T>> keys) {
        return newArrayList(ofy().load().keys(keys).values());
    }

    protected Ofy ofy() {
        if (lazyOfy == null) {
            lazyOfy = ofyFactory.begin();
        }
        return lazyOfy;
    }

    protected LoadType<T> query() {
        return ofy().query(clazz);
    }
}
