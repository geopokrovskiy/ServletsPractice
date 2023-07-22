package com.geopokrovskiy.repository;

import java.util.List;

public interface GenericRepository <T, ID>{
    T addNew(T value);
    T getById(ID id);
    List<T> getAll();
    T update(T value);
    boolean delete(ID id);

}
