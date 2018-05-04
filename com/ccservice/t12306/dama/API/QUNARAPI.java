package com.ccservice.t12306.dama.API;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.ccservice.t12306.dama.DaMaCommon;
import com.ccservice.t12306.dama.API.UUYunLinuxApi.Md5;
import com.ccservice.util.ExceptionUtil;
import com.ccservice.util.WriteLog;

public class QUNARAPI {
    public static void main1(String[] args) {
        //        String hmac = "";
        String imgFilepath = "d:\\12306\\";
        //        String image = GetImageStr(imgFilepath);
        //        hmac = Md5.MD5(ProductKey + ProductagentCode + image);
        //        Map<String, String> param = new HashMap<String, String>();
        //        param.put("agentCode", ProductagentCode);
        //        param.put("image", image);
        //        param.put("hmac", hmac);
        //        post(QunarProducturl, param);

        //        DaMaCommon common = yibuprint("http://1461898508194.jpg", imgFilepath);
        //        System.out.println(common);
        //        error(common.getId());
        //
        //        System.out.println(findCodeResut("hangt1512250704339230102"));
    }

    public static final String TAOBAOIMGURL = "http://taobaodama.hangtian123.net";

    //    private final static String qunarapiurl = "http://api.pub.train.qunar.com/api/captcha.jsp？agentCode=hangt&img=";
    //    private final static String QUNARAPIURL = "http://api.pub.train.dev.qunar.com/trainCrawler/api/captcha.jsp";
    private final static String QUNARAPIURL = "http://api.pub.train.qunar.com/captcha/api/captcha.jsp";

    //http://api.pub.train.qunar.com

    private final static String QunarProducturl = "/captcha/api/uploadCaptcha.jsp";//去哪正式地址(异步)

    private final static String QunarProducturlCallBack = "/captcha/api/queryResult.jsp";//正式结果查询

    private final static String QunarFeedbackresult = "/captcha/api/feedbackResult.jsp";//回调打码结果

    private final static String ProductKey = "hangt";

    private final static String ProductagentCode = "0C13D7C3566147EB90D1E273278DCDD9";

    private final static int DAMA_TIME = 4000;

    //    private final static int QunarTimeoutTime = 20;

    //本地打码，imgPath格式如： D:\\image\\vcode.jpg
    public static String localPrint(String imgPath) {
        return displayJsonResult(createByPost(imgPath));
    }

    //远程打码，imgUrl格式如：http://www.yeebooking.com/images/vcode.jpg
    public static String remotePrint(String imgUrl) {
        return displayJsonResult(createByUrl(imgUrl));
    }

