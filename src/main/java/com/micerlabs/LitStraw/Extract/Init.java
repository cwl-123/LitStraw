package com.micerlabs.LitStraw.Extract;

import com.micerlabs.LitStraw.Domain.Context;
import com.micerlabs.LitStraw.Domain.StatisForPara;
import com.micerlabs.LitStraw.Domain.Text;
import com.micerlabs.LitStraw.Utils.MatchUtils;
import com.micerlabs.LitStraw.Utils.RegexUtils;
import com.micerlabs.LitStraw.Utils.StatisticsUtils;
import com.spire.doc.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Init {

    public static Context initContext(String taskPath, String fileName) {
        Context context = new Context();
        // 设定路径名
        context.setPdfLib(taskPath + "/pdf");
        context.setWordLib(taskPath + "/word");
        context.setTxtLib(taskPath + "/txt");
        new File(context.getPdfLib()).mkdirs();
        new File(context.getWordLib()).mkdirs();
        new File(context.getTxtLib()).mkdirs();
        // 读取文件名
        context.setImportFileName(RegexUtils.getPdfName(fileName));
        context.setImportPdfFile(context.getPdfLib() + "/" + context.getImportFileName() + context.getPdfSuffix());
        context.setImportDocxFile(context.getWordLib() + "/" + context.getImportFileName() + context.getWordSuffix());
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
     * 初步统计doc信息
     *
     * @param context
     * @return
     */
    public static List<StatisForPara> preAnalyseForDoc(Context context) {
        List<StatisForPara> statisForParas = new ArrayList<>();
        try {
            statisForParas = StatisticsUtils.statisticsForParas(new Document(context.getImportDocxFile()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        StatisticsUtils.statisticsForFullDocx(statisForParas, context);
        return statisForParas;
    }

    /**
     * 初步统计得到标题和字体信息
     *
     * @param textList
     * @param context
     */
    public static void statis(List<Text> textList, Context context) {
        // 得到标题
        for (Text text : textList) {
            if (context.getTitleTextType() != null && text.getTextType().equals(context.getTitleTextType()) && text.getText().length() > 30) {
                context.setTitle(text.getText());
                break;
            }
        }

        // 得到次一级正文字体
        for (Text text : textList) {
            // ToDo：暂时设置成500，最好和文章长度百分比匹配
            if (text.getText().length() > 500 && !context.getMainBodyTextType().equals(text.getTextType())
                    && !context.getReferenceTextType().equals(text.getTextType())) {
                context.getSecondaryBodyTextTypeSet().add(text.getTextType());
            }
        }

        for (int i = 0; i < textList.size(); i++) {
            if (MatchUtils.matchKeyWordForPattern(textList.get(i).getText(), "Introduction")) {
                context.setIntroductionIndex(i);
            }
            if (MatchUtils.matchKeyWordForPattern(textList.get(i).getText(), "abstract")) {
                context.setAbstractIndex(i);
            }
            // 得到Reference Caption TextType,当没书签时根据CaptionTextTypeSet来判断是否为Caption
            if (MatchUtils.matchKeyWordForPattern(textList.get(i).getText(), "Reference")) {
                context.getCaptionTextTypeSet().add(textList.get(i).getTextType());
            }
        }
    }
}
