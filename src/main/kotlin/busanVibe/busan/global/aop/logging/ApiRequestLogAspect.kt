package busanVibe.busan.global.aop.logging

import busanVibe.busan.domain.user.service.login.AuthService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class ApiRequestLogAspect {

    private val log: Logger = LoggerFactory.getLogger(ApiRequestLogAspect::class.java)

    /**
     * 전/후 처리 모두 가능
     * CommonPointcut.controllerPointCut Pointcut 지정하여
     * API 호출 시 호출 전후로 로그 출력하도록 하는 Aspect
     */
    @Around("CommonPointcut.restControllerEndpoints()")
    fun logApiRequest(jp: ProceedingJoinPoint): Any{

        // 시작시간 기록
        val start = System.currentTimeMillis()

        // 요청정보
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        val uri = request.requestURI
        val httpMethod = request.method
        val currentUser = runCatching { AuthService().getCurrentUser() }.getOrNull()

        // swagger, actuator 등 제외
        if (uri.startsWith("/swagger") || uri.startsWith("/v3/api-docs") || uri.startsWith("/actuator")) {
            return jp.proceed()
        }

        // 컨트롤러 정보
        val methodName = jp.signature.name
        val args = jp.args.joinToString()

        // 시작 로그
        log.info("API 호출: 유저[${currentUser?.id}] - 요청 정보 [${httpMethod}: ${uri} ] - [ ${methodName}(${args}) ]")


        // 이거 기준으로 위에는 before
        val obj = jp.proceed()
        // 이거 기준으로 아래는 after

        // 종료시간 기록
        val end = System.currentTimeMillis()

        // 종료 로그
        log.info("API 호출종료: 유저[${currentUser?.id}] - 요청 정보 [${httpMethod}: ${uri} ] - 실행시간[${end-start}ms]")

        return obj
    }

//    @Before("CommonPointcut.controllerPointcut()")
//    fun controllerStart(jp: JoinPoint){
//
//        val method = jp.signature.name
//        val args = jp.args.joinToString()
//        log.info("API 호출: $method, args=[$args]")
//
//    }
//
//    @AfterReturning("CommonPointcut.controllerPointcut()")
//    fun controllerFinish(jp: JoinPoint){
//        val method = jp.signature.name
//        log.info("API 종료(정상): $method")
//    }
//
//    @AfterThrowing("CommonPointcut.controllerPointcut()")
//    fun controllerThrowing(jp: JoinPoint){
//        val method = jp.signature.name
//        log.info("API 종료(비정상): $method")
//    }

}