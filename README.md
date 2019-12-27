# USB e-Tour app

### Installation

Build project in Android Studio
-   Install latest version of Android Studio (3.4) and plug in device (Android 7.0 or higher)
-   Open project and wait for Android Studio to load it
-   Click on Build -> Clean Project
-   Click on Build -> Rebuild Project
-   Click on Run (green arrow in top-right corner) and select device to run on

OR

-   Install apk directly onto device

### Package walkthrough

*   /com.example.usb/

	- About:	Information regarding the building such as facts.
	- Contacts:	Contact information and timetable related to the USB.
	- Drawer:	Class to display the floor plan with the drawn path.
	- Location:	Class that integrates the logic of our implementation of Google Maps.
	- MainActivity:	Starting point of the app.
	- MainPage:	Main menu of the app.
	- MapsActivity: Activity that handles initialising the Google Maps fragment.
	- Navigation:	Fragment that displays the map and takes inputs for the map navigation.
	- RoomPage:	Displays information about each room, such as room name, room number, occupants, etc.
	- RoomSearch:	List of all the available rooms in the usb.

This package contains all the different fragments and activities that are used in our application, such as the navigation, room search or contact page.

*   /com.example.usb/database/

	- DatabaseHelper: This class is used to open the database for reading and manipulating data using SQlite.

*   /com.example.usb/map/

	- Dijkstra:	The algorithm behind the pathfinding of the room navigation feature.
	- MapDrawer:	Used to draw the returned path from Dijkstra on the map.

*   /com.example.usb/map/factory/

	- EdgeFactory:	Pulls information from database regarding edges.
	- FloorFactory:	Pulls information from database regarding floors.
	- NodeFactory:	Pulls information from database regarding nodes.
	- RoomFactory:	Pulls information from database regarding rooms.

These factories pull information from the database and initialises them as objects.

*   /com.example.usb/map/graphelems/

	- Graph:	Holds all the nodes and edges of a floor.
	- Edge:		Links nodes between each others with a given weight.
	- Node:		Waypoints used for navigation
	- ElevationNode:Node interface for stairs and lifts.
	- LiftNode:	Node representing lifts.
	- StairNode:	Node representing stairs.
	- TransitNode:	Nodes that are not rooms used to traverse the graph.
	- RoomNode:	Nodes that represent rooms the user is navigating to and from.
	
Contains information regarding objects that form a graph.

*   /com.example.usb/map/mapelems/

	- Building:	Used to represent a building as a whole, contains all floors and rooms as well as objects from graphelems.
	- Floor:	Holds a reference of all rooms on that floor.
	- Room:		Holds information regarding a given room.

Contains information about rooms, floors and building.

*   /com.example.usb/map/other/

	- RoomNumberCompare: Used to order rooms by room number.

*   /com.example.usb/roomsearch/

	- RoomRecyclerViewAdapter: Format for the list used in RoomSearch fragment.

### Explanation behind the technology

*   MapDrawer

The path array list contains the nodes that we want to be displayed as a path, it is an array list
that contains another array list.

The outer arrayList represents the floors and the inner arrayList contains the nodes in order
for the user to follow them 

When the function loadMap is called, it always runs the code to draw a path passing in the arrayList 
of the floor that is going to be displayed. 
When the map is first generated the array lists are empty so they will not display anything.

Once the user types in where they want to go the app will generates a list of nodes that the user will
need to take. The app then needs to separate these nodes (keeping the order) into their respective floors 
and place them into the array list so that when the map gets loaded, it 
displays these nodes as a paths.

In summary:

1. The algorithm produces an array of all the nodes the user is going to go through.
2. It separates the nodes by floor, keeping the order of the path.
3. Place into the array lists of their respective floors, then reload the map with the current floor which should display the path.

*   Map objects

There are two main categories to consider:

- "Physical" objects used to make up the building, mostly objects that users can interact with and see directly (Building, Floor, Room).
- "Virtual" objects used for the path-finding algorithm, the classes that interact with the physical objects (Graph, Edge, Node, etc).

When creating an instance of a physical objects, we can either create a pre-populated constructor (eg: an already existing list of floors within a building).
We can also create an empty instance without any content inside (eg: a floor without any rooms).

For physical objects, when creating either a Floor or Room, these objects create and store a new Graph object and RoomNode
object respectively upon their creation. Therefore, DestinationNode and Graph should not be instantiated by themselves, or there would be duplicate variables.

*   Dijkstra's algorithm

The app uses Dijkstra's algorithm and has been adapted to work with our current implementation of graphs.

It has also been modified to work when looking for paths across different floors and through different methods (stairs or lift).

Base algorithm has been adapted from https://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html

### Screenshots

Please view the user guide or testing proof for screenshots of the app.

### Disclaimer

This application was created in the context of an academic team project and served to prepare us for the different stages of real-life software development.

This application is NOT guaranteed to hold accurate information about the pathfinding feature and should NOT be relied on in the case of an emergency.
If you find yourself in an emergency, please follow the building's exit signs and fire emergency escape route.

Written by: Kazymir Rabier
