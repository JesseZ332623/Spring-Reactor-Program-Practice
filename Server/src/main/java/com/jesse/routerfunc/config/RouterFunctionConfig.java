
package com.jesse.routerfunc.config;

import com.jesse.routerfunc.controller.QueryRequestInterface;
import com.jesse.routerfunc.controller.TestRequestInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.*;

@Slf4j
@Configuration
@EnableWebFlux
public class RouterFunctionConfig
{
    @Autowired
    private QueryRequestInterface queryRequestComponent;

    @Autowired
    private TestRequestInterface testRequestComponent;

    /**
     * 有别于传统的，基于注解的 Spring MVC 编程模型，
     * WebFlux 使用了 Router Function（路由函数）来将前端的请求与后端的处理操作进行映射。
     * 因此需要添加一个 @Bean 注解，由 Spring 去调用这个方法。
     */
    @Bean
    public RouterFunction<?> startRouterFunction()
    {
        /*
         * 最常用的路由函数大概就是：RouterFunctions.route() 了。
         * 按照官方的注释：
         *      Route to the given handler function
         *      if the given request predicate applies.
         * 翻译过来就是：
         *      若给定的请求谓词适用，则路由到给定的处理回调函数。
         *
         * 比起全调用
         *      route(RequestPredicate predicate, HandlerFunction<T> handlerFunction)
         * 生成器风格的调用更适合简单的路由配置。
        */
        RouterFunction<ServerResponse> basicRequestRoute
                = RouterFunctions.route()
                  .GET("/hello", this.testRequestComponent::helloHandle)
                  .GET("/bye",   this.testRequestComponent::byeHandle)
                  .build();


        RouterFunction<ServerResponse> complexRequestRoute
                = RouterFunctions.route()
                  .GET("/api/query/recent_score",   this.queryRequestComponent::getRecentScore)
                  .GET("/api/query/score_record",   this.queryRequestComponent::getScoreRecordById)
                  .GET("/api/query/paginate_score", this.queryRequestComponent::getScoreByPagination)
                  .POST("/api/add_new_score",       this.queryRequestComponent::insertNewScoreRecord)
                  .PUT("/api/update_score",         this.queryRequestComponent::updateSpecifiedScoreRecord)
                  .DELETE("/api/delete_score",      this.queryRequestComponent::deleteScoreRecordById)
                  .DELETE("/api/truncate_score",    this.queryRequestComponent::truncateScoreRecord)
                  .build();

        return basicRequestRoute.and(complexRequestRoute);
    }
}
