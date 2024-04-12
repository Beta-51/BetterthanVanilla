package lusiiplugin.utils.TPA;

import lusiiplugin.LusiiPlugin;
import lusiiplugin.utils.HomePosition;
import net.minecraft.core.entity.player.EntityPlayer;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class PlayerTPInfo {
	private Instant lastTPtime;
	private HomePosition lastPos;
	private ArrayList<String> requestsOrder;
	private HashMap<String, RequestType> requests;

	public PlayerTPInfo(EntityPlayer p) {
		lastTPtime = Instant.now().minus(Duration.ofSeconds(LusiiPlugin.TPTimeout));
		requestsOrder = new ArrayList<>();
		requests = new HashMap<>();
		lastPos = new HomePosition(p.x, p.y, p.z, p.dimension);
	}

	// Returns 0 if tp is available
	public int cooldown() {
		Instant now = Instant.now();
		Instant TPAvailable = lastTPtime.plus(Duration.ofSeconds(LusiiPlugin.TPTimeout));

		if (now.isAfter(TPAvailable)) {
			return 0;
		}

		return Math.toIntExact(Duration.between(now, TPAvailable).getSeconds());
	}

	public boolean canTP() {
		return cooldown() == 0;
	}

	public HomePosition getLastPos() {
		return new HomePosition(lastPos.x, lastPos.y, lastPos.z, lastPos.dim);
	}

	public void update(EntityPlayer p) {
		lastTPtime = Instant.now();
		lastPos = new HomePosition(p.x, p.y, p.z, p.dimension);
	}

	public boolean atNewPos(EntityPlayer p) {
		HomePosition newPos = new HomePosition(p.x, p.y, p.z, p.dimension);
		return !(newPos.equals(lastPos));
	}

	/**
	 * Send a teleport request from a user.
	 * <br>
	 * If a user with a pending request sends a new request of a diffent type (TPA vs TPAHERE)
	 * the old request is removed and the new one is moved to the front of the list.
	 * @param username
	 * @return <code>true</code> if request was sent, <code>false if that user has a pending request</code>
	 */
	public boolean sendRequest(String username, RequestType type) {
		if (requests.get(username) == type) {
			return false;
		}
		requestsOrder.remove(username);
		requestsOrder.add(username);
		requests.put(username, type);
		return true;
	}

	public boolean hasNoRequests() {
		return requests.isEmpty();
	}

	public Request getNewestRequest() {
		// Get the index of the newest request
		int lastIndex = requests.size() - 1;

		// Check if there are any requests
		if (lastIndex >= 0) {
			String lastRequestName = requestsOrder.get(lastIndex);
			return new Request(requests.get(lastRequestName), lastRequestName);
		} else {
			// Return null if there are no requests
			return null;
		}
	}

	public List<String> getAllRequests() {
		ArrayList<String> out = new ArrayList<>();

		for (String username : requestsOrder) {
			out.add(new Request(requests.get(username), username).toString());
		}

		return out;
	}

	public boolean hasRequestFrom(String username) {
		return requests.containsKey(username);
	}

	public void removeRequest(String username) {
		requestsOrder.remove(username);
		requests.remove(username);
	}

	// Override hashCode and equals methods for proper functioning in HashMap
	@Override
	public int hashCode() {
		return Objects.hash(lastTPtime, lastPos, requestsOrder, requests);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (getClass() != o.getClass())
			return false;
		PlayerTPInfo other = (PlayerTPInfo) o;
		if (lastPos != other.lastPos)
			return false;
		if (lastTPtime != other.lastTPtime)
			return false;
		if (requests != other.requests)
			return false;
		return true;
	}
}


