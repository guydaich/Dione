package gui;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import config.config;


public class all_tabs_window extends Shell
{
	
	config config = new config();
	int window_height = config.get_window_height();
	int window_width = config.get_window_width();
	
	public all_tabs_window(final Display display)
	{
		super(display, SWT.SHELL_TRIM & (~SWT.RESIZE) & (~SWT.MAX));
			
		this.setSize(window_width, window_height);
		
		System.out.println("tabs win creating...");
		
		
		//String currnt_user_str = get_current_username();
		String current_user_str = "some username";
		this.setText("MovieBook - Logged in As: " + current_user_str);
		
		this.setLayout(new FillLayout());
		TabFolder tab_folder = new TabFolder(this, SWT.NONE);
		
		//tab1
		TabItem tab1 = new TabItem(tab_folder, SWT.NONE);
	    tab1.setText("Overview");
	    
	   
	    //tab2
	    TabItem tab2 = new TabItem(tab_folder, SWT.NONE);
	    tab2.setText("Search Movie");
	    
		  
	    //tab3
		TabItem tab3 = new TabItem(tab_folder, SWT.NONE);
	    tab3.setText("Social Zone");
	    
	  	   
	    //tab4
	    TabItem tab4 = new TabItem(tab_folder, SWT.NONE);
	    tab4.setText("Reccomended Movies");
	    
	    
	    //tab5
	    TabItem tab5 = new TabItem(tab_folder, SWT.NONE);
	    tab5.setText("Settings");
	    
	    overview_tab my_overview_tab = new overview_tab(display, tab_folder, SWT.NONE);
	    tab1.setControl(my_overview_tab);
	    
	    search_movie_tab my_search_movie_tab = new search_movie_tab(display, tab_folder, SWT.NONE);
	    tab2.setControl(my_search_movie_tab);
	    
	    social_tab my_social_tab = new social_tab(display, tab_folder, SWT.NONE);
	    tab3.setControl(my_social_tab);
	    
	    recommendation_tab my_recommendation_tab = new recommendation_tab(display, tab_folder, SWT.NONE);
	    tab4.setControl(my_recommendation_tab);
	    
	    settings_tab my_settings_tab = new settings_tab(display, tab_folder, SWT.NONE);
	    tab5.setControl(my_settings_tab);
	    
	    
		this.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e) 
			{
				for(movie_details_window win: gui_utils.movie_windows)
				{
					if(!win.isDisposed())
						win.dispose();
				}
				
				if(gui_utils.pref_win != null)
					if(!gui_utils.pref_win.isDisposed())
					{
						gui_utils.EXIT_ON_LOGIN = false;
						gui_utils.pref_win.dispose();
					}
				if(gui_utils.EXIT_ON_LOGIN == true)
				{
					display.dispose();
					//shachar: app is exiting here
				}
				
				else
					gui_utils.EXIT_ON_LOGIN = true;
			}		
		});
	}
	
	



protected void checkSubclass()
{
}




}