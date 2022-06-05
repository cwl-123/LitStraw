package com.micerlabs.LitStraw.Domain;

import lombok.Data;

@Data
public class TextLabel {

    private String text;
    private TextType textType;
    private TextLabelTypeEnum labelType;

    public TextLabel(String text, TextType textType, TextLabelTypeEnum textLabelType) {
        this.text = text;
        this.textType = textType;
        this.labelType = textLabelType;
    }

    @Override
    public String toString() {
        return "Label{" +
                "labelType=" + labelType +
                ", textType=" + textType +
                ", text='" + text + '\'' +
                '}';
    }
}