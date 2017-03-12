package com.latios.garfield.entity;

import java.util.List;
import java.util.Map;

/**
 * @author zebin
 * @since 2016-10-29.
 */
public class Email {
    private String title;
    private String sendTo;
    private String content = "";

    public Email(String title, String sendTo) {
        this.title = title;
        this.sendTo = sendTo;
    }

    public void addTable(String tableName, List<Map<String, String>> rstList, List<String> columns) {
        StringBuilder builder = new StringBuilder();
        builder.append("<table border=\"1\"><tbody>");
        builder.append("<tr>");
        for (String column : columns) {
            builder.append("<td>").append(column).append("</td>");
        }
        builder.append("</tr>");
        for (Map<String, String> row : rstList) {
            builder.append("<tr>");
            for (String column : columns) {
                String cell = row.get(column);
                cell = cell == null ? "" : cell;
                builder.append("<td>").append(cell).append("</td>");
            }
            builder.append("</tr>");
        }
        builder.append("</tbody></table>");
        content += builder.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
