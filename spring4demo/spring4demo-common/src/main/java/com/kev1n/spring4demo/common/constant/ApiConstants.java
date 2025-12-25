package com.kev1n.spring4demo.common.constant;

/**
 * API常量
 * 
 * @author spring4demo
 * @version 1.0.0
 */
public final class ApiConstants {
    
    private ApiConstants() {
        // Utility class
    }
    
    /** API版本 */
    public static final String API_VERSION_V1 = "v1";
    
    /** API前缀 */
    public static final String API_PREFIX = "/api";
    
    /** API版本前缀 */
    public static final String API_VERSION_PREFIX = API_PREFIX + "/" + API_VERSION_V1;
    
    /** 请求头 */
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_X_REQUESTED_WITH = "X-Requested-With";
    
    /** 响应头 */
    public static final String HEADER_X_TOTAL_COUNT = "X-Total-Count";
    public static final String HEADER_X_PAGE_COUNT = "X-Page-Count";
    
    /** 内容类型 */
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
    
    /** 时间格式 */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    
    /** 分页参数 */
    public static final String PARAM_PAGE = "page";
    public static final String PARAM_SIZE = "size";
    public static final String PARAM_SORT = "sort";
    public static final String PARAM_ORDER = "order";
    
    /** 默认分页参数 */
    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 1000;
    
    /** 排序方向 */
    public static final String ORDER_ASC = "asc";
    public static final String ORDER_DESC = "desc";
    
    /** 错误代码 */
    public static final int CODE_ERROR = -1;
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_BAD_REQUEST = 400;
    public static final int CODE_UNAUTHORIZED = 401;
    public static final int CODE_FORBIDDEN = 403;
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_INTERNAL_SERVER_ERROR = 500;
    
    /** 错误消息 */
    public static final String MESSAGE_SUCCESS = "操作成功";
    public static final String MESSAGE_BAD_REQUEST = "请求参数错误";
    public static final String MESSAGE_UNAUTHORIZED = "未授权访问";
    public static final String MESSAGE_FORBIDDEN = "禁止访问";
    public static final String MESSAGE_NOT_FOUND = "资源不存在";
    public static final String MESSAGE_INTERNAL_SERVER_ERROR = "服务器内部错误";
}