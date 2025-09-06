package busanVibe.busan.global.aop.logging

import org.aspectj.lang.annotation.Pointcut

class CommonPointcut {

    /**
     * API 호출 시 로그 찍도록 @RestController 어노테이션에 PointCut 설정
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    fun restControllerEndpoints(){}

}