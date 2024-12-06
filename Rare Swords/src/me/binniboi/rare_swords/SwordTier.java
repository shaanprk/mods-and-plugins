package me.binniboi.rare_swords;

import org.apache.commons.lang.WordUtils;

public enum SwordTier {

    SUPREME_GRADE, GREAT_GRADE, SKILLFUL_GRADE, GRADE;

    public static SwordTier fromString(String str) {
        for (SwordTier tier : values()) {
            if (tier.toString().equalsIgnoreCase(str)) return tier;
        }
        return null;
    }

    public String getID() {
        return toString().toLowerCase();
    }

    public String getDisplayName() {
        return WordUtils.capitalize(toString().toLowerCase().replace("_", " "));
    }

}
