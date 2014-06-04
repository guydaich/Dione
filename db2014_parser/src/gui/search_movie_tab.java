package gui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import bl.movie_logics;
import bl.verifier;
import config.config;
//import db.db_queries_movies;


//////// functions the tab is waiting for: ////////

// List<String> get_genres()



////// Listeners to be implemented: //////////

// search_button
// perhaps auto-complete for text fields



public class search_movie_tab extends Composite
{
	
	org.eclipse.swt.widgets.List	movie_list = null;
	List<String> movie_names ;

	public search_movie_tab(final Display display, Composite parent, int style)
	{
		super(parent, style);
		
		
		List<Label> tags_labels = new ArrayList<Label>();	
		List<Label> actors_labels = new ArrayList<Label>();
		
		final List<Text> tags_texts = new ArrayList<Text>();
		final List<Text> actors_texts = new ArrayList<Text>();
				
		
		final List<Button> rating_checkboxes = new ArrayList<Button>();
		final List<Button> genres_checkboxes;
		
		List<String> genres;
		
		//results area
		Composite  results_area = new Composite(this, SWT.NONE);
		
		//results headline
		Label results_headline = new Label(results_area, SWT.BOLD);
		results_headline.setText("Search Results");
		results_headline.setLayoutData(gui_utils.grid_data_factory(-1, -1, 2, -1, SWT.CENTER, -1));
		final Font font_results_headline = new Font(display, "Ariel", 12, SWT.NONE);
		results_headline.setFont(font_results_headline);
		results_headline.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e) 
			{
				font_results_headline.dispose();
			}		
		});
		
		//result list
		movie_list = new org.eclipse.swt.widgets.List(results_area, SWT.V_SCROLL);
		movie_list.setLayoutData(gui_utils.grid_data_factory(353, 90, -1, -1, SWT.FILL, -1, SWT.FILL, -1));
		
		
	
		//window background
		String imgURL = ".\\src\\gui\\images\\blue_640_480_3.jpg";
		final Image background = new Image(display, imgURL);
		this.setBackgroundImage(background);
		this.setBackgroundMode(SWT.INHERIT_DEFAULT);
		this.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e) 
			{
				background.dispose();
			}		
		});
		
		
		FormLayout form_layout_tab = new FormLayout();
		this.setLayout(form_layout_tab);
		final Color color_window = display.getSystemColor(SWT.COLOR_GRAY);
		this.setBackground(color_window);
		this.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e) 
			{
				color_window.dispose();
			}		
		});
		
		
		final Font font_left_labels = new Font(display, "Ariel",12, SWT.NONE );
		this.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e) 
			{
				font_left_labels.dispose();
			}		
		});
		
		
		//headline
		Label headline_label = new Label(this, SWT.NONE);
		headline_label.setText("Search Movie");
		final Font font_headline_label = new Font(display, "Ariel", 21, SWT.BOLD );
		headline_label.setFont(font_headline_label);
		headline_label.setLayoutData(gui_utils.form_data_factory(-1, -1, 0, 210));		
		headline_label.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e) 
			{
				font_headline_label.dispose();
			}		
		});
		
		
		
		//left area
		Composite left_area = new Composite(this, SWT.NONE);
		left_area.setLayoutData(gui_utils.form_data_factory(180, 210, 40, 10));
		GridLayout grid_layout_left_area = new GridLayout(2, false);
		left_area.setLayout(grid_layout_left_area);
		
		
		
		
		
		//title label
		Label title_label = new Label(left_area, SWT.NONE);
		title_label.setText("Title");
		title_label.setFont(font_left_labels);
		title_label.setLayoutData(gui_utils.grid_data_factory(-1, 0, -1, -1, -1, -1));
		
		
		
		//title text
		final Text title_text = new Text(left_area, SWT.BORDER);
		title_text.setLayoutData(gui_utils.grid_data_factory(15, 0, -1, -1, -1, -1));
		title_text.setBounds(100, 300, 50, 100);

		
		
		//director label
		Label director_label = new Label(left_area, SWT.NONE);
		director_label.setText("Director");
		director_label.setFont(font_left_labels);
		director_label.setLayoutData(gui_utils.grid_data_factory(-1, 15, -1, -1, -1, -1));
		
		
		
		//director text
		final Text director_text = new Text(left_area, SWT.BORDER);
		director_text.setLayoutData(gui_utils.grid_data_factory(15, 15, -1, -1, -1, -1));
		
		
		//language label
		Label language_label = new Label(left_area, SWT.NONE);
		language_label.setText("Language");
		language_label.setFont(font_left_labels);
		language_label.setLayoutData(gui_utils.grid_data_factory(-1, 15, -1, -1, -1, -1));
		
		
		//language text
		final Text language_text = new Text(left_area, SWT.BORDER);
		language_text.setLayoutData(gui_utils.grid_data_factory(15, 15, -1, -1, -1, -1));
		
		
		
		
		//actors labels and texts
		for(int i = 0; i < 3; i++)
		{
			actors_labels.add(new Label(left_area, SWT.NONE));
			actors_labels.get(i).setText("Actor" + (i+1));
			actors_labels.get(i).setFont(font_left_labels);
			if(i == 0)
				actors_labels.get(i).setLayoutData(gui_utils.grid_data_factory(-1, 15, -1, -1, -1, -1));
			else
				actors_labels.get(i).setLayoutData(gui_utils.grid_data_factory(-1, 5, -1, -1, -1, -1));
			
			actors_texts.add(new Text(left_area, SWT.BORDER));
			if(i == 0)
				actors_texts.get(i).setLayoutData(gui_utils.grid_data_factory(15, 15, -1, -1, -1, -1));
			else
				actors_texts.get(i).setLayoutData(gui_utils.grid_data_factory(15, 5, -1, -1, -1, -1));
		}
		
		
		
		
		
		//year area
		Composite year_area = new Composite(this, SWT.NONE);
		year_area.setLayoutData(gui_utils.form_data_factory(377, 43, 250, 10));
		GridLayout grid_layout_year_area = new GridLayout(4, false);
		year_area.setLayout(grid_layout_year_area);
		
		
		
		//year from label
		Label year_from_label = new Label(year_area, SWT.NONE);
		year_from_label.setText("Year(from)");
		year_from_label.setFont(font_left_labels);
		year_from_label.setLayoutData(gui_utils.grid_data_factory(-1, 10, -1, -1, -1, -1));
		
		
		
		
		//year from text
		final Text year_from_text = new Text(year_area, SWT.BORDER);
		year_from_text.setLayoutData(gui_utils.grid_data_factory(14, 10, -1, -1, -1, -1));
		
		
		
		
		//year until label
		Label year_until_label = new Label(year_area, SWT.NONE);
		year_until_label.setText("Year(until)");
		year_until_label.setFont(font_left_labels);
		year_until_label.setLayoutData(gui_utils.grid_data_factory(-1, 10, -1, -1, -1, -1));
		
		
		
		
		//year until text
		final Text year_until_text = new Text(year_area, SWT.BORDER);
		year_until_text.setLayoutData(gui_utils.grid_data_factory(9, 10, -1, -1, -1, -1));
		
		
		
		//tags area
		Composite tags_area = new Composite(this, SWT.NONE);
		tags_area.setLayoutData(gui_utils.form_data_factory(197, 155, 95, 190));
		GridLayout grid_layout_tags_area = new GridLayout(2, false);
		tags_area.setLayout(grid_layout_tags_area);
		
		
		
		
		//tags labels and texts
		for(int i = 0; i < 5; i ++)
		{
			tags_labels.add(new Label(tags_area, SWT.NONE));
			tags_labels.get(i).setText("Tag" + (i+1));
			tags_labels.get(i).setFont(font_left_labels);
			tags_labels.get(i).setLayoutData(gui_utils.grid_data_factory(10, 5, -1, -1, -1, -1));
			
			
			tags_texts.add(new Text(tags_area, SWT.BORDER));
			tags_texts.get(i).setLayoutData(gui_utils.grid_data_factory(25, 5, -1, -1, -1, -1));
		}

	
		
		//rating area
		Composite rating_area = new Composite(this, SWT.NONE);
		rating_area.setLayoutData(gui_utils.form_data_factory(200, 60, 40, 190));
		GridLayout grid_layout_rating_area = new GridLayout(5, false);
		rating_area.setLayout(grid_layout_rating_area);
		
		
		
		//rating label
		Label rating_label = new Label(rating_area, SWT.NONE);
		rating_label.setText("Rating (in stars)");
		rating_label.setLayoutData(gui_utils.grid_data_factory(-1, -1, 5, -1, -1, -1));
		final Font font_rating_label = new Font(display, "Ariel", 12, SWT.NONE); 
		rating_label.setFont(font_rating_label);
		rating_label.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e) 
			{
				font_rating_label.dispose();
			}		
		});
		
		
		
		//rating radios
		for(int i = 0; i < 5; i++)
		{
			rating_checkboxes.add(new Button(rating_area, SWT.CHECK));
			rating_checkboxes.get(i).setText(Integer.toString(i+1));
			rating_checkboxes.get(i).setLayoutData(gui_utils.grid_data_factory(5, -1, -1, -1, -1, -1));
		}
		
		
		
		//genres area
		Composite genres_area = new Composite(this, SWT.NONE);
		genres_area.setLayoutData(gui_utils.form_data_factory(210, 320, 40, 410));
		GridLayout grid_layout_genres_area = new GridLayout(2, false);
		genres_area.setLayout(grid_layout_genres_area);
		
		
		
		//genres label
		Label genres_label = new Label(genres_area, SWT.NONE);
		genres_label.setText("Genres");
		genres_label.setLayoutData(gui_utils.grid_data_factory(-1, -1, 2, -1, SWT.CENTER, -1));
		final Font font_genres_label = new Font(display, "Ariel", 12, SWT.NONE);
		genres_label.setFont(font_genres_label);
		genres_label.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e) 
			{
				font_genres_label.dispose();
			}		
		});
		
		
		//genres check-boxes

		try {
			genres = movie_logics.get_genres();
		} catch (SQLException e1) {
			gui_utils.raise_sql_error_window(display);
			genres = new ArrayList<String>();	
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		genres_checkboxes = new ArrayList<Button>();
	
		
		
		for(int i = 0; i < genres.size(); i++)
		{
			Button checkbox = new Button(genres_area, SWT.CHECK);		
			checkbox.setLayoutData(gui_utils.grid_data_factory(5, -1, -1, -1, -1, -1));
			checkbox.setText(genres.get(i));
			genres_checkboxes.add(checkbox);
		}
		
		
		//search button
		Button search_button = new Button(this, SWT.PUSH | SWT.WRAP);
		search_button.setLayoutData(gui_utils.form_data_factory(80, 47, 367, 410));
		search_button.setText("Search");
		final Font font_search_button = new Font(display, "Ariel", 12, SWT.NONE); 
		search_button.setFont(font_search_button);
		search_button.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e) 
			{
				font_search_button.dispose();
			}		
		});
		
		
		final List<String> genres_for_annonymus = genres;
		//Listener
		search_button.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent arg0) {

				
				
				String title = title_text.getText();
				String director =  director_text.getText();
				
				String language = language_text.getText();
			
				Integer year_from = null;
				Integer year_until = null;
				if( !year_from_text.getText().equals(""))
				{
					 year_from  = Integer.parseInt(year_from_text.getText());
				}
				
				if( !year_until_text.getText().equals(""))
				{
					 year_until  = Integer.parseInt(year_until_text.getText());
				}
				
			
	
				
				List<String> actor_list = new  ArrayList<String>();
				get_text(actors_texts, actor_list);
				
				List<String> tags_list = new  ArrayList<String>();
				get_text(tags_texts, tags_list);
				
				
				List<Boolean> rating_radios_text_list = new ArrayList<Boolean>();
				gui_utils.get_text_button(rating_checkboxes, rating_radios_text_list);
						
				List<Boolean> genres_numbers_list =  new ArrayList<Boolean>();
				gui_utils.get_text_button(genres_checkboxes, genres_numbers_list );
				
				boolean[] rating_radios_text = gui_utils.convert_list_array(rating_radios_text_list);
				
				
			
				List<String> desired_genres = gui_utils.get_genres_string(  genres_numbers_list, genres_for_annonymus);
				 try {
					
					if(movie_logics.does_movie_exists(title,director,language,year_from ,year_until ,actor_list,tags_list,
							desired_genres,rating_radios_text))  //to be implemented next on
				
					{
						
					//	.//movie_list.removeSelectionListener(arg0);
						movie_list.removeAll();
						movie_list.removeListener(SWT.MouseUp,list_listener);
						
						//movie_list = new org.eclipse.swt.widgets.List(results_area, SWT.V_SCROLL);
						
						
						 movie_names = movie_logics.get_relevant_movies_names(title,director,language,year_from ,year_until ,actor_list,tags_list,
								desired_genres,rating_radios_text);
					
						
						for(String str: movie_names)
						{
							movie_list.add(str);
						}
							
						/* 
					     * handle selection of movie number selectedItem
					     */
					    
//						movie_list.addSelectionListener(new SelectionAdapter() {
//							public void widgetSelected(SelectionEvent event) {
//						     int selectedItem = movie_list.getSelectionIndex();
//						     try 
//						     {
//						    	 int movie_id = movie_logics.get_movie_id(movie_names.get(selectedItem));
//						    		movie_details_window movie_details = new movie_details_window( display, movie_id);
//							
//									movie_details.open();
//								
//						     } catch (SQLException e) {
//						    		gui_utils.raise_sql_error_window(display);
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//							        
//							        
//							      }
//							});
//			
						
						movie_list.addListener(SWT.MouseUp,list_listener);
						
					//refresh list
						
					}
					
					else //no movie found
					{
						MessageBox messageBox = new MessageBox(display.getActiveShell(), SWT.ICON_WARNING);
						messageBox.setText("No Movie Found");
						messageBox.setMessage("No movie Found. Please try again.");
						messageBox.open();
					}
				} catch (NumberFormatException e) {
					gui_utils.raise_sql_error_window(display);
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					gui_utils.raise_sql_error_window(display);
					e.printStackTrace();
				}
			
	
				 }

		});
		
		
		
		//results area
		results_area.setLayoutData(gui_utils.form_data_factory(377, 120, 300, 10));
		GridLayout grid_layout_results_area = new GridLayout(1, false);
		results_area.setLayout(grid_layout_results_area);
		
		
	}
	

	/*
	 * listener for the movie_list selection
	 */
	Listener list_listener = new Listener(){
		public void handleEvent(Event event) {
	     int selectedItem = movie_list.getSelectionIndex();
	     try 
	     {
	    	 System.out.println(selectedItem );
	    	 System.out.println(movie_names.size());
	    	 if ( selectedItem!=-1)
	    	 {
	    		
	    	 int movie_id = movie_logics.get_movie_id(movie_names.get(selectedItem));
	    		movie_details_window movie_details = new movie_details_window( gui_utils.display, movie_id);
		
				movie_details.open();
	    	 }
	    	 
			
	     } catch (SQLException e) {
	    		gui_utils.raise_sql_error_window(gui_utils.display);
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		        
		      }
	};

	

	

	
	void get_text(List<Text> from,List<String> to)
	{
		for (Text a: from)
		{
			to.add((a.getText()));
		}
	}
	 
	
	

	

}
