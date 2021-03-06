package edu.oregonstate.general;

import java.util.*;

/**
 * String Operation
 * 
 * @author Jun Xie (xie@eecs.oregonstate.edu)
 *
 */
public class StringOperation {

	private StringOperation() {
	}
	
	/**
	 * split the string according to the splitter and trim the spaces
	 * 
	 * @param string
	 * @param splitter
	 * @return
	 */
	public static String[] splitString(String string, String splitter) {
		String[] elements = string.split(splitter);
		// trim the space before and after
		int length = elements.length;
		String[] trimdElements = new String[length];
		for (int index = 0; index < length; index++) {
			String value = elements[index];
			trimdElements[index] = value.trim();
		}
		
		return trimdElements;
	}
	
	/**
	 * convert the set of string to a set of lower case string
	 * 
	 * @param set
	 * @return
	 */
	public static Set<String> lowercase(Set<String> set) {
		Set<String> result = new HashSet<String>();
		
		for (String element : set) {
			result.add(element.toLowerCase());
		}
		
		return result;
	}
	
}
