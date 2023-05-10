package edu.lwtech.csd297.tap4tap.daos;

import java.sql.*;
import java.util.*;

import org.apache.logging.log4j.*;

import edu.lwtech.csd297.tap4tap.pojos.*;
import edu.lwtech.csd297.tap4tap.utils.*;

public class BrewerySqlDAO implements DAO<Brewery> {
    
    private static final Logger logger = LogManager.getLogger(BrewerySqlDAO.class.getName());

    private Connection conn = null;

    public BrewerySqlDAO() {
        this.conn = null;                                   // conn must be created during init()
    }

    public boolean initialize(String initParams) {
        logger.info("Connecting to the database...");

        conn = SQLUtils.connect(initParams);
        if (conn == null) {
            logger.error("Unable to connect to SQL Database: {}", initParams);
            return false;
        }
        logger.info("...connected!");

        return true;
    }

    public void terminate() {
        SQLUtils.disconnect(conn);
        conn = null;
    }

    public int insert(Brewery brewery) {
        logger.debug("Inserting {}...", brewery);

        if (brewery.getRecID() != -1) {
            logger.error("Attempting to add previously added Brewery: {}", brewery);
            return -1;
        }

        String query = "INSERT INTO Brewery (username, password) VALUES (?,?)";

        int recID = SQLUtils.executeSQLInsert(conn, query, "recID", brewery.getUsername(), brewery.getPassword());    
        
        logger.debug("Brewery successfully inserted with ID = {}", recID);
        return recID;
    }

    public Brewery retrieveByID(int recID) {
        logger.debug("Trying to get Brewery with ID: {}", recID);
        
        String query = "SELECT recID, username, password";
        query += " FROM Brewery WHERE recID=" + recID;

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);
        
        if (rows != null) {
            logger.debug("Found brewery!");
        } else {
            logger.debug("Did not find brewery.");
            return null;
        }
        
        SQLRow row = rows.get(0);
        Brewery brewery = convertRowToBrewery(row);
        return brewery;
    }
    
    public Brewery retrieveByIndex(int index) {
        logger.debug("Trying to get Brewery with index: {}", index);
        
        index++;                                    // SQL uses 1-based indexes

        if (index < 1)
            return null;

        String query = "SELECT recID, username, password";
        query += " FROM Brewery ORDER BY recID LIMIT " + index;

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);
        
        if (rows == null) {
            logger.debug("Did not find brewery.");
            return null;
        }
        
        SQLRow row = rows.get(rows.size()-1);
        Brewery brewery = convertRowToBrewery(row);
        return brewery;
    }
    
    public List<Brewery> retrieveAll() {
        logger.debug("Getting all Breweries...");
        
        String query = "SELECT recID, username, password";
        query += " FROM Brewery ORDER BY recID";

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);
        
        if (rows == null) {
            logger.debug("No breweries found!");
            return new ArrayList<>();
        }

        List<Brewery> breweries = new ArrayList<>();
        for (SQLRow row : rows) {
            Brewery brewery = convertRowToBrewery(row);
            breweries.add(brewery);
        }
        return breweries;
    }
    
    public List<Integer> retrieveAllIDs() {
        logger.debug("Getting all Brewery IDs...");

        String query = "SELECT recID FROM Brewery ORDER BY recID";

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);
        
        if (rows == null) {
            logger.debug("No breweries found!");
            return new ArrayList<>();
        }
        
        List<Integer> recIDs = new ArrayList<>();
        for (SQLRow row : rows) {
            String value = row.getItem("recID");
            int i = Integer.parseInt(value);
            recIDs.add(i);
        }
        return recIDs;
    }

    public List<Brewery> search(String keyword) {
        logger.debug("Searching for brewery with '{}'", keyword);

        String query = "SELECT recID, username, password FROM Brewery WHERE";
        query += " username like ?";
        query += " ORDER BY recID";

        keyword = "%" + keyword + "%";
        List<SQLRow> rows = SQLUtils.executeSQL(conn, query, keyword);
        
        if (rows == null) {
            logger.debug("No breweries found!");
            return new ArrayList<>();
        }

        List<Brewery> breweries = new ArrayList<>();
        for (SQLRow row : rows) {
            Brewery brewery = convertRowToBrewery(row);
            breweries.add(brewery);
        }
        return breweries;
    }

    public boolean update(Brewery brewery) {
        throw new UnsupportedOperationException("Unable to update existing brewery in database.");
    }

    public void delete(int recID) {
        logger.debug("Trying to delete Brewery with ID: {}", recID);

        String query = "DELETE FROM Brewery WHERE recID=" + recID;
        SQLUtils.executeSQL(conn, query);
    }
    
    public int size() {
        logger.debug("Getting the number of rows...");

        String query = "SELECT count(*) FROM Brewery";

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);
        if (rows == null) {
            logger.error("No breweries found!");
            return 0;
        }

        String value = rows.get(0).getItem();
        return Integer.parseInt(value);
    }    

    // =====================================================================

    private Brewery convertRowToBrewery(SQLRow row) {
        logger.debug("Converting {} to Brewery...", row);
        String breweryId = row.getItem("brewery_id");
        String name = row.getItem("name");
        String breweryType = row.getItem("brewery_type");
        String address1 = row.getItem("address_1");
        String address2 = row.getItem("address_2");
        String address3 = row.getItem("address_3");
        String city = row.getItem("city");
        String stateProvince = row.getItem("state_province");
        String postalCode = row.getItem("postal_code");
        String country = row.getItem("country");
        String websiteUrl = row.getItem("website_url");
        String phone = row.getItem("phone");
        String longitude = row.getItem("longitude");
        String latitude = row.getItem("latitude");
        return new Brewery(breweryId, name, breweryType,
        address1, address2, address3, city,
        stateProvince, postalCode, country,
        websiteUrl, phone, longitude, latitude);
    }

}
