package me.carc.fakecallandsms_mvp.common.utils;

/**
 * Basic algorithms that are not in jdk
 */
public class Common {
	private static final int BUFFER_SIZE = 1024;

	public static boolean isEmpty(String s) { return s == null || s.length() == 0; }

	private static boolean isDigit(char charAt) {
		return charAt >= '0' && charAt <= '9';
	}

	public static String capitalizeFirstLetterAndLowercase(String s) {
		if (s != null && s.length() > 1) {
			// not very efficient algorithm
			return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
		} else {
			return s;
		}
	}
}