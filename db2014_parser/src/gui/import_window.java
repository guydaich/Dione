package gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class import_window extends Shell
{
	static import_window import_win = null;

	public import_window(final Display display)
	{
		super(display, SWT.SHELL_TRIM & (~SWT.RESIZE) & (~SWT.MAX));
		import_win = this;
		
		this.setSize(250, 230);
		this.setText("Import Data");
				
		this.setLayout(new FormLayout());
		
		final Color import_window_color = display.getSystemColor(SWT.COLOR_GRAY);
		this.setBackground(import_window_color);
		
		this.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e) 
			{
				import_window_color.dispose();
			}		
		});
		
		
		
		//window background
		String imgURL = ".\\src\\gui\\images\\blue_300.jpg";
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
		
		
		
		//headline label
		Label headline_label = new Label(this, SWT.NONE);
		headline_label.setText("Before you run MovieBook on\na new device, a massive data\nimport is needed");
		headline_label.setLayoutData(gui_utils.form_data_factory(225, 65, 10, 10));
		final Font font_headline_label = new Font(display, "Ariel",13, java.awt.Font.PLAIN );
		headline_label.setFont(font_headline_label);
		headline_label.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e) 
			{
				font_headline_label.dispose();
			}		
		});
		
		
		//import button
		Button import_button = new Button(this, SWT.PUSH);
		import_button.setText("Import Data");
		import_button.setLayoutData(gui_utils.form_data_factory(100, 50, 100, 70));
		final Font font_import_button = new Font(display, "Ariel",13, java.awt.Font.PLAIN );
		import_button.setFont(font_import_button);
		import_button.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e) 
			{
				font_import_button.dispose();
			}		
		});

		import_button.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent arg0)
			{
				import_progress_window import_progress_win = new import_progress_window(display);
				import_progress_win.open();
				dispose_import_window();
				
			}
			
			
		});
		
		
		
		//note label
		Label note_label = new Label(this, SWT.NONE);
		note_label.setText("Note: This operation my take a while...");
		note_label.setLayoutData(gui_utils.form_data_factory(225, 20, 160, 10));
		final Font font_note_label = new Font(display, "Ariel",10, java.awt.Font.PLAIN );
		note_label.setFont(font_note_label);
		note_label.addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e) 
			{
				font_note_label.dispose();
			}		
		});
		
		
		
	}
	
	

private static void dispose_import_window()
{
	import_win.dispose();
}
	

protected void checkSubclass()
{
}
	
}