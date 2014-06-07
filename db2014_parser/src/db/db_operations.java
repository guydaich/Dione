package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import config.config;
import parser_entities.light_entity_movie;


/**
 * The communication with the db is being made
 * by this class methods. All the operation against the DB:
 */
public abstract class db_operations
{
	public enum invocation_code {YAGO_UPDATE, USER_PREFENCE};
	
// BASIC FUNCTIONS
	
	/** insert tuple
	 * @param table - the table name we want to insert to
	 * @param 	values 	- the values to insert in the correct order
	 * 					- for the time stamp insert the string "TimeStamp"
	 * @return -1 error, else number of rows efected
	 * @throws SQLException 
	 */
	protected static int insert(String table, String columns, Object... values) 
			throws SQLException 
	{
		if (table == null || values == null || values.length == 0)
			return (-1);
		
		// Set the connection
		Connection conn = null;
		conn = jdbc_connection_pooling.get_instance().get_connection();
		
		// Build insert string
		String insert_string;
		if 	(columns == null  || columns == "")
			insert_string = "INSERT INTO " + table + " VALUES (";
		else
			insert_string = "INSERT INTO " + table + " (" + columns + ") VALUES (";
		
		// Isert the currect number of '?' for values to be inserted
		for (int i = 0 ; i < values.length - 1 ; i++)
			insert_string += "?, ";
		insert_string += "?)";
			
		// Set the statment
		PreparedStatement stmt = null; 
		stmt = conn.prepareStatement(insert_string);

		for (int i = 0; i < values.length; i++) 
		{
			if (values[i] instanceof Integer)
			{
				stmt.setInt((i + 1), (int) values[i]);
				continue;
			} 
			if (values[i] instanceof String)
			{
				// when the current time is needed
				if ((String)values[i] == "TimeStamp")
				{
					stmt.setTimestamp((i + 1), new Timestamp((new Date()).getTime()));
					continue;
				}
				
				stmt.setString((i + 1), (String) values[i]);
				continue;
			}
		}
		
		// execute insert SQL stetement
		int rows_effected = stmt.executeUpdate();
		
		// Close connection
		jdbc_connection_pooling.get_instance().close(conn);
		return (rows_effected);
	}

	/** insert tuple
	 * @param querey - an sql querey fully wrote, using '?' for values
	 * @param values - an array of values to put where the ?s are
	 * @throws SQLException 
	 */
	protected static int run_querey(String querey, Object... values) 
			throws SQLException 
	{
		if (querey == null || values == null)
			return (-1);
		
		if (charCount(querey, '?') != values.length)
			throw(new SQLException("wrong number of parameters for querey"));
		
		// Set the connection
		Connection conn = null;
		conn = jdbc_connection_pooling.get_instance().get_connection();
			
		// Set the statment
		PreparedStatement stmt = null; 
		stmt = conn.prepareStatement(querey);

		for (int i = 0; i < values.length; i++) 
		{
			if (values[i] instanceof Integer)
			{
				stmt.setInt((i + 1), (int) values[i]);
				continue;
			} 
			if (values[i] instanceof String)
			{
				// when the current time is needed
				if ((String)values[i] == "TimeStamp")
				{
					stmt.setTimestamp((i + 1), new Timestamp((new Date()).getTime()));
					continue;
				}
				
				stmt.setString((i + 1), (String) values[i]);
				continue;
			}
		}
		
		// execute insert SQL stetement
		int rows_effected = stmt.executeUpdate();
		
		// Close connection
		jdbc_connection_pooling.get_instance().close(conn);
		return (rows_effected);
	}

