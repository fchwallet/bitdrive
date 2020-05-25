package com.upload.app.core.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 服务端工具类
 *
 * @author Retina.Ye
 */
public class ServerUtil {

    private static Log logger = LogFactory.getLog(ServerUtil.class);

    /**
     * 获取本机IP
     *
     * @return
     */
    public static String getLocalIp() {
        List<String> list = ServerUtil.getHostAddress();
        String ip = null;
        for (String currentIp : list) {
            if (!StringUtils.equals(currentIp, "127.0.0.1")) {
                ip = currentIp;
                break;
            }
        }
        return ip;
    }

    public static String getClientIp(HttpServletRequest request) {
        String ipAddress = null;
        //ipAddress = this.getRequest().getRemoteAddr();
        ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                }
                catch (UnknownHostException e) {
                    logger.error(e.getMessage(), e);
                }
                if (inet != null) {
                    ipAddress = inet.getHostAddress();
                }
            }

        }

        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    private static List<String> getHostAddress() {// NOPMD
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        }
        catch (SocketException e) {
            logger.info(e.getMessage(), e);
            return null;
        }
        List<String> list = new ArrayList<String>();
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = netInterfaces.nextElement();
            String displayName = ni.getDisplayName();
            // System.out.println("getDisplayName:" + ni.getDisplayName());
            Enumeration<InetAddress> ips = ni.getInetAddresses();

            List<String> subList = null;
            if ("eth0".equalsIgnoreCase(displayName)) {
                Enumeration<NetworkInterface> subInterfaces = ni.getSubInterfaces();
                subList = getHostAddress(subInterfaces);
            }
            String currentIp = null;
            while (ips.hasMoreElements()) {
                InetAddress inet = ips.nextElement();
                String ip = inet.getHostAddress();
                if (ip.indexOf(":") == -1) {
                    // 不要使用IPv6
                    if (currentIp == null) {
                        currentIp = ip;
                    }
                    else if (subList != null && !subList.contains(ip)) {
                        currentIp = ip;
                    }
                    else {// NOPMD
                        // 忽略
                    }
                }
            }
            if (currentIp != null) {
                list.add(currentIp);
            }
        }
        return list;
    }

    private static List<String> getHostAddress(Enumeration<NetworkInterface> netInterfaces) {
        List<String> list = new ArrayList<String>();
        // System.out.println("getHostAddress:" + netInterfaces);

        while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = netInterfaces.nextElement();
            // System.out.println("getDisplayName:" + ni.getDisplayName());
            Enumeration<InetAddress> ips = ni.getInetAddresses();
            while (ips.hasMoreElements()) {

                InetAddress inet = ips.nextElement();
                String ip = inet.getHostAddress();

                if (ip.indexOf(":") == -1) {
                    // 不要使用IPv6
                    list.add(ip);
                    // System.out.println("ip:" + ip);
                }
            }
        }
        return list;
    }

}
