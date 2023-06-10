package com.example.lab_2_ui;
import java.sql.*;
public class Functions {
    public Connection connect_to_DB(String dbname, String user, String pass){
        Connection conn=null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + dbname,user,pass);
            if (conn!=null){
                System.out.println("Соединение с БД было установлено для "+user);
            }else{
                System.out.println("Соединение с БД не было установлено");
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return conn;
    }
    public void createTableFunction (Connection conn, String tableName) throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.executeUpdate("CREATE OR REPLACE FUNCTION createTable()\r\n" +
                    "RETURNS void\r\n" +
                    "AS\r\n" +
                    "$$\r\n" +
                    "BEGIN\r\n" +
                    "EXECUTE 'CREATE TABLE IF NOT EXISTS "+tableName+"(\r\n" +
                    "id int PRIMARY KEY CHECK (id > 0),\r\n" +
                    "name VARCHAR(50) NOT NULL,\r\n" +
                    "city VARCHAR(50) NOT NULL,\r\n" +
                    "phone_number VARCHAR(15) UNIQUE\r\n" +
                    ")';\r\n" +
                    "END;\r\n" +
                    "$$ LANGUAGE plpgsql;");
        }
        catch (SQLException e) {
            System.out.println(e);
        };
    }
    public void insertValuesFunction (Connection conn, String tableName) throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.executeUpdate("CREATE OR REPLACE FUNCTION insertValues(col1 int, col2 VARCHAR(50), col3 VARCHAR(50), col4 VARCHAR(15))\r\n" +
                    "RETURNS void\r\n" +
                    "AS\r\n" +
                    "$$\r\n" +
                    "BEGIN\r\n" +
                    "INSERT INTO "+ tableName +" (id, name, city, phone_number)\r\n" +
                    "VALUES (col1, col2, col3, col4\r\n"+
                    ");\r\n" +
                    "END;\r\n" +
                    "$$ LANGUAGE plpgsql;");
        }
        catch (SQLException e) {
            System.out.println(e);
        };

    }
    public void searchCityFunction (Connection conn, String tableName) throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.executeUpdate("CREATE OR REPLACE FUNCTION searchCity(searched_city VARCHAR(50))\r\n" +
                    "RETURNS TABLE (st_id INTEGER, st_name VARCHAR(50), st_city VARCHAR(50), st_phone_number VARCHAR (15))\r\n" +
                    "AS\r\n" +
                    "$$\r\n" +
                    "BEGIN\r\n" +
                    "RETURN QUERY SELECT id, name, city, phone_number FROM " + tableName + " \r\n" +
                    "WHERE city ILIKE '%' || searched_city || '%' ;\r\n" +
                    "END;\r\n" +
                    "$$ LANGUAGE plpgsql;");
        } catch (SQLException e) {
            System.out.println(e);
        };
    }
    public void updateTableFunction (Connection conn, String tableName) throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.executeUpdate("CREATE OR REPLACE FUNCTION updateTable(up_id INTEGER, nw_name VARCHAR(50), nw_city VARCHAR(50)," +
                    " nw_phone_number VARCHAR(15) )\r\n" +
                    "RETURNS VOID \r\n" +
                    "AS\r\n" +
                    "$$\r\n" +
                    "BEGIN\r\n" +
                    "UPDATE "+ tableName + " SET name = nw_name, city = nw_city, phone_number = nw_phone_number\r\n" +
                    "WHERE up_id = id ;\r\n" +
                    "END;\r\n" +
                    "$$ LANGUAGE plpgsql;");
        } catch (SQLException e) {
            System.out.println(e);
        };
    }
    public void deleteByNameFunction (Connection conn, String tableName) throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.executeUpdate("CREATE OR REPLACE FUNCTION deleteByName(del_name VARCHAR(50))\r\n" +
                    "RETURNS VOID \r\n" +
                    "AS\r\n" +
                    "$$\r\n" +
                    "BEGIN\r\n" +
                    "DELETE FROM "+ tableName + " \r\n" +
                    "WHERE del_name = name ;\r\n" +
                    "END;\r\n" +
                    "$$ LANGUAGE plpgsql;");
        } catch (SQLException e) {
            System.out.println(e);
        };
    }
    public void clearTableFunction (Connection conn, String tableName) throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.executeUpdate("CREATE OR REPLACE FUNCTION clearTable()\r\n" +
                    "RETURNS VOID \r\n" +
                    "AS\r\n" +
                    "$$\r\n" +
                    "BEGIN\r\n" +
                    "DELETE FROM "+ tableName + "; \r\n" +
                    "END;\r\n" +
                    "$$ LANGUAGE plpgsql;");
        } catch (SQLException e) {
            System.out.println(e);
        };
    }
    public void printTableFunction (Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.executeUpdate("CREATE OR REPLACE FUNCTION printTable(table_name text)\r\n" +
                    "RETURNS TABLE (st_id INTEGER, st_name VARCHAR(50), st_city VARCHAR(50), st_phone_number VARCHAR (15)) \r\n" +
                    "AS\r\n" +
                    "$$\r\n" +
                    "BEGIN\r\n" +
                    "RETURN QUERY EXECUTE 'SELECT id, name, city, phone_number FROM ' || table_name; \r\n" +
                    "END;\r\n" +
                    "$$ LANGUAGE plpgsql;");
        } catch (SQLException e) {
            System.out.println(e);
        };
    }
    public void newReaderFunction (Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.executeUpdate("CREATE OR REPLACE FUNCTION newReader(username text, password text)\r\n" +
                    "RETURNS VOID \r\n" +
                    "AS\r\n" +
                    "$$\r\n" +
                    "BEGIN\r\n" +
                    "EXECUTE 'CREATE USER ' || username || ' WITH PASSWORD ''' || password || ''';';\r\n" +
                    "EXECUTE 'REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA public FROM ' || username || ';';\r\n" +
                    "EXECUTE 'REVOKE ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public FROM ' || username || ';';\r\n" +
                    "EXECUTE 'REVOKE ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public FROM ' || username || ';';\r\n" +
                    "EXECUTE 'DROP OWNED BY ' || username || ';';\r\n" +
                    "EXECUTE 'DROP USER IF EXISTS ' || username || ';';\r\n" +
                    "EXECUTE 'CREATE USER ' || username || ' WITH PASSWORD ''' || password || ''';';\r\n" +
                    "END;\r\n" +
                    "$$ LANGUAGE plpgsql;");
        } catch (SQLException e) {
            System.out.println(e);
        };
    }
    public void giveRightsFunction (Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.executeUpdate("CREATE OR REPLACE FUNCTION giveRights(username VARCHAR(50))\r\n" +
                    "RETURNS VOID \r\n" +
                    "AS\r\n" +
                    "$$\r\n" +
                    "BEGIN\r\n" +
                    "EXECUTE 'GRANT SELECT ON Students TO ' || username || ';';\r\n" +
                    "EXECUTE 'ALTER FUNCTION printTable(text) OWNER TO ' || username || ';';\r\n" +
                    "EXECUTE 'ALTER FUNCTION searchCity(VARCHAR(50)) OWNER TO ' || username || ';';\r\n" +
                    "END;\r\n" +
                    "$$ LANGUAGE plpgsql;");
        } catch (SQLException e) {
            System.out.println(e);
        };
    }
}
