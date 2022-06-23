/*****************************************************************
* The Turtle System program allows the user to insert			**
* text commands in a command bar and execute simple commands	**
* to draw into a canvas with a turtle.							**
* This is the extension of the super class Turtle Graphics. 	**
******************************************************************
* @author  Andrea La Fauci De Leo								**
*****************************************************************/
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.io.PrintWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.ac.leedsbeckett.oop.TurtleGraphics;

@SuppressWarnings("serial")
public class TurtleSystem extends TurtleGraphics
{	
	//	Initialising a Save and load button
	JButton save = new JButton("Save");
	JButton load = new JButton("Load");
	JButton help = new JButton("Help");
	JButton changeTurtle = new JButton("Change Turtle");
	
	boolean saveFlag = false;	// Save flag to check if the image was saved
	
	//	Initialising the new frame icon for the program
	ImageIcon icon = new ImageIcon("turtleIcon.png");
	
	// Default status of the pen DOWN when it is True
	boolean penActive = true;		
	//----------------------------------------
	//	Constructor of the TurtleSystem class
	//----------------------------------------
	public TurtleSystem()
    {	
		JFrame MainFrame = new JFrame("Turtle Graphics");  //create a frame to display the turtle panel on
		
		MainFrame.setIconImage(icon.getImage());
		MainFrame.setLayout(new FlowLayout(FlowLayout.CENTER));
		//////////////////////////////////////////////////////////
		//	Adding the Save and Load buttons in the margins 	//
		//////////////////////////////////////////////////////////
		MainFrame.add(load);
        MainFrame.add(save);						
        MainFrame.add(help);
        MainFrame.add(changeTurtle);
		
        //////////////////////////////////
        //	Displaying the Main Frame	//
        //////////////////////////////////
        MainFrame.add(this);						//"this" is this object that extends turtle graphics so we are adding a turtle graphics panel to the frame
        MainFrame.pack();							//set the frame to a size we can see
        MainFrame.setVisible(true);                //now display it
        
        menu(MainFrame);
        
        saveLoadButtons();					//	Initialising the Action Listeners for the buttons
        helpButton();						//	Initialising the Action Listener for the help button       
        changeTurtleButton();				//	Initialising the ActionListener for the Turtle button       
        newPosition();						//	Default initial position for the turtle
        
    }

