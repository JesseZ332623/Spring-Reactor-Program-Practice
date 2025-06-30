package com.jesse.routerfunc.controller.utils;

import org.springframework.web.reactive.function.server.ServerRequest;

import static java.lang.String.format;

public class URIParamPrase
{
    /**
     * <p>从 HTTP 请求的 URI 中查询指定的参数，将其转化成数字类型使用。</p>
     *
     * <p>
     *     比如 URI：<code>/api/query?id=114</code>
     *     调用本方法 <code>getRequestParam(request, "id")</code>，返回整数 114。
     * </p>
     *
     * @param request    从前端传来的 HTTP 请求实例
     * @param paramName  参数名
     *
     * @throws IllegalArgumentException
     *         当 paramName 在 URI 中查询不到时抛出。
     */
    public static Integer
    praseNumberRequestParam(ServerRequest request, String paramName)
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

        try
        {
            String paramVal = request.queryParam(paramName).get();
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
