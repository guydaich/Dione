package parser_entities.imdb_parsers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import config.config;


import java.sql.BatchUpdateException;

import java.sql.SQLException;
import java.sql.Statement;

import parser_entities.entity_movie;

import db.db_operations;
import db.db_queries_movies;
import db.db_queries_persons;
import db.jdbc_connection_pooling;


/** parser_src_imdb is initialized with an existing 
 * movie catalog and enriches it with IMDB data.  
 * It populates tag entities, and their relations to movies**/


public abstract class abstract_imdb_parser {
	
	/*parser specific parameters*/
	protected String 	list_start;			/*imdb file list beggining string*/
	protected String 	list_end;			/*imdb file list end string*/
	protected String 	filepath;			/*imdb file to parse */
	protected String 	imdb_object; 		/*object imdb */
	
	/*counters*/
	protected int 		enrich_count = 0;	/*imdb list beggining string*/
	protected int 		reject_count = 0;	/**/
	
	/*entity maps*/
	protected HashMap<String,entity_movie> parser_movie_map;	/* yago movie catalog to be enriched*/
	//protected HashSet<String> parser_language_set;				/* imdb entities - languge*/	 
	//protected HashSet<String> parser_genre_set;					/* imdb entities - genres*/
	//protected HashSet<String> parser_tag_set;					/* imdb entities - tags*/
	
	/*helper maps*/
	private HashMap<String,String> imdb_to_yago;			/*holds all possible imdb names, that are relevant to yago films*/
	//protected HashMap<String,Integer> parser_tag_count_map;	/* handles tag counts, to establish top 10 per movie*/
	protected HashMap<String,String> imdb_name_to_director;	/* maps imdb movie name to imdb director*/ 
	
	protected config properties = new config();
	
	/*genreal constructor, for initial build of misc data*/
	public abstract_imdb_parser(HashMap<String,entity_movie> movie_map)
	{
		/*init movie catalog*/
		this.parser_movie_map = movie_map; 
		this.imdb_to_yago = new HashMap<String,String>();
		this.imdb_name_to_director = new HashMap<String,String>();
	}
	
	/**
	 * overloaded constructor - we have directors, and name maps
	 * @param movie_map
	 * @param director_map
	 * @param imdb_to_yago
	 */
	public abstract_imdb_parser(HashMap<String,entity_movie> movie_map, HashMap<String,String> director_map, 
			HashMap<String,String> imdb_to_yago)
	{
		/*init movie catalog*/
		this.parser_movie_map = movie_map; 
		this.imdb_to_yago = imdb_to_yago;
		this.imdb_name_to_director = director_map;
	}
	
	
	
	/**
	 * populates the YAGO possible name-resolutions to a dictionary, 
	 * we later search here, to find a YAGO film to enrich from IMDB
	 */
	public void map_imdb_yago_names()
	{
		/* maps upper-case movie names, to their real fq-names */
		for (entity_movie movie : parser_movie_map.values())
		{
			/*set translation in hash*/
			imdb_to_yago.put(movie.get_movie_qualified_name().toUpperCase(),movie.get_movie_qualified_name());
			/*add also foreign names*/
			for (String label_fq_name : movie.get_label_fq_names())
					imdb_to_yago.put(label_fq_name.toUpperCase(),movie.get_movie_qualified_name());
		}
	}
	
	public HashMap<String,String> get_imdb_to_yago()
	{
		return this.imdb_to_yago;
	}

	
	/**
	 * splits line in some manner, tries to extract relevant data
	 * and update appropriate datastructures accordingly
	 * @param line - line to handle
	 * @return
	 */
	protected abstract int handle_single_line(String line, BufferedReader br);

