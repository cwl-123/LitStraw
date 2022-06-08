package com.micerlabs.LitStraw.Extract;

import com.micerlabs.LitStraw.Domain.*;
import com.micerlabs.LitStraw.Utils.FileUtil;
import com.micerlabs.LitStraw.Utils.MatchUtils;
import com.micerlabs.LitStraw.Utils.RegexUtils;
import com.github.houbb.word.checker.util.EnWordCheckers;
import com.micerlabs.LitStraw.Utils.StatisticsUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostProcess {

    private static final String ABSTRACT = "Abstract|A b s t r a c t";

    // 不需要在txt里保留的Section
    private static final String KEYWORDS = "Keywords";
    private static final String INTRODUCTION = "Introduction";
    private static final String REFERENCES = "References";
    private static final String APPENDIX = "Appendix";

    // 正文不要的Section的CaptionNameSet
    private static final Set<String> removeSectionCaptionNameSet = Sets.newHashSet("Acknowledgements", "Acknowledgement", "Acknowledgments", "Funding", "Data Availability", "Open Access", "Supplementary materials", "Appendices. Supplementary data", "Supplementary data");


    /************************1. 日志观察 **********************************************************/

    /**
     * 分别提取出各Pattern看看
     */
    public static Map<TextPatternTypeEnum, List<TextPattern>> separatePattern(List<TextPattern> textPatternList) {
        Map<TextPatternTypeEnum, List<TextPattern>> map = new HashMap<>();
        for (TextPattern textPattern : textPatternList) {
            TextPatternTypeEnum patternType = textPattern.getPatternType();
            if (map.containsKey(patternType)) {
                List<TextPattern> textPatterns = map.get(patternType);
                textPatterns.add(textPattern);
            } else {
                List<TextPattern> textPatterns = new ArrayList<>();
                textPatterns.add(textPattern);
                map.put(patternType, textPatterns);
            }
        }
        return map;
    }

    /************************2. Pattern 过滤 **********************************************************/

    /**
     * Pattern 过滤后的结果
     */
    public static List<TextPattern> textPatternsFilter(List<TextPattern> patternList, Context context) {
        if (CollectionUtils.isEmpty(patternList)) {
            return Collections.emptyList();
        }
        List<TextPattern> filteredPattern = new ArrayList<>();
        for (TextPattern textPattern : patternList) {
            if (shouldRetainPattern(textPattern)) {
                filteredPattern.add(textPattern);
            }
        }
        return filteredPattern;
    }

    /**
     * Pattern过滤器 考虑Pattern是否保留问题
     */
    // ToDo:封装成规则器
    public static boolean shouldRetainPattern(TextPattern textPattern) {
        if (textPattern == null) {
            return false;
        }
        TextPatternTypeEnum patternType = textPattern.getPatternType();
        if (patternType.equals(TextPatternTypeEnum.UNKNOWN)) {
            return false;
        }
        if (patternType.equals(TextPatternTypeEnum.OTHER)) {
            return false;
        }
        /**
         * MainBody中一些东西剔除 如 合并大块后文本数量实在太少的
         */
        if (patternType.equals(TextPatternTypeEnum.MAIN_BODY)) {
            if (textPattern.getTextContent().length() <= 3) {
                // ToDo:3封装成参数
                return false;
            }
        }

        // 通用匹配器 [+-*/><]|纯数字
        String generalRegex = "\\d+|\\+|\\-|\\*|\\/|\\>|\\<";
        if (Pattern.matches(generalRegex, textPattern.getTextContent())) {
            return false;
        }
        return true; //默认保留
    }


    /************************3. PatternList转Literature **********************************************************/


    /**
     * patternList转化为Literature
     */
    public static Literature patternList2Literature(Context context, List<TextPattern> patternList) {
        //构造Literature
        Literature literature = new Literature();
        // 挑出图表出来作为浮动体，不放进去
        literature.setFloatSectionList(selectFloatSection(patternList));
        // 查找各自模块
        List<Section> sectionList = patternListToSectionList(formatPatternList(textPatternsFilter(patternList, context)));
        int abstractIdx = findFeatureCaptionIdx(sectionList, ABSTRACT);
        int keywordsIdx = findFeatureCaptionIdx(sectionList, KEYWORDS);
        int introductionIdx = findFeatureCaptionIdx(sectionList, INTRODUCTION);
        int referencesIdx = findFeatureCaptionIdx(sectionList, REFERENCES);
        int appendixIdx = findFeatureCaptionIdx(sectionList, APPENDIX);

        // 标题
        if (context.getTitle() != null) {
            literature.setTitle(context.getTitle());
        }
        // 摘要
        if (abstractIdx != -1) {
            literature.setSummary(sectionList.get(abstractIdx));
        } else if (sectionList.get(0).getCaption() == null) {
            // 如果没有Abstract关键字怎么办?找到第一个没有Caption的Section，人工设置为"Abstract"
            literature.setSummary(sectionList.get(0));
            abstractIdx = 0;
        }
        // 关键词
        if (keywordsIdx != -1) {
            literature.setKeywordList(sectionList.get(keywordsIdx).getContent().getTextContent());
        }
        // 引言
        if (introductionIdx != -1) {
            literature.setIntroduction(sectionList.get(introductionIdx));
        }
        // 附录
        if (appendixIdx != -1) {
            literature.setAppendix(sectionList.get(appendixIdx));
        }
        // 引用
        if (referencesIdx != -1) {
            literature.setCitationList(sectionList.get(referencesIdx));
        }

        //正文处理
        // 论文正文搜索起止范围
        int startInx = 0, endInx = sectionList.size() - 1;
        // TODO:如果顺序不一定是这样就需要封装成List排序取开头或结尾
        // 靠前范围
        if (introductionIdx != -1) {
            startInx = introductionIdx + 1;
        } else if (keywordsIdx != -1) {
            startInx = keywordsIdx + 1;
        } else if (abstractIdx != -1) {
            startInx = abstractIdx + 1;
        }
        //靠后范围
        if (appendixIdx != -1) {
            endInx = appendixIdx - 1;
        } else if (referencesIdx != -1) {
            endInx = referencesIdx - 1;
        }

        for (int i = startInx; i <= endInx; i++) {
            Section section = sectionList.get(i);
            if (section.getCaption() == null || !removeSectionCaptionNameSet.contains(section.getCaption().getTextContent())) {
                literature.getMainBody().add(section);
            }
        }
        return literature;
    }

    /**
     * 提取出 floatSection 即表格Section
     *
     * @param patternList
     * @return
     */
    private static List<Section> selectFloatSection(List<TextPattern> patternList) {
        List<Section> floatSection = new ArrayList<>();
        List<TextPattern> shouldDeleteSection = new ArrayList<>();
        for (TextPattern textPattern : patternList) {
            if (textPattern.getTextLabel().getLabelType().equals(TextLabelTypeEnum.VICE_MAIN_BODY)) {
                floatSection.add(new Section(null, textPattern, null));
                shouldDeleteSection.add(textPattern);
            }
        }
        patternList.removeAll(shouldDeleteSection);
        return floatSection;
    }


    /**
     * patternList生成SectionList
     */
    public static List<Section> patternListToSectionList(List<TextPattern> patternList) {
        List<Section> sections = new ArrayList<>();
        List<Integer> allMainBodyIdx = getAllMainBodyIdx(patternList);
        int j = 0;
        for (int i = 0; i < patternList.size() && j < allMainBodyIdx.size(); i++) {
            int currentMainBodyIndex = allMainBodyIdx.get(j);
            int dex = currentMainBodyIndex - i;
            Section section = new Section();
            section.setContent(patternList.get(allMainBodyIdx.get(j)));
            if (dex >= 1) {
                section.setCaption(patternList.get(allMainBodyIdx.get(j) - 1));
            }
            if (dex >= 2) {
                section.setOutCaption(patternList.get(allMainBodyIdx.get(j) - 2));
            }
            sections.add(section);
            i = allMainBodyIdx.get(j);
            j++;
        }
        return sections;
    }

    /**
     * 找到所有的MainBody Idx
     */
    public static List<Integer> getAllMainBodyIdx(List<TextPattern> patternList) {
        List<Integer> mainBodyIdxList = new ArrayList<>();
        for (int i = 0; i < patternList.size(); i++) {
            if (patternList.get(i).getPatternType().equals(TextPatternTypeEnum.MAIN_BODY)) {
                mainBodyIdxList.add(i);
            }
        }
        return mainBodyIdxList;
    }

    /**
     * 把 patternList 里的 mainBody 类型合并起来
     *
     * @param patternList
     * @return
     */
    private static List<TextPattern> formatPatternList(List<TextPattern> patternList) {
        List<TextPattern> formattedPatternList = new ArrayList<>();
        TextPatternTypeEnum lastFormattedPatternType = TextPatternTypeEnum.UNKNOWN;
        for (TextPattern textPattern : patternList) {
            if (textPattern.getPatternType().equals(TextPatternTypeEnum.MAIN_BODY)
                    && lastFormattedPatternType.equals(TextPatternTypeEnum.MAIN_BODY)) {
                TextPattern lastFormattedPattern = formattedPatternList.get(formattedPatternList.size() - 1);
                lastFormattedPattern.getTextLabel().getText().setContent(lastFormattedPattern.getTextContent() +
                        " " + textPattern.getTextContent());
            } else {
                formattedPatternList.add(textPattern);
                lastFormattedPatternType = textPattern.getPatternType();
            }
        }
        return formattedPatternList;
    }


    /**
     * 从patternList中 找到某Caption所在位置
     */
    public static int findFeatureCaptionIdx(List<Section> sectionList, String feature) {
        if (CollectionUtils.isEmpty(sectionList)) {
            System.out.println(feature + " Caption location is null!");
            return -1;
        }
        String[] split = feature.split("\\|");
        for (int i = 0; i < sectionList.size(); i++) {
            Section section = sectionList.get(i);
            TextPattern caption = section.getCaption();
            for (String pattern : split) {
                if (caption != null && MatchUtils.matchKeyWordForPattern(caption.getTextContent(), pattern)) {
                    return i;
                }
            }
        }
        return -1;
    }


    /************************4. literatureToTxt **********************************************************/

    /**
     * Literature转化输出为txt
     */
    public static void literature2Txt(Literature literature, String txtLib, String txtFileName) {
        // 没有文件创建文件~
        FileUtil.createFile(txtLib, txtFileName);
        String outputLocation = txtLib + "/" + txtFileName;
        // 只要摘要和正文+表格附加在最后
        if (literature != null) {
            if (literature.getTitle() != null && StringUtils.isNotBlank(literature.getTitle())) {
                List<String> title = textProcessor(literature.getTitle());
                for (String s : title) {
                    appendToTxt(outputLocation, s);
                }
            }

            Section summary = literature.getSummary();
            if (summary != null) {
                appendToTxt(outputLocation, "==Abstract==");
                List<String> summaryText = textProcessor(summary.getContent().getTextContent());
                summaryText.remove("All rights reserved");
                // 去掉 ：© 2011 Elsevier Ltd. 和 All rights reserved
                String s1 = summaryText.get(summaryText.size() - 1);
                String replaceAll = s1.replaceAll("\\©(.*)", "");
                summaryText.set(summaryText.size() - 1, replaceAll);
                for (String s : summaryText) {
                    appendToTxt(outputLocation, s);
                }
            }

            //ToDo : 暂时开放Introduction
            Section introduction = literature.getIntroduction();
            if (introduction != null) {
                appendToTxt(outputLocation, "==Introduction==");
                List<String> introductionText = textProcessor(introduction.getContent().getTextContent());
                for (String s : introductionText) {
                    appendToTxt(outputLocation, s);
                }
            }


            List<Section> mainBody = literature.getMainBody();
            if (CollectionUtils.isNotEmpty(mainBody)) {
                appendToTxt(outputLocation, "==MainBody==");
                for (Section section : mainBody) {
                    List<String> mainBodyText = textProcessor(section.getContent().getTextContent());
                    if (section.getCaption() != null) {
                        mainBodyText.add(0, "Caption: " + section.getCaption().getTextContent());
                    }
                    if (section.getOutCaption() != null) {
                        mainBodyText.add(0, "OutCaption: " + section.getOutCaption().getTextContent());
                    }
                    for (String s : mainBodyText) {
                        appendToTxt(outputLocation, s);
                    }
                }
            }

            List<Section> floatSection = literature.getFloatSectionList();
            if (CollectionUtils.isNotEmpty(floatSection)) {
                appendToTxt(outputLocation, "==floatSection==");
                for (Section section : floatSection) {
                    List<String> floatSectionText = new ArrayList<>();
                    if (section.getContent() != null) {
                        floatSectionText = textProcessor(section.getContent().getTextContent());
                    }
                    if (section.getCaption() != null) {
                        floatSectionText.add(0, section.getCaption().getTextContent());
                    }
                    for (String s : floatSectionText) {
                        appendToTxt(outputLocation, s);
                    }
                }
            }
        }
    }


    /**
     * 内容追加写入txt
     * 文件不存在会自动创建，但文件夹得存在，不然抛npe
     *
     * @param file
     * @param content
     */
    public static void appendToTxt(String file, String content) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(content + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Caption内容追加写入txt
     *
     * @param outputLocation
     * @param literature
     */
    public static void appendCaptionTextToTxt(String outputLocation, Literature literature) {
        // title
        appendToTxt(outputLocation, literature.getTitle());
        // Section Caption
        List<Section> mainBody = literature.getMainBody();
        for (Section section : mainBody) {
            if (section != null) {
                if (section.getOutCaption() != null) {
                    appendToTxt(outputLocation, section.getOutCaption().getTextContent());
                }
                if (section.getCaption() != null) {
                    appendToTxt(outputLocation, section.getCaption().getTextContent());
                }
            }
        }
    }

    /**
     * 所有内容追加写入txt
     *
     * @param outputLocation
     * @param literature
     */
    public static void appendContentTextToTxt(String outputLocation, Literature literature) {
        // title
        appendToTxt(outputLocation, literature.getTitle());
        // Section Caption
        List<Section> mainBody = literature.getMainBody();
        for (Section section : mainBody) {
            if (section != null) {
                if (section.getOutCaption() != null) {
                    appendToTxt(outputLocation, section.getOutCaption().getTextContent());
                }
                if (section.getCaption() != null) {
                    appendToTxt(outputLocation, section.getCaption().getTextContent());
                }
            }
        }
    }


    /**
     * 文本处理器--文本质量增强
     * Note:Stable Rule
     */
    public static List<String> textProcessor(String text) {
        // 0.删除乱码
        String str = StatisticsUtils.convertSpecSpaceOfAscii(text);

        // 1.过滤掉引用  规则:若(<>)被()|[]括起来，则剔除掉 .*?最短匹配而非贪婪匹配
        String deletePattern = "\\(.*?\\(\\<\\>\\).*?\\)";
        List<String> brackets = RegexUtils.matchAllRegex(str, deletePattern);
        for (String bracket : brackets) {
            str = str.replace(bracket, "");
        }

        // 2."  "->" "; " )"替换成")"
        String textAfterReplace = str.replace("   ", " ").replace("  ", " ").replace(" )", ")").replace(" ,", ",").replace("- ", "");

        // 3.处理barPattern,带"-"字符串
        String textAfterBarProcess = processWordContainsBar(textAfterReplace);

        // 4.分割成句子 换行
        String[] split = textAfterBarProcess.split("\\.");

        // 5.组装句子
        List<String> sentences = sentenceAssemble(split);

        return sentences;
    }

    /**
     * 组装成完整句子
     * 因为按照"."分割出来的结果，不是一个个句子，还可能是一些特殊case，如：
     * Fig.1
     * et al. 2016
     * 0.05
     * v.3.10.1
     * Elsevier B.V. All rights reserved.
     * 这些case里面的"."不该被打断，需要重新拼接起来。是否拼接的判断依据就是sentence的第一个字符是否大写。
     * 如果大写，则判定为是一句话的开头，不做处理；若不大写则判定为是被"误分割"，需要重新拼接到上一句末尾！
     */
    public static List<String> sentenceAssemble(String[] strings) {
        List<String> sentence = new ArrayList<>(strings.length);
        for (String str : strings) {
            if (str != null && str.trim().length() > 0) {
                if ((Character.isUpperCase(str.trim().charAt(0)) && str.length() > 2) || sentence.size() == 0) {
                    sentence.add(str.trim());
                } else {
                    String s = sentence.get(sentence.size() - 1);
                    String appendedString = s + "." + str.trim();
                    sentence.set(sentence.size() - 1, appendedString);
                }
            }
        }
        return sentence;
    }

    /**
     * 处理带"-"的字符，调用EnWordCheckers外部包
     *
     * @param input
     * @return
     */
    public static String processWordContainsBar(String input) {
        List<String> barPatterns = RegexUtils.getBarPatterns(input);
        if (CollectionUtils.isNotEmpty(barPatterns)) {
            for (String barPattern : barPatterns) {
                String correct = EnWordCheckers.correct(barPattern);
                input = input.replaceAll(barPattern, correct);
            }
        }
        return input;
    }

}
