package lusiiplugin.utils;

import net.minecraft.core.entity.player.EntityPlayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlayerHomes implements Serializable {
	private static final long serialVersionUID = 1L; // Ensures version compatibility during deserialization
	HashMap<String, HomePosition> userHomes;

	private PlayerHomes() {
		this.userHomes = new HashMap<>();
	}

	public static PlayerHomes blank() {
		return new PlayerHomes();
	}

	public int getAmount() {
		return userHomes.size();
	}
	public static PlayerHomes fromMap(Map<String, HomePosition> userHomes) {
		PlayerHomes ph = new PlayerHomes();
		ph.userHomes = (HashMap<String, HomePosition>) userHomes;
		return ph;
	}

	/**
	 * @return <code>true</code> if the home was added, <code>false</code> if the home exists.
	 */
	public boolean setHome(EntityPlayer p, String homeName) {
		if (userHomes.containsKey(homeName)) {
			return false;
		}

		userHomes.put(homeName, new HomePosition(p.x, p.y, p.z, p.dimension));
		return true;
	}

	public void addHome(String name, double x, double y, double z, int dim) {
		userHomes.put(name, new HomePosition(x, y, z, dim));
	}

	/**
	 * @return <code>true</code> if the home was removed, <code>false</code> if the home does not exist.
	 */
	public boolean delHome(String homeName) {
		if (!userHomes.containsKey(homeName)) {
			return false;
		}

		userHomes.remove(homeName);
		return true;
	}

	/**
	 * @return An Optional containing the <code>HomePosition</code> for the player, or an empty Optional if it does not exist.
	 */
	public Optional<HomePosition> getHomePos(String homeName) {
		return Optional.ofNullable(userHomes.get(homeName));
	}

	/**
	 * @return An Optional containing an <code>ArrayList</code> of home names for the player,
	 * or an empty Optional if the player has no homes.
	 */
	public Optional<ArrayList<String>> getHomesList() {
		ArrayList<String> result = new ArrayList<>(userHomes.keySet());

		if (result.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(result);
		}
	}

}
