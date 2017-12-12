package com.noah.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class BaseDAO {

	private DataSource dataSource;

	public BaseDAO(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public interface QueryMapping<T> {
		T mapping(ResultSet rs) throws Exception;
	}

	public interface TransactionTask<T> {
		T run(Connection connection) throws Exception;
	}


	public <T> T executeQuery(String sql, QueryMapping<T> callback) throws Exception {
		return executeQueryWithArgs(sql, callback);
	}

	public <T> T executeQueryWithArgs(String sql, QueryMapping<T> callback, Object... args) throws Exception{
		try(
				Connection connection = getConnection();
				PreparedStatement statement =  sqlBindParameter(connection.prepareStatement(sql), args);
				ResultSet resultSet = statement.executeQuery()){
			resultSet.setFetchSize(1000);
			return callback.mapping(resultSet);
		}
		catch (Exception e) {
			throw e;
		}
	}
	
	
	public int executeUpdate (String sql, Object... args) throws Exception{
		int result = 0;
		try(
				Connection connection = getConnection();
				PreparedStatement statement = sqlBindParameter(connection.prepareStatement(sql), args)
				){
			result = statement.executeUpdate();
		}catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	public int executeUpdateInTransaction (Connection connection, String sql, Object... args) throws Exception{
		int result = 0;
		try(
				PreparedStatement statement = sqlBindParameter(connection.prepareStatement(sql), args)
				){
			result = statement.executeUpdate();
		}catch (Exception e) {
			throw e;
		}
		return result;
	}
	
	public <T> T executeTransaction(TransactionTask<T> task) throws Exception {
		Connection connection = getConnection();
		try{
			connection.setAutoCommit(false);
			T t = task.run(connection);
			connection.commit();
			return t;
		}catch(Exception e){
			if(null != connection){
				connection.rollback();
			}
			throw e;
		}finally {
			if(null != connection){
				connection.close();
			}
		}
	}

	
	private PreparedStatement sqlBindParameter(PreparedStatement statement, Object... args) throws SQLException {
		int index = 1;
		for (Object arg : args) {
			statement.setObject(index, arg);
			index++;
		}
		return statement;
	}
	
	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
	


}
