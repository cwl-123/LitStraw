package com.micerlabs.LitStraw.Domain;

import lombok.Data;

@Data
public class TextLabel {

    private Text text;
    private TextLabelTypeEnum labelType;

    public TextLabel(Text text, TextLabelTypeEnum textLabelType) {
        this.text = text;
        this.labelType = textLabelType;
    }

    @Override
    public String toString() {
        return "Label{" +
                "labelType=" + labelType +
                ", text='" + text + '\'' +
                '}';
    }
}