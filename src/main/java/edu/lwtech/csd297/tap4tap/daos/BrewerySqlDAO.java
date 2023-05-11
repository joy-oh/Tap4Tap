package edu.lwtech.csd297.tap4tap.daos;

import java.sql.*;
import java.util.*;

import org.apache.logging.log4j.*;

import edu.lwtech.csd297.tap4tap.pojos.*;
import edu.lwtech.csd297.tap4tap.utils.*;

public class BrewerySqlDAO implements DAO<Brewery, String> {

    private static final Logger logger = LogManager.getLogger(BrewerySqlDAO.class.getName());

    private Connection conn = null;

    public BrewerySqlDAO() {
        this.conn = null; // conn must be created during init()
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

        if (brewery.getBreweryId() != null) {
            logger.error("Attempting to add previously added Brewery: {}", brewery);
            return -1;
        }

        String query = "INSERT INTO brewery (brewery_id, name, brewery_type, address_1, address_2, address_3, city, state_province, postal_code, country, phone, website_url, longitude, latitude) VALUES (?,?)";

        int breweryId = SQLUtils.executeSQLInsert(conn, query, "brewery_id",
                brewery.getName(), brewery.getBreweryType(), brewery.getAddress1(), brewery.getAddress2(),
                brewery.getAddress3(), brewery.getCity(), brewery.getStateProvince(), brewery.getPostalCode(),
                brewery.getCountry(), brewery.getWebsiteUrl(), brewery.getPhone(), "" + brewery.getLongitude(),
                "" + brewery.getLatitude());

        logger.debug("Brewery successfully inserted with ID = {}", breweryId);
        return breweryId;
    }

    public Brewery retrieveByID(String breweryId) {
        logger.debug("Trying to get Brewery with ID: {}", breweryId);

        String query = "SELECT *";
        query += " FROM brewery WHERE brewery_id=?";

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query, new String[]{"brewery_id", breweryId});

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

        index++; // SQL uses 1-based indexes

        if (index < 1)
            return null;

        String query = "SELECT *";
        query += " FROM brewery ORDER BY brewery_id LIMIT " + index;

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);

        if (rows == null) {
            logger.debug("Did not find brewery.");
            return null;
        }

        SQLRow row = rows.get(rows.size() - 1);
        Brewery brewery = convertRowToBrewery(row);
        return brewery;
    }

    public List<Brewery> retrieveAll() {
        logger.debug("Getting all Breweries...");

        String query = "SELECT *";
        query += " FROM brewery ORDER BY brewery_id";

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

    public List<String> retrieveAllIDs() {
        logger.debug("Getting all Brewery IDs...");

        String query = "SELECT brewery_id FROM brewery ORDER BY brewery_id";

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);

        if (rows == null) {
            logger.debug("No breweries found!");
            return new ArrayList<>();
        }

        List<String> breweryIds = new ArrayList<>();
        for (SQLRow row : rows) {
            String value = row.getItem("brewery_id");
            breweryIds.add(value);
        }
        return breweryIds;
    }

    public List<Brewery> search(SearchParameter[] params) {
        // logger.debug("Searching for brewery with '{}'", query);
        String[] sqlParams = new String[params.length * 2];
        String sqlStatement = "SELECT * FROM brewery WHERE";
        if (params.length > 0) {
            if (params[0].isExact()){
                sqlStatement += " ? = ?";
            }
            else{
                sqlStatement += " ? like ?";
            }
            sqlParams[0] = params[0].getName();
            sqlParams[1] = params[0].getValue();
        }
        
        for (int i = 1; i < params.length; i++) {
            if (params[i].isExact()){
                sqlStatement += " AND ? = ?";
            }
            else{
                sqlStatement += " AND ? like ?";
            }
            sqlParams[2*i] = params[2*i].getName();
            sqlParams[2*i+1] = params[2*i].getValue();
        }

        sqlStatement += " ORDER BY brewery_id";

        // query = "%" + query + "%";
        List<SQLRow> rows = SQLUtils.executeSQL(conn, sqlStatement, sqlParams);

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

    public void delete(String breweryId) {
        logger.debug("Trying to delete Brewery with ID: {}", breweryId);

        String query = "DELETE FROM brewery WHERE brewery_id=?";
        SQLUtils.executeSQL(conn, query, new String[]{"brewery_id", breweryId});
    }

    public int size() {
        logger.debug("Getting the number of rows...");

        String query = "SELECT count(*) FROM brewery";

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
        double longitude = Double.parseDouble(row.getItem("longitude"));
        double latitude = Double.parseDouble(row.getItem("latitude"));
        return new Brewery(breweryId, name, breweryType,
                address1, address2, address3, city,
                stateProvince, postalCode, country,
                websiteUrl, phone, longitude, latitude);
    }

}