	public void parse_imdb_file()
	{
		// assert file exists
		File fl = new File(this.filepath);
		if (this.filepath == null || ! fl.exists() )
			return;

		try {
			FileReader fr = new FileReader(this.filepath);
			BufferedReader br = new BufferedReader(fr);
			String line; 
			try {
				/*read until start*/
				while ((line = br.readLine()) != null)
					if (line.contains(this.list_start))
						break;
				/*parse until EOF*/
				while ((line = br.readLine()) != null)
				{
					/*list end reached - terminate*/
					if (this.list_end != null && line.contains(this.list_end))
						break;
					int ret = handle_single_line(line, br); 
					
					if (ret>=1)
						enrich_count+=ret;
					else if (ret==0) 
						reject_count++;
				}
			}
			catch (Exception ex){}
		}
		catch (Exception ex){}
		System.out.println("added " + this.imdb_object + " to :" + enrich_count);
	}
			
	private String[] get_line_parsing(BufferedReader br)
	{		
		String line;
		int i;
		
		try{
			if((line = br.readLine()) != null) 
			{ 
				/*split next line*/
				line = line.trim();
				String[] splitted_line = line.split("\\t");
				/*check for expected number of parameters*/
				if (splitted_line.length != 2)
					return new String[2];
				/*take "name (year)" part*/
				splitted_line[0] = splitted_line[0].substring(0,splitted_line[0].indexOf(")")+1);  
				
				return splitted_line;
			}
			else
				return null;
		}
		catch (Exception ex){
			System.out.println("error on parsing line:" + ex.getMessage());
		}
		return null;
	}
	
	/*remove bad characters from movie names*/
	protected String clean_imdb_name (String imdb_movie_name)
	{
		imdb_movie_name =imdb_movie_name.replaceAll("\"", "");		
		imdb_movie_name = imdb_movie_name.replaceAll("$#*! ", "");
		imdb_movie_name = imdb_movie_name.replaceAll("$#*! ", "");
		imdb_movie_name = imdb_movie_name.substring(0,imdb_movie_name.indexOf(")")+1);
		return imdb_movie_name;
	}
	
	protected ArrayList<String> get_movie_keys(String imdb_movie_name)
	{
		ArrayList<String> keys = new ArrayList<String>();
		String imdb_year = null;
		if (imdb_movie_name == null || imdb_movie_name.equals(""))
			return keys; 
		
		String imdb_director = null;
		String imdb_name = clean_imdb_name(imdb_movie_name);
		if (imdb_name.indexOf("(") > 0)
		{
			imdb_year = imdb_name.substring(imdb_name.indexOf("(") +1 , imdb_name.indexOf(")"));
			imdb_name = imdb_name.substring(0, imdb_name.indexOf("("));
		}
		imdb_name = imdb_name.trim();
		
		if (imdb_name_to_director.get(imdb_movie_name) == null)
			imdb_director = "NAN";
		else
			imdb_director = imdb_name_to_director.get(imdb_movie_name);
		if (imdb_year == null || imdb_year.equals(""))
			imdb_director = "NAN";
		
		/*we create all possible matches for this movie*/
		keys.add((imdb_name + " (" + imdb_year + ") (" + imdb_director +")").toUpperCase());
		keys.add((imdb_name + " (NAN) (" + imdb_director +")").toUpperCase());
		keys.add((imdb_name + " (" + imdb_year + ") (NAN)").toUpperCase());
		keys.add((imdb_name + " (NAN) (NAN)").toUpperCase());
	
		return keys;
		
	}
	

	
	/**
	 * tries all possible keys for this name, and returns a yago movie
	 * if a movie matches
	 * @param imdb_movie_name 
	 * @return
	 */
	protected entity_movie get_movie_by_imdb_name(String imdb_movie_name)
	{
		ArrayList<String> movie_keys  = get_movie_keys(imdb_movie_name);
		String yago_name = null; 
		for (String key : movie_keys)
		{
			yago_name = imdb_to_yago.get(key);
			if (yago_name != null)
				break;
		}
		if (yago_name == null)
			return null;
		entity_movie movie = this.parser_movie_map.get(yago_name);
		return movie; 
	}
}

	