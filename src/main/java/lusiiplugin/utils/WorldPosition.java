package lusiiplugin.utils;

import java.io.Serializable;
import java.util.Objects;

public class WorldPosition implements Serializable {
	private static final long serialVersionUID = 1L; // Ensures version compatibility during deserialization

	public double x, y, z;
	public int dim;

	public WorldPosition(double x, double y, double z, int dim) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.dim = dim;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z, dim);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WorldPosition that = (WorldPosition) o;
		return Double.compare(that.x, x) == 0 &&
			Double.compare(that.y, y) == 0 &&
			Double.compare(that.z, z) == 0 &&
			dim == that.dim;
	}
}