	@Override
	public void processCommand(String input)
	{	
		String[] sInput = input.toLowerCase().split(" ");		//	Transforming the input to lower case and splitting it in parts
		String commandString = "", parameterString = "";
		String rString, gString, bString;
		
		int pixel = 0, degrees = 0;			// Default moving pixels or degrees for turning
		int r = 0, g = 0 , b = 0;
		
		//--------------------------------------------------------------------
		//	RGB command If chain to assign the right parameter and convert it
		//--------------------------------------------------------------------
		if(sInput[0].contains("color") && sInput.length == 4)
		{
			commandString = sInput[0];
			rString = sInput[1];
			gString = sInput[2];
			bString = sInput[3];
			
			//	Checking if they are numbers. If they are assigning into Integer variables
			if(isNum(rString) && isNum(gString) && isNum(bString))
			{
				r = Integer.parseInt(rString);
				g = Integer.parseInt(gString);
				b = Integer.parseInt(bString);
				
				//	Setting the colour bounds. If more the error will be displayed
				if(r > 255 || g > 255 || b > 255)
				{
					displayMessage("Color parameters not in bound. Min: 0 - Max: 255 - Type Help for commands");
					return;
				}
			}
		
			//	If not numbers Display Error
			else if(!isNum(rString) && !isNum(gString) && !isNum(bString))
			{
				displayMessage("Non numeric parameters - Type Help for commands");
				return;
			}
			else
			{
				displayMessage("Too many parameters - Type Help for commands");
				return;
			}
		}	//	End of the IF statement for RGB
		
		//-------------------------------------------------------------------------------
		//	Assigning the parameters to the variables based on the length of the command
		//-------------------------------------------------------------------------------
		
		switch(sInput.length)
		{
		case 1:
			commandString = sInput[0];
			parameterString = null;
			break;
			
		case 2:
			commandString = sInput[0];
			parameterString = sInput[1];
			break;
			
		case 4:
			break;
			
		default:
			displayMessage("Invalid command - Too long - Type Help for commands");
			return;			
		}
		
		//------------------------------------
		//	Converting parameters to integers
		//-------------------------------------
		
		if(commandString.contains("turn") && hasParameters(commandString))
		{
			degrees = parameterConverter(parameterString);
		}
		else if(isCommand(commandString) && hasParameters(commandString))
		{
			pixel = parameterConverter(parameterString);
		}
		
		//-----------------//
		// Handling errors //
		//-----------------//
	
		// Non valid Command
		if(!isCommand(commandString))
		{
			displayMessage("Error - Invalid command - Type Help for commands");
			return;
		}
		
		//	Parameter not inserted for commands that are not taking parameters
		else if(isBasicCommand(commandString) && parameterString == null)
		{
			displayMessage("Error - Command Exists. Parameter not inserted - Type Help for commands");
			
		}
			
		//	Non numeric Parameter
		else if(sInput.length > 1 && !isNum(parameterString))
		{
			displayMessage("Error - Non numeric parameter  - Type Help for commands");
			
		}
		
		//	Bounds parameters
		if (!inBound(pixel, degrees)) return;
		
		String stdCommand = standardizingCommand(input);		// Standardising the command to match
		String colourInUse;		// Current colour in Use
		
    	//-----------------------------
		//	Assigning movement commands
    	//-----------------------------
		
    	if (stdCommand.equals("forward" + pixel))
    		{
    			forward(pixel);
    			displayMessage("Going forward by " + pixel);
    		}
    	
    	else if (stdCommand.equals("backward" + pixel))
    		{
    			backward(pixel);
    			displayMessage("Going backward by " + pixel);
    		}	
    	
    	else if (stdCommand.equals("turnright" + degrees))
    	{
    		turnRight(degrees);
    		displayMessage("Turning Right by " + degrees);
    	}
    	
    	else if (stdCommand.equals("turnleft" + degrees))
    	{
    		turnLeft(degrees);
    		displayMessage("Turning Left by " + degrees);
    	}
    	
    	//	Custom pen colour command
    	else if(stdCommand.equals("color"+ r + g + b))
    	{
    		setPenColour(r, g, b);
    		displayMessage("Custom Color set" + ": " + r + " - " + g + " - " + b);	// Printing the color in use
    	}
    	
    	
    	//-------------------------------------
    	//	Circle command with specific radius
    	//-------------------------------------
    	else if (stdCommand.equals("circle"+ pixel))
    	{
    		circle(pixel);
    		displayMessage("This is a circle with radius " + pixel);
    	}
    	
 	
    	//----------------------------------
		//	Assigning miscellaneous commands
    	//----------------------------------
    	switch (stdCommand)
		{
    		case "":
    			super.about();	//	Calling the default about method of the super class
    			displayMessage("Turtle Animation v.1");
    			break;
    			
			case "about":
				displayMessage("Not a Turtle Animation");
				about();
				break;
				
			case "new":
				
				if(!this.saveFlag)		//	If the image is not saved the user will be prompted
				{
					try
					{
						promptSavingGUI();
					} catch (IOException e)
					{
						
						displayMessage("Error");
					}
					penStatus(penActive);	// 	Status of the pen, default down
					newCanvasReset();			//	Starting a new canvas
					setTurtleImage("turtleDefault.png");		//	Reset the Turtle to the default image
					break;
				}

			else
				{	
					this.saveFlag = false;	//	If the image is saved the user will not be prompted
					penStatus(penActive); 	// Status of the pen but default down
					newPosition();
					setTurtleImage("turtleDefault.png");
					break;
				}
								
			case "reset":				//	Placing the turtle in the middle ready to write
				defaultPosition();
				displayMessage("Reset to default position");
				break;
			
			//--------------
			//	Pen Command
			//--------------
			
			case "penup":
	    		penUp();
	    		penStatus(penActive == false);
	    		break;
	    		
			case "pendown":
				penDown();
				penStatus(penActive);
				break;
			
			//--------------------
			//	Colour Settings
			//--------------------
			
			case "red":
				colourInUse = "Red";
				setPenColour(Color.red);
				displayMessage("Colour set to " + colourInUse);
				break;
				
			case "blue":
				colourInUse = "Blue";
				setPenColour(Color.blue);
				displayMessage("Colour set to " + colourInUse);
				break;
				
			case "green":
				colourInUse = "Green";
				setPenColour(Color.green);
				displayMessage("Colour set to " + colourInUse);
				break;
				
			case "white":
				colourInUse = "White";
				setPenColour(Color.white);
				displayMessage("Colour set to " + colourInUse);
				break;
				
			case "yellow":
				colourInUse = "Yellow";
				setPenColour(Color.yellow);
				displayMessage("Colour set to " + colourInUse);
				break;
			
			case "black":
				colourInUse = "Black";
				setPenColour(Color.black);
				displayMessage("Colour set to " + colourInUse);
				break;
			
			//-----------------------------
			//	Saving and Loading commands
			//-----------------------------
			case "save":
			try
			{
				saveToImage();
			} 
			catch (IOException e1)
			{
				displayMessage("Cannot save");
			}
				this.saveFlag = true;
				break;
				
			case "load":
				try 
				{
					promptSavingGUI();
					loadToFile();
					setTurtleImage("turtleDefault.png");
				}
				catch(IOException e)
				{
					displayMessage("Image cannot be loaded");
				}
				this.saveFlag = false;
				break;
				
			//--------------------------
			//	Assigning shape commands
		    //--------------------------
			case "triangle":
				triangle();
				break;
				
			case "square":
				square();
				break;
				
			case "star":
				star();
				break;
				
			case "peace":
				peace();
				break;
			
			case "help":
				try
				{
				writeCommandsToFile();
				helpScreen();
				}
				catch(Exception e)
				{
					displayMessage("Error");
				}
				break;
			
			case "turtle":
				changeImage();
		}
    	
    	//---------------
		// Bounds Screen
    	//---------------
		int xTurtlePos = getxPos(), yTurtlePos = getyPos();
		final int T_X_MAX = 1000;
		final int T_Y_MAX = 400;
		
		//	y Position Screen boundaries
		if(yTurtlePos > T_Y_MAX)
		{
			setyPos(T_Y_MAX);
			displayMessage("Error: Turtle Out of bound, it cannot move further.");
		}
		else if(yTurtlePos < 0)
		{
			setyPos(0);
			displayMessage("Error: Turtle Out of bound, it cannot move further.");
		}
		
		//	x Position Screen boundaries
		if(xTurtlePos > T_X_MAX)
		{
			setxPos(T_X_MAX);
			displayMessage("Error: Turtle Out of bound, it cannot move further.");
		}
		else if(xTurtlePos < 0)
		{
			setxPos(0);
			displayMessage("Error: Turtle Out of bound, it cannot move further.");
		}

	}	//	Process command end method
	
