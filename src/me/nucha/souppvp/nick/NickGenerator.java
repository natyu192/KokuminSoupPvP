package me.nucha.souppvp.nick;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NickGenerator {

	@SuppressWarnings("unused")
	private static List<String> shortWords;
	private static List<String> longWords;
	@SuppressWarnings("unused")
	private static List<String> conjuctions;
	private static List<String> onlyNames;
	private static List<String> japaneseNames;

	public static void init() {
		shortWords = Arrays.asList(new String[] { "About", "Active", "Admit", "Advise", "Again", "After",
				"Agent", "Alive", "Alone", "Beach", "Basket", "Basic", "Bath", "Battle", "Bean", "Beat",
				"Bed", "Become", "Begin", "Before", "Beer", "Behind", "Blade", "Black", "Blue", "Bomb",
				"Brush", "Build", "Bunch", "Button", "Biz", "Busy", "Box", "Boy", "Break", "Best", "Better",
				"Cake", "Camera", "Campus", "Cap", "Card", "Care", "Case", "Catch", "Center", "Chain", "Chair",
				"Chara", "Charge", "Chase", "Cheap", "Cheese", "Check", "Close", "Choose", "Christ", "Circle",
				"Dad", "Dance", "Dark", "Data", "Dead", "Defend", "Desert", "Desk", "Device", "Detect",
				"Dinner", "Direct", "Dirt", "Dirty", "Doctor", "Down", "Drama", "Draw", "Dream", "Drop",
				"Earth", "Eat", "Easy", "Editor", "Effect", "Eight", "Elect", "Effort", "Emote", "Enter",
				"Engine", "Enemy", "Empty", "Entry", "Error", "Enough", "Every", "Exact", "Eye", "Expert",
				"Face", "Fact", "Fade", "Fail", "Family", "Famous", "Farmer", "Father", "Fight", "Find", "Finger",
				"Fire", "First", "Fit", "Fix", "Fish", "Field", "Floor", "Focus", "Fly", "Forest", "Force", "Frame" });
		longWords = Arrays.asList(new String[] { "Actually", "Aircraft", "Backbone", "Blooming", "Brightly", "Building",
				"Camellia", "Cardinal", "Careless", "Chemical", "Cheerful", "Civilian", "Daughter", "Demolish", "Detector",
				"Disaster", "Disposal", "Electron", "Elective", "Engaging", "Enormous", "Erection", "Evidence", "Exertion",
				"External", "Faithful", "Familiar", "Favorite", "Fearless", "Fixation", "Fragment", "Generous", "Grateful",
				"Grievous", "Hydrogen", "Horrible", "Ignorant", "Industry", "Majority", "Military", "Mountain", "Mythical",
				"Normally", "Numerous", "Organism", "Overview", "Pacifist", "Pentagon", "Perilous", "Physical", "Precious",
				"Prestige", "Puzzling", "Railroad", "Reckless" });
		conjuctions = Arrays.asList(new String[] { "The", "And", "Of", "Is" });
		onlyNames = Arrays.asList(new String[] { "Ibirawyr", "Niniel", "Celahan", "Gwysien", "Figovudd", "Zathiel",
				"Adwiawyth", "Nydinia", "Laraeb", "Eowendasa", "Grendakin", "Werradia", "Cauth", "Umigolian",
				"Tardond", "Dwearia", "Yeiwyn", "Adraclya", "Zaev", "Thabeth", "Chuven", "Zaredon",
				"Bob", "Robert", "Johnny", "Joy", "Matthew", "Michael", "Jacob", "Joshua",
				"Daniel", "Christopher", "Andrew", "Ethan", "Joseph", "William", "Anthony", "David", "Alexander", "Nicholas",
				"Ryan", "Tyler", "James", "John", "Jonathan", "Noah", "Brandon", "Christian", "Dylan", "Samuel", "Benjamin",
				"Nathan", "Zachary", "Logan", "Justin", "Gabriel", "Emily", "Madison", "Emma", "Olivia", "Hannah", "Abigail",
				"Isabella", "Samantha", "Elizabeth", "Ashley", "Alexis", "Sarah", "Sophia", "Alyssa", "Grace", "Ava", "Taylor",
				"Brianna", "Lauren", "Chloe", "Natalie", "Kayla", "Jessica", "Anna", "Victoria", "Mia", "Hailey", "Sydney", "Jasmine" });
		japaneseNames = Arrays.asList(
				new String[] { "Ai", "Aya", "Ayako", "Itsuki", "Eita", "Eiko", "Kanta", "Kaito", "Kenta", "Kento", "Kouki", "Kouta",
						"Kouya", "Kou", "Keito", "Keita", "Saya", "Sayako", "Sara", "Sizuki", "Sizuko", "Sizuno", "Sizuya",
						"Suzuka", "Suzuki", "Suzuko", "Sumi", "Seiya", "Souta", "Souya", "Sou", "Taichi", "Takuya", "Tatsuki",
						"Chitose", "Tutomu", "Tumuya", "Tetsuya", "Tetsuto", "Tekuto", "Touya", "Tomi", "Nami", "Nao", "Neo", "Notomi",
						"Haruya", "Harumi", "Haruto", "Hitomi", "Hitoshi", "Fuuta", "Fuyuki", "Fuuto", "Mami", "Maya", "Mai", "Masaya",
						"Masahiro", "Masato", "Misaki", "Mitsuki", "Mutsuki", "Mei", "Yae", "Yuuto", "Yuuta", "Yuuya", "Youta", "Youki", });
	}

	public static String generate() {
		Random random = new Random();
		return generate(NicknamePattern.values()[random.nextInt(NicknamePattern.values().length)]);
	}

	public static String generate(NicknamePattern pattern) {
		String theNick = null;
		if (pattern.equals(NicknamePattern.NameWithNumbers)) {
			Random random = new Random();
			String name = onlyNames.get(random.nextInt(onlyNames.size()));
			if (name.length() <= 10 && chance(50)) {
				theNick = name + "_" + random.nextInt(9999);
			} else {
				theNick = name + random.nextInt(9999);
			}
		}
		if (pattern.equals(NicknamePattern.TwoShortsWithConjuction)) {
			/*Random random = new Random();
			String short1 = shortWords.get(random.nextInt(shortWords.size()));
			String short2 = shortWords.get(random.nextInt(shortWords.size()));
			String conjuction = conjuctions.get(random.nextInt(conjuctions.size()));
			if ((short1.length() + short2.length() + conjuction.length()) < 14 && chance(50)) {
				theNick = short1 + "_" + conjuction + "_" + short2;
			} else {
				theNick = short1 + conjuction + short2;
			}*/
			pattern = NicknamePattern.JapaneseNameWithBirth;
		}
		if (pattern.equals(NicknamePattern.JapaneseNameWithBirth)) {
			Random random = new Random();
			String name = japaneseNames.get(random.nextInt(japaneseNames.size()));
			String birth = String.valueOf(random.nextInt(12) + 1) + String.valueOf(random.nextInt(30) + 1);
			if (chance(50)) {
				theNick = name + "_" + birth;
			} else {
				theNick = name + birth;
			}
		}
		if (pattern.equals(NicknamePattern.LongWithNumbers)) {
			Random random = new Random();
			String name = longWords.get(random.nextInt(longWords.size()));
			if (chance(50)) {
				theNick = name + "_" + random.nextInt(9999);
			} else {
				theNick = name + random.nextInt(9999);
			}
		}
		if (chance(50)) {
			theNick = theNick.toLowerCase();
		}
		return theNick;
	}

	public enum NicknamePattern {
		NameWithNumbers, TwoShortsWithConjuction, JapaneseNameWithBirth, LongWithNumbers;
	}

	private static boolean chance(double percent) {
		return Math.random() < (percent / 100);
	}

}
