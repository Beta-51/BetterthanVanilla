package lusiiplugin.utils;

import lusiiplugin.LusiiPlugin;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.util.phys.Vec3d;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerTPInfo {
	private Instant lastTPtime;
	private HomePosition lastPos;
	private ArrayList<String> requests;

	public PlayerTPInfo(EntityPlayer p) {
		lastTPtime = Instant.now().minus(Duration.ofSeconds(LusiiPlugin.TPTimeout));
		requests = new ArrayList<>();
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

	public boolean sendRequest(String reqester) {
		if (requests.contains(reqester)) {
			return false;
		}
		requests.add(reqester);
		return true;
	}

	public boolean hasNoRequest() {
		return requests.isEmpty();
	}

	public String getNewestRequest() {
		// Get the index of the newest request
		int lastIndex = requests.size() - 1;

		// Check if there are any requests
		if (lastIndex >= 0) {
			return requests.get(lastIndex);
		} else {
			// Return null if there are no requests
			return null;
		}
	}

	public List<String> getAllRequests() {
		return requests;
	}

	public boolean hasRequestFrom(String acceptedPlayer) {
		return requests.contains(acceptedPlayer);
	}

	public void removeRequest(String removePlayer) {
		requests.remove(removePlayer);
	}

	// Override hashCode and equals methods for proper functioning in HashMap
	@Override
	public int hashCode() {
		return Objects.hash(lastTPtime, lastPos, requests);
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
