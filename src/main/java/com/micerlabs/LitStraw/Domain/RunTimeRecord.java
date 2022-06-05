package com.micerlabs.LitStraw.Domain;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class RunTimeRecord {
    private String date;
    private int fileNum;
    private long costTime;
    private String errorMsg;

    public RunTimeRecord() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        this.date = dateFormat.format(date);
    }

    @Override
    public String toString() {
        return "RunTimeRecord{" +
                "date='" + date +'\'' +
                ", fileNum=" + fileNum +
                ", costTime=" + costTime + "ms" +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
