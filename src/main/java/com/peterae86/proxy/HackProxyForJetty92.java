//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.peterae86.proxy;

import org.eclipse.jetty.proxy.ProxyServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class HackProxyForJetty92 extends ProxyServlet {
    public HackProxyForJetty92() {
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String originURL = request.toString();

        if (originURL.contains("http://")) {
            request = new HTTPSchemeRequest(request);
        }

        super.service(request, response);
    }

    public static class HTTPSchemeRequest extends HttpServletRequestWrapper {
        private StringBuffer stringBuffer;

        public HTTPSchemeRequest(HttpServletRequest request) {
            super(request);
            StringBuffer sb = super.getRequestURL();
            this.stringBuffer = new StringBuffer(sb.toString().replace("https://", "http://"));
        }

        public String getScheme() {
            return "http";
        }

        public StringBuffer getRequestURL() {
            return this.stringBuffer;
        }
    }
}
