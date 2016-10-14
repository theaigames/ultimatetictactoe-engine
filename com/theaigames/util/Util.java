package com.theaigames.util;

public final class Util {

	private Util() {
	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);  
	}
	
}