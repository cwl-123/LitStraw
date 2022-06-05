package com.micerlabs.LitStraw.Domain;

import lombok.Data;

@Data
public class TextTypeNum implements Comparable<TextTypeNum> {
    private TextType textType;
    private int num;

    public TextTypeNum(TextType textType, int num) {
        this.textType = textType;
        this.num = num;
    }

    @Override
    public int compareTo(TextTypeNum o) {
        return o.num - this.num;
    }

}