	//-------------------------------------------------
	//	Method that creates an array of valid commands
	//-------------------------------------------------
	public ArrayList<String> arrayCommands()
	{
		String[] commands = 
			{	
					//	Movement commands
					"forward", "turnright", "turnleft", 
					"backward", "backwards", 
								
					//	Miscellaneous commands
					"reset", "about", "new", "", "help", "turtle",
								
					//	Pen Commands and colours
					"penup", "pendown",
					"color", "black", "yellow", 
					"blue", "green","red", "white", 
								
					//	Save and load commands
					"save", "load", 
								
					//	Shapes
					"triangle", "square", "circle", 
					"star", "peace"
			};
		ArrayList<String> commandList = new ArrayList<String>();
		for(int i = 0; i < commands.length; i++)
		{
			commandList.add(commands[i]);
		}
		return commandList;
	}		

	//--------------------------------------------------
	//	Method that checks if the command is a valid one
	//--------------------------------------------------   
	public boolean isCommand(String command)
	{
		ArrayList<String> listOfCommands = new ArrayList<String>();
		listOfCommands = arrayCommands();
		
		if (listOfCommands.contains(command))
			return true;
		else return false;
			    	
	}

	//--------------------------------
	//	Method to check basic command
	//-------------------------------
	public boolean isBasicCommand(String command)
	{
		if(command.contains("turn") || command.contains("forward") || command.contains("back") || command.contains("circle"))
			return true;
		else
			return false;
	}

	//---------------------------------------------------------------------------------
	//	Method that merges the command passed to make it lower case and removing spaces
	//---------------------------------------------------------------------------------
	public String standardizingCommand(String command)
	{
		String standardCommand = command.toLowerCase().replace(" ", "");
		return standardCommand;
	}

	//---------------------------------------------------------------
	//	Method that checks if the parameter can be converted into Int
	//---------------------------------------------------------------
	public boolean isNum(String parameter)
	{
		try
		{
			Integer.parseInt(parameter);
			return true;
		}
		catch (NumberFormatException error)
		{
			return false;
		}
	}

	//---------------------------------------------------
	//	Method that checks if the command has parameters
	//---------------------------------------------------
	public boolean hasParameters(String commandString)
	{
		if(commandString.length() > 1)
			return true;
		else
			return false;
	}

	//-------------------------------------------------------------------
	//	Method that returns the parameter from string converted if isNum
	//-------------------------------------------------------------------
	public int parameterConverter(String parameterString)
	{
		int parameter = 0;
		try
		{
			parameter = Integer.parseInt(parameterString);
			return parameter;
		}
		catch(Exception error)
		{
			//displayMessage("Non numeric parameter");
			return parameter;
		}    	
			
	}

	//--------------------------------------------------
	//	Method that checks if the parameter is in bound
	//--------------------------------------------------
	public boolean inBound(int parameter, int degrees)
	{	
		if (parameter > 300 || parameter < 0)
		{
			displayMessage("Invalid parameter: The parameter must be between 0 and 300");
			return false;
		}
		
		if (degrees < 0 || degrees > 360)
		{
			displayMessage("Invalid parameter: The parameter must be between 1 and 360 degrees");
			return false;
		}
		else return true;		
	}
	
	//-----------------------------------------------------------
    //	Method which indicates the status of the pen: Up or Down
    //-----------------------------------------------------------
    public void penStatus(boolean penStatus)
    {
    	if(penStatus)
    	{
    		displayMessage("The pen is DOWN - ready to write");
    	}
    	else
    	{
    		displayMessage("The pen is UP - Will not write");
    	}
    }
    
