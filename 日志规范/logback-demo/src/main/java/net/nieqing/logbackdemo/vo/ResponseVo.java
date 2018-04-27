package net.nieqing.logbackdemo.vo;



import java.io.Serializable;

import net.nieqing.logbackdemo.global.GlobalResponseCode;


/**
 * 通用返回结果
 * 
 * @author xingkong
 */
public class ResponseVo<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	// 信息码
	private int code;

	// 信息提示
	private String message;

	// 日志信息
	private String log;

	// 返回结果集
	private T data;

	public ResponseVo() {
		super();
	}

	public static ResponseVo fail(int code, String message) {
		return new ResponseVo(code, message);
	}


	public static ResponseVo success() {
		return new ResponseVo(GlobalResponseCode.SUCCESS, "OK");
	}

	public static <T> ResponseVo<T> success(T data) {
		return new ResponseVo(GlobalResponseCode.SUCCESS, "OK", data);
	}

	public ResponseVo(T data) {
		this(GlobalResponseCode.SUCCESS, "OK", data);
	}

	public ResponseVo(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public ResponseVo(int code, String message, T data) {
		super();
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public static ResponseVo createUnKnowExceptionResultBody() {
		return new ResponseVo(GlobalResponseCode.UNKONW_EXCEPTION, "Exception");
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}
}
