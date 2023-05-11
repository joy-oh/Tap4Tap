package edu.lwtech.csd297.tap4tap.daos;

import java.util.*;

// Generic Data Access Object (DAO) Interface

public interface DAO<T, PK> {

    // Life Cycle ----------------------------------------
    boolean initialize(String initParams);
    void terminate();

    // Create --------------------------------------------
    int insert(T item);

    // Retrieve ------------------------------------------
    // ...one at a time
    T retrieveByID(PK id);
    T retrieveByIndex(int index);
    // T retrieveByName(String name);
    // ...all at once
    List<T> retrieveAll();
    List<PK> retrieveAllIDs();
    // ...some at a time
    // TODO: Change search to work with different fields and/or multiple fields
    List<T> search(String keyword);
    // TODO: Add method for retrieving all that exactly match a field

    // Update ---------------------------------------------
    boolean update(T item);
    
    // Delete ---------------------------------------------
    void delete(PK id);

    // Miscellaneous -------------------------------------
    int size();
}
