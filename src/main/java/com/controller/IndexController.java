package com.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sd4324530.fastweixin.api.MediaAPI;
import com.github.sd4324530.fastweixin.api.MenuAPI;
import com.github.sd4324530.fastweixin.api.config.ApiConfig;
import com.github.sd4324530.fastweixin.api.entity.Menu;
import com.github.sd4324530.fastweixin.api.enums.MediaType;
import com.github.sd4324530.fastweixin.api.enums.ResultType;
import com.github.sd4324530.fastweixin.api.response.UploadMediaResponse;
import com.github.sd4324530.fastweixin.message.BaseMsg;
import com.github.sd4324530.fastweixin.message.ImageMsg;
import com.github.sd4324530.fastweixin.message.TextMsg;
import com.github.sd4324530.fastweixin.message.req.*;
import com.github.sd4324530.fastweixin.servlet.WeixinControllerSupport;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by hdy on 03/11/2017.
 */
@RequestMapping("wx")
@RestController
public class IndexController extends WeixinControllerSupport {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);
    private ApiConfig apiConfig = null;
    private Random random = new Random();
    //临时保存文件的地址
//    @Value("${image.save.url}")
//    private String saveUrl;

    @Override
    protected String getToken() {
        return "hzkjzyjsxy";
    }

    @RequestMapping("/index2")
    public String index() {
        apiConfig = new ApiConfig("wx2279c1e17f10dd9e", "f2cb084c36ced62a2e9a59fce1ca0a1a");
        return apiConfig.getAccessToken();
    }

    @RequestMapping("index")
    public void inde() {
        String content = "ok.jpg";
        if (content.contains(".jpg") && content.length() > 4) {
            //说明是想要图片
            MediaAPI mediaAPI = new MediaAPI(getAccessToken());
            ArrayList<String> images = getImages(content.substring(0, content.lastIndexOf(".")));
            if (images == null || images.size() == 0) {
                System.out.println("对不起搜索不到与关键词相关的图片!");
            }
            File file = downloadImage(images.get(random.nextInt(images.size())));
            if (file != null && file.exists()) {
//                UploadImgResponse uploadImgResponse = mediaAPI.uploadImg(file);
//                String url = uploadImgResponse.getUrl();
//                return new ImageMsg(url);
                UploadMediaResponse mediaResponse = mediaAPI.uploadMedia(MediaType.IMAGE, file);
                System.out.println(mediaResponse);
                String mediaId = mediaResponse.getMediaId();
                System.out.println("mediaId:" + mediaResponse.getMediaId() + " " + mediaResponse.toJsonString());
            } else {
                System.out.println("后台出现异常!请稍后再试!");
            }
        }
        System.out.println("暂时实现的功能为输入:关键词.jpg可以随机获得相应的图像.如呵呵.jpg");
    }

    //重写父类方法，处理对应的微信消息
    @Override
    protected BaseMsg handleTextMsg(TextReqMsg msg) {
        String content = msg.getContent().trim().toLowerCase();
        log.debug("用户发送到服务器的内容:{}", content);
        if (content.contains(".jpg") && content.length() > 4) {
            //说明是想要图片
            MediaAPI mediaAPI = new MediaAPI(getAccessToken());
            ArrayList<String> images = getImages(content.substring(0, content.lastIndexOf(".")));
            if (images == null || images.size() == 0) {
                return new TextMsg("对不起搜索不到与关键词相关的图片!");
            }
            File file = downloadImage(images.get(random.nextInt(images.size())));
            if (file != null && file.exists()) {
                UploadMediaResponse mediaResponse = mediaAPI.uploadMedia(MediaType.IMAGE, file);
                String mediaId = mediaResponse.getMediaId();
                ImageMsg imageMsg = new ImageMsg(mediaId);
                imageMsg.setCreateTime(mediaResponse.getCreatedAt().getTime());
                imageMsg.setMsgType(mediaResponse.getType());
//                ImageMsg imageMsg = new ImageMsg(mediaId);
//                imageMsg.setCreateTime(mediaResponse.getCreatedAt().getTime());
//                imageMsg.setFromUserName(msg.getFromUserName());
//                imageMsg.setToUserName(msg.getToUserName());
                return imageMsg;
            } else {
                return new TextMsg("后台出现异常!请稍后再试!" + file.toString());
            }
        }
        return new TextMsg("暂时实现的功能为输入:关键词.jpg可以随机获得相应的图像.如呵呵.jpg");
    }

    public File downloadImage(String url) {
        InputStream in = null;
        FileOutputStream outputStream = null;
        try {
            URL u = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) u.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            in = httpURLConnection.getInputStream();
            File saveFile = new File("/home/hdy/upload", new File(url).getName());
//            File saveFile = new File("/Users/hdy/Desktop/untitled folder 2", new File(url).getName());
            outputStream = new FileOutputStream(saveFile);
            byte[] bytes = new byte[in.available()];
            int len = -1;
            while ((len = in.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
            return saveFile;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    public ArrayList<String> getImages(String str) {
        try {
            Connection.Response execute = Jsoup.connect("http://www.doutula.com/search?keyword=" + URLEncoder.encode(str, "utf-8"))
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,la;q=0.4,zh-TW;q=0.2")
                    .header("Cache-Control", "no-cache")
                    .header("Connection", "keep-alive").execute();
            Document parse = null;
            parse = execute.parse();
            Elements random_picture = parse.select("div.random_picture > a > img[class!=gif]");
            ArrayList<String> lists = new ArrayList<>();
            for (int i = 0; i < random_picture.size(); i++) {
                Element element = random_picture.get(i);
                lists.add(element.attr("data-original"));
            }
            return lists;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 处理图片消息，有需要时子类重写
     *
     * @param msg 请求消息对象
     * @return 响应消息对象
     */
    @Override
    protected BaseMsg handleImageMsg(ImageReqMsg msg) {
        return new ImageMsg(msg.getMediaId());
//        return new ImageMsg("ZCjUEy5OPDQQmSzezxx6Z5FOI_QiKbNiD6UH4brfj4eL-AGE9HYKF4zBd8fxtI-O");
//        return new TextMsg(JSON.toJSONString(msg));
    }

    public ApiConfig getAccessToken() {
        if (apiConfig == null) {
            apiConfig = new ApiConfig("wx2279c1e17f10dd9e", "f2cb084c36ced62a2e9a59fce1ca0a1a");
        }
        return apiConfig;
    }


    /**
     * 处理小视频消息，有需要时子类重写
     *
     * @param msg 请求消息对象
     * @return 响应消息对象
     */
    @Override
    protected BaseMsg hadnleShortVideoMsg(VideoReqMsg msg) {
        return new TextMsg("暂不支持处理小视频.请等待后续更新!");
    }

    @RequestMapping("/create-menu")
    public String createMenu() {
        File file = new File(IndexController.class.getClassLoader().getResource("menu.txt").getFile());
        String json = null;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] bytes = new byte[inputStream.available()];
            int len = -1;
            while ((len = inputStream.read(bytes)) != -1) {
                json = new String(bytes, 0, len);
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        Menu menu = null;
        try {
            menu = mapper.readValue(json, Menu.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //创建菜单
        ApiConfig config = new ApiConfig("wx2279c1e17f10dd9e", "f2cb084c36ced62a2e9a59fce1ca0a1a");
        MenuAPI menuAPI = new MenuAPI(config);
        ResultType resultType = menuAPI.createMenu(menu);
        return resultType.toString();
    }

    @Override
    protected BaseMsg handleMenuClickEvent(MenuEvent event) {
        String key = event.getEventKey();
        switch (key.toUpperCase()) {
            case "music":
                return new TextMsg("音乐功能正在整合中..");
            default:
                return new TextMsg("不识别的菜单命令");
        }
    }


    /**
     * 处理链接消息，有需要时子类重写
     *
     * @param msg 请求消息对象
     * @return 响应消息对象
     */
    @Override
    protected BaseMsg handleLinkMsg(LinkReqMsg msg) {
        return new TextMsg("暂不支持处理链接.请等待后续更新!");
    }

    /**
     * 处理取消关注事件，有需要时子类重写
     *
     * @param event 取消关注事件对象
     * @return 响应消息对象
     */
    @Override
    protected BaseMsg handleUnsubscribe(BaseEvent event) {
        return new TextMsg("再见");
    }

    /**
     * 处理添加关注事件，有需要时子类重写
     *
     * @param event 添加关注事件对象
     * @return 响应消息对象
     */
    @Override
    protected BaseMsg handleSubscribe(BaseEvent event) {
        return new TextMsg("感谢您的关注!");
    }

}
