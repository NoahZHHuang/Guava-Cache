package com.noah.dao;

import javax.sql.DataSource;

public class OtherDAO {

	private BaseDAO baseDAO;

	public OtherDAO(DataSource dataSource) {
		baseDAO = new BaseDAO(dataSource);
	}
	
	public Object someMethod() {
		return null;
	}
}
