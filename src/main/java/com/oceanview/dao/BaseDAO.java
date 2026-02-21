package com.oceanview.dao;

import com.oceanview.exception.DAOException;
import java.util.List;
import java.util.Optional;

/**
 * Generic DAO interface defining CRUD operations
 * Implements DAO Design Pattern
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public interface BaseDAO<T, ID> {
    /**
     * Create new entity
     * @param entity Entity to create
     * @return Created entity with generated ID
     * @throws DAOException if creation fails
     */
    T create(T entity) throws DAOException;
    
    /**
     * Find entity by primary key
     * @param id Primary key
     * @return Optional containing entity if found
     * @throws DAOException if query fails
     */
    Optional<T> findById(ID id) throws DAOException;
    
    /**
     * Find all entities
     * @return List of all entities
     * @throws DAOException if query fails
     */
    List<T> findAll() throws DAOException;
    
    /**
     * Update existing entity
     * @param entity Entity to update
     * @return true if update successful
     * @throws DAOException if update fails
     */
    boolean update(T entity) throws DAOException;
    
    /**
     * Delete entity by primary key
     * @param id Primary key
     * @return true if deletion successful
     * @throws DAOException if deletion fails
     */
    boolean delete(ID id) throws DAOException;
    
    /**
     * Count total entities
     * @return Total count
     * @throws DAOException if query fails
     */
    long count() throws DAOException;
}
