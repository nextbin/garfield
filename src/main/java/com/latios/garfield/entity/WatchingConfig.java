package com.latios.garfield.entity;

/**
 * @author zebin
 * @since 2016-10-05.
 */
public class WatchingConfig {
    private String name;
    private String url;
    private String selector;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    @Override
    public String toString() {
        return "WatchingConfig{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", selector='" + selector + '\'' +
                '}';
    }
}
