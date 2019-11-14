package me.nucha.souppvp.util;

public class SymbolUtils {

	public static String warn() {
		return "⚠";
	}

	public static String x() {
		return "✕";
	}

	public static String check() {
		return "✔";
	}

	public static String wool_placed() {
		return "█";
	}

	public static String wool_pickedup() {
		return "▒";
	}

	public static String wool_unpicked() {
		return "⬜";
	}

	public static String star(int level) {
		switch (level) {
		case 2:
			return "✶";
		case 3:
			return "✷";
		case 4:
			return "✸";
		case 5:
			return "✹";
		case 6:
			return "✺";
		default:
			return "✴";
		}
	}

	public static String convertRomaNumbers(int number) {
		switch (number) {
		case 1:
			return "I";
		case 2:
			return "II";
		case 3:
			return "III";
		case 4:
			return "IV";
		case 5:
			return "V";
		case 6:
			return "VI";
		case 7:
			return "VII";
		case 8:
			return "VIII";
		case 9:
			return "IX";
		case 10:
			return "X";
		case 11:
			return "XI";
		case 12:
			return "XII";
		case 13:
			return "XIII";
		case 14:
			return "XIV";
		case 15:
			return "XV";
		case 16:
			return "XVI";
		case 17:
			return "XVII";
		case 18:
			return "XVIII";
		case 19:
			return "XIX";
		case 20:
			return "XX";
		default:
			return String.valueOf(number);
		}
	}

}
