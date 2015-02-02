import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class TextBox extends Button
{
	static Font norm = new Font(null, 0, 13);

	int enterID;

	char[] text;
	int size;

	public TextBox (int x, int y, int w, int h, int id)
	{
		super(x, y, w, h);
		ID = id;
		enterID = id+1;

		text = new char[5001];
		size = 0;
	}

	public void paint (Graphics2D buf, ImageObserver obs)
	{
		buf.setColor(Color.white);
		buf.fillRect(xPos, yPos, width, height);

		if (clicked)
			buf.setColor(Color.red);
		else
			buf.setColor(Color.black);
		buf.drawRect(xPos, yPos, width, height);


		buf.setColor(Color.black);
		buf.setFont(norm);
		FontMetrics font = buf.getFontMetrics();

		String text = getText();

		Scanner scan = new Scanner(text);
		String write = "";
		int lineHeight = 16;
		int y = lineHeight - 2;

		while (scan.hasNext())
		{
			String next = scan.next();
			String test = write + next;
			if (font.stringWidth(test) < width-8)
			{
				write = test + " ";
			}
			else
			{
				buf.drawString(write, xPos+4, yPos+y);
				y += lineHeight;
				write = next + " ";
			}
		}
		buf.drawString(write, xPos+4, yPos+y);
	}

	public int click(int x, int y)
	{
		boolean hit = hitBy(x, y);

		//if (clicked && !hit || hit && !clicked)
		if (clicked ^ hit)
		{
			clicked = hit;
			return ID;
		}
		return 0;
	}
	public boolean unclick(int x, int y)
	{
		return false;
	}

	public void addChar (char c)
	{
		if (size >= text.length-1)
			return;
		text[size++] = c;
	}

	public void backspace()
	{
		if (size <= 0)
			return;
		size--;
	}

	public void clear()
	{
		size = 0;
	}

	public void setText (String t)
	{
		char[] set = t.toCharArray();
		for (int i = 0; i < set.length; i++)
		{
			text[i] = set[i];
		}
		size = set.length;
	}

	public String getText()
	{
		return new String(text, 0, size);
	}
}