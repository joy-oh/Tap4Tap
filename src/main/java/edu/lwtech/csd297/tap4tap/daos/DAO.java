package edu.lwtech.csd297.tap4tap.daos;

import java.util.*;

import edu.lwtech.csd297.tap4tap.pojos.SearchParameter;

// Generic Data Access Object (DAO) Interface

public interface DAO<T, K> {

    // Life Cycle ----------------------------------------
    boolean initialize(String initParams);

    void terminate();

    // Create --------------------------------------------
    int insert(T item);

    // Retrieve ------------------------------------------
    // ...one at a time
    T retrieveByID(K id);

    T retrieveByIndex(int index);

    // ...all at once
    List<T> retrieveAll();

    List<K> retrieveAllIDs();

    // ...some at a time
    List<T> search(SearchParameter[] params);

    // Update ---------------------------------------------
    boolean update(T item);

    // Delete ---------------------------------------------
    void delete(K id);

    // Miscellaneous -------------------------------------
    int size();
}