    //--------------------------------------------
    //	Method to allow the turtle to do Backwards
    //--------------------------------------------
    public void backward(int pixel)
    {
    	turnRight(180);
		forward(pixel);
		turnRight(180);
    }
    
    //-------------------------------------------------------------------------------------
    //	Method which returns the Turtle in the default position
    //	faced down in the middle of the canvas and reset the canvas with a background colour
    //--------------------------------------------------------------------------------------
	public void newPosition()
	{
	    setBackground_Col(Color.black);
		clear();
		clearInterface();
		reset();
		turnLeft();
		penDown();
	}
	
	public void newCanvasReset()
	{
		 	setBackground_Col(Color.black);
			clear();
			clearInterface();
	}

    //----------------------------------------------------------------------------------
    //	Method to reset the position of the turtle in the middle faced down and pen down
    //----------------------------------------------------------------------------------
	public void defaultPosition()
	{
		reset();
		penDown();
		turnLeft(90);
	}
	
    //-------------------------------------------
    //	Method to reset the display status board
    //-------------------------------------------
    public void resetStatusMessage()
    {
    	displayMessage("");
    }

    //-----------------------------------------
    //	Method which set both x and y positions
    //-----------------------------------------
    public void position(int x, int y)
    {
    	setxPos(x);
    	setyPos(y);
    }
        
    //---------------------------------
    //	A method which saves the image	
    //---------------------------------
    
    public void saveToImage() throws IOException
    {
    	try
    	{
    	JFileChooser fc = new JFileChooser();
    	fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    	fc.setDialogTitle("Save File");
    	fc.setFileFilter(new FileNameExtensionFilter("*.png", "png"));	// Restricting to PNG extension
    	
    	int status = fc.showSaveDialog(null);
    	if(status == JFileChooser.APPROVE_OPTION)
    	{
    		File outputFile = fc.getSelectedFile();
    		String fileName = outputFile.getAbsolutePath();
    		
    		
    		BufferedImage buffImage = getBufferedImage();
    		
    		if(!outputFile.exists() && !fileName.endsWith(".png"))
    		{
    			outputFile = new File(fileName + ".png");	// Adding the extension if not specified from the User
    			ImageIO.write(buffImage, "png", outputFile);
    			JOptionPane.showMessageDialog(null, outputFile.getName() + " Saved");
    			
    		}
    		else
    		{
    			ImageIO.write(buffImage, "png", outputFile);
    			JOptionPane.showMessageDialog(null, outputFile.getName() + " Saved");
    		}
    	}
    
    	}
    	catch(Exception e)
    	{
    		JOptionPane.showMessageDialog(null, "File not saved.");
    	}
    }
    //-----------------------------
    //	A Method to load the image
    //-----------------------------
    
    public void loadToFile() throws IOException
    {
    	JFileChooser fc = new JFileChooser();
    	fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    	fc.setDialogTitle("Load File");
    	int status = fc.showOpenDialog(null);
    	if (status == JFileChooser.APPROVE_OPTION)
    	{
    		File selectedFile = fc.getSelectedFile();
    		BufferedImage bImage = ImageIO.read(selectedFile);    		
    		
    		try
    		{		    			
    			setBufferedImage(bImage);		// Loading the image on screen
    			JOptionPane.showMessageDialog(null, selectedFile.getName() + " Loaded"); 	// Successful prompt which tells which image was loaded			   			
    		}
    		catch(Exception e)
    		{
    			displayMessage("Image cannot be loaded");
    		}
    	}    	   	   	
    }
    
    //--------------------------------------------------
    //	Method which prompts the user to save the image
    //--------------------------------------------------
    public void promptSavingGUI() throws IOException
    {
    	int reply = 0;
    	JFrame optionFrame = new JFrame();
  	
    	if(!this.saveFlag)
    	{
    		reply = JOptionPane.showConfirmDialog(optionFrame, "Current image not saved\n" + "Do you want to save it?", "Turtle Graphics", JOptionPane.YES_NO_OPTION);
    		if (reply == JOptionPane.YES_OPTION)
			{
    			saveToImage();
    			JOptionPane.showMessageDialog(null, "Image Saved");			
			}    		
    		else
    		{
    			JOptionPane.showMessageDialog(null, "Image Not Saved");
    		}
    	
    	}
    }
      
    //--------------------------------------
	//	Method to override the about method
	//--------------------------------------
	@Override
	public void about() 
	{
		Thread thread = new Thread() {
			public void run()
			{    
				setTurtleImage("spider.png");
				setTurtleSpeed(6);
		    	
				penDown();
		    	setPenColour(255,255,255);
		    	
		    	int j = 0;
		    	while(j < 200)
		    	{
		    		int i = 50;
		    		turnRight(45);
		    		forward(i);
		    		i--;
		    		j++;
		    		
		    		while(j < 300)
		    		{
		    			turnRight(45);
	    	    		forward(i);
	    	    		i--;
	    	    		j++;
		    		}   	    		
		    	}
		    	reset();
		    	turnLeft();    	
		    	
		    	Graphics g;
		    	g = getGraphicsContext();
		    	g.drawString("Spider Graphics animated", 800, 150);  
		    	g.drawString("Definitely not a turtle animation", 800, 130);
		    	repaint();   	    	
			}
		};
		thread.start();    	    	
	}

