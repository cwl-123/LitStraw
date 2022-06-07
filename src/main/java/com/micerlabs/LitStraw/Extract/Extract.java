package com.micerlabs.LitStraw.Extract;

import com.micerlabs.LitStraw.Config.Config;
import com.micerlabs.LitStraw.Domain.*;
import com.micerlabs.LitStraw.Domain.StatisForPara;
import com.micerlabs.LitStraw.Utils.MatchUtils;
import com.micerlabs.LitStraw.Utils.RegexUtils;
import com.micerlabs.LitStraw.Utils.StatisticsUtils;
import com.google.common.collect.Lists;
import com.spire.doc.DocumentObject;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.fields.TextRange;
import com.spire.doc.formatting.CharacterFormat;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Extract {

    /************************1. 提取TextList **********************************************************/
//    /**
//     * statisForParas 转 List<Text>
//     *
//     * @return
//     */
//    public static List<Text> extractTextOfSpire(List<StatisForPara> statisForParas, Context context) {
//        if (CollectionUtils.isEmpty(statisForParas)) {
//            return Collections.emptyList();
//        }
//        List<Text> literature = new ArrayList<>();
//        for (StatisForPara statisForPara : statisForParas) {
//            List<Text> textList = paragraphToTextListOfSpire(statisForPara);
//            literature.addAll(textList);
//        }
//        return convertToFixAccuracy(literature);
//    }

    /**
     *
     * @param jsonPath eg: "data/2/structuredData.json"
     * @return
     * @throws IOException
     */
    public static List<Text> extractTextOfAdobe(String jsonPath) {
        File file = new File(jsonPath);
        String content = null;
        try {
            content = FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jsObj = new JSONObject(content);
        JSONArray elements = jsObj.getJSONArray("elements");
        List<Text> textList = new ArrayList<>();
        for (Object element : elements) {
            JSONObject e = (JSONObject) element;
            double textSize = e.optDouble("TextSize", 0.0D);
            if (textSize != 0.0D) {
                System.out.println(e.getString("Path") + textSize);
                textList.add(JsonAnalysis.convert2Text(e));
            }
        }
        return textList;
    }


    /**
     * 段落转化成List<Text>
     *
     * @param statisForPara
     * @return
     */
//    private static List<Text> paragraphToTextListOfSpire(StatisForPara statisForPara) {
//        if (statisForPara == null || statisForPara.getPara() == null) {
//            return Collections.emptyList();
//        }
//        // 如果最频繁textType占比>0.95，那直接用getParaText()拼上最频繁TextType,来避免特殊空格" "
//        // Note: Fixing-Trick Rule 字体归化逻辑 这样应该可以大大减少错误&&减少执行次数 后期可以看下0.95阈值是否合理
//        if (statisForPara.getMostFrequentlyTextType().getValue() > 0.95) {
//            Text text = new Text(statisForPara.getParaText(), statisForPara.getMostFrequentlyTextType().getKey());
//            return Lists.newArrayList(text);
//        }
//        Paragraph para = statisForPara.getPara();
//        List<Text> textList = new ArrayList<>();
//        TextType lastTextType = new TextType();
//        for (int i = 0; i < para.getChildObjects().getCount(); i++) {
//            DocumentObject documentObject = para.getChildObjects().get(i);
//            if (documentObject instanceof TextRange) {
//                TextRange run = (TextRange) documentObject;
//                if (StringUtils.isNotBlank(run.getText())) {
//                    CharacterFormat characterFormat = run.getCharacterFormat();
//                    TextType currentTextType = new TextType(characterFormat.getFontSize(), characterFormat.getFontName(), characterFormat.getTextColor().getColorSpace().getType(), characterFormat.getBold(), characterFormat.getItalic());
//                    if (lastTextType.equals(currentTextType)) {
//                        Text text = textList.get(textList.size() - 1);
//                        text.setContent(text.getContent() + run.getText());
//                    } else {
//                        lastTextType = currentTextType;
//                        textList.add(new Text(run.getText(), currentTextType));
//                    }
//                } else if (textList.size() >= 1) {
//                    // 如果是空格，则补上~
//                    Text text = textList.get(textList.size() - 1);
//                    text.setContent(text.getContent() + " ");
//                }
//            }
//        }
//        return textList;
//    }

    /**
     * 为了修复pdf转word的精确度问题，写的一个转换TextType函数
     * Note:Fixing Rule 文本增强器
     *
     * @return
     */
    public static List<Text> convertToFixAccuracy(List<Text> literature) {
        // Note: Fixing-Stable Rule 字体融合 ,length<=5 && 全是英文字母[a-z,A-Z] && 前后格式一样，就它不一样，推测是pdf转word准确度问题，把正常单词打断了
        // 以后pdf转word文本质量高了，就可以删除这个逻辑
        // && text.getText().matches("[a-z]+") 暂时删除这个逻辑 有些单位也被分割，应该加上
        // 打断的单词，OCR识别错误的部分转换字体类型
        for (int i = 1; i < literature.size() - 1; i++) {
            Text text = literature.get(i);
            if (text.getContent().length() <= 5 && literature.get(i - 1).getTextType().equals(literature.get(i + 1).getTextType())) {
                literature.get(i - 1).setContent(literature.get(i - 1).getContent() + text.getContent() + literature.get(i + 1).getContent());
                literature.remove(i);
                literature.remove(i);
                i = i - 1;
            }
        }
        // 出去前转化下，把fi，fl等特殊字符处理好
        for (Text text : literature) {
            text.setContent(StatisticsUtils.convertSpecSpaceOfAscii(text.getContent()));
        }
        return literature;
    }


    /************************2. TextListToLabelList *****************************************************/


    /**
     * 第一次转化：List<Text> -> List<TextLabel>
     *
     * @param textList
     * @param context
     * @return
     */
    public static List<TextLabel> text2label(List<Text> textList, Context context) {
        if (CollectionUtils.isEmpty(textList)) {
            return Collections.emptyList();
        }
        List<TextLabel> textLabelList = new ArrayList<>();
        TextLabelTypeEnum lastLabelType = TextLabelTypeEnum.UNKNOWN;
        for (Text text : textList) {
            TextLabelTypeEnum currentLabelType = markTagForLabel(text, context);
//            // 合并同类项
//            if (lastLabelType.equals(currentLabelType) && (currentLabelType.equals(TextLabelTypeEnum.MAIN_BODY) || currentLabelType.equals(TextLabelTypeEnum.REFERENCE))) {
//                TextLabel textLabel = textLabelList.get(textLabelList.size() - 1);
//                textLabel.setText(textLabel.getText() + fillPoint(textLabel.getText(), text.getText()) + text.getText());
//            } else {
//                lastLabelType = currentLabelType;
//                textLabelList.add(new TextLabel(text.getText(), text.getTextType(), currentLabelType));
//            }
            textLabelList.add(new TextLabel(text.getContent(), text.getTextType(), currentLabelType));
        }
        convertLabelAfterMarkTag(textLabelList, context);
        return textLabelList;
    }

    /**
     * 根据TextType打LabelType标签
     * 第一次打标 TextType -> TextLabelType
     *
     * @return
     */
    private static TextLabelTypeEnum markTagForLabel(Text text, Context context) {

        // 1.优先判断是否是Caption
        if (CollectionUtils.isNotEmpty(context.getFormatBookMarkSet())) {
            // 1.1 有书签根据书签来判断
            // ToDO:这里有空考虑下性能：Set直接Contains当然性能最好，但是格式不对，还是得遍历Set来MatchPattern
            if (context.getFormatBookMarkSet().contains(RegexUtils.formatString(text.getContent()))) {
                return TextLabelTypeEnum.CAPTION;
            }
        } else {
            // 1.2 没书签根据CaptionTextTypeSet来判断，由reference关键字锚定
            if (CollectionUtils.isNotEmpty(context.getCaptionTextTypeSet())) {
                if (context.getCaptionTextTypeSet().contains(text.getTextType())) {
                    // ToDO：因为存在识别问题，容易有些许OCR识别错误的字体被当成Caption，如：de facto 所以需要对Caption长度做限制
                    // 没书签就不允许出现这种错别字..
                    if (text.getContent().length() <= 100) {
                        return TextLabelTypeEnum.CAPTION;
                    }
                }
            } else {
                // 1.3 没有CaptionTextTypeSet，用加粗判断
                if (text.getTextType().getFontName() != null && text.getTextType().isBold()) {
                    return TextLabelTypeEnum.CAPTION;
                }
            }
        }
        // 1.4 如果是CaptionKeyWordSet里的关键词，也直接被打上Caption标签 eg Abstract
        // 这里只要是partOf && IgnoreCase
        if (MatchUtils.matchKeyWordForCaption(context.getCaptionKeyWordsSet(), text.getContent())) {
            return TextLabelTypeEnum.CAPTION;
        }

        // 2. 其次，判断是否是表格标题,vice-caption
        // (?i)表示不区分大小写
        String pattern = "(?i)Fig.*|(?i)Tab.*";
        if (text.getContent().length() < 10 && text.getContent().matches(pattern)) {
            return TextLabelTypeEnum.VICE_CAPTION;
        }

        // 3. 再判断是否是MainBody 问：有可能Caption又被识别为MainBody吗？答：有可能，但是如果被识别为Caption，就提前返回了
        if (context.getMainBodyTextType() != null && context.getMainBodyTextType().equals(text.getTextType())) {
            return TextLabelTypeEnum.MAIN_BODY;
        }

        // 4. 判断是否是 Secondary-MainBody。
        if (!context.getSecondaryBodyTextTypeSet().isEmpty() && context.getSecondaryBodyTextTypeSet().contains(text.getTextType())) {
            return TextLabelTypeEnum.MAIN_BODY;
        }
        // 5. 最后判断是否是reference
        if (context.getReferenceTextType() != null && context.getReferenceTextType().equals(text.getTextType())) {
            return TextLabelTypeEnum.REFERENCE;
        }
        return TextLabelTypeEnum.OTHER;
    }

    /**
     * 第一轮打完标后的标签转换。
     */
    private static void convertLabelAfterMarkTag(List<TextLabel> textLabelList, Context context) {
        // 上一个/下一个 (i-1/i+1) 的TextLabelTypeEnum类型
        TextLabelTypeEnum lastTextLabelType = TextLabelTypeEnum.UNKNOWN;
        TextLabelTypeEnum nextTextLabelType = TextLabelTypeEnum.UNKNOWN;

        for (int i = 0; i < textLabelList.size(); i++) {
            if ((i + 1) < textLabelList.size()) {
                nextTextLabelType = textLabelList.get(i + 1).getLabelType();
            }
            TextLabel textLabel = textLabelList.get(i);

            if (CollectionUtils.isEmpty(context.getBookMarkSet())) {
                // 没书签情况下，收集Caption TextType信息
                if (textLabel.getLabelType().equals(TextLabelTypeEnum.CAPTION)) {
                    context.getCaptionTextTypeSet().add(textLabel.getTextType());
                }
            }

            // Vice-Caption下一个非MainBody的需要转成Vice-MainBody
            if (textLabel.getLabelType().equals(TextLabelTypeEnum.VICE_CAPTION)
                    && !nextTextLabelType.equals(TextLabelTypeEnum.MAIN_BODY)) {
                textLabelList.get(i + 1).setLabelType(TextLabelTypeEnum.VICE_MAIN_BODY);
            }

            // Note:Stable Rule 处理公式 MainBody->Other。MainBody上下都是Other&&太短，转成Other
            if (textLabel.getLabelType().equals(TextLabelTypeEnum.MAIN_BODY) && textLabel.getText().length() < 15
                    && nextTextLabelType.equals(TextLabelTypeEnum.OTHER) && lastTextLabelType.equals(TextLabelTypeEnum.OTHER)) {
                textLabelList.get(i).setLabelType(TextLabelTypeEnum.OTHER);
            }
            lastTextLabelType = textLabel.getLabelType();
        }

        // Note: Trick Rule Abstract后面只保留一个MainBody作为摘要，其他从Abstract到Introduction的东西都不要。All->Other
        if (context.getAbstractIndex() != 0 && context.getIntroductionIndex() != 0) {
            for (int i = context.getAbstractIndex() + 2; i < context.getIntroductionIndex(); i++) {
                textLabelList.get(i).setLabelType(TextLabelTypeEnum.OTHER);
            }
        }

        if (CollectionUtils.isEmpty(context.getBookMarkSet())) {
            for (TextLabel textLabel : textLabelList) {
                // Stable Rule : 没书签的情况下，Caption字体，需要把相同字体的 Other 转 Caption 或 MainBody
                if (textLabel.getLabelType().equals(TextLabelTypeEnum.OTHER) && context.getCaptionTextTypeSet().contains(textLabel.getTextType())) {
                    // Caption长度需要限制
                    if (textLabel.getText().length() >= 10 && textLabel.getText().length() <= 100) {
                        textLabel.setLabelType(TextLabelTypeEnum.CAPTION);
                    } else {
                        textLabel.setLabelType(TextLabelTypeEnum.MAIN_BODY);
                    }
                }
            }
        }

    }

    /**
     * 拼接同类型的字符串时，若上个句子没句号&&下个句子首字符大写，则补上一个句号作为分隔
     * Note:Stable Rule
     */
    public static String fillPoint(String firstStr, String lastStr) {
        if (firstStr.charAt(firstStr.length() - 1) != '.' && Character.isUpperCase(lastStr.charAt(0))) {
            return ". ";
        }
        return "";
    }


    /************************3. LabelListToPatternList *****************************************************/

    /**
     * 第二次转化：List<TextLabel> -> List<TextPattern>
     *
     * @return
     */
    public static List<TextPattern> label2pattern(List<TextLabel> textLabelList, Context context) {
        if (CollectionUtils.isEmpty(textLabelList)) {
            return Collections.emptyList();
        }
        /**
         * Stable Rule: 公式勘测 Equation Survey
         * 识别成Other
         */


        List<TextPattern> textPatternList = new ArrayList<>();
        for (int i = 0; i < textLabelList.size(); i++) {
            TextLabel textLabel = textLabelList.get(i);
            TextPatternTypeEnum patternType = markTagForPattern(textLabel, i / textLabelList.size());
            textPatternList.add(new TextPattern(patternType, textLabel.getText(), textLabel.getTextType(), textLabel.getLabelType()));
        }
        return textPatternList;
    }

    /**
     * 根据textLabel打PatternType标签
     * 第二次打标: TextLabel->TextPattern
     * // caption -> Caption
     * // mainBody -> MainBody
     * // vice-Caption -> Caption
     * // vice-MainBody -> MainBody
     * // Other -> Other
     * // Reference -> MainBody 或 Other
     */
    private static TextPatternTypeEnum markTagForPattern(TextLabel textLabel, double position) {
        if (textLabel.getLabelType().equals(TextLabelTypeEnum.CAPTION)) {
            return TextPatternTypeEnum.CAPTION;
        }
        if (textLabel.getLabelType().equals(TextLabelTypeEnum.MAIN_BODY)) {
            return TextPatternTypeEnum.MAIN_BODY;
        }
        if (textLabel.getLabelType().equals(TextLabelTypeEnum.VICE_CAPTION)) {
            return TextPatternTypeEnum.CAPTION;
        }
        if (textLabel.getLabelType().equals(TextLabelTypeEnum.VICE_MAIN_BODY)) {
            return TextPatternTypeEnum.MAIN_BODY;
        }
        if (textLabel.getLabelType().equals(TextLabelTypeEnum.OTHER)) {
            return TextPatternTypeEnum.OTHER;
        }
        if (textLabel.getLabelType().equals(TextLabelTypeEnum.REFERENCE)) {
            // Reference标签，若position靠前则无用，需要打Other标签后期删除;
            // 若position靠后则需要打MainBody标签后期保留
            // ToDo:0.8,250可配置成参数
            if (textLabel.getText().length() > new Config().getReferenceToMainBodyLenLimit() || position > new Config().getReferenceToMainBodyposLimit()) {
                return TextPatternTypeEnum.MAIN_BODY;
            }
            return TextPatternTypeEnum.OTHER;
        }
        return TextPatternTypeEnum.OTHER;
    }
}
