package com.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sd4324530.fastweixin.api.MenuAPI;
import com.github.sd4324530.fastweixin.api.config.ApiConfig;
import com.github.sd4324530.fastweixin.api.entity.Menu;
import com.github.sd4324530.fastweixin.api.enums.ResultType;
import com.github.sd4324530.fastweixin.message.BaseMsg;
import com.github.sd4324530.fastweixin.message.ImageMsg;
import com.github.sd4324530.fastweixin.message.TextMsg;
import com.github.sd4324530.fastweixin.message.req.*;
import com.github.sd4324530.fastweixin.servlet.WeixinControllerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by hdy on 03/11/2017.
 */
@RequestMapping("wx")
@RestController
public class IndexController extends WeixinControllerSupport {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @Override
    protected String getToken() {
        return "111";
    }

    //重写父类方法，处理对应的微信消息
    @Override
    protected BaseMsg handleTextMsg(TextReqMsg msg) {
        String content = msg.getContent();
        log.debug("用户发送到服务器的内容:{}", content);
        return new TextMsg("暂时收到");
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
    }

    @RequestMapping("/access-token")
    public String getAccessToken() {
        ApiConfig config = new ApiConfig("wx2279c1e17f10dd9e", "f2cb084c36ced62a2e9a59fce1ca0a1a");
        return config.getAccessToken();
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
        ApiConfig config = new ApiConfig("wx2279c1e17f10dd9e", "11111");
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