	//--------------------------------------
    //	Method to draw a circle of radius n 
    //--------------------------------------
    @Override
    public void circle(int rad)
    {   	
    	Graphics g = getGraphicsContext();
    	g.setColor(PenColour);
    	g.drawOval((getxPos()- (rad/2)), (getyPos() - (rad/2)), rad, rad);	//	In order to have the circle around the turtle.
    	
    }
    
    //-------------------------------------------------
    //	Method to set the pen colour with 3 parameters
    //-------------------------------------------------
    public void setPenColour(int r, int g, int b)
    {
    	try 
    	{
    	Color rgbVar = new Color(r, g, b);	//	Creating a colour using the giving RGB values
    	setPenColour(rgbVar);				//	Using the built-in method to set that colour as the active one
    	}
    	catch(IllegalArgumentException error)
    	{
    		displayMessage("Color parameters not in bound. Min: 0 - Max: 255");
    	}
    	
    }
 
    //----------------------------
	//	Method to draw a triangle
	//----------------------------
	public void triangle()
	{
		reset();
		penDown();    	
		setyPos(300);
		setxPos(450);
		penDown();
		forward(100);
		turnRight(90);
		forward(200);
		turnRight(135);
		forward(283);
		turnRight(135);
		forward(100);
		displayMessage("This is a Triangle");
	}

	//-------------------------
	//	Method to draw a Square
	//------------------------- 
	public void square()
	{
		reset();
		penDown();
		int side;
		for(side = 4; side > 0; side--)
		{
			forward(100);
			turnRight(90);
		}
		displayMessage("This is a Square");
	}

	//-------------------------
	//	Method to draw a Star
	//------------------------- 
	public void star()
	{
		reset();
		penDown();
		
		turnRight(45);
		forward(100);
		
		turnRight(135);
		forward(100);
		
		turnLeft(45);
		forward(100);
		
		turnRight(90);
		forward(100);
		
		turnLeft(45);
		forward(100);
		
		turnRight(135);
		forward(100);
		
		turnLeft(90);
		forward(100);
		
		turnRight(145);
		forward(165);
		
		turnLeft(25);
		forward(165);
		
		turnRight(140);
		forward(100);
		
		penUp();
		forward(200);
		
		
		
	}
	
	//----------------------------------
	//	Method to draw the peace symbol
	//------------------------- --------
	public void peace()
	{
		defaultPosition();
		
		circle(300);
		drawLine(this.PenColour, 500, 200, 500, 50);		// Set the current pen colour and draw a line
		forward(150);
		backward(150);
		turnRight(45);
		forward(150);
		penUp();
		backward(150);
		penDown();
		turnLeft(90);
		forward(150);
		Graphics text = getGraphicsContext();
		text.drawString("PEACE!", 150, 150);
		displayMessage("Peace symbol");    	
	}

