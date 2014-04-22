package gui;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
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
		super(display);
	
		this.setSize(window_width, window_height);
		this.setText("MovieBook");
		
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
	    tab3.setText("Add a Friend");
	    
	  
	   
	    //tab4
	    TabItem tab4 = new TabItem(tab_folder, SWT.NONE);
	    tab4.setText("Tab 4");
	    
	    
	    overview_tab overview_tab = new overview_tab(display, tab_folder, SWT.NONE);
	    tab1.setControl(overview_tab);
	    
	    search_movie_tab search_movie_tab = new search_movie_tab(display, tab_folder, SWT.NONE);
	    tab2.setControl(search_movie_tab);
	    
	    social_tab search_friends_tab = new social_tab(display, tab_folder, SWT.NONE);
	    tab3.setControl(search_friends_tab);
	    
	    recommendation_tab recommendation_tab_wndow = new recommendation_tab(display, tab_folder, SWT.NONE);
	    tab4.setControl(recommendation_tab_wndow);
	}
	
	public static void main(String args[])
	{
		Display display = new Display();
		all_tabs_window tabs_win = new all_tabs_window(display);
		
		tabs_win.open();
		
		while (!display.isDisposed()) 
		{
			 if (!display.readAndDispatch())
			 {
				 display.sleep();
			 }
		}
	}



protected void checkSubclass()
{
}


}