	/** delete tuple/s
	 * @param tableName	- the name of the table we want to delete from
	 * @param whereCol	- "WHERE column1 = ? AND column 2 = ? Or ..."
	 * @param values	- the objects to switch '?' in the where clause 
	 * @throws SQLException 
	 */
	protected static int delete(String table, String whereClause, Object... values) throws SQLException 
	{
		if (table == null)
			return (-1);
		
		// Does value count match the number of "?"
		if (values != null)
			if (countChar(whereClause, '?') != values.length)
				return (-1);
		
		// Set the connection
		Connection conn = null;
		conn = jdbc_connection_pooling.get_instance().get_connection();
		
		// Build insert string
		String delete_string;
		if 	(whereClause == null || whereClause == "")
			delete_string = "DELETE FROM " + table;
		else
			delete_string = "DELETE FROM " + table + " WHERE " + whereClause;
			
		// Set the statment
		PreparedStatement stmt = null; 
		stmt = conn.prepareStatement(delete_string);

		// Fill the question marks
		if (values != null)
		{
			for (int i = 0; i < values.length; i++) 
			{
				if (values[i] instanceof Integer)
				{
					stmt.setInt((i+1), (int) values[i]);
					continue;
				} 
				if (values[i] instanceof String)
				{	
					stmt.setString((i+1), (String) values[i]);
					continue;
				}
			}
		}
		
		// execute insert SQL stetement
		int rows_effected = stmt.executeUpdate();
		
		// Close connection
		jdbc_connection_pooling.get_instance().close(conn);
		return (rows_effected);
	}

	/** select tuple/s
	 * @param tableName	- the name of the table we want to delete from
	 * @param whereCol	- "WHERE column1 = ? AND column 2 = ? Or ..."
	 * @param values	- the objects to switch '?' in the where clause 
	 * @throws SQLException 
	 */
	protected static ResultSet select(String select, String table , String whereClause, Object... values) throws SQLException 
	{
		if (table == null)
			throw (new SQLException("null table name"));
		
		// Does value count match the number of "?"
		if (values != null)
			if (countChar(whereClause, '?') != values.length)
				throw (new SQLException("wrong number of values entered"));
		
		// Make sure theres a limit
		whereClause = whereClause.toLowerCase();
		if (whereClause != null && whereClause != "" && !whereClause.contains("limit"))
			whereClause += " limit " + (new config().get_default_large_limit());
		
		// Set the connection
		Connection conn = null;
		conn = jdbc_connection_pooling.get_instance().get_connection();
		
		// Build insert string
		String select_string;
		if 	(whereClause == null || whereClause == "")
			select_string = "SELECT " + select + " FROM " + table;
		else
			select_string = "SELECT " + select + " FROM " + table + " WHERE " + whereClause;
			
		// Set the statment
		PreparedStatement stmt = null; 
		stmt = conn.prepareStatement(select_string);

		// Fill the question marks
		if (values != null)
		{
			for (int i = 0; i < values.length; i++) 
			{
				if (values[i] instanceof Integer)
				{
					stmt.setInt((i+1), (int) values[i]);
					continue;
				} 
				if (values[i] instanceof String)
				{	
					stmt.setString((i+1), (String) values[i]);
					continue;
				}
			}
		}
		
		// execute insert SQL stetement
		ResultSet rows_effected = stmt.executeQuery();
		
		// Close connection
		jdbc_connection_pooling.get_instance().close(conn);
		return (rows_effected);
	}

	/** select tuple/s
	 * @param tableName	- the name of the table we want to delete from
	 * @param whereCol	- "WHERE column1 = ? AND column 2 = ? Or ..."
	 * @param values	- the objects to switch '?' in the where clause 
	 * @throws SQLException 
	 */
	protected static ResultSet select(String select, String table , String whereClause, List<Object> values) throws SQLException 
	{
		if (table == null)
			throw (new SQLException("null table name"));
		
		// Does value count match the number of "?"
		if (values != null)
			if (countChar(whereClause, '?') != values.size())
				throw (new SQLException("wrong number of values entered"));
		
		// Make sure theres a limit
				whereClause = whereClause.toLowerCase();
				if (whereClause != null && whereClause != "" && !whereClause.contains("limit"))
					whereClause += " limit " + (new config().get_default_large_limit());
		
		// Set the connection
		Connection conn = null;
		conn = jdbc_connection_pooling.get_instance().get_connection();
		
		// Build insert string
		String insert_string;
		if 	(whereClause == null || whereClause == "")
			insert_string = "SELECT " + select + " FROM " + table;
		else
			insert_string = "SELECT " + select + " FROM " + table + " WHERE " + whereClause;
			
		// Set the statment
		PreparedStatement stmt = null; 
		stmt = conn.prepareStatement(insert_string);

		// Fill the question marks
		if (values != null)
		{
			// Place index
			int i = 0;
			
			for (Object value : values) 
			{
				i++;
				if (value instanceof Integer)
				{
					stmt.setInt(i, (int) value);
				} 
				else if (value instanceof String)
				{	
					stmt.setString(i, (String) value);
				}
			}
		}
		
		// execute insert SQL stetement
		ResultSet rows_effected = stmt.executeQuery();
		
		// Close connection
		jdbc_connection_pooling.get_instance().close(conn);
		return (rows_effected);
	}

// GENERICS

