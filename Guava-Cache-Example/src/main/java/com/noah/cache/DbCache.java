package com.noah.cache;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.noah.dao.AddressDAO;
import com.noah.dao.OtherDAO;
import com.noah.domain.Address;

public class DbCache {
	
	private static AddressDAO addressDAO;
	private static OtherDAO otherDAO;
	
	public DbCache(AddressDAO addressDAO, OtherDAO otherDAO){
		this.addressDAO = addressDAO;
		this.otherDAO = otherDAO;
	}

	public static final String CACHE_ADDRESS_TABLE = "Address";
	public static final String CACHE_ADDRESS_METHOD_QUERY_ALL = "QueryAll";
	public static final String CACHE_ADDRESS_METHOD_QUERY_BY_CITY = "QueryByCity";

	public static final String CACHE_OTHER_TABLE = "OtherTable";
	public static final String CACHE_OTHER_METHOD = "OtherMethod";
	
	public List<Address> getAllAddress() throws ExecutionException{
		CacheKey key = new CacheKey(CACHE_ADDRESS_TABLE, CACHE_ADDRESS_METHOD_QUERY_ALL, null);
		List addresses = (List<Address>)STATIC_DB_CACHE.get(key);
		//Before return , we can manually invalid this key, then each time we call it, it will hit DB
		//STATIC_DB_CACHE.invalidate(key);
		return addresses;
	}
	
	public List<Address> getAddressByCity(String city) throws ExecutionException{
		CacheKey key = new CacheKey(CACHE_ADDRESS_TABLE, CACHE_ADDRESS_METHOD_QUERY_BY_CITY, new Object []{city});
		List addresses = (List<Address>)STATIC_DB_CACHE.get(key);
		return addresses;
	}

	private static final LoadingCache<CacheKey, Object> STATIC_DB_CACHE = CacheBuilder.newBuilder()
			.expireAfterWrite(5, TimeUnit.SECONDS).build(new CacheLoader<CacheKey, Object>() {
				@Override
				public Object load(CacheKey key) throws Exception {
					Object object = null;
					switch (key.getTableName()) {
					case CACHE_ADDRESS_TABLE:
						switch (key.getQueryMethod()) {
						case CACHE_ADDRESS_METHOD_QUERY_ALL:
							object = addressDAO.queryAll();
							break;
						case CACHE_ADDRESS_METHOD_QUERY_BY_CITY:
							object = addressDAO.queryByCity((String)key.getParams()[0]);
							break;
						default:
							break;
						}
						break;
					case CACHE_OTHER_TABLE:
						switch (key.getQueryMethod()) {
						case CACHE_OTHER_METHOD:
							object = otherDAO.someMethod();
							break;
						default:
							break;
						}
						break;
					default:
						break;
					}
					return object;
				}

			});

	private static class CacheKey {
		private String tableName;
		private String queryMethod;
		private Object[] params;

		public CacheKey(String tableName, String queryMethod, Object[] params) {
			super();
			this.tableName = tableName;
			this.queryMethod = queryMethod;
			this.params = params;
		}

		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public String getQueryMethod() {
			return queryMethod;
		}

		public void setQueryMethod(String queryMethod) {
			this.queryMethod = queryMethod;
		}

		public Object[] getParams() {
			return params;
		}

		public void setParams(Object[] params) {
			this.params = params;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(params);
			result = prime * result + ((queryMethod == null) ? 0 : queryMethod.hashCode());
			result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CacheKey other = (CacheKey) obj;
			if (!Arrays.equals(params, other.params))
				return false;
			if (queryMethod == null) {
				if (other.queryMethod != null)
					return false;
			} else if (!queryMethod.equals(other.queryMethod))
				return false;
			if (tableName == null) {
				if (other.tableName != null)
					return false;
			} else if (!tableName.equals(other.tableName))
				return false;
			return true;
		}

	}

}
