package com.micerlabs.LitStraw.Domain;

import lombok.Data;

@Data
public class TextType {

    // 字体大小
    private float font;
    // 字体样式
    private String fontName;
    // 字体 weight
    private double weight;
    // 字体倾斜 true为倾斜
    private boolean isItalic = false;

    public TextType(float font, String fontName, double weight, boolean isItalic) {
        this.font = font;
        this.fontName = fontName;
        this.weight = weight;
        this.isItalic = isItalic;
    }
    public TextType(){}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextType)) return false;

        TextType textType = (TextType) o;

        if (Float.compare(textType.getFont(), getFont()) != 0) return false;
        if (Double.compare(textType.getWeight(), getWeight()) != 0) return false;
        if (isItalic() != textType.isItalic()) return false;
        return getFontName() != null ? getFontName().equals(textType.getFontName()) : textType.getFontName() == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (getFont() != +0.0f ? Float.floatToIntBits(getFont()) : 0);
        result = 31 * result + (getFontName() != null ? getFontName().hashCode() : 0);
        temp = Double.doubleToLongBits(getWeight());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (isItalic() ? 1 : 0);
        return result;
    }
}