	/** internaly used in functions that retrieve HM<Str, Int>
	 * @param table		- table name
	 * @param values	- "IntegerVal, StringVal", exactly these values!
	 * @return a HashMap of wanted values from the table
	 * @throws SQLException 
	 */
	protected static List <String> ResultSet2ListString(ResultSet result) 
			throws SQLException
	{
		// Enumerate all movies
		List<String> retList =  new ArrayList<String>(); 
				
		// is table empty
		if (result.next())
		{
			while (result.next())
			{
				retList.add(result.getString(1));
			}
		}
		
		return (retList);
	}
		
	/** internaly used in functions that retrieve HM<Str, Int>
	 * @param table		- table name
	 * @param values	- "IntegerVal, StringVal", exactly these values!
	 * @return a HashMap of wanted values from the table
	 * @throws SQLException 
	 */
	protected static HashMap <String,Integer> generic_get_two_values(String values, String table, String where) 
			throws SQLException
	{
		ResultSet result = select(values, table, where);
		
		// Enumerate all movies
		HashMap <String,Integer> retMap =  new HashMap<String,Integer>(); 
				
		// is table empty
		if (result.next())
		{	
			do
			{
				Integer id = result.getInt(1);
				String name = result.getString(2);
				
				retMap.put(name,id);
			} while (result.next());
		}
		
		return (retMap);
	}
	
	/** internally used by functions that exchange name for id
	 * @param value 		- name of table with capital first latter
	 * @param value_name	- actual content of string you wish to get
	 * @return 				- the ID of what you wanted
	 * @throws SQLException
	 */
	protected static int generic_id_getter(String value, String value_name) 
			throws SQLException
	{
		if (value == null)
			return (-1);
		
		String whereClause = value.toLowerCase() + "Name = ?";
		ResultSet results = select("id"+ value, value.toLowerCase() , whereClause, value_name);
		
		try{
		// did select find souch user
		if (results.next())
			return (Integer.parseInt(results.getString("id" + value)));
		else
			return (0);
		} catch (NumberFormatException e)
		{
			return (-1);
		}
	}
		
	/** get statement from the conn 
	 * @throws SQLException */
	protected Statement getStatement() 
			throws SQLException 
	{
		jdbc_connection_pooling jdbc_con = jdbc_connection_pooling.get_instance();
		Connection conn = jdbc_con.get_connection();
		
		return (conn.createStatement());
	}
	
	/**
	 * make sure to select all needed fields
	 * @return result may be an empry entity
	 * @throws SQLException
	 */
	protected static light_entity_movie get_light_entity_movie(ResultSet result) 
			throws SQLException 
	{
		if (result != null)
		{
			int id = result.getInt("idMovie");
			String name = result.getString("movieName");
			int movie_year = result.getInt("year");
			String wiki = result.getString("wiki");
			String movie_director = result.getString("personName");
			int duration = result.getInt("duration");
			String plot = result.getString("plot");
			
			return(new light_entity_movie(id, name, movie_year, wiki, movie_director, duration, plot));
		}
		
		return(new light_entity_movie(0, "", 0, "", "", 0, ""));
	}
	
	protected static List<Integer> get_all_ids(String field, String table) 
			throws SQLException
	{
		// where string includes "order by" field to get the prefered tags
		ResultSet result = select(field, table, null);
		
		// Enumerate all movies
		List<Integer> returnedList = new ArrayList<Integer>();
		
		// is table empty
		if (result != null)
		{
			while (result.next())
			{
				Integer id = result.getInt(1);
				
				returnedList.add(id);
			}
		}
		
		return (returnedList);
	}
	
// Internal usage
	
