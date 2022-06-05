package com.micerlabs.LitStraw.Utils;

import com.micerlabs.LitStraw.Config.Config;

import java.util.Set;

public class MatchUtils {

    /**
     * 用keywordSet尝试匹配到Caption
     *
     * @param formatStrSet
     * @param pattern
     * @return
     */
    public static boolean matchKeyWordForCaption(Set<String> formatStrSet, String pattern) {
        if (pattern.length() >= new Config().getKeywordMatchMaxLen()) {
            return false;
        }
        for (String str : formatStrSet) {
            if (str.matches("(?i)" + RegexUtils.formatString(pattern))) {
                return true;
            }
        }
        return false;
    }


    public static boolean matchKeyWordForPattern(String text, String pattern) {
        if (text.length() >= new Config().getKeywordMatchMaxLen()) {
            return false;
        }
        return RegexUtils.formatString(pattern).matches("(?i)" + RegexUtils.formatString(text));
    }
}
