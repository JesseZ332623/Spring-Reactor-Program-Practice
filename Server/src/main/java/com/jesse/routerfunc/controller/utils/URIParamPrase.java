package com.jesse.routerfunc.controller.utils;

import org.springframework.web.reactive.function.server.ServerRequest;

import static java.lang.String.format;

public class URIParamPrase
{
    /**
     * <p>从 HTTP 请求的 URI 中查询指定的参数。</p>
     *
     * <p>
     *     比如 URL：<code>/api/query?name=perter</code>
     *     调用本方法 <code>getRequestParam(request, "name")</code>，返回字符串 perter。
     * </p>
     *
     * @param request    从前端传来的 HTTP 请求实例
     * @param paramName  参数名
     *
     * @throws IllegalArgumentException
     *         当 paramName 在 URI 中查询不到时抛出。
     */
    public static String
    praseRequestParam(ServerRequest request, String paramName)
    {
        if (request.queryParam(paramName).isEmpty())
        {
            throw new IllegalArgumentException(
                format(
                    "Parameter name [%s] not exist in request!",
                    paramName
                )
            );
        }

        return request.queryParam(paramName).get();
    }

    /**
     * <p>从 HTTP 请求的 URI 中查询指定的参数，将其转化成数字类型使用。</p>
     *
     * <p>
     *     比如 URL：<code>/api/query?id=114</code>
     *     调用本方法 <code>getRequestParam(request, "id")</code>，返回整数 114。
     * </p>
     *
     * @param request    从前端传来的 HTTP 请求实例
     * @param paramName  参数名
     *
     * @throws IllegalArgumentException
     *         1. 当 paramName 在 URI 中查询不到时抛出。</br>
     *         2. 当 phraseVal 的值小于 0 时抛出。
     * @throws NumberFormatException
     *         当 paramName 转化成数字失败时抛出。
     */
    public static Integer
    praseNumberRequestParam(ServerRequest request, String paramName)
    {
        try
        {
            String paramVal = praseRequestParam(request, paramName);
            int phraseVal   = Integer.parseInt(paramVal);

            if (phraseVal < 0) {
                throw new IllegalArgumentException("Parameter not less than 0!");
            }

            return phraseVal;
        }
        catch (NumberFormatException exception)
        {
            throw new IllegalArgumentException(
                    exception.getMessage()
            );
        }
    }
}
