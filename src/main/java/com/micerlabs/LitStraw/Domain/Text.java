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
    private String content;
    // Adobe 分配路径
    private String path;
    // 文本边界
    private float[] bounds;
    // 文本所在页面
    private int page;

    public Text(String content, TextType textType) {
        this.content = content;
        this.textType = textType;
    }

    public Text(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Text{" +
                "textType=" + textType +
                ", text='" + content + '\'' +
                '}';
    }
}
