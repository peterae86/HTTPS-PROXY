package com.peterae86.proxy;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by test on 2016/4/24.
 */
@Controller
public class ProxyController {
//    HackProxyForJetty92 hackProxyForJetty92 = new HackProxyForJetty92();
//
//    @RequestMapping("/")
//    public void proxy(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        hackProxyForJetty92.service(request, response);
//    }

    @RequestMapping("/peterae86/pac")
    @ResponseBody
    public String pac() throws ServletException, IOException {
        return "function FindProxyForURL(url, host) {\n" +
                "  return \"HTTPS www.backkoms.com:8443;\";\n" +
                "}";
    }

}
