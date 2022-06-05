package com.micerlabs.LitStraw.Domain;

import com.spire.doc.documents.Paragraph;
import lombok.Data;
import org.apache.commons.math3.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * 段落统计信息
 */
@Data
public class StatisForPara {
    // runs数量
    private int runsNum;
    // 去空后runs数量
    private int runsNumWithoutEmpty;
    // 文本类型Map <TextType, String.length>
    private Map<TextType, Integer> textTypeMap = new HashMap<>();
    // 段落文字
    private String paraText;
    // 段落文字数量
    private int paraTextNum;
    // 段落里最多的TextType和它的长度
    private Pair<TextType, Integer> mostFrequentlyTextTypeNum;
    // 段落里最多的TextType和它的占比
    private Pair<TextType, Double> mostFrequentlyTextType;
    // 包含para元信息
    private Paragraph para;

}
