package com.codebloom.cineman.service.impl;

import java.util.List;

public interface CRUDService<T, KeyType> {

    /**
     * Hàm tạo mới một thực thể trong database
     * @param entity: dữ liệu của một thực thể
     * @return thực thể vừa được tạo
     */
     T create(T entity);

     T update(T entity, KeyType key);

     void delete(KeyType key);

      T findById(KeyType key);

     List<T> findAll();

}
