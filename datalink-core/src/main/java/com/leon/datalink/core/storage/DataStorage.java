

package com.leon.datalink.core.storage;

import java.util.List;

/**
 * Universal storage interface.
 *
 * @author Leon
 */
public interface DataStorage<T> {

    T get(String key);

    void put(String key, T value);

    void delete(String key);

    List<String> getKeys();

    List<T> getValues();

    long count();

}
