package com.learn.jdbcpg;

import com.learn.jdbcpg.model.dao.AppUser;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class App {

    private static Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws ParseException, IOException, SQLException {
        final Properties prop = readPropertyFile(args);

        final String jdbcUrl = prop.getProperty("jdbcUrl");
        final String jdbcUser = prop.getProperty("jdbcUser");
        final String jdbcPswd = prop.getProperty("jdbcPswd");

        log.info("====== PRINT DB INFO ======");
        DbInfo dbInfo = new DbInfo(jdbcUrl, jdbcUser, jdbcPswd);
        log.info(dbInfo.printDbInfo());

        log.info("====== SEED TABLES ======");
        SeedData seedData = new SeedData(jdbcUrl, jdbcUser, jdbcPswd);
        AppUser seedUser = new AppUser("guest", "Guest User", "welcome");
        seedData.seed(seedUser);

        log.info("====== MOCK A LOGIN ======");
        MockSession mockSession = new MockSession(jdbcUrl, jdbcUser, jdbcPswd);
        mockSession.mockLogin("guest", "invalidPassword");
        mockSession.mockLogin("invalidUsername", "welcome");
        mockSession.mockLogin("guest", "welcome");

        log.info("====== JSON QUERY ======");
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPswd);

            stmt = conn.prepareStatement("SELECT userName FROM appUser WHERE others->'Gender' ?? ? ");
            stmt.setString(1, "F");
            rs = stmt.executeQuery();
            while (rs.next()) {
                log.info( rs.getString(1) );
            }
            rs.close();
            stmt.close();
          
        } catch (Exception e) {
            log.error( "",e );
        } finally {
            try { rs.close(); } catch (SQLException ex) {}
            try { stmt.close(); } catch (SQLException ex) {}
            try { conn.close(); } catch (SQLException ex) {}
        }
    }

    private static Properties readPropertyFile(String[] args) throws ParseException, IOException {
        final Options options = new Options().addOption("f", true, "configuration file");
        final CommandLine cmd = (new DefaultParser()).parse(options, args);

        String fileName = null;
        if (cmd.hasOption("f")) {
            fileName = cmd.getOptionValue("f");
        } else {
            fileName = "config.properties";
        }
        final File f = new File(fileName);
        log.info("get property file name: " + f.getCanonicalPath());
        final Properties prop = new Properties();
        prop.load(new FileInputStream(f.getCanonicalPath()));
        return prop;
    }
}
