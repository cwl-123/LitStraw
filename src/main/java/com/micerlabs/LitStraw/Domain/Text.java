package com.micerlabs.LitStraw.Domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Text {
    // 文本样式
    private TextType textType;
    // 文本内容
    private String text;
    // Adobe 分配路径
    private String path;
    // 文本边界
    private float[] bounds;
    // 文本所在页面
    private int page;

    public Text(String text, TextType textType) {
        this.text = text;
        this.textType = textType;
    }

    public Text(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Text{" +
                "textType=" + textType +
                ", text='" + text + '\'' +
                '}';
    }
}
