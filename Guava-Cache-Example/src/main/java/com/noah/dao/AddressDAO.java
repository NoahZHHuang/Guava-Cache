package com.noah.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.noah.domain.Address;

public class AddressDAO {

	private BaseDAO baseDAO;

	public AddressDAO(DataSource dataSource) {
		baseDAO = new BaseDAO(dataSource);
	}

	public List<Address> queryAll() throws Exception {
		System.out.println("AddressDAO executing queryAll()...");
		// above sentence will only be printed out when the cache is expired or
		// manually invalid
		List<Address> addressesReturned = baseDAO
				.executeQuery("select addr_id as addrId, city, street, zip_code as zipCode from address", (rs) -> {
					List<Address> addresses = new ArrayList<>();
					while (rs.next()) {
						Address address = new Address();
						address.setAddrId(rs.getInt("addrId"));
						address.setCity(rs.getString("city"));
						address.setStreet(rs.getString("street"));
						address.setZipCode(rs.getString("zipCode"));
						addresses.add(address);
					}
					return addresses;
				});
		return addressesReturned;
	}

	public List<Address> queryByCity(String city) throws Exception {
		System.out.println("AddressDAO executing queryByCity(\"" + city + "\")...");
		List<Address> addressesReturned = baseDAO.executeQueryWithArgs(
				"select addr_id as addrId, city, street, zip_code as zipCode from address where city = ?", (rs) -> {
					List<Address> addresses = new ArrayList<>();
					while (rs.next()) {
						Address address = new Address();
						address.setAddrId(rs.getInt("addrId"));
						address.setCity(rs.getString("city"));
						address.setStreet(rs.getString("street"));
						address.setZipCode(rs.getString("zipCode"));
						addresses.add(address);
					}
					return addresses;
				} , city);
		return addressesReturned;
	}

}
