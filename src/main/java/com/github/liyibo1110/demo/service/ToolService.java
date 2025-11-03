package com.github.liyibo1110.demo.service;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Service;

/**
 * @author liyibo
 * @date 2025-11-03 15:45
 */
@Service
public class ToolService {

    /**
     * 告诉AI,什么样子的对话要调用function-call
     * 注解P就是让AI去提取的信息
     * @param name
     * @return
     */
    @Tool("北京有多少个名字的")
    public Integer nameCount(@P("姓名") String name) {
        System.out.println(name);   // name相当于是要带入到业务逻辑来计算的入参
        return 10;
    }

    @Tool("退票")
    public String cancelTicket(@P("车次") String bookingNumber,
                                @P("姓名") String name) {
        System.out.println(bookingNumber);
        System.out.println(name);
        // 调用退票业务功能
        return "退票成功";
    }
}
