package com.micerlabs.LitStraw.Utils;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    /**
     * Adobe Regex
     */
    private static String paraRegex = "//Document/P(.*)";
    private static String HeadRegex = "//Document/H(.*)";
    private static String TitleRegex = "//Document/Title";
    private static String TableRegex = "//Document/Table(.*)";
    private static String TableTextRegex = "//Document/P\\[\\d+\\]/Sub(.*)";
    private static String FootnoteRegex = "//Document/Footnote(.*)";

    /**
     *
     */

    /**
     * 输入pdf文件全称，获取pdf文件前缀名
     * @param totalName
     * @return
     */
    public static String getPdfName(String totalName) {
        return matchSingleRegex(totalName, "(.*)\\.pdf");
    }

    /**
     * match 所有a-b类型的pattern
     */
    public static List<String> getBarPatterns(String text) {
        return matchAllRegex(text,"([a-zA-Z]+\\-[a-z]+)");
    }

    /**
     * 找到String中match regex的部分，找不到则返回null
     * @param string
     * @param regex
     * @return
     */
    public static String matchSingleRegex(String string, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(string);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    /**
     * 找到String中match 所有regex的部分，找不到则返回null
     * @param str
     * @param regex
     * @return
     */
    public static List<String> matchAllRegex(String str, String regex) {
        List<String> res = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
//            System.out.println(matcher.group());
            res.add(matcher.group());
        }
        return res;
    }


    /**
     * 格式化String 只保留英文字符
     * @param str
     * @return
     */
    public static String formatString(String str){
        return str.replaceAll("[^a-zA-Z]", "");
    }

    /**
     * 格式化整个StringSet
     * @param strSet
     * @return
     */
    public static Set<String> formatStringSet(Set<String> strSet){
        Set<String> formatStrSet = new HashSet<>();
        for (String str:strSet){
            formatStrSet.add(formatString(str));
        }
        return formatStrSet;
    }




    public static boolean match(String regex,String text){
        return text.matches(regex);
    }

}
