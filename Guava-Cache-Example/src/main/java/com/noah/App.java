package com.noah;

import java.util.Properties;

import org.postgresql.ds.PGSimpleDataSource;

import com.noah.cache.DbCache;
import com.noah.dao.AddressDAO;
import com.noah.dao.OtherDAO;

public class App {

	public static void main(String[] args) throws Exception {

		Properties dbProperties = new Properties();
		dbProperties.load(App.class.getResourceAsStream("/db.properties"));
		PGSimpleDataSource dataSource = new PGSimpleDataSource();
		dataSource.setServerName(dbProperties.getProperty("db.server"));
		dataSource.setDatabaseName(dbProperties.getProperty("db.name"));
		dataSource.setPortNumber(Integer.valueOf(dbProperties.getProperty("db.port")));
		dataSource.setUser(dbProperties.getProperty("db.username"));
		dataSource.setPassword(dbProperties.getProperty("db.password"));

		AddressDAO addressDAO = new AddressDAO(dataSource);
		OtherDAO otherDAO = new OtherDAO(dataSource);
		
		DbCache dbCache = new DbCache(addressDAO, otherDAO);
		
		for(int i = 0; i < 10; i++){
			dbCache.getAllAddress().stream().forEach(System.out::println); 
			Thread.sleep(2000L);
		}
		
		for(int i = 0; i < 10; i++){
			dbCache.getAddressByCity("YF").stream().forEach(System.out::println); 
			Thread.sleep(2000L);
		}
		

	}

}
