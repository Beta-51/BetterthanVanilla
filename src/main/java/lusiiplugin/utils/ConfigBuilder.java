package lusiiplugin.utils;

import lusiiplugin.LusiiPlugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ConfigBuilder {
	private static final Map<String, String> colorMap = new HashMap<>(24);
	static  {
		colorMap.put("white", "0");
		colorMap.put("orange", "1");
		colorMap.put("magenta", "2");
		colorMap.put("aqua", "3");
		colorMap.put("yellow", "4");
		colorMap.put("lime", "5");
		colorMap.put("pink", "6");
		colorMap.put("grey", "7");
		colorMap.put("gray", "7");
		colorMap.put("silver", "8");
		colorMap.put("cyan", "9");
		colorMap.put("purple", "a");
		colorMap.put("blue", "b");
		colorMap.put("brown", "c");
		colorMap.put("green", "d");
		colorMap.put("red", "e");
		colorMap.put("black", "f");
		colorMap.put("obf", "k");
		colorMap.put("b", "l");
		colorMap.put("s", "m");
		colorMap.put("u", "n");
		colorMap.put("i", "o");
		colorMap.put("r", "r");
		colorMap.put("reset", "r");
	}

	private Path filePath;
	private String fileName;
	private List<String> defaultContent;
	private boolean syntaxEnabled;
	public ConfigBuilder(String fileName, List<String> defaultContent, boolean syntaxEnabled) {
		this.fileName = fileName;
		this.defaultContent = defaultContent;
		this.syntaxEnabled = syntaxEnabled;
		this.filePath = Paths.get(LusiiPlugin.CFG_DIR);
		createBase();
	}

	private void createBase() {
		Path baseFile = filePath.resolve(fileName + ".txt");
		if (!Files.exists(baseFile)) {
			try {
				System.out.println(baseFile + " does not exist. Creating it for you...");
				Files.write(baseFile, defaultContent, StandardCharsets.UTF_8);
				System.out.println("Done! Check your config folder for " + baseFile);
			} catch (IOException e) {
				System.err.println("Error creating file: " + e.getMessage());
			}
		}
	}
	public List<String> get(int pageNumber) {
		if (pageNumber <= 1) {
			return readFile(filePath.resolve(fileName + ".txt"));
		}
		return readFile(filePath.resolve(fileName + pageNumber + ".txt"));
	}

	private List<String> readFile(Path path) {
		try {
			List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
			if (syntaxEnabled) {
				return parseSyntax(lines);
			} else {
				return lines;
			}
		} catch (IOException e) {
			List<String> errorMsg = Collections.singletonList(parseTags("<orange>Page not found.<r>"));
			return parseSyntax(errorMsg);
		}
	}

	private List<String> parseSyntax(List<String> content) {
		List<String> parsedLines = new ArrayList<>();

		for (String line : content) {
			if (line.startsWith("///")) continue; // Skip comments
			line = parseTags(line);
			parsedLines.add(line);
		}

		return parsedLines;
	}

	private String parseTags(String line) {
		// Handle escaping
		line = line.replaceAll("\\\\<", "ESCAPED_LT").replaceAll("\\\\>", "ESCAPED_GT");
		// Process color tags
		for (Map.Entry<String, String> entry : colorMap.entrySet()) {
			line = line.replaceAll("<" + entry.getKey() + ">", "§" + entry.getValue());
		}
		// Revert escaped characters
		return line.replaceAll("ESCAPED_LT", "<").replaceAll("ESCAPED_GT", ">");
	}
}
