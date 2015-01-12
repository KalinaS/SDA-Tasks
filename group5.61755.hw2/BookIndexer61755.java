import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookIndexer61755 implements IBookIndexer {
	private Pattern pagePattern = Pattern.compile("^=== Page (\\d+) ===$");

	@Override
	public void buildIndex(String bookFilePath, String[] keywords, String indexFilePath) {
		try {
			List<String> lines = Files.readAllLines(Paths.get(bookFilePath), Charset.defaultCharset());
			Map<String, Set<Integer>> index = initIndex(keywords);

			Map<String, String> keywordMap = new HashMap<String, String>();
			for (String keyword : keywords) {
				keywordMap.put(keyword.toLowerCase(), keyword);
			}

			String keywordsPatternString = concatenateKeywords(keywords);
			if (keywordsPatternString != null) {
				Pattern keywordPattern = Pattern.compile(keywordsPatternString, Pattern.CASE_INSENSITIVE);

				int currentPage = -1;
				for (String line : lines) {
					Matcher matcher = pagePattern.matcher(line);
					if (matcher.find()) {
						currentPage = Integer.parseInt(matcher.group(1));
					} else {
						Matcher keywordMatcher = keywordPattern.matcher(line);
						while (keywordMatcher.find()) {
							index.get(keywordMap.get(keywordMatcher.group(1).toLowerCase())).add(currentPage);
						}
					}

				}
			}
			writeIndex(index, indexFilePath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeIndex(Map<String, Set<Integer>> index, String fileName) throws IOException {
		StringBuilder builder = new StringBuilder("INDEX");
		for (Entry<String, Set<Integer>> entry : index.entrySet()) {
			builder.append("\r\n").append(entry.getKey()).append(", ");
			Iterator<Integer> iterator = entry.getValue().iterator();
			if (iterator.hasNext()) {
				Integer startRange = iterator.next();
				Integer rangeLength = 1;
				while (iterator.hasNext()) {
					Integer page = iterator.next();
					if (page - rangeLength == startRange) {
						rangeLength++;
					} else {
						printPages(builder, startRange, rangeLength, false);
						rangeLength = 1;
						startRange = page;
					}

				}
				printPages(builder, startRange, rangeLength, true);
			}
		}
		Files.write(Paths.get(fileName), builder.toString().getBytes());
	}

	private void printPages(StringBuilder builder, Integer startRange, Integer rangeLength, boolean isLast) {
		if (rangeLength == 1) {
			builder.append(startRange);
		} else {
			builder.append(startRange).append("-").append(startRange + rangeLength - 1);
		}
		if (!isLast) {
			builder.append(",");
		}
	}

	private Map<String, Set<Integer>> initIndex(String[] keywords) {
		Map<String, Set<Integer>> index = new TreeMap<String, Set<Integer>>();
		for (String keyword : keywords) {
			index.put(keyword, new TreeSet<Integer>());
		}
		return index;
	}

	private String concatenateKeywords(String[] keywords) {
		String regexp = "(";
		for (String keyword : keywords) {
			regexp += keyword + "|";
		}
		if (regexp.endsWith("|")) {
			regexp = regexp.substring(0, regexp.length() - 1) + ")";
		} else {
			regexp = null;
		}
		return regexp;
	}

}