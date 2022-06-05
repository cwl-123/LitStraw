package com.micerlabs.LitStraw.Domain;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TextLabelTypeEnum {

    UNKNOWN(-1, "Unknown", "未知LabelType"),
    REFERENCE(0, "reference", "引用字体"),
    MAIN_BODY(1, "main_body", "正文字体"),
    VICE_MAIN_BODY(2, "vice_main_body", "副正文字体，如图表底下的描述文字"),
    CAPTION(3, "caption", "内容块标题字体"),
    VICE_CAPTION(4, "vice_caption", "副内容块标题字体，如图表Table 1"),
    OTHER(5, "Other", "杂项字体，一般无用");

    /**
     * 类型
     */
    private int type;
    /**
     * 类型名
     */
    private String typeName;
    /**
     * 描述
     */
    private String desc;

}
