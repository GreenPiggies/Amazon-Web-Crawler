package awc.csv;

import java.util.LinkedHashMap;
import java.util.Map;

public class Entry {
    private LinkedHashMap<String, String> mp;

    public Entry() {
        mp = new LinkedHashMap<>();
        mp.put("RECORD_ID", "");
        mp.put("RECORD_DATETIME", "");
        mp.put("RECORD_URL", "");
        mp.put("RECORD_TITLE", "");
        mp.put("RECORD_TEXT", "");
        mp.put("DOMAIN_ROOT_URL", "");
        mp.put("CITY_NAME", "");
        mp.put("STATE_CODE", "");
        mp.put("COUNTRY_CODE", "");
        mp.put("GPS_COORDINATES", "");
        mp.put("AUTHOR_ID", "");
        mp.put("AUTHOR_HANDLE", "");
        mp.put("AUTHOR_NAME", "");
        mp.put("AUTHOR_GENDER", "");
        mp.put("AUTHOR_DESCRIPTION", "");
        mp.put("AUTHOR_PROFILE_URL", "");
        mp.put("AUTHOR_AVATAR_URL", "");
        mp.put("AUTHOR_FOLLOWERS", "");
        mp.put("AUTHORS_VERIFIED_STATUS", "");
        mp.put("META_TAGS", "");
        mp.put("META_TAGS2", "");
        mp.put("NET_PROMOTER_SCORE", "");
        mp.put("OVERALL_STAR_RATING", "");
        mp.put("OVERALL_SURVEY_SCORE", "");
        mp.put("SOURCE_TYPE", "");
    }

    public void set(String key, String value) {
        if (key != null && mp.keySet().contains(key)) {
            mp.replace(key, value);
        }
    }

    public String get(String key) {
        if (key != null && mp.keySet().contains(key)) {
            return mp.get(key);
        }
        return null;
    }

    public String toString() {
        StringBuffer buff = new StringBuffer();
        for (String key : mp.keySet()) {
            System.out.println(key);
            String value = mp.get(key);
            System.out.println(value);
            if (value.startsWith("<")) {
                return "";
            }
            value = value.replaceAll("\"", "\"\"");
            value = "\"" + value + "\"";
            buff.append(value + ',');
        }
        return buff.toString().substring(0, buff.toString().length() - 1); // remove the final comma
    }
}
