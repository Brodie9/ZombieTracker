import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class User
{
	static Font small = new Font(null, 0, 12);
	static Font med = new Font(null, 0, 14);
	static Font large = new Font(null, 1, 16);
	static Image userImage = ZombieTracker.loadImage("UserList.png");

	String name;
	String status;
	int state;

	int xPos, yPos;
	//double distance;

	public User (String n, String s, int st, int x, int y)
	{
		name = n;
		status = s;
		state = st;


		xPos = x;
		yPos = y;
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
		buf.drawImage(userImage, x, y, obs);

		buf.setColor(Color.black);

		buf.setFont(large);
		FontMetrics font = buf.getFontMetrics();
		int width = font.stringWidth(name);
		buf.drawString(name, x+(178-width)/2, y+24);

		buf.setFont(med);
		font = buf.getFontMetrics();
		width = font.stringWidth(status);
		buf.drawString(status, x+(256-width)/2, y+52);

		buf.setFont(small);
		font = buf.getFontMetrics();
		String s = String.format("%.1fkm", distanceTo(UserX, UserY));
		width = font.stringWidth(s);
		buf.drawString(s, x+212+(22-width/2), y+22);
	}
}
