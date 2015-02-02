import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class Tag
{
	static Font small = new Font(null, 0, 11);
	static Font med = new Font(null, 0, 12);
	static Font large = new Font(null, 1, 13);

	static Font small2 = new Font(null, 0, 10);
	static Font med2 = new Font(null, 0, 14);
	static Font large2 = new Font(null, 1, 16);
	static Image tagImage = ZombieTracker.loadImage("Tag.png");

	int type;
	String category;
	String subCategory;
	String name;
	String description;
	//double distance;

	int xPos, yPos;
	double rating;
	int numRatings;


	public Tag (int t, String c, String sc, String N, String D,
											int x, int y, double r, int n)
	{
		type = t;
		category = c;
		subCategory = sc;
		name = N;
		description = D;

		xPos = x;
		yPos = y;

		rating = r;
		numRatings = n;
	}

	public double distanceTo (int x, int y)
	{
		int dx = x-xPos;
		int dy = y-yPos;

		return Math.sqrt(dx*dx + dy*dy)/1000;
	}

	public void paint (Graphics2D buf, ImageObserver obs,
									int UserX, int UserY, int x, int y)
	{
		buf.drawImage(tagImage, x, y, obs);

		buf.setColor(Color.black);
		buf.setFont(small);
		FontMetrics font = buf.getFontMetrics();

		int width = font.stringWidth(category);
		buf.drawString(category, x+2+(37-width/2), y+12);

		width = font.stringWidth(subCategory);
		buf.drawString(subCategory, x+2+(37-width/2), y+27);

		String s = String.format("%.1fkm", distanceTo(UserX, UserY));
		width = font.stringWidth(s);
		buf.drawString(s, x+174+(22-width/2), y+22);

		s = (int)rating+"%";
		width = font.stringWidth(s);
		buf.drawString(s, x+218+(20-width/2), y+22);

		buf.setFont(large);
		font = buf.getFontMetrics();
		width = font.stringWidth(name);
		buf.drawString(name, x+78+(48-width/2), y+22);
	}

	public void paintFull (Graphics2D buf, ImageObserver obs,
													int UserX, int UserY)
	{
		final int tfw = 224, tfh = 288;
		buf.drawLine(4, 32, tfw-4, 32);
		buf.drawLine(4, 64, tfw-4, 64);

		buf.drawLine(4, 244, 176, 244);
		buf.drawLine(4, 64, 4, 244);
		buf.drawLine(176, 64, 176, 244);

		buf.setColor(Color.green);
		buf.setFont(med2);
		buf.drawString(category+" -> "+subCategory, 16, 24);

		buf.setFont(large2);
		buf.drawString(name, 16, 56);

		buf.setFont(small2);
		buf.drawString("Distance:", 178, 78);
		buf.drawString("Rating:", 178, 118);

		buf.setFont(med);
		FontMetrics font = buf.getFontMetrics();
		String s = String.format("%.1fkm", distanceTo(UserX, UserY));
		int width = font.stringWidth(s);
		buf.drawString(s, 180+(44-width)/2, 96);

		s = (int)rating+"%";
		width = font.stringWidth(s);
		buf.drawString(s, 184+(40-width)/2, 136);


		Scanner scan = new Scanner(description);
		String write = "";
		int lineHeight = 16;
		int y = 64 + lineHeight - 2;

		while (scan.hasNext())
		{
			String next = scan.next();
			String test = write + next;
			if (font.stringWidth(test) < 164)
			{
				write = test + " ";
			}
			else
			{
				buf.drawString(write, 8, y);
				y += lineHeight;
				write = next + " ";
			}

		}
		buf.drawString(write, 8, y);
	}
}