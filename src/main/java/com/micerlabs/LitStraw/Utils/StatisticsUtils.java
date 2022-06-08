package com.micerlabs.LitStraw.Utils;

import com.micerlabs.LitStraw.Domain.*;
import com.micerlabs.LitStraw.Domain.StatisForPara;
import com.spire.doc.Document;
import com.spire.doc.Section;
import com.spire.doc.documents.Paragraph;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.bookmarks.PdfBookmark;
import com.spire.pdf.bookmarks.PdfBookmarkCollection;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;

import java.util.*;

public class StatisticsUtils {


    /**
     * @param doc
     * @return
     */
    public static List<StatisForPara> statisticsForParas(Document doc) {
        List<StatisForPara> statisForParas = new ArrayList<>();
        for (int i = 0; i < doc.getSections().getCount(); i++) {
            Section section = doc.getSections().get(i);
            for (int j = 0; j < section.getParagraphs().getCount(); j++) {
                Paragraph paragraph = section.getParagraphs().get(j);
                if (StringUtils.isNotBlank(paragraph.getText())) {
                    statisForParas.add(StatisticsUtils.statisticsForSinglePara(paragraph));
                }
            }
        }
        return statisForParas;
    }


    /**
     * 统计一个paragraph里的基本信息 Paragraph of Spire
     */
    public static StatisForPara statisticsForSinglePara(Paragraph paragraph) {
        StatisForPara statis = new StatisForPara();
        statis.setPara(paragraph);
//        if (paragraph != null & paragraph.getChildObjects().getCount() != 0) {
//            DocumentObjectCollection childObjects = paragraph.getChildObjects();
//            statis.setRunsNum(childObjects.getCount());
//            int runsNumWithoutEmpty = 0;
//            for (int i = 0; i < childObjects.getCount(); i++) {
//                DocumentObject documentObject = childObjects.get(i);
//                if (documentObject instanceof TextRange) {
//                    TextRange run = (TextRange) documentObject;
//                    if (StringUtils.isNotBlank(run.getText())) {
//                        runsNumWithoutEmpty++;
//                        CharacterFormat characterFormat = run.getCharacterFormat();
//                        TextType textType = new TextType(characterFormat.getFontSize(), characterFormat.getFontName(), characterFormat.getTextColor().getColorSpace().getType(), characterFormat.getBold(), characterFormat.getItalic());
//                        if (statis.getTextTypeMap().containsKey(textType)) {
//                            statis.getTextTypeMap().put(textType, statis.getTextTypeMap().get(textType) + run.getText().length());
//                        } else {
//                            statis.getTextTypeMap().put(textType, run.getText().length());
//                        }
//                    }
//                }
//            }
//            statis.setRunsNumWithoutEmpty(runsNumWithoutEmpty);
//            statis.setParaText(paragraph.getText());
//            statis.setParaTextNum(paragraph.getText().length());
//
//            if (MapUtils.isNotEmpty(statis.getTextTypeMap())) {
//                int maxNum = 0, totalNum = 0;
//                TextType textType = new TextType();
//                for (Map.Entry<TextType, Integer> entry : statis.getTextTypeMap().entrySet()) {
//                    totalNum = totalNum + entry.getValue();
//                    if (entry.getValue() > maxNum) {
//                        textType = entry.getKey();
//                        maxNum = entry.getValue();
//                    }
//                }
//                statis.setMostFrequentlyTextTypeNum(new Pair<>(textType, maxNum));
//                statis.setMostFrequentlyTextType(new Pair<>(textType, (double) maxNum / totalNum));
//            }
//        }
        return statis;
    }

