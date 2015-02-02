This is our Software Prototype for SENG 310 Assignment 4

Brodyn Roberts
Jason Cummer
Robert Prior

This program should be run as:

	java ZombieTracker

The folder "images" must be in the same folder as all of the .java files.


Scenarios:

The Zombie Apocalypse has begun:

  Bill is walking down town and hears some sirens. His iPhone starts to vibrate, he looks and sees the zombie tracker badge has turned red. It loads the map and he sees that there are some undead about a block ahead of him. He now has to get home or get to safety.


Bill: Need More Ammo! 

  World War Z has begun. Bill needs to get 7.62x54mmR ammo for his trusty Dragunov rifle, and needs some snacks. He searches for the closest store with this ammo. The Zombie Tracker tells him to go to the Guns n Grub store on Zomboni road. He wants to move quickly and silently around the horde to get to the store as efficiently as possible. He does not want to draw attention to himself and needs to leave the city as soon as possible. He will update the store’s status (food/ammo reserves) after he gets to safety.


Long term survival:

  Aiden has been thinking about long term food needs, its spring and he needs seeds for edible plants. He remembers a Home Depot and some grocery stores in a nearby town, but the Zombie Tracker shows a medium risk. Aiden looks at the Network and sends a message to several other users in the area to organize a raid. Once they’ve met up using the Map, Aiden quickly makes sure the voice directions are off, sets warnings to vibrate, and begins the mission.




The first Scenario can be launched specially by clicking the second "Z" icon on the iPhone page (right icon). The main program is launched by using the left-most "Z" icon on this page.

The small icon bellow the "Network" tab on the Map page, the "Walk" button, is NOT part of the interface. This buttin is placed here to simulate time passing when it is clicked.
During the second scenario, this passes the time between plotting a route to the ammo store, and ariving there. Otherwise, this button returns the user to the iPhone (first) screen.



Notes on scenarios:

Scenario 1:
	See above for how to launch Scenario 1.
	Getting to safety is done using the PANIC button.

Scenario 2:
	Clicking "Plot Route" on the first "7.62x54mmR" ammo advances this scenario.
	Clicking Walk advances again.
	Clicking PANIC plots a route to safety.

Scenario 3:
	Clicking "Plot Route" on the first "Home Depot" tag advances the
	scenario.
	Completing a conversation with any user advances again.
	The last step would be done by clicking the Stealth button; however, these changes can't be shown visually, so noting happens in this prototype.