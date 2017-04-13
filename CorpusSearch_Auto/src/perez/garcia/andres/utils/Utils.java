package perez.garcia.andres.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Utils {

	public static String normalizeString(String stringToNormalize) {
		return stringToNormalize.toLowerCase().trim().replace(" ", "_").replaceAll("[^a-zA-Z0-9_]+", "");
	}

	public static String convertStreamToString(InputStream inputStream) throws IOException {
		Scanner scanner = new Scanner(inputStream);
		scanner.useDelimiter("\\A");
		String string = scanner.hasNext() ? scanner.next() : "";
		scanner.close();
		return string;
	}

	public static boolean askUserToContinue(String question) {
		System.out.print(question);
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String answer = scanner.nextLine();

		switch (answer.toLowerCase()) {
		case "yes":
		case "y":
			return true;
		case "no":
		case "n":
			return false;
		default:
			System.out.println("Invalid choice.");
			return askUserToContinue(question);
		}
	}

	public static Map<TimeUnit, Long> getElapsedTime(Date oldDate, Date newDate) {
		long milliesRest = newDate.getTime() - oldDate.getTime();
		List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
		Collections.reverse(units);
		Map<TimeUnit, Long> result = new LinkedHashMap<TimeUnit, Long>();
		for (TimeUnit unit : units) {
			long diff = unit.convert(milliesRest, TimeUnit.MILLISECONDS);
			long diffInMilliesForUnit = unit.toMillis(diff);
			milliesRest = milliesRest - diffInMilliesForUnit;
			result.put(unit, diff);
		}
		return result;
	}
	
	public static void successfulMessage(String processName, Date startDate, Date endDate) {
		Map<TimeUnit, Long> elapsedTime = Utils.getElapsedTime(startDate, endDate);
		long hours, minutes, seconds;
		hours = elapsedTime.get(TimeUnit.HOURS) + (elapsedTime.get(TimeUnit.DAYS) * 24);
		minutes = elapsedTime.get(TimeUnit.MINUTES);
		seconds = elapsedTime.get(TimeUnit.SECONDS);
		System.out.println(processName + " successfully processed in " + hours + " hours, " + minutes + " minutes and " + seconds + " seconds.");
	}
	
	public static String removeExtension(String name) {
	    String separator = System.getProperty("file.separator");
	    String filename;

	    int lastSeparatorIndex = name.lastIndexOf(separator);
	    if (lastSeparatorIndex == -1) {
	        filename = name;
	    } else {
	        filename = name.substring(lastSeparatorIndex + 1);
	    }

	    int extensionIndex = filename.lastIndexOf(".");
	    if (extensionIndex == -1)
	        return filename;

	    return filename.substring(0, extensionIndex);
	}
}