	//-----------------------------------
    //	Method to initialise the buttons
    //-----------------------------------
    public void saveLoadButtons()
    {
        save.addActionListener((ActionListener) new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					saveToImage();
					saveFlag = true;
				} 
				catch (IOException e1)
				{
					displayMessage("Cannot save");
				}					
					
			}
		});
		load.addActionListener((ActionListener) new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				try 
				{
					promptSavingGUI();
					loadToFile();
					setTurtleImage("turtleDefault.png");
					saveFlag = false;
				}
				catch(IOException e2)
				{
					displayMessage("Image cannot be loaded");
				}				
			}
		}); 
    }// End of the Method body

    //-------------------------------------------
    //	Method which implements the button help
    //-------------------------------------------
    public void helpButton()
    {
    	help.addActionListener((ActionListener) new ActionListener()
    			{
    				public void actionPerformed(ActionEvent e)
    				{
    					try
    					{
    					writeCommandsToFile();
    					helpScreen();
    					}
    					catch(Exception error)
    					{
    						displayMessage("Error");
    					}
    				}
    			});
    }
    
    //--------------------------------------------------
    //	Method which implements the change turtle button
    //--------------------------------------------------
    public void changeTurtleButton()
    {
    	changeTurtle.addActionListener((ActionListener) new ActionListener()
    			{
    				public void actionPerformed(ActionEvent e)
    				{
    					changeImage();
    				}
    			});
    }
    //-------------------------------------------------------------
    //	Method which writes the available commands into a TXT file
    //-------------------------------------------------------------
    public void writeCommandsToFile() throws IOException
	{
		File commandList = new File("commands.txt");
		FileWriter fw = new FileWriter(commandList);
		PrintWriter pw = new PrintWriter(fw);
		
		pw.println("- Help: \t\tShows the available commands");
		pw.println("- Forward <parameter>: \tMove forward by <parameter>");
		pw.println("- Backward <parameter>: \tMove backwards by <parameter>");
		pw.println("- TurnRight <parameter>: \tTurn right by <parameter> degrees");
		pw.println("- TurnLeft <parameter>: \tTurn left by <parameter> degrees");
		pw.println("- penup: \t\tset the pen up so it will not write");
		pw.println("- pendown: \t\tset the pen down so it will write");
		pw.println("- reset: \t\tReset the turtle to the default position");    	
		pw.println("- new: \t\tClear the canvas keeping the Turtle position");
		pw.println("- about: \t\tStart a graphic animation");
		pw.println("- black: \t\tSet the pen color to Black");   	
		pw.println("- yellow: \t\tSet the pen color to Yellow");
		pw.println("- blue: \t\tSet the pen color to Blue");
		pw.println("- green: \t\tSet the pen color to Green");
		pw.println("- red: \t\tSet the pen color to Red");
		pw.println("- white: \t\tSet the pen color to White");
		pw.println("- color <col#> <col#> <col#>: \tSet the pen color to <col#> <col#> <col#> - MAX 255");    	
		pw.println("- triangle: \t\tThe turtle will draw a Triangle");
		pw.println("- square: \t\tThe turtle will draw a Square");
		pw.println("- circle <rad>: \t\tA Circle with radius <rad> will be drew");
		pw.println("- peace: \t\tThe symbol of Peace will appear on screen");    	
		pw.println("- save: \t\tWill save the image on screen");
		pw.println("- load: \t\tWill load the previous saved image");
		pw.println("- turtle:\t\tWill change the turtle image with one of your choice");
		pw.close();
	}

	//-----------------------------
    //	Method for the help screen	
    //-----------------------------
    public void helpScreen() throws FileNotFoundException, IOException
    {   
    	
    	JFrame helpFrame = new JFrame("Command List");
    	JTextArea helpGui = new JTextArea();
    	
    	
    	helpGui.setBounds(1, 1, 500, 500);
    	helpGui.setEditable(false);
    	
    	try
    	{
    	helpGui.read(new BufferedReader(new FileReader("commands.txt")), null);
    	}
    	catch(Exception e)
    	{
    		writeCommandsToFile();
    	}
    	
    	helpFrame.add(helpGui);
    	
    	helpFrame.setIconImage(icon.getImage());
    	helpFrame.setSize(500, 450);
    	helpFrame.setLayout(null);
    	helpFrame.setVisible(true);
    	
    
    	
    }

    //------------------------------------------------
    //	Method to change image with the specified one
    //------------------------------------------------
    public void changeImage()
    {
    	JFileChooser fc = new JFileChooser();
    	fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    	fc.setDialogTitle("Choose a picture");
    	fc.setFileFilter(new FileNameExtensionFilter("*.png", "png"));
    	
    	int status = fc.showOpenDialog(null);
    	if(status == JFileChooser.APPROVE_OPTION)
	    	{
	    	File newTurtle = fc.getSelectedFile();
	    	setTurtleImage(newTurtle.getAbsolutePath());
	    	}
    }
    
    //----------------------------------------------
    //	Method which adds a menu to the frame passed
    //----------------------------------------------
    public void menu(JFrame frame)
    {	
    	//	Initialising the Menu Bar and the different menu in it	//
    	JMenuBar menubar = new JMenuBar();
    	JMenu file = new JMenu("File");   	
    	JMenu commands = new JMenu("Commands");
    	JMenu help = new JMenu("Help");
    	
    	//	Sub Menu	//
    	JMenu movement = new JMenu("Movement");
    	JMenu color = new JMenu("Color");
    	JMenu shapes = new JMenu("Shapes");
   	
    	//	Items in Sub Menu
    	//	FILE	//
    	String[] fileContainer = {"New", "Save", "Load"};	// Container with the Menu Items
    	
    	//	COMMANDS	//
    	String[] movementSub = {"Forward","Backward", "Turn Right", "Turn Left", "Pen Up", "Pen Down", "Reset"};
    	String[] colorSub = {"Black", "Yellow", "Blue", "Green","Red", "White"};
    	String[] shapesSub = {"Triangle", "Square", "Circle", "Star", "Peace"};
    	
    	//	HELP	//
    	String[] helpSub = {"Command List", "Default About", "New About"};
    	
    	//	Indexes for each Item Array
    	int i = 0, x = 0, y = 0, j = 0, k = 0;
    	
    	//////////////////////////////////
    	//	FILE - Populating the Menu	//
    	//////////////////////////////////
    	JMenuItem[] subFile	= new JMenuItem[fileContainer.length];
    	for(final String item : fileContainer)
    	{
    		subFile[i] = file.add(item);
    		i++;
    	}
    	
    	//////////////////////////////////   	
    	//	Adding the sub Menu MOVEMENT
    	//////////////////////////////////
    	commands.add(movement);
    	    	
    	//////////////////////////////////////////
    	//	MOVEMENT - Populating the Sub Menu	//
    	//////////////////////////////////////////
    	JMenuItem[] subMovement = new JMenuItem[movementSub.length];
    	for(final String subItem : movementSub)
    	{
    		subMovement[j] = movement.add(subItem);
    		j++;
    	}
    	//////////////////////////////////
    	//	Adding the sub Menu COLOR	//
    	//////////////////////////////////
    	commands.add(color);	
    	
    	//////////////////////////////////////////
    	//	COLOR - Populating the Sub Menu		//
    	//////////////////////////////////////////
    	JMenuItem[] subColor = new JMenuItem[colorSub.length];
    	for(final String subItem : colorSub)
    	{
    		subColor[x] = color.add(subItem);
    		x++;
    	}    	
    	//////////////////////////////////
    	//	Adding the sub Menu SHAPES	//
    	//////////////////////////////////
    	commands.add(shapes);
    	
    	//////////////////////////////////////////
    	//	SHAPES - Populating the Sub Menu	//
    	//////////////////////////////////////////
    	JMenuItem[] subShape = new JMenuItem[shapesSub.length];
    	for(final String subItem : shapesSub)
    	{
    		subShape[y] = shapes.add(subItem);
    		y++;
    	}
    	//////////////////////////////////////
    	//	HELP - Populating the Sub Menu	//
    	//////////////////////////////////////
    	JMenuItem[] subHelp = new JMenuItem[helpSub.length];
    	for(final String subItem : helpSub)
    	{
    		subHelp[k] = help.add(subItem);
    		k++;
    	}
    	
    	//////////////////////////////////////////////////////
    	//	Action Listener FILE - 0 New. 1 Save. 2 Load.	//
    	//////////////////////////////////////////////////////
    	
    	//	New		//
    	subFile[0].addActionListener((ActionListener) new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if(!saveFlag)		//	If the image is not saved the user will be prompted
				{
					try
					{
						promptSavingGUI();
					} catch (IOException error)
					{
						
						displayMessage("Error");
					}
					penStatus(penActive);	// 	Status of the pen, default down
					newCanvasReset();			//	Starting a new canvas
					setTurtleImage("turtleDefault.png");		//	Reset the Turtle to the default image
					
				}

			else
				{	
					saveFlag = false;	//	If the image is saved the user will not be prompted
					penStatus(penActive); 	// Status of the pen but default down
					newPosition();
					setTurtleImage("turtleDefault.png");
					
				}
			}
		});
    	
    	//	Save	//
    	subFile[1].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			try
				{
					saveToImage();
					saveFlag = true;
				} 
				catch (IOException e1)
				{
					displayMessage("Cannot save");
				}
    		}
    	});
    	
    	//	Load	//
    	subFile[2].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			try 
				{
					promptSavingGUI();
					loadToFile();
					setTurtleImage("turtleDefault.png");
					saveFlag = false;
				}
				catch(IOException e2)
				{
					displayMessage("Image cannot be loaded");
				}				
    		}
    	});
 
    	//////////////////////////////////////////////////////////////////////////////////////////////////////////
    	//	Action Listener MOVEMENT - 0 Forward, 1 Backward, 2 Turn Right, 3 Turn Left, 4 Pen Up, 5 Pen Down	//
    	//////////////////////////////////////////////////////////////////////////////////////////////////////////
    	
    	//	Forward		//
    	subMovement[0].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			String parameter = JOptionPane.showInputDialog("How much do you want to move? ");
    			if(!isNum(parameter))
    			{
    				JOptionPane.showMessageDialog(null, "The parameter is not a number.");
    				return;
    			}
    			else
    			{
    				int iParameter = Integer.parseInt(parameter);
    				forward(iParameter);
    			}
    			
    		}
    	});
    	
    	//	Backward	//
    	subMovement[1].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			String parameter = JOptionPane.showInputDialog("How much do you want to move? ");
    			if(!isNum(parameter))
    			{
    				JOptionPane.showMessageDialog(null, "The parameter is not a number.");
    				return;
    			}
    			else
    			{
    				int iParameter = Integer.parseInt(parameter);
    				backward(iParameter);
    			}		
    		}
    	});
    	
    	//	Turn Right	//
    	subMovement[2].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			String parameter = JOptionPane.showInputDialog("How many degrees you want to turn? ");
    			if(!isNum(parameter))
    			{
    				JOptionPane.showMessageDialog(null, "The parameter is not a number.");
    				return;
    			}
    			else
    			{
    				int iParameter = Integer.parseInt(parameter);
    				turnRight(iParameter);
    			}		
    		}
    	});
    	
    	//	Turn Left	//
    	subMovement[3].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			String parameter = JOptionPane.showInputDialog("How many degrees you want to turn? ");
    			if(!isNum(parameter))
    			{
    				JOptionPane.showMessageDialog(null, "The parameter is not a number.");
    				return;
    			}
    			else
    			{
    				int iParameter = Integer.parseInt(parameter);
    				turnLeft(iParameter);
    			}		
    		}
    	});
    	
    	//	Pen Up	//
    	subMovement[4].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			penUp();
    			displayMessage("The pen is UP - It will not write");
    		}
    	});
    	
    	//	Pen Down
    	subMovement[5].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			penDown();
    			displayMessage("The pen is DOWN - Ready to write");	
    		}
    	});
    	
    	//	Reset	//
    	subMovement[6].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			defaultPosition();
    			displayMessage("Reset to default position");
    		}
    	});
    	
    	//////////////////////////////////////////////////////////////////////////////////
    	//	Action Listener COLOR - 0 Black, 1 Yellow, 2 Blue, 3 Green, 4 Red, 5 White.	//
    	//////////////////////////////////////////////////////////////////////////////////
    	
    	//		Black	//
    	subColor[0].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{	
    			String colourInUse;
    			colourInUse = "Black";
				setPenColour(Color.black);
				displayMessage("Colour set to " + colourInUse);
    		}
    	});
    	//    	Yellow	//
    	subColor[1].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			String colourInUse;
    			colourInUse = "Yellow";
				setPenColour(Color.yellow);
				displayMessage("Colour set to " + colourInUse);
    		}
    	});
    	//    	Blue	//
    	subColor[2].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			String colourInUse;
    			colourInUse = "Blue";
				setPenColour(Color.blue);
				displayMessage("Colour set to " + colourInUse);
    		}
    	});
    	//    	Green	//
    	subColor[3].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			String colourInUse;
    			colourInUse = "Green";
				setPenColour(Color.green);
				displayMessage("Colour set to " + colourInUse);
    		}
    	});
    	//    	Red	//
    	subColor[4].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			String colourInUse;
    			colourInUse = "Red";
				setPenColour(Color.red);
				displayMessage("Colour set to " + colourInUse);
    		}
    	});
    	//    	White	//	
    	subColor[5].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			String colourInUse;
    			colourInUse = "White";
				setPenColour(Color.white);
				displayMessage("Colour set to " + colourInUse);
    		}
    	});
    	
    	//////////////////////////////////////////////////////////////////////////////
    	//	Action Listener SHAPES - 0 Triangle 1 Square 2 Circle 3 Star 4 Peace	//
    	//////////////////////////////////////////////////////////////////////////////
    	
    	//	Triangle	//
    	subShape[0].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			triangle();
    		}
    	});
    	//	Square		//
    	subShape[1].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			square();
    		}
    	});
    	//	Circle		//
    	subShape[2].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			String parameter = JOptionPane.showInputDialog("What radius for the circle? ");
    			if(!isNum(parameter))
    			{
    				JOptionPane.showMessageDialog(null, "The parameter is not a number.");
    				return;
    			}
    			
    			else
    			{
    				//	Converting into a number
    				int radius = Integer.parseInt(parameter);
    				
    				//	Checking the bounds
    				if(radius < 0 || radius > 300)
    				{
    					JOptionPane.showMessageDialog(null, "Invalid range\n" + "Must be between 0 and 300");
        				return;
    				}
    				else
    				{	
    					//	If in bounds executing the code
    					circle(radius);
    				}
    			}		
    		}
    	});
    	//	Star		//
    	subShape[3].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			star();
    		}
    	});
    	//	Peace		//
    	subShape[4].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			peace();
    		}
    	});
    	
    	//////////////////////////////////////////////////////////////////////////
    	//	Action Listener HELP - 0 Command List, 1 Default About, 2 New About	//
    	////////////////////////////////////////////////////////////////////////// 	
    	
    	//	Help	//
    	subHelp[0].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
				try
				{
				writeCommandsToFile();
				helpScreen();
				}
				catch(Exception error)
				{
					displayMessage("Error");
				}
    		}
    	});

    	//	Default About	//
    	subHelp[1].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
				TurtleSystem.super.about();
				displayMessage("Turtle Animation v.1");
    		}
    	});
    	
    	//	New About		//
    	subHelp[2].addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
				about();
				displayMessage("Not a Turtle Animation");
    		}
    	});
    	

    	//	Adding the elements to the Menu Bar
    	menubar.add(file);
    	menubar.add(commands);
    	menubar.add(help);

    	//	Adding the elements to the frame.
    	frame.setJMenuBar(menubar);
    	frame.setSize(frame.getPreferredSize());
    	frame.setLayout(null);
    	frame.setVisible(true);
	
    }	// End of the menu(Frame) Method
       

}	//	End of the Turtle System Class Body

