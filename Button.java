import javax.imageio.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class Button
{
	int ID;
	int xPos, yPos;
	int width, height;
	boolean clicked;
	BufferedImage upImage, downImage;

	public Button (int x, int y, int w, int h)
	{
		xPos = x;
		yPos = y;
		width = w;
		height = h;
		clicked = false;
	}

	public Button (int x, int y, int id, String s)
	{
		xPos = x;
		yPos = y;
		ID = id;
		clicked = false;
		try
		{
			String dir = ZombieTracker.imageDir+"/";
			String end = ZombieTracker.imageEnd;
			upImage = ImageIO.read(new File(dir+s+end));
			width = upImage.getWidth();
			height = upImage.getHeight();
			downImage = ImageIO.read(new File(dir+s+"_down"+end));
		}
		catch (Exception e)
		{
			System.out.println("Could not load image: " + s);
		}
	}

	public Button (int x, int y, String s, int id)
	{
		xPos = x;
		yPos = y;
		ID = id;
		clicked = false;
		try
		{
			String dir = ZombieTracker.imageDir+"/";
			String end = ZombieTracker.imageEnd;
			upImage = ImageIO.read(new File(dir+s+end));
			width = upImage.getWidth();
			height = upImage.getHeight();
		}
		catch (Exception e)
		{
			System.out.println("Could not load image: " + s);
		}
	}

	public boolean hitBy (int x, int y)
	{
		return (xPos <= x && x < xPos+width &&
				yPos <= y && y < yPos+height);
	}

	public void paint (Graphics2D buf, ImageObserver obs)
	{
		if (!clicked || downImage == null)
			buf.drawImage(upImage, xPos, yPos, obs);
		else
			buf.drawImage(downImage, xPos, yPos, obs);
	}

	public int click(int x, int y)
	{
		if (hitBy(x, y))
		{
			clicked = true;
			return ID;
		}
		return 0;
	}
	public boolean unclick(int x, int y)
	{
		if (clicked && hitBy(x, y))
		{
			clicked = false;
			return true;
		}
		clicked = false;
		return false;
	}
}

class AntiButton extends Button
{
	public AntiButton (int x, int y, int w, int h, int id)
	{
		super(x, y, w, h);
		ID = id;
	}

	public boolean hitBy (int x, int y)
	{
		return (xPos <= x && x < xPos+width &&
				yPos <= y && y < yPos+height);
	}

	public int click(int x, int y)
	{
		if (!hitBy(x, y))
		{
			return ID;
		}
		return 0;
	}
}

class ClickButton extends Button
{
	public ClickButton (int x, int y, int id, String s)
	{
		super(x, y, id, s);
	}

	public int click(int x, int y)
	{
		if (hitBy(x, y))
		{
			clicked = !clicked;
			return ID;
		}
		return 0;
	}
	public boolean unclick(int x, int y)
	{
		return false;
	}
}

class EasyButton extends Button
{
	Color color;

	public EasyButton (int x, int y, int w, int h, Color c)
	{
		super(x, y, w, h);
		color = c;
	}

	public void paint (Graphics2D buf, ImageObserver obs)
	{
		buf.setColor(color);
		buf.fillRect(xPos, yPos, width, height);
	}
}

/*
	ListButton class
	For making a drop-down list of other buttons from one button
	The whole system closes after the second click
*/
class ListButton extends Button
{
	LinkedList<Button> list;
	int ID2;

	public ListButton (int x, int y, int id, String s, LinkedList<Button> l)
	{
		this(x, y, id, 1, s, l);
	}

	public ListButton (int x, int y,int id,int id2, String s,
														LinkedList<Button> l)
	{
		super(x, y, id, s);
		list = l;
		ID2 = id2;
	}

	public void paint (Graphics2D buf, ImageObserver obs)
	{
		if (!clicked)
		{
			buf.drawImage(upImage, xPos, yPos, obs);
		}
		else
		{
			buf.drawImage(downImage, xPos, yPos, obs);
			Iterator<Button> it = list.listIterator(0);
			while (it.hasNext())
			{
				it.next().paint(buf, obs);
			}
		}

	}

