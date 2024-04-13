package lusiiplugin.utils;

import java.util.List;
import java.util.Collections;

public class Paginator {
	public static <T> List<T> getPage(List<T> list, int pageNumber, int itemsPerPage) {
		if (list == null || list.isEmpty() || itemsPerPage <= 0 || pageNumber < 1) {
			return Collections.emptyList(); // Return an empty list for invalid input
		}

		int totalItems = list.size();
		int startIndex = (pageNumber - 1) * itemsPerPage;
		if (startIndex >= totalItems || startIndex < 0) {
			return Collections.emptyList(); // Page number out of range
		}

		int endIndex = startIndex + itemsPerPage;
		endIndex = Math.min(endIndex, totalItems); // Ensure the end index does not exceed the list size

		return list.subList(startIndex, endIndex);
	}
}
