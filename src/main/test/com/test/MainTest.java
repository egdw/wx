package com.test;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by hdy on 05/11/2017.
 */
public class MainTest {

    @Test
    public void get() throws IOException {
        Connection.Response execute = Jsoup.connect("http://www.doutula.com/search?keyword=" + URLEncoder.encode("呵", "utf-8"))
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,la;q=0.4,zh-TW;q=0.2")
                .header("Cache-Control", "no-cache")
                .header("Connection", "keep-alive").execute();
        Document parse = execute.parse();
        Elements random_picture = parse.select("div.random_picture > a > img[class!=gif]");
        for (int i = 0; i < random_picture.size(); i++) {
            Element element = random_picture.get(i);
//            System.out.println(element.attr("data-original"));
            System.out.println(new File(element.attr("data-original")).getName());
        }
    }

//    @Test
//    public void get2() {
//        String content = "呵呵.jpg";
//        String substring = content.substring(0, content.lastIndexOf("."));
//        System.out.println(substring);
//    }
}
