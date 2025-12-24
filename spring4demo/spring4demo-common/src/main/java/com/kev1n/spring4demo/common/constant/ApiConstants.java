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
}