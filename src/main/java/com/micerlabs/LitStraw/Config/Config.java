package com.micerlabs.LitStraw.Config;


import lombok.Data;

/**
 * 用户配置文件
 */
@Data
public class Config {
    // 标题页在pdf的第几页
//    private int firstPageIndex = 1;
    // 正文字体查找因子
    double mainBodyFactor = 0.80;

    /**
     * 0. 初始化阶段
     */

    /**
     * 1. doc转List<Text>阶段
     */

    /**
     * 2. Text转LabeledText阶段
     */
    // 限制与KeyWord匹配的pattern的最大长度，减少判断次数
    int keywordMatchMaxLen = 20;

    /**
     * 3. LabeledText转TextPattern阶段
     */
    //Reference标签转MainBody  长度超过250，或位置超过0.8
    int referenceToMainBodyLenLimit = 250;
    double referenceToMainBodyposLimit = 0.8;

    /**
     * 4. Pattern过滤阶段
     */

    /**
     * 5. PatternList转Literature阶段
     */

    /**
     * 6. 转txt文本处理阶段
     */

    /**
     * 7. 其他
     */


}
