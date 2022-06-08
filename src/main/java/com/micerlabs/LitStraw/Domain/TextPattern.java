package com.micerlabs.LitStraw.Domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextPattern {
    private TextLabel textLabel;
    private TextPatternTypeEnum patternType;

    public String getTextContent() {
        if (textLabel != null && textLabel.getText() != null) {
            return textLabel.getText().getContent();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Pattern{" +
                "patternType=" + patternType +
                ", textLabel=" + textLabel +
                '}';
    }


}
