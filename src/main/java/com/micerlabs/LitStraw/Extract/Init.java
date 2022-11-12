package com.micerlabs.LitStraw.Extract;

import com.micerlabs.LitStraw.Domain.*;
import com.micerlabs.LitStraw.Utils.RegexUtils;
import com.micerlabs.LitStraw.Utils.StatisticsUtils;

import java.io.File;
import java.util.*;

public class Init {

    public static Context initContext(String taskPath, String fileName) {
        Context context = new Context();
        // 设定路径名
        context.setPdfLib(taskPath + "/pdf");
        context.setTxtLib(taskPath + "/txt");
        context.setJsonLib(taskPath + "/json");
        new File(context.getPdfLib()).mkdirs();
        new File(context.getJsonLib()).mkdirs();
        new File(context.getTxtLib()).mkdirs();
        // 读取文件名
        context.setImportFileName(RegexUtils.getPdfName(fileName));
        context.setImportPdfFile(context.getPdfLib() + "/" + context.getImportFileName() + context.getPdfSuffix());
        context.setImportJsonFile(context.getJsonLib() + "/structuredData.json");
        // 读取pdf的书签
        Set<String> bookmarkSet = new HashSet<>();
        try {
            bookmarkSet = StatisticsUtils.getBookmarkSet(context.getImportPdfFile());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        context.setBookMarkSet(bookmarkSet);
        context.setFormatBookMarkSet(RegexUtils.formatStringSet(bookmarkSet));
        return context;
    }

    /**
     * 初步统计字体信息
     *
     * @param textList
     * @param context
     */
    public static void statis(List<Text> textList, Context context) {
        for (Text text : textList) {
            if (context.getTextTypeMapForPaper().containsKey(text.getTextType())) {
                context.getTextTypeMapForPaper().put(text.getTextType(), context.getTextTypeMapForPaper().get(text.getTextType())
                        + text.getContent().length());
            } else {
                context.getTextTypeMapForPaper().put(text.getTextType(), text.getContent().length());
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

        Text lastText = textList.get(textList.size() - 1);
        if (lastText.getPath().matches("//Document/L(.*)/LBody")) {
            context.setReferenceTextType(lastText.getTextType());
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

        // 得到次一级正文字体
        for (Text text : textList) {
            // ToDo：暂时设置成500，最好和文章长度百分比匹配
            if (text.getContent().length() > 500 && !context.getMainBodyTextType().equals(text.getTextType())
                    && !context.getReferenceTextType().equals(text.getTextType())) {
                context.getSecondaryBodyTextTypeSet().add(text.getTextType());
            }
        }
    }
}