    /**
     * 统计一个FullDocx里的基本信息
     */
    public static void statisticsForFullDocx(List<StatisForPara> statisForParas, Context context) {
        if (CollectionUtils.isEmpty(statisForParas)) {
            return;
        }
        // 统计docx里的字体类型及比例
        for (StatisForPara statisForPara : statisForParas) {
            List<String> barPatterns = RegexUtils.getBarPatterns(statisForPara.getParaText());
            context.getBarPatternList().addAll(barPatterns);
            for (Map.Entry<TextType, Integer> entry : statisForPara.getTextTypeMap().entrySet()) {
                if (context.getTextTypeMapForPaper().containsKey(entry.getKey())) {
                    context.getTextTypeMapForPaper().put(entry.getKey(), context.getTextTypeMapForPaper().get(entry.getKey()) + entry.getValue());
                } else {
                    context.getTextTypeMapForPaper().put(entry.getKey(), entry.getValue());
                }
            }
        }

        // 统计主要字体
        List<TextTypeNum> textTypeNumList = new ArrayList<>();
        int totalNum = 0;
        for (Map.Entry<TextType, Integer> entry : context.getTextTypeMapForPaper().entrySet()) {
            TextTypeNum textTypeNum = new TextTypeNum(entry.getKey(), entry.getValue());
            textTypeNumList.add(textTypeNum);
            totalNum += entry.getValue();
        }
        Collections.sort(textTypeNumList);
        int addedNum = 0;
        for (int i = 0; i < textTypeNumList.size(); i++) {
            TextTypeNum textTypeNum = textTypeNumList.get(i);
            if (addedNum <= context.getConfig().getMainBodyFactor() * totalNum) {
                addedNum += textTypeNum.getNum();
                // 主要字体 出现比例最大的几个
                context.getMainTextTypeSet().add(textTypeNum.getTextType());
            }
        }
        // 设定正文字体和引文字体
        Pair<TextType, Double> lastParaMostFrequentlyTextType = statisForParas.get(statisForParas.size() - 1).getMostFrequentlyTextType();
        if (lastParaMostFrequentlyTextType.getValue() > 0.9 && context.getMainTextTypeSet().contains(lastParaMostFrequentlyTextType.getKey())) {
            // 最后一个自然段一般是引文，最频繁字体占比超过90% && 该字体是主要字体 => 则该字体是引文字体
            context.setReferenceTextType(lastParaMostFrequentlyTextType.getKey());
            // textTypeNumList 里按占比从前往后排，第一个非引文字体的字体就是正文字体
            for (TextTypeNum textTypeNum : textTypeNumList) {
                if (!textTypeNum.getTextType().equals(context.getReferenceTextType())) {
                    context.setMainBodyTextType(textTypeNum.getTextType());
                    break;
                }
            }
        } else {
            // 简单处理，最多的字体是正文，第二多的是reference字体
            context.setMainBodyTextType(textTypeNumList.get(0).getTextType());
            context.setReferenceTextType(textTypeNumList.get(1).getTextType());
        }

        // TODO:给个中间字体大小辅助判断？
        // 标题字体 字数超过40的font最大的TextType
        TextType titleTextType = new TextType();
        for (Map.Entry<TextType, Integer> entry : context.getTextTypeMapForPaper().entrySet()) {
            // ToDo:寻找最合适的字数限制 目前是40，支持配置&&中英文应该不一样
            if (entry.getValue() > 40 && entry.getKey().getFont() > titleTextType.getFont()) {
                titleTextType = entry.getKey();
            }
        }

        // 统计barPatterns,直接保留到context里
        for (String barPattern : context.getBarPatternList()) {
            Map<String, Integer> barPatternCountMap = context.getBarPatternCountMap();
            if (barPatternCountMap.containsKey(barPattern)) {
                barPatternCountMap.put(barPattern, barPatternCountMap.get(barPattern) + 1);
            } else {
                barPatternCountMap.put(barPattern, 1);
            }
        }
    }

    /**
     * 获取pdf书签集合
     */
    public static Set<String> getBookmarkSet(String pdfFilePath) {
        //加载包含书签的PDF文档
        PdfDocument pdf = new PdfDocument();
        pdf.loadFromFile(pdfFilePath);

        //获取书签集合
        Set<String> bookMarkSet = new HashSet<>();
        getBookmarkTitle(pdf.getBookmarks(), bookMarkSet);
        return bookMarkSet;
    }

    /**
     * 定义方法获取书签标题
     */
    public static void getBookmarkTitle(PdfBookmarkCollection bookmarkCollection, Set<String> bookMarkSet) {
        if (bookmarkCollection.getCount() > 0) {
            for (int i = 0; i < bookmarkCollection.getCount(); i++) {
                PdfBookmark parentBookmark = bookmarkCollection.get(i);
                String title = parentBookmark.getTitle();
                bookMarkSet.add(convertSpecSpaceOfAscii(title).trim());
                //递归文档多级书签
                getBookmarkTitle(parentBookmark, bookMarkSet);
            }
        }
    }

    /**
     * 字符串转换ASCII编码 处理特殊字符
     */
    public static String convertSpecSpaceOfAscii(String originalString) {
        List<Integer> asciiCode = string2Ascii(originalString);
        List<Integer> filteredAsciiCode =  new ArrayList<>();
        // 剔除掉乱码
        for (Integer integer : asciiCode) {
            if (integer < 50000) {
                filteredAsciiCode.add(integer);
            }
        }
        return ascii2String(filteredAsciiCode);
    }


    /**
     * 字符串转换为Ascii
     *
     * @param value
     * @return
     */
    public static List<Integer> string2Ascii(String value) {
        List<Integer> ints = new ArrayList<>();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            ints.add((int) chars[i]);
        }
        return ints;
    }

    /**
     * Ascii转换为字符串
     *
     * @return
     */
    public static String ascii2String(List<Integer> asciiCode) {
        StringBuffer sbu = new StringBuffer();
        for (Integer integer:asciiCode){
            sbu.append((char) integer.intValue());
        }
        return sbu.toString();
    }
}

