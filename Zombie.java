public class Zombie
{
	static int mapWidth, mapHeight;
	static double speed = 2;

	double xPos, yPos;
	double dir;


	public Zombie (double x, double y)
	{
		xPos = x;
		yPos = y;
		dir = Math.random()*2*Math.PI;
	}

	public void AI()
	{
		dir += Math.random()*Math.PI/6 - Math.PI/12;

		xPos += speed*Math.sin(dir);
		yPos += speed*Math.cos(dir);

		if (xPos < 0)
			xPos += mapWidth;
		else if (mapWidth <= xPos)
			xPos -= mapWidth;

		if (yPos < 0)
			yPos += mapHeight;
		else if (mapHeight <= yPos)
			yPos -= mapHeight;
	}
}