	/**
	 * how much times is c found in str
	 * @return
	 */
	private static int countChar(String str, char c)
	{
		int count = 0;
		
		for (int i = 0 ; i < str.length() ; i ++)
		{
			if (str.charAt(i) == c)
				++count;
		}
		
		return (count);
	}

	/**
	 *  Count the number of times c appears in str
	 */
	private static int charCount(String str, char c) 
	{
		int count = 0;
		
		for (int i=0;i<str.length();i++)
		{
			if (c == str.charAt(i))
				++count;
		}
		
		return (count);
	}

	
// Publics

	/** get a connection 
	 * @throws SQLException */
	public static Connection getConnection() 
			throws SQLException 
	{
		jdbc_connection_pooling jdbc_conn = jdbc_connection_pooling.get_instance();

		return (jdbc_conn.get_connection());
	}
	
	/**
	 * Get the current time in a string of YYYY-MM-DD hh:mm:ss
	 * @return
	 */
	public static String get_curr_time()
	{
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
	}
	
	/**
	 * insert a row to invocations table with the current Timestamp
	 * @param code - the invocation code as found in the enum invocation_code
	 * @return true iff did succeed
	 * @throws SQLException
	 */
	public static boolean perform_invocation(invocation_code code) 
			throws SQLException
	{
		return (insert("invocations", "`invokeCode`, `invokeDate`", code.ordinal(), get_curr_time()) > 0);
	}

	/**
	 * @return the Timestamp of the last invocation of this code
	 * @param code - the invocation code as found in the enum invocation_code
	 * @throws SQLException
	 */
	public static Timestamp get_last_invocation(invocation_code code) 
			throws SQLException
	{
		String whereClause = "invokeCode = ? ORDER BY invokeDate desc";
		
		ResultSet results = select("invokeDate" , "invocations" , whereClause, code.ordinal());
	
		if (results.next())
			return (results.getTimestamp(1));
		else
			return (null);
	}
	
	/**
	 * @return true iff there was at least one invocation of this code
	 * @param code - the invocation code as found in the enum invocation_code
	 * @throws SQLException
	 */
	public static boolean was_there_an_invocation(invocation_code code) 
			throws SQLException
	{
		String whereSegment = "invocationCode = " + code;
		ResultSet result = select("invocationCode", "invocations", whereSegment);
		
		// did select find souch user
		return (result.next());
	}

	/**
	 * delete the last invocation from invocations table with the max date
	 * @param code - the invocation code as found in the enum invocation_code
	 * @throws SQLException
	 */
	public static void delete_last_invocation(invocation_code code) 
			throws SQLException 
	{
		String where = " invokeDate = (SELECT max(invokeDate) FROM invocations where invokeCode = ?) ";
		
		if (delete("invocations", where , code.ordinal()) > 0)
			throw (new SQLException("Deletion error"));
	}
	
	/**
	 * using the table 
	 */
	public static void fill_movie_tag_relation() 
	{
		try {
			Timestamp ts = get_last_invocation(invocation_code.USER_PREFENCE);
			
			// Is it ok not to redo this
			if (ts != null)
			{		
				Timestamp now = new Timestamp(new java.util.Date().getTime() - 1000*60*15);
				
				// was this performed in the last 15 minutes
				if (now.before(ts))
					return;
			}	
		
			
			// Invoke performance
			if (!perform_invocation(invocation_code.USER_PREFENCE))
				return;
			
			// delete old
			if (delete("movie_tag_rate", "") < 0)
			{
				delete_last_invocation(invocation_code.USER_PREFENCE);
			}
			else
			{
				// perform new
				if (run_querey("INSERT INTO movie_tag_rate (idMovie, idTag, rate) " +
										" SELECT idMovie, idTag, round(avg(rate)) as rate" +
										" FROM user_tag_movie " +
										" GROUP BY idMovie, idTag") < 0)
						delete_last_invocation(invocation_code.USER_PREFENCE);
			}
		} 
		catch (SQLException e) {
			// Do nothing, somone else will run this again
		}
	}

}