	public int click(int x, int y)
	{
		if (!clicked)
		{
			if (hitBy(x, y))
			{
				clicked = true;
				return ID;
			}
		}
		else
		{
			if (hitBy(x, y))
			{
				clicked = false;
				return ID2;
			}
			Iterator<Button> it = list.listIterator(0);
			while (it.hasNext())
			{
				int bID = it.next().click(x, y);
				if (bID != 0)
				{
					if (ID2 != 1)
						clicked = false;
					return bID;
				}
			}
			clicked = false;
			return ID2;
		}
		return 0;
	}
	public boolean unclick(int x, int y)
	{
		if (clicked)
		{
			Iterator<Button> it = list.listIterator(0);
			while (it.hasNext())
			{
				if (it.next().unclick(x, y))
				{
					clicked = false;
					return true;
				}
			}
		}
		return false;
	}
}


/*
	TabButton class
	For displaying a set of buttons after clicking one button
	The system remains open until closed manually
*/
class TabButton extends Button
{
	LinkedList<Button> list;
	Button frame;
	boolean active;
	int ID2;

	public TabButton (int x, int y, int id1, int id2, String s,
											LinkedList<Button> l, Button f)
	{
		super(x, y, id1, s);
		list = l;
		frame = f;
		ID2 = id2;
	}

	public void paint (Graphics2D buf, ImageObserver obs)
	{
		if (!clicked)
			buf.drawImage(upImage, xPos, yPos, obs);
		else
			buf.drawImage(downImage, xPos, yPos, obs);

		if (active)
		{
			Iterator<Button> it = list.listIterator(0);
			while (it.hasNext())
			{
				it.next().paint(buf, obs);
			}
		}

	}

	public int click(int x, int y)
	{
		if (!clicked && hitBy(x, y))
		{
			clicked = true;
			active = !active;
			return ID;
		}

		if (active)
		{
			if (!frame.hitBy(x, y))
			{
				active = false;
				return ID2;
			}

			Iterator<Button> it = list.listIterator(0);
			while (it.hasNext())
			{
				int bID = it.next().click(x, y);
				if (bID != 0)
					return bID;
			}
		}
		return 0;
	}
	public boolean unclick(int x, int y)
	{
		if (x == -1 && y == -1)
		{
			clicked = false;
			active = false;
			Iterator<Button> it = list.listIterator(0);
			while (it.hasNext())
			{
				it.next().clicked = false;
			}
		}

		if (clicked)
		{
			clicked = false;
			return true;
		}
		else if (active)
		{
			Iterator<Button> it = list.listIterator(0);
			while (it.hasNext())
			{
				if (it.next().unclick(x, y))
					return true;
			}
		}
		return false;
	}
}

class TriButton extends Button
{
	int type; //Top-left, Top-right, Bottom-right, Bottom-left : 0,1,2,3
	int xOff;

	public TriButton (int x, int y, int t, int id, String s)
	{
		super(x, y, id, s);
		type = t;
	}

	public TriButton (int x, int y, int x2, int t, int id, String s)
	{
		this(x, y, t, id, s);
		xOff = x2;
	}

	public TriButton (int x, int y, int t, String s, int id)
	{
		super(x, y, s, id);
		type = t;
	}

	public void paint (Graphics2D buf, ImageObserver obs)
	{
		if (!clicked || downImage == null)
			buf.drawImage(upImage, xPos, yPos, obs);
		else
			buf.drawImage(downImage, xPos+xOff, yPos, obs);
	}

	public int click(int x, int y)
	{
		if (!clicked && hitBy(x, y))
		{
			boolean hit = false;
			switch (type)
			{
				case 0: hit = (x-xPos)+(y-yPos) < width; break;
				case 1: hit = (y-yPos)-(x-xPos) < 0; break;
				case 2: hit = (x-xPos)+(y-yPos) > width; break;
				case 3: hit = (x-xPos)-(y-yPos) < 0; break;
			}
			if (hit)
			{
				clicked = true;
				return ID;
			}
		}
		return 0;
	}
	public boolean unclick(int x, int y)
	{
		clicked = false;
		return false;
	}
}