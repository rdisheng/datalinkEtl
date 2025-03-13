package com.leon.datalink.core.service;

import java.util.List;

public interface BaseService<T> {

    T get(String id);

    void add(T t);

    void update(T t);

    void remove(String id);

    List<T> list(T t);

    long getCount();

}
