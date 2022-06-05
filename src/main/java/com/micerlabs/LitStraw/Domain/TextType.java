package com.micerlabs.LitStraw.Domain;

import lombok.Data;

@Data
public class TextType {

    // 字体大小
    private float font;
    // 字体样式
    private String fontType;
    // 字体颜色 Color Space Type
    private int color;
    // 字体加粗 true为加粗
    private boolean isBold = false;
    // 字体倾斜 true为倾斜
    private boolean isItalic = false;

    public TextType(float font, String fontType, int color, boolean isBold, boolean isItalic) {
        this.font = font;
        this.fontType = fontType;
        this.color = color;
        this.isBold = isBold;
        this.isItalic = isItalic;
    }
    public TextType(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextType textType = (TextType) o;

        if (Float.compare(textType.getFont(), getFont()) != 0) return false;
        if (getColor() != textType.getColor()) return false;
        if (isBold() != textType.isBold()) return false;
        if (isItalic() != textType.isItalic()) return false;
        return getFontType().equals(textType.getFontType());
    }

    @Override
    public int hashCode() {
        int result = (getFont() != +0.0f ? Float.floatToIntBits(getFont()) : 0);
        result = 31 * result + getFontType().hashCode();
        result = 31 * result + getColor();
        result = 31 * result + (isBold() ? 1 : 0);
        result = 31 * result + (isItalic() ? 1 : 0);
        return result;
    }
}
