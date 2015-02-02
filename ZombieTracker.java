import javax.swing.*;
import javax.imageio.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

public class ZombieTracker extends Canvas implements KeyListener,
													 MouseListener,
													 MouseMotionListener,
													 MouseWheelListener
{
	//iPhone   is 320x480
	//iPhone 4 is 640x960
	public static final int xSize = 320, ySize = 480;
	public static final int tfx = 6, tfy = 70, tfw = 308, tfh = 340;
	public static final int tf2x = 32, tf2y = 96, tf2w = 224, tf2h = 288;
	public static final int statusBar = 20;
	public static final String imageDir = "images";
	public static final String imageEnd = ".png";

	public static final Color Color_darkBlue = new Color(0, 0, 100);
	public static final Color Color_tagBG = new Color(150, 100, 175, 80);
	public static final Color Color_stealthBG = new Color(0, 0, 0, 50);

	public static Font searchFont = new Font(null, 1, 16);
	public static Font messageFont = new Font(null, 0, 14);
	public static Font nameFont = new Font(null, 0, 12);

	public static int numZombies = 25;


	String name = "Me";

	static int[][] locations = {{2645, 1530},		//ECS 342
								{1935, 4015},		//Brodie
								{360, 320},			//Robert
								{904, 3880}};		//Jason

	//ECS X42:
	static int xPos = locations[0][0], yPos = locations[0][1];
	//Brodie:
	//int xPos = locations[1][0], yPos = locations[1][1];
	//Robert:
	//int xPos = locations[2][0], yPos = locations[2][1];
	//Jason:
	//int xPos = locations[3][0], yPos = locations[3][1];


	Stroke stroke = new BasicStroke(3);
	boolean target = false;
	int targetX, targetY;


	public static final int mapRad = 160;
	BufferedImage fullMap;
	int mapWidth, mapHeight;
	int mapX = xPos, mapY = yPos;
	int clickX, clickY;
	double scale = 1.0;
	boolean mouseIn = false;

	Image youAreHere = loadImage("YouAreHere.png");
	Image zombieImage = loadImage("Zombie16.png");
	Image[] tagImages = new Image[3];


	int replyIndex = 0;
	String[] replies = {"Another survivor? Awesome!",
						"I heard there are boats leaving from the Yacht Club.",
						"Can't talk, zombies"};

	Image statusBarImage;
	Image mapOverlayImage;

	Image Loading = loadImage("iPhone.png");
	Image ZB1 = loadImage("ZombieIcon.png");


	int state; //Tags = 1, Map = 2, Network = 3, 0:Loading page
	int tagState; //1:Blank, 2:Tags, 3:Search, 4:Item, 5:TagHelpOverSearch
	int mapState; //1:Map, 2:Ledgend, 3:Panic
	int networkState; //2:Blank, 2:Users, 3:Search, 4:Read chat, 5:Type chat

	boolean stealth = false;


	//Buttons
	LinkedList<Button> buttons;
	LinkedList<Button> buttons2;
	Button Panic;
	Button downButton;
	TextBox activeTextBox;

	LinkedList<Button> loadingButtons;
	LinkedList<Button> emptyList = new LinkedList<Button>();

	LinkedList<Button> tagButtons;
	LinkedList<Button> tagList;
	LinkedList<Button> searchList;
	LinkedList<Button> itemList;
	Button tagMenuButton;
	TextBox tagSearchBox = new TextBox(32, 100, 224, 32, 23);
	String tagSearchString = "";

	LinkedList<Button> mapButtons;
	Button legendButton;

	LinkedList<Button> networkButtons;
	LinkedList<Button> radiusList;
	LinkedList<Button> friendsList;
	LinkedList<Button> chatBoxStub;
	LinkedList<Button> chatList;
	TextBox netChatBox = new TextBox(32, 100, 224, 96, 55);
	LinkedList<String[]> chatHistory;


	//Tags
	LinkedList<Tag> tags = new LinkedList<Tag>();
	LinkedList<Tag> allTags;
	Tag displayTag;

	int tagRadius = 20;


	//Users
	LinkedList<User> users;
	LinkedList<User> friends;
	LinkedList<User> allUsers;
	User displayUser;

	int userRadius = 20;


	//Zombies
	LinkedList<Zombie> allZombies;


	Image tagFrame;
	Graphics2D tagBuf;
	Button tagFrameButton = new Button(tfx, tfy, tfw, tfh);
	Image tagFrame2;
	Graphics2D tagBuf2;

	Image netFrame;
	Graphics2D netBuf;

	Graphics2D buf;
	Image offscreen;

	java.util.Timer timer;
	java.util.TimerTask task;

	public ZombieTracker()
	{
		setSize(xSize, ySize);
		setBackground(Color.black);
		offscreen = new BufferedImage(xSize,ySize,
										BufferedImage.TYPE_INT_ARGB);
		buf = (Graphics2D)offscreen.getGraphics();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);

		downButton = new Button(0,0,0,0);

		statusBarImage = loadImage("statusBar.bmp");
		tagImages[0] = loadImage("Supplies.png");
		tagImages[1] = loadImage("Location.png");
		tagImages[2] = loadImage("People.png");


		//Buttons: x, y, function number, image String
		//funtion number must be >= 2

		/*
			Tag Buttons
		*/
		tagList = new LinkedList<Button>();
		tagList.add(new Button(0, 64, 6, "NewTag"));
		tagList.add(new Button(0, 108, 7, "MyTags"));
		tagList.add(new Button(0, 152, 8, "Information"));

		searchList = new LinkedList<Button>();
		searchList.add(tagSearchBox);
		searchList.add(new Button(256, 94, 24, "SearchSmall"));

		itemList  = new LinkedList<Button>();
		itemList.add(new Button(36, 346, 91, "AddOver"));
		itemList.add(new Button(112, 346, 92, "AddFavs"));
		itemList.add(new Button(188, 346, "PlotRoute", 93));
		itemList.add(new Button(213, 248, 94, "Rate"));
		itemList.add(new Button(213, 296, 95, "Edit"));
		itemList.add(new Button(224, 96, "X", 96));
		itemList.add(new AntiButton(tf2x, tf2y, tf2w, tf2h, 96));

		LinkedList<Button> guideList = new LinkedList<Button>();
		guideList.add(new Button(0, 64, "TagGuide2", 10));

		tagMenuButton = new ListButton(0, 20, 1, "Tags", tagList);

		tagButtons = new LinkedList<Button>();
		tagButtons.add(tagMenuButton);
		tagButtons.add(new Button(50, 20, 21,  "Search"));
		tagButtons.add(new Button(100, 20, 3, "Ruler"));
		tagButtons.add(new Button(150, 20, 4, "Overlay"));
		tagButtons.add(new Button(200, 20, 5, "Star"));
		tagButtons.add(new Button(260, 20, "MapRight", 9));
		tagButtons.add(new ListButton(0, 416, 10, 10, "TagGuide", guideList));
		tagButtons.add(new Button(256, 416, 12, "Sort"));


		/*
			Map Buttons
		*/
		mapButtons = new LinkedList<Button>();
		mapButtons.add(new Button(0, 20, "TagsPage", 2));
		mapButtons.add(new Button(214, 20, "NetPage", 3));
		mapButtons.add(new Button(0, 416, 5, "ClearOverlay"));
		mapButtons.add(new ClickButton(256, 416, 6, "Stealth"));

		mapButtons.add(new TriButton(220, 68, 1, "Walk", 8));
		mapButtons.add(new TriButton(220, 316, 2, "Center", 9));
		mapButtons.add(new TriButton(4, 316, 3, "Plot", 10));
		legendButton = new TriButton(4, 68, -4, 0, 7, "LegendTL");
		mapButtons.add(legendButton);
		//mapButtons.add(new Button(272, 80, "Walk2", 9));



		/*
			Network Buttons
		*/
		radiusList = new LinkedList<Button>();
		radiusList.add(new Button(148, 64, 41, "List"));
		radiusList.add(new Button(148, 108, 42, "Chat"));

		friendsList = new LinkedList<Button>();
		friendsList.add(new Button(234, 64, 51, "List"));
		friendsList.add(new Button(234, 108, 52, "Chat"));

		chatBoxStub = new LinkedList<Button>();
		chatBoxStub.add(new Button(32, 370, "textBox244", 54));

		chatList = new LinkedList<Button>();
		chatList.add(netChatBox);
		chatList.add(new Button(256, 94, 56, "SearchSmall"));

		chatHistory = new LinkedList<String[]>();

		networkButtons = new LinkedList<Button>();
		networkButtons.add(new Button(0, 20, "MapLeft", 2));
		networkButtons.add(new Button(62, 20, 3, "UserSearch"));
		networkButtons.add(new ListButton(148, 20, 40, "Radius", radiusList));
		networkButtons.add(new ListButton(234, 20, 50,"Friends",friendsList));
		networkButtons.add(new Button(256, 416, 6,"NetSort"));


		Panic = new Button(100, 416, -1, "PANIC!");


		loadingButtons = new LinkedList<Button>();
		loadingButtons.add(new Button(240, 220, "ZombieIcon", 2));



		initTags();
		tagState = 1;

		initMap();
		mapState = 1;

		initNetwork();
		networkState = 1;


		allZombies = new LinkedList<Zombie>();
		for (int i = 0; i < numZombies; i++)
		{
			double x = Math.random()*mapWidth;
			double y = Math.random()*mapHeight;

			allZombies.add(new Zombie(x, y));
		}

		setStateLoading();



		task  = new java.util.TimerTask()
		{
			public void run()
			{
				Iterator<Zombie> zombieIt = allZombies.listIterator(0);
				while (zombieIt.hasNext())
				{
					zombieIt.next().AI();
				}
				repaint();
			}
		};
		timer = new java.util.Timer();
		timer.schedule(task, 50, 250);
	}

	public void initTags()
	{
		tagFrame = new BufferedImage(308, 340, BufferedImage.TYPE_INT_ARGB);
		tagBuf = (Graphics2D)tagFrame.getGraphics();
		tagFrame2 = new BufferedImage(308, 340, BufferedImage.TYPE_INT_ARGB);
		tagBuf2 = (Graphics2D)tagFrame2.getGraphics();

		allTags = new LinkedList<Tag>();
		TagLoader.loadTags(allTags);
	}

	public void initMap()
	{
		mapOverlayImage = loadImage("MapOverlay.png");

		try
		{
			fullMap = ImageIO.read(new File(imageDir+"/Full.png"));
			mapWidth = fullMap.getWidth();
			mapHeight = fullMap.getHeight();

			Zombie.mapWidth = mapWidth;
			Zombie.mapHeight = mapHeight;
		}
		catch (Exception e)
		{
			System.out.println("Could not load fillMap!");
			e.printStackTrace();
		}
	}

	public void initNetwork()
	{
		netFrame = new BufferedImage(308, 340, BufferedImage.TYPE_INT_ARGB);
		netBuf = (Graphics2D)netFrame.getGraphics();

		User f1 = new User("Sgt Pepper",
					"Always remember to double tap.", 1, 215, 3320);
		User f2 = new User("Nibbler",
					"Everyone out of the universe! Quick!", 1, 1621, 3126);


		allUsers = new LinkedList<User>();
		allUsers.add(new User("Joe Chambers",
					"Got the kill of the week!", 1, 1163, 2856));
		allUsers.add(new User("Bill Walker",
					"Trading for coffee. Anyone?", 1, 2142, 2823));
		allUsers.add(new User("Sarah Connor",
					"This sucks, I want the robots back.", 1, 310, 1197));
		allUsers.add(f1);
		allUsers.add(f2);


		friends = new LinkedList<User>();
		friends.add(f1);
		friends.add(f2);


		displayUser = friends.peek();
		users = allUsers;
	}

	public void update (Graphics g)
	{
		paint(g);
	}

	public void paint (Graphics g)
	{
		buf.setColor(Color_darkBlue);
		buf.fillRect(0, 0, xSize, ySize);

		switch (state)
		{
			case 0: paintLoading(); break;
			case 1: paintTags(); break;
			case 2: paintMap(); break;
			case 3: paintNetwork(); break;
		}

		if (stealth)
		{
			buf.setColor(Color_stealthBG);
			buf.fillRect(0, 0, xSize, ySize);
		}

		paintStatusBar();

		g.drawImage(offscreen, 0, 0, this);
	}

	public void paintStatusBar()
	{
		buf.drawImage(statusBarImage, 0, 0, this);
	}

	public void paintLoading()
	{
		buf.drawImage(Loading, 0, 0, this);
		paintButtons();
	}

	public void paintTags()
	{
		if (tagState == 1)
		{
			tagBuf.setColor(Color_darkBlue);
			tagBuf.fillRect(0, 0, tfw, tfh);
			tagBuf.setColor(Color_tagBG);
			tagBuf.fillRect(0, 0, tfw, tfh);
			buf.drawImage(tagFrame, tfx, tfy, this);
		}
		else if (tagState == 2)
		{
			tagBuf.setColor(Color_darkBlue);
			tagBuf.fillRect(0, 0, tfw, tfh);
			tagBuf.setColor(Color_tagBG);
			tagBuf.fillRect(0, 0, tfw, tfh);

			int y = 6;
			Iterator<Tag> it = tags.listIterator(0);
			while (it.hasNext())
			{
				Tag t = it.next();
				t.paint(tagBuf, this, xPos, yPos, 6, y);
				y += 42;
			}
			buf.drawImage(tagFrame, tfx, tfy, this);
		}
		else if (tagState == 3)
		{
			tagBuf.setColor(Color_darkBlue);
			tagBuf.fillRect(0, 0, tfw, tfh);
			tagBuf.setColor(Color_tagBG);
			tagBuf.fillRect(0, 0, tfw, tfh);


			tagBuf.setColor(Color.green);
			tagBuf.setFont(searchFont);
			tagBuf.drawString("Search:", 5, 20);
			buf.drawImage(tagFrame, tfx, tfy, this);
		}
		else if (tagState == 4)
		{
			buf.drawImage(tagFrame, tfx, tfy, this);
			tagBuf2.setColor(Color_darkBlue);
			tagBuf2.fillRect(0, 0, tf2w, tf2h);
			tagBuf2.setColor(Color.black);
			tagBuf2.drawRect(0, 0, tf2w, tf2h);

			displayTag.paintFull(tagBuf2, this, xPos, yPos);

			buf.drawImage(tagFrame2, tf2x, tf2y, this);
		}

		paintButtons();
		if (tagState == 3)
			tagMenuButton.paint(buf, this);
	}
	public void paintMap()
	{
		double xOff = mapRad/scale;
		double yOff = mapRad*1.5/scale;

		buf.scale(scale, scale);
		buf.drawImage(fullMap, (int)xOff-mapX, (int)yOff-mapY, this);
		buf.scale(1/scale, 1/scale);

		Iterator<Tag> tagIt = allTags.listIterator(0);
		while (tagIt.hasNext())
		{
			Tag t = tagIt.next();
			double x = (t.xPos-mapX)*scale + mapRad;
			double y = (t.yPos-mapY)*scale + mapRad*1.5;
			buf.drawImage(tagImages[t.type], (int)x - 6, (int)y - 13, this);
		}

		Iterator<User> userIt = allUsers.listIterator(0);
		while (userIt.hasNext())
		{
			User u = userIt.next();
			double x = (u.xPos-mapX)*scale + mapRad;
			double y = (u.yPos-mapY)*scale + mapRad*1.5;
			buf.drawImage(tagImages[2], (int)x - 6, (int)y - 13, this);
		}

		if (target)
		{
			Stroke s2 = buf.getStroke();
			buf.setStroke(stroke);
			buf.setColor(Color.green);
			buf.drawLine((int)((xPos-mapX)*scale+mapRad),
						 (int)((yPos-mapY)*scale+1.5*mapRad),
						 (int)((targetX-mapX)*scale+mapRad),
						 (int)((targetY-mapY)*scale+1.5*mapRad));
			buf.setStroke(s2);
		}

		Iterator<Zombie> zombieIt = allZombies.listIterator(0);
		while (zombieIt.hasNext())
		{
			Zombie z = zombieIt.next();
			double x = (z.xPos-mapX)*scale + mapRad;
			double y = (z.yPos-mapY)*scale + mapRad*1.5;
			buf.drawImage(zombieImage, (int)x - 10, (int)y - 10, this);
		}


		/*double scale2 = Math.sqrt(scale);
		buf.scale(scale2, scale2);
		Iterator<Zombie> zombieIt = allZombies.listIterator(0);
		while (zombieIt.hasNext())
		{
			Zombie z = zombieIt.next();
			double x = (z.xPos-mapX) + mapRad/scale2;
			double y = (z.yPos-mapY) + mapRad*1.5/scale2;
			buf.drawImage(zombieImage, (int)x - 10, (int)y - 10, this);
		}
		buf.scale(1/scale2, 1/scale2);*/


		double x = (xPos-mapX)*scale + mapRad;
		double y = (yPos-mapY)*scale + mapRad*1.5;
		buf.drawImage(youAreHere, (int)x - 5, (int)y - 15, this);

		buf.drawImage(mapOverlayImage, 0, 0, this);

		//System.out.println("Map center: "+mapX +", "+mapY);

		paintButtons();
	}
	public void paintNetwork()
	{
		if (networkState == 1 || networkState == 3)
		{
			netBuf.setColor(Color_darkBlue);
			netBuf.fillRect(0, 0, tfw, tfh);
			netBuf.setColor(Color_tagBG);
			netBuf.fillRect(0, 0, tfw, tfh);
			buf.drawImage(netFrame, tfx, tfy, this);
		}
		else if (networkState == 2)
		{
			netBuf.setColor(Color_darkBlue);
			netBuf.fillRect(0, 0, tfw, tfh);
			netBuf.setColor(Color_tagBG);
			netBuf.fillRect(0, 0, tfw, tfh);

			int y = 6;
			Iterator<User> it = users.listIterator(0);
			while (it.hasNext() && y < 302)
			{
				User u = it.next();
				u.paint(netBuf, this, xPos, yPos, 6, y);
				y += 74;
			}
			buf.drawImage(netFrame, tfx, tfy, this);
		}
		else if (networkState == 4)
		{
			netBuf.setColor(Color_darkBlue);
			netBuf.fillRect(0, 0, tfw, tfh);
			netBuf.setColor(Color_tagBG);
			netBuf.fillRect(0, 0, tfw, tfh);

			netBuf.setColor(Color.black);
			netBuf.setFont(messageFont);
			FontMetrics messageMet = buf.getFontMetrics();

			int y = 6;
			Iterator<String[]> it = chatHistory.listIterator(0);
			while (it.hasNext())
			{
				String[] s = it.next();

				netBuf.setFont(nameFont);
				netBuf.drawString(s[0]+":", 6, y+14);

				netBuf.setFont(messageFont);
				int i = 1;
				int lineHeight = 16;
				Scanner scan = new Scanner(s[1]);
				String write = "";

				while (scan.hasNext())
				{
					String next = scan.next();
					String test = write + next;
					if (messageMet.stringWidth(test) < 240)
					{
						write = test + " ";
					}
					else
					{
						netBuf.drawString(write, 26, y+14+i*16);
						i++;
						write = next + " ";
					}
				}
				netBuf.drawString(write, 26, y+14+i*16);
				y += 16 + i*16;
			}

			buf.drawImage(netFrame, tfx, tfy, this);
		}
		else if (networkState == 5)
		{
			netBuf.setColor(Color_darkBlue);
			netBuf.fillRect(0, 0, tfw, tfh);
			netBuf.setColor(Color_tagBG);
			netBuf.fillRect(0, 0, tfw, tfh);


			buf.drawImage(netFrame, tfx, tfy, this);
		}

		paintButtons();
	}

	public void paintButtons()
	{
		Iterator<Button> it = buttons.listIterator(0);
		while (it.hasNext())
		{
			Button b = it.next();
			b.paint(buf, this);
		}
		if (buttons2 != null)
		{
			it = buttons2.listIterator(0);
			while (it.hasNext())
			{
				Button b = it.next();
				b.paint(buf, this);
			}
		}
		if (state > 0)
			Panic.paint(buf, this);
	}

	public void keyTyped (KeyEvent e)
	{
		if (activeTextBox != null)
		{
			if ((int)e.getKeyChar() == 8)
				activeTextBox.backspace();
			else if ((int)e.getKeyChar() == 10)
				buttonAction(activeTextBox.enterID);
			else
				activeTextBox.addChar(e.getKeyChar());
			repaint();
		}
	}
	public void keyPressed (KeyEvent e)
	{
	}
	public void keyReleased (KeyEvent e)
	{
	}
	public void mouseClicked(MouseEvent me)
	{
	}
	public void mousePressed(MouseEvent me)
	{
		int xm = me.getX();
		int ym = me.getY();
		boolean hit = false;

		int ID = Panic.click(xm, ym);
		if (ID != 0)
		{
			repaint();
			PANIC();
			return;
		}

		hit |= click(buttons, xm, ym);
		if (buttons2 != null)
			hit |= click(buttons2, xm, ym);

		if (state == 1 && tagState == 2 && !hit)
		{
			int y = 6;
			Iterator<Tag> it = tags.listIterator(0);
			while (it.hasNext())
			{
				Tag t = it.next();
				if (12 <= xm && xm < 268 &&
					70+y <= ym && ym < 102+y)
				{
					tagState = 4;
					displayTag = t;
					buttons2 = itemList;
					break;
				}
				y += 42;
			}
		}
		else if (state == 2 && mapState == 1)
		{
			if (insideMap(xm, ym))
			{
				clickX = xm;
				clickY = ym;
			}
		}
		else if (state == 3 && networkState == 2 && !hit)
		{
			int y = 6;
			Iterator<User> it = users.listIterator(0);
			while (it.hasNext())
			{
				User u = it.next();
				if (12 <= xm && xm < 268 &&
					70+y <= ym && ym < 134+y)
				{
					setNetworkState(4);
					displayUser = u;
					break;
				}
				y += 72;
			}
		}

		repaint();
	}
	public void mouseReleased(MouseEvent me)
	{
		int xm = me.getX();
		int ym = me.getY();

		Panic.unclick(xm, ym);
		downButton.unclick(xm, ym);

		unclick(buttons, xm, ym);
		if (buttons2 != null)
			unclick(buttons2, xm, ym);
		repaint();
	}
	public void mouseEntered(MouseEvent me)
	{
	}
	public void mouseExited(MouseEvent me)
	{
	}

	public void mouseMoved(MouseEvent me)
	{
		if (state == 2 && mapState == 1)
		{
			mouseIn = insideMap(me.getX(), me.getY());
		}
	}
	public void mouseDragged(MouseEvent me)
	{
		int xm = me.getX();
		int ym = me.getY();

		if (state == 2 && mapState == 1)
		{
			boolean last = mouseIn;
			mouseIn = insideMap(xm, ym);

			if (last != mouseIn && mouseIn)
			{
				clickX = xm;
				clickY = ym;
			}

			if (mouseIn && !legendButton.clicked)
			{
				mapX -= (int)((xm - clickX)/scale);
				clickX = xm;
				mapY -= (int)((ym - clickY)/scale);
				clickY = ym;
				repaint();
			}
		}
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		if (state == 2 && mapState == 1)
		{
			if (mouseIn && !legendButton.clicked)
			{
				int clicks = e.getWheelRotation();
				scale -= clicks*0.1;
				if (scale < 0.1)
					scale = 0.1;
				else if (scale > 1.0)
					scale = 1.0;
				//System.out.println(scale);
				repaint();
			}
		}
	}


	public boolean click (LinkedList<Button> list, int x, int y)
	{
		boolean hit = false;
		Iterator<Button> it = list.listIterator(0);
		while (it.hasNext())
		{
			Button b = it.next();
			int ID = b.click(x, y);
			if (ID != 0)
			{
				buttonAction(ID);
				downButton = b;
				hit = true;
			}
		}
		return hit;
	}
	public void unclick (LinkedList<Button> list, int x, int y)
	{
		Iterator<Button> it = list.listIterator(0);
		while (it.hasNext())
		{
			Button b = it.next();
			if (b.unclick(x, y))
				break;
		}
	}

	public void buttonAction (int ID)
	{
		//System.out.println("Button ID = " + ID);
		switch (state)
		{
			case 0: loadingButtonActions(ID); break;
			case 1: tagButtonActions(ID); break;
			case 2: mapButtonActions(ID); break;
			case 3: networkButtonActions(ID); break;
		}
	}

	public void loadingButtonActions (int ID)
	{
		switch (ID)
		{
			case 2: setStateMap(); break;
		}
	}

	public void tagButtonActions (int ID)
	{
		switch (ID)
		{
			case 3: buildTagList(tagRadius);
					setTagState(2);
					break;

			case 9: setStateMap(); break;

			case 10: if (tagState == 3)
						setTagState(5);
					 else if (tagState == 5)
					 	setTagState(3);
					 break;

			case 21: setTagState(3);
					 break;

			case 23: break;
			case 24: tagSearchString = tagSearchBox.getText();
					 if (!tagSearchString.equals(""))
					 {
						buildTagList(tagSearchString);
						tagSearchBox.clear();
						downButton.unclick(-1, -1);
						setTagState(2);
					 }
					 break;

			case 93: target = true;
					 targetX = displayTag.xPos;
					 targetY = displayTag.yPos;
					 setTagState(2);
					 setStateMap();
					 break;
			case 96: buttons2 = null;
					 if (tagState == 4)
					 	tagState = 2;
					 break;
		}
	}
	public void mapButtonActions (int ID)
	{
		switch (ID)
		{
			case 2: setStateTags(); break;
			case 3: setStateNetwork(); break;
			case 6: stealth = !stealth; break;


			case 7: break;
			case 8: if (target)
					{
						mapX = xPos = targetX;
						mapY = yPos = targetY;
						target = false;
					}
					else
					{
						//setStateLoading();
					}
					break;

			case 9: mapX = xPos;
					mapY = yPos;
					break;

			case 10: break;
		}
	}
	public void networkButtonActions (int ID)
	{
		switch (ID)
		{
			case 2: setStateMap(); break;
			case 3: //setNetworkState(3);
					break;

			case 41: users = allUsers;
					 setNetworkState(2); break;
			case 42: displayUser = allUsers.peek();
					 setNetworkState(4); break;

			case 51: users = friends;
					 setNetworkState(2); break;
			case 52: displayUser = friends.peek();
					 setNetworkState(4); break;

			case 54: setNetworkState(5);
					 netChatBox.clicked = true;

			case 55: if (netChatBox.clicked)
					 	activeTextBox = netChatBox;
					 else
					 	activeTextBox = null;
					 break;
			case 56: String send = netChatBox.getText();
					 if (!send.equals(""))
					 {
						netChatBox.clear();
						downButton.unclick(-1, -1);
						sendMessage(send);
					 }
					 setNetworkState(4);
					 break;
		}
	}


	public void setState (int s)
	{
		exitState(state);
		state = s;

		switch (s)
		{
			case 0: buttons = loadingButtons; stealth = false; break;
			case 1: buttons = tagButtons;
					setTagState(tagState);
					break;
			case 2: buttons = mapButtons;
					setMapState(mapState);
					break;
			case 3: buttons = networkButtons;
					setNetworkState(networkState);
					break;
		}
	}

	public void exitState (int s)
	{
		buttons2 = null;

		switch (s)
		{
			case 1: if (tagState == 3)
					{
						tagState = 2;
						activeTextBox = null;
					}
					else if (tagState == 4)
						tagState = 2;
					else if (tagState == 5)
						tagState = 3;
					break;
			case 2: break;
			case 3: break;
		}
	}

	public void setStateLoading()
	{
		setState(0);
	}
	public void setStateTags()
	{
		setState(1);
	}
	public void setStateMap()
	{
		setState(2);
	}
	public void setStateNetwork()
	{
		setState(3);
	}

	public void setTagState (int state)
	{
		tagState = state;
		buttons2 = null;

		if (state != 3)
			activeTextBox = null;

		if (state == 2)
			Collections.sort(tags, new DistanceComparator());
		else if (state == 3)
		{
			buttons2 = searchList;
			activeTextBox = tagSearchBox;
		}
	}

	public void setMapState (int state)
	{
		mapState = state;
		buttons2 = null;
	}

	public void setNetworkState (int state)
	{
		networkState = state;
		buttons2 = null;

		if (state == 2)
			Collections.sort(users, new UserDistanceComparator());
		else if (state == 4)
			buttons2 = chatBoxStub;
		else if (state == 5)
			buttons2 = chatList;
	}

	public void sendMessage (String send)
	{
		chatHistory.add(new String[]{name, send});
		if (replyIndex < 3)
		{
			chatHistory.add(new String[]{displayUser.name,
												replies[replyIndex++]});
		}
	}

	public void PANIC()
	{
		setStateMap();
		mapX = xPos;
		mapY = yPos;

		target = true;
		targetX = (int)(Math.random()*mapWidth);
		targetY = (int)(Math.random()*mapHeight);
	}

	public boolean insideMap (int x, int y)
	{
		int xp = (x - mapRad);
		int yp = (y-240);

		return xp*xp + yp*yp < mapRad*mapRad;
	}


	public void buildTagList (String s)
	{
		String[] s2 = s.split(" ");

		LinkedList<Tag> ret = new LinkedList<Tag>();

		Iterator<Tag> it = allTags.listIterator(0);
		while (it.hasNext())
		{
			Tag t = it.next();
			for (int i = 0; i < s2.length; i++)
			{
				if (t.name.toLowerCase().startsWith(s2[i].toLowerCase()))
				{
					ret.add(t);
					break;
				}
			}
		}
		Collections.sort(ret, new NameComparator());
		tags = ret;
	}

	public void buildTagList (int r)
	{
		LinkedList<Tag> ret = new LinkedList<Tag>();

		Iterator<Tag> it = allTags.listIterator(0);
		while (it.hasNext())
		{
			Tag t = it.next();
			if (t.distanceTo(xPos, yPos) <= r)
				ret.add(t);
		}
		Collections.sort(ret, new DistanceComparator());
		tags = ret;
	}






	public static Image loadImage (String s)
	{
		try
		{
			return ImageIO.read(new File(imageDir+"/"+s));
		}
		catch (Exception e)
		{
			System.out.println("Could not load image: "+s);
			e.printStackTrace();
		}
		return null;
	}

	public static void main (String[] args)
	{
		if (args.length == 1)
		{
			try
			{
				int i = Integer.parseInt(args[0]);
				if (1 <= i && i <= 3)
				{
					xPos = locations[i][0];
					yPos = locations[i][1];
				}
			}
			catch (Exception e)
			{
				System.out.println("Invalid argument: " + args[0]);
			}
		}

		new Viewer();
	}

	static class Viewer extends JFrame
	{
		ZombieTracker dis;

		public Viewer ()
		{
			super("Zombie Tracker");
			setSize(xSize+100, ySize+100);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			dis = new ZombieTracker();
			getContentPane().add(dis);
			pack();
			setVisible(true);
		}
	}

	class DistanceComparator implements Comparator<Tag>
	{
		public int compare(Tag t1, Tag t2)
		{
			double d1 = t1.distanceTo(xPos, yPos);
			double d2 = t2.distanceTo(xPos, yPos);

			if (d1 > d2)
				return 1;
			else if (d1 == d2)
				return 0;
			else
				return -1;
		}
	}

	class NameComparator implements Comparator<Tag>
	{
		public int compare(Tag t1, Tag t2)
		{
			int c = t1.name.compareTo(t2.name);
			if (c == 0)
			{
				double d1 = t1.distanceTo(xPos, yPos);
				double d2 = t2.distanceTo(xPos, yPos);

				if (d1 > d2)
					return 1;
				else if (d1 == d2)
					return 0;
				else
					return -1;
			}
			return c;
		}
	}

	class RatingComparator implements Comparator<Tag>
	{
		public int compare(Tag t1, Tag t2)
		{
			if (t1.rating > t2.rating)
				return 1;
			else if (t1.rating == t2.rating)
				return 0;
			else
				return -1;
		}
	}

	class UserDistanceComparator implements Comparator<User>
	{
		public int compare(User u1, User u2)
		{
			double d1 = u1.distanceTo(xPos, yPos);
			double d2 = u2.distanceTo(xPos, yPos);

			if (d1 > d2)
				return 1;
			else if (d1 == d2)
				return 0;
			else
				return -1;
		}
	}
}