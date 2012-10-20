package com.hardcode.gdbms.engine.customQuery;

import java.util.HashMap;

/**
 * Manages the custom queries
 * 
 * @author Fernando González Cortés
 */
public class QueryManager {
	private static HashMap queries = new HashMap();

	/**
	 * Registers a query by name
	 * 
	 * @param query
	 *            Name of the query
	 * 
	 * @throws RuntimeException
	 *             If a query with the name already exists
	 */
	public static void registerQuery(CustomQuery query) {
		String queryName = query.getName().toLowerCase();

		if (queries.get(queryName) != null) {
			throw new RuntimeException("Query already registered");
		}

		queries.put(queryName, query);
	}

	/**
	 * Gets the query by name
	 * 
	 * @param queryName
	 *            Name of the query
	 * 
	 * @return An instance of the query
	 */
	public static CustomQuery getQuery(String queryName) {
		queryName = queryName.toLowerCase();

		return (CustomQuery) queries.get(queryName);
	}
}
