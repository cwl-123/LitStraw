package com.micerlabs.LitStraw.Domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextPattern {
    private TextPatternTypeEnum patternType;
    private String text;
    private TextType textType;
    private TextLabelTypeEnum labelType;

    @Override
    public String toString() {
        return "Pattern{" +
                "patternType=" + patternType +
                ", labelType=" + labelType +
                ", text='" + text + '\'' +
                ", textType=" + textType +
                '}';
    }


}
