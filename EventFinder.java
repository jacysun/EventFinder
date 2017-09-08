import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

public class EventFinder {
	
	// Event class
	class Event {
		List<Integer> location;          // location of the event
		long eventId;                    // unique numeric identifier of the event
		PriorityQueue<Double> tickets;   // tickets for the event sorted in ascending order of price
		
		Event(List<Integer> location, long eventId) {
			this.location = location;
			this.eventId = eventId;
			tickets = new PriorityQueue<>();
		}
	}
	
	int[] rangeX;                           // World range on X axis
	int[] rangeY;                           // World range on Y axis
	static int eventCount;                  // event counter in the system to assign unique id for new event
	static Map<List<Integer>, Event> map;   // map location to event, each location has maximum one event
	
	EventFinder(int[] rangeX, int[] rangeY) {
		this.rangeX = rangeX;
		this.rangeY = rangeY;
		eventCount = 0;
		map = new HashMap<>();
	}
	
	// Create an event, can be used by Admin to post a new event to the website
	// @param Location for the new event
	public void addEvent(List<Integer> location) {
		Event event = new Event(location, ++eventCount);
		map.put(location, event);
	}
	
	// Remove an event from the system. Since at most 1 event at each location, remove it from the map.
	//@ Location of the event
	public void removeEvent(List<Integer> location) {
		map.remove(location);
	}
	
	// When a user wants to post a ticket for selling, add the ticket to this event in the system
	// @param Location of the event
	// @param Price of the ticket
	public void addTicketToEvent(List<Integer> location, double price) {
		if (!map.containsKey(location)) { // input location is invalid
			System.out.println("Please first create an event.");
		} else {
			Event event = map.get(location);
			event.tickets.offer(price);
		}
	}
	
	// When a user purchases a ticket, remove the ticket from the event in the system
	// @param Location of the event
	// @param Price of the ticket
	public void removeTicketFromEvent(List<Integer> location, double price) {
		if (!map.containsKey(location)) { // input location is invalid
			System.out.println("There is no event.");
		} else {
			Event event = map.get(location);
			if (event.tickets.isEmpty()) { // this event has no tickets left
				System.out.println("No tickets available for removal.");
			} else {
				event.tickets.remove(price);
			}
		}
	}
	
	// Get 5 closest events with at least one ticket left using BFS
	// @param Location
	// @return A list of event 
	public List<Event> findEvents(List<Integer> location) {
		int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
		List<Event> events = new ArrayList<>();   
		Queue<List<Integer>> queue = new LinkedList<>();
		boolean[][] visited = new boolean[rangeX[1] - rangeX[0] + 1][rangeY[1] - rangeY[0] + 1]; // keep track of the visited locations
		
		// First check if the user location itself has event or not
		if (map.containsKey(location) && !map.get(location).tickets.isEmpty()) {
			events.add(map.get(location));
			visited[location.get(0)+10][location.get(1)+10] = true;
		}
		
		queue.offer(location);
		while (events.size() < 5 && !queue.isEmpty()) {
			int size = queue.size();
			for (int i = 0; i < size; i++) {
				List<Integer> cur = queue.poll();
				for (int[] dir : dirs) {
					int x = cur.get(0) + dir[0];
					int y = cur.get(1) + dir[1];
					List<Integer> loc = new ArrayList<>(Arrays.asList(x, y));
					if (x >= rangeX[0] && x <= rangeX[1] && y >= rangeY[0] && y <= rangeY[1] && !visited[x-rangeX[0]][y-rangeX[0]]) {
						if (events.size() < 5 && map.containsKey(loc) && !map.get(loc).tickets.isEmpty()) {
							events.add(map.get(loc));
						}
						visited[x-rangeX[0]][y-rangeX[0]] = true;
						queue.offer(loc);
					}
				}
			}
		}
		return events;
	}
	
	public static void main(String[] args) {
		
		// create EventFinder object with specified world range along X axis and Y axis
		int[] rangeX = {-10, 10};
		int[] rangeY = {-10, 10};
		EventFinder eventFinder = new EventFinder(rangeX, rangeY);
		
		// Add 6 Events, each at one different location
		List<Integer> location1 = new ArrayList<>(Arrays.asList(1, 0));  eventFinder.addEvent(location1);
		List<Integer> location2 = new ArrayList<>(Arrays.asList(0, 1));  eventFinder.addEvent(location2);
		List<Integer> location3 = new ArrayList<>(Arrays.asList(1, 1));  eventFinder.addEvent(location3);
		List<Integer> location4 = new ArrayList<>(Arrays.asList(0, -1)); eventFinder.addEvent(location4);
		List<Integer> location5 = new ArrayList<>(Arrays.asList(-1, 0)); eventFinder.addEvent(location5);
		List<Integer> location6 = new ArrayList<>(Arrays.asList(3, 5));  eventFinder.addEvent(location6);
		
		// Add two tickets to each event
		eventFinder.addTicketToEvent(location1, 30.51);
		eventFinder.addTicketToEvent(location1, 29.04);
		
		eventFinder.addTicketToEvent(location2, 20.32);
		eventFinder.addTicketToEvent(location2, 33.51);
		
		eventFinder.addTicketToEvent(location3, 32.51);
		eventFinder.addTicketToEvent(location3, 38.14);
		
		eventFinder.addTicketToEvent(location4, 23.45);
		eventFinder.addTicketToEvent(location4, 43.34);
		
		eventFinder.addTicketToEvent(location5, 32.36);
		eventFinder.addTicketToEvent(location5, 20.51);
		
		eventFinder.addTicketToEvent(location6, 14.23);
		eventFinder.addTicketToEvent(location6, 34.52);
		
		/* 
		 * You can also un-comment this block below to remove tickets from event to simulate the ticket purchasing operations
		 * Events with no tickets left won't show up when searching based on my assumption since there is no cheapest price.
		//eventFinder.removeTicketFromEvent(location2, 20.32);
		//eventFinder.removeTicketFromEvent(location2, 33.51);
		 * 
		 */
		
		// generate a user location randomly within the world range
		System.out.println("Please Input Coordinates:");
		Random random = new Random();
		int x = random.nextInt(21) - 10;
		int y = random.nextInt(21) - 10;
		System.out.println(x + "," + y);
		List<Integer> userLoc = new ArrayList<>(Arrays.asList(x, y));
		
		// get a list of the five closest events, along with the cheapest ticket price for each event
		List<Event> events = eventFinder.findEvents(userLoc);
		System.out.println("Closest Event to " + "(" + x + "," + y + "):");
		for (Event event : events) {
			System.out.println("Event" + event.eventId + " - " + "$" + event.tickets.peek() + ", " + "Distance " + (Math.abs(event.location.get(0) - x) + Math.abs(event.location.get(1) - y)));
		}
	}
}


