package parser_entities.loaders;

import java.sql.SQLException;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


import db.db_queries_movies;

public class tag_loader extends abstract_loader {
	private PreparedStatement insert;
	HashMap<String,Integer> entity_map;
	
	public tag_loader() throws SQLException {
		super();
		this.entity_table_name = "Tag";
		this.entity_map = new HashMap<String,Integer>(); 	
	}

	@Override
	/** check existence against this **/
	protected void sync_update_tables() throws SQLException {
		this.entity_map =  db_queries_movies.get_tag_names_and_ids();
		if (entity_map==null)
			entity_map = new HashMap<String,Integer>();		
	}


	@Override
	protected void set_perpared_statments(Connection db_conn) throws SQLException {
		insert = db_conn.prepareStatement("INSERT INTO tag(tagName) VALUES(?)");
	}

	@Override
	protected int create_statments(Object obj) throws SQLException {
		if (entity_map.get(((String)obj).toString()) == null)
		{
			/*create and add*/
			insert.setString(1, ((String)obj).toString());
			insert.addBatch();
			return 1;
		}
		return 0;
	}

	@Override
	protected int execute_batches() {
		int fail_count=0;
		fail_count += execute_batch(insert);
		return fail_count;
	}



}