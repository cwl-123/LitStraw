package com.micerlabs.LitStraw.Domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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