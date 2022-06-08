package com.micerlabs.LitStraw.Domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum TextPatternTypeEnum {
    UNKNOWN(-1,"Unknown","未知Pattern"),
    MAIN_BODY(0,"MainBody","正文内容"),
    CAPTION(1,"Caption","内容块标题字体"),
    EQUATION(2,"Equation","正文公式"),
    OTHER(3,"Other","杂项字体，一般无用");

    /** 类型 */
    private int type;
    /** 类型名 */
    private String typeName;
    /** 描述 */
    private String desc;
}
