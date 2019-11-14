package me.nucha.souppvp.language;

public enum Language {
	JAPANESE, ENGLISH;

	public static Language parse(String languageName) {
		for (Language language : values()) {
			if (language.name().equalsIgnoreCase(languageName)) {
				return language;
			}
		}
		return Language.JAPANESE;
	}
}