    public static String createByPost(String filePath) {
        String result = "";
        try {
            File f = new File(filePath);
            if (f.exists()) {
                int size = (int) f.length();
                byte[] data = new byte[size];
                FileInputStream fis = new FileInputStream(f);
                fis.read(data, 0, size);
                fis.close();
                if (data.length > 0) {
                    result = qunarcoding(data);
                }
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static String createByUrl(String filePath) {
        String result = "";
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            ImageIO.write(ImageIO.read(new URL(filePath)), "jpg", baos);
            result = qunarcoding(baos.toByteArray());
            System.out.println(result);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 传验证码给qunar 
     * @param data
     * @return
     * @throws UnsupportedEncodingException
     * @time 2014年12月20日 下午2:48:55
     * @author fiend
     */
    public static String qunarcoding(byte[] data) throws UnsupportedEncodingException {
        System.out.println(data.length);
        String result = test1(data);
        return result;
    }

    public static String displayJsonResult(String json) {
        try {
            JSONObject jsonObject = JSONObject.fromObject(json);
            if (jsonObject.has("ret") && jsonObject.getString("ret").equals("true")) {
                JSONObject jsondata = jsonObject.getJSONObject("data");
                return jsondata.getString("passcode");
            }
            else if (jsonObject.has("ret") && jsonObject.getString("ret").equals("false")) {
                JSONObject jsondata = jsonObject.getJSONObject("data");
                String errorstr = jsondata.getString("errmsg");
                System.out.println("打码失败：" + errorstr);
                return "false";
            }
            else {
                System.out.println("未知错误");
                return "false";
            }
        }
        catch (Exception e) {
            System.out.println("失败传输");
            return "false";
        }
    }

    private static String test1(byte[] img) {
        URL url;
        String result = "-1";
        try {
            url = new URL(QUNARAPIURL + "?agentCode=hangt");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("accept", "text/xml;text/html");
            connection.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(img);
            outputStream.flush();
            outputStream.close();
            BufferedReader in = null;
            StringBuffer sb = new StringBuffer();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
            System.out.println("result ==" + result);
            connection.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String GetImageStr(String imgFilepath) {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(imgFilepath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//返回Base64编码过的字节数组字符串
    }

    /**
     * 查询打码结果
     * @param url
     * @return
     */
    public static String findCodeResut(String key) {
        Map<String, String> map = new HashMap<String, String>();
        String hmac = Md5.MD5(ProductagentCode + ProductKey + key);
        map.put("agentCode", ProductKey);
        map.put("globalId", key);
        map.put("hmac", hmac);
        String reusltstr = post(TAOBAOIMGURL + QunarProducturlCallBack, map);
        return reusltstr;
    }

    /**
     * 
     * @param key
     */
    public static void error(String key) {
        Map<String, String> map = new HashMap<String, String>();
        String isRight = "false";
        String hmac = Md5.MD5(ProductagentCode + ProductKey + key + isRight);
        map.put("agentCode", ProductKey);
        map.put("globalId", key);
        map.put("isRight", isRight);
        map.put("hmac", hmac);
        String reusltstr = post(TAOBAOIMGURL + QunarFeedbackresult, map);
        WriteLog.write("t_qunaryibuerror", reusltstr);
    }

    /**
     * 去哪异步打码
     * @param picturepath 图片路径  
     * @param dirpath 文件路径
     * @return
     */
    public static DaMaCommon yibuprint(String picturepath, String dirpath) {
        picturepath = "C:\\Users\\Administrator\\Desktop\\img\\1524516029173.jpg";
        DaMaCommon dmc = new DaMaCommon();
        dmc.setTpye(DaMaCommon.QUNARYIBU);
        try {
            String image = GetImageStr(picturepath);
            String hmac = Md5.MD5(ProductagentCode + ProductKey + image);
            Map<String, String> param = new HashMap<String, String>();
            param.put("agentCode", ProductKey);
            param.put("image", image);
            param.put("hmac", hmac);
            long starttime = System.currentTimeMillis();//开始时间
//            WriteLog.write("t_qunardamayibu", picturepath + ":发送请求");
            System.out.println(picturepath + "发送请求");
            String jsonstr = post(TAOBAOIMGURL + QunarProducturl, param);
//            WriteLog.write("t_qunardamayibu", picturepath + ":去哪返回结果:[" + jsonstr + "]");
            System.out.println(picturepath + "返回结果" + jsonstr);
            if (jsonstr != null && !"".equals(jsonstr)) {
                JSONObject obj = JSONObject.fromObject(jsonstr);
                if ("true".equals(obj.getString("ret"))) {
                    JSONObject data = obj.getJSONObject("data");
                    String state = data.getString("state");
                    String id = data.getString("globalId");
                    if ("1".equals(state)) {
                        dmc.setId(id + "");
                        /*try {
                            Thread.sleep(1000L);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                        String resultval = findCodeResut(id);
                        WriteLog.write("t_qunardamayibu", picturepath + ":find去哪返回结果:[" + resultval + "]");
                        JSONObject parserobj = JSONObject.fromObject(resultval);
                        String ret = parserobj.getString("ret");
                        JSONObject resultobj = parserobj.getJSONObject("data");
                        String stateval = resultobj.getString("state");
                        while ("0".equals(stateval)
                                && (System.currentTimeMillis() - starttime) < (getqunarCodeTimeout() * 1000)) {
                            /*try {
                                Thread.sleep(1000L);
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/
                            resultval = findCodeResut(id);
                            WriteLog.write("t_qunardamayibu", picturepath + ":find去哪返回结果:[" + resultval + "]");
                            parserobj = JSONObject.fromObject(resultval);
                            ret = parserobj.getString("ret");
                            resultobj = parserobj.getJSONObject("data");
                            stateval = resultobj.getString("state");
                        }
                        long t2 = System.currentTimeMillis() - starttime;
                        if ("1".equals(stateval)) {
                           /* try {
                                if (t2 < DAMA_TIME) {
                                    WriteLog.write("t_qunardamayibu", id + ":打码成功,休息：[" + (DAMA_TIME - t2) + "]");
                                    Thread.sleep(DAMA_TIME - t2);
                                }
                            }
                            catch (Exception e1) {
                                e1.printStackTrace();
                            }*/
                            String result = resultobj.getString("result");
                            try {
                                result = changeDaMaResult(resultobj.getString("result"));
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            dmc.setResult(result);
                            WriteLog.write("t_qunardamayibu", id + ":打码成功,用时：" + t2 + ",msg:" + resultval);
                        }
                        else {
                            WriteLog.write("t_qunardamayibu", id + ":打码失败,msg：" + resultval);
                        }
                    }
                    else {
                        WriteLog.write("t_qunardamayibu", id + ":请求受理失败");
                    }
                }
                else {
                    WriteLog.write("t_qunardamayibu", ":请求请求失败：" + jsonstr);
                }
            }

        }
        catch (Exception e) {
            dmc.setResult("出错了");
            ExceptionUtil.writelogByException("qunarapierror", e);
        }
        return dmc;
    }

    public static void main(String[] args) {
        DaMaCommon yibuprint = yibuprint("", "");
        System.out.println(yibuprint.getResult());
    }

    /**
     * 获取去哪儿的超时时间
     * @return
     * @time 2015年10月13日 下午4:29:06
     * @author chendong
     */
    private static int getqunarCodeTimeout() {
        return 30;
    }

    /**
     * 
     * @param url
     * @param params
     * @return
     */
    public static String post(String url, Map<String, String> params) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
        HttpPost httppost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }
        String responseStr = "";
        try {
            httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            responseStr = EntityUtils.toString(resEntity, "UTF-8");
            httpclient.getConnectionManager().shutdown();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return responseStr.replaceAll("&quot;", "\"");
    }

    /**
     * 异步上传图片
     * @param img
     * @param agentCode
     * @param image
     * @param hmac
     * @return
     */
    private static String ansyImgRequest() {
        URL url;
        String result = "-1";
        return result;
    }

    public static boolean GenerateImage(String imgStr) {//对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {//调整异常数据
                    b[i] += 256;
                }
            }
            //生成jpeg图片
            String imgFilePath = "d:\\222.jpg";//新生成的图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * 修饰打码结果
     * 
     * @param damaResult
     * @return
     * @time 2016年12月1日 下午4:42:42
     * @author fiend
     */
    public static String changeDaMaResult(String damaResult) {
        String damaChangeResult = "";
        try {
            if (damaResult != null && damaResult.contains(",")) {
                String[] damaResults = damaResult.split(",");
                for (int i = 0; i < damaResults.length; i++) {
                    damaChangeResult += changeInt(Integer.valueOf(damaResults[i]));
                    if (i != damaResults.length - 1) {
                        damaChangeResult += ",";
                    }
                }
            }
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            damaChangeResult = damaResult;
        }
        return damaChangeResult;
    }

    /**
     * 数字随机+（0-20）的数
     * 
     * @param result
     * @return
     * @time 2016年12月1日 下午4:42:22
     * @author fiend
     */
    public static int changeInt(int result) {
        return result > 20 ? result + (int) (Math.random() * 20) : result;
    }

}
