package com.max.homon.core.bean.base;

import com.max.homon.core.enums.RespType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ResponseParams<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /*** 交易终端编号 ***/
    private String reqNo;

    /*** 通信标示码【SUCCESS/FAIL】 ***/
    private String resultCode;

    /*** 通信信息 ***/
    private String resultMsg;

    /*** 业务标示码 ***/
    private String respCode;

    /*** 业务返回消息 ***/
    private String respMsg;

    /*** 返回出参 ***/
    private T param;

    /*** 加密参数 ***/
    private String sign;

    /*** 当前时间戳 ***/
    private String timestamp;

    /**
     * 签名类型
     */
    private String signType;

    /**
     * 加密类型
     */
    private String encryptType;

    /**
     * 版本号
     */
    private String version;

    /**
     * 密文
     */
    private String encryptData;

    public ResponseParams() {
    }

    public ResponseParams(String resultCode, String resultMsg, String respCode, String respMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.respCode = respCode;
        this.respMsg = respMsg;
    }

    public ResponseParams(String resultCode, String resultMsg, String respCode, String respMsg, T data) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.respCode = respCode;
        this.respMsg = respMsg;
        this.param = data;
    }

    public static ResponseParams init(String code, String msg, Object param
            , String signType, String encryptType, String reqNo) {
        ResponseParams<Object> response = new ResponseParams();
        response.setRespCode(code);
        response.setRespMsg(msg);
        response.setParam(param);
        response.setTimestamp(String.valueOf(System.currentTimeMillis()));
        response.setSignType(signType);
        response.setEncryptType(encryptType);
        response.setReqNo(reqNo);
        return response;
    }

    public static ResponseParams<Object> buildResponseParams(RequestParams requestParams, RespType respType) {
        return init(String.valueOf(respType.code), respType.msg, null, requestParams.getSignType(), requestParams.getEncryptType(), requestParams.getReqNo());
    }

    public static ResponseParams<Object> buildResponseParams(RequestParams requestParams, RespType respType,String msg) {
        return init(String.valueOf(respType.code), msg, null, requestParams.getSignType(), requestParams.getEncryptType(), requestParams.getReqNo());
    }

}
