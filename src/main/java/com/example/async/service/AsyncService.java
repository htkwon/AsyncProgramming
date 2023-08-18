package com.example.async.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


/**
 * <<정리>>
 * 기본적으로, Spring Framework에서 비동기를 처리하려면, Spring Framework의 도움이 필요하다.
 * Spring FrameWork가 개발자가 비동기로 처리하고자 하는 메소드(이 프로젝트에서는 EmailService -> 빈으로 등록되어 있음) 즉, 이 bean을 가져왔을 때
 * EmailService로 부터 순수한 Bean을 AsyncService에 반환을 해주는 것이 아니라,
 * EmailService 같은 경우 Async하게 동작해야하기 때문에 한번 더 Mapping (proxy객체로) 을 해준다. -> proxy 객체를 반환해줌.
 * 그러면, AsyncSerivce는 순수한 Bean을 받는게 아니라 Mapping된 EmailService를 받게된다.
 * 결국, 스프링 컨테이너에 등록된 Bean을 사용해야한다는 것 !! -> 2,3 번이 안되는 이유.
 *
 * 비동기적 프로그래밍을 할 때엔
 * 반드시 bean 주입을 받아야함. (하지만 @Async 메소드를 해당 내부 클래스에서 만들어서 사용했더라도 비동기하게 동작하지 않음)
 */


@Service
@RequiredArgsConstructor
public class AsyncService {

    private final EmailService emailService;

    /**
     * 빈을 주입받아서 주입받은 빈안의 메소드를 호출하는 case
     * 결과 : 정상 동작
     * 비동기로 동작할 수 있게 Sub Thread에게 위임
     */
    public void asyncCall_1(){
        System.out.println("{asyncCall_1} :: " + Thread.currentThread().getName());
        emailService.sendMail();;
        emailService.sendMailWithCustomThreadPool();
    }

    /**
     * 빈 주입이 아닌 인스턴스를 선언하고 인스턴스 안의 메소드를 호출할 때 비동기로 동작하는지에대한 case
     * 결과 : 동일한 쓰레드가 모두 다 처리 -> 스프링 컨테이너에 등록된 Bean을 사용한 것이 아니기에 Spring FrameWork의 도움을 받을 수 없음.( 동기적으로 작동 )
     */
    public void asyncCall_2(){
        System.out.println("{asyncCall_2} :: " + Thread.currentThread().getName());
        EmailService emailService = new EmailService();
        emailService.sendMail();
        emailService.sendMailWithCustomThreadPool();
    }

    /**
     * 해당 클래스 내부 메소드의 Async 메소드를 호출하였을 때 비동기로 동작하는지에대한 case
     * 결과 : 동일한 쓰레드가 모두 다 처리 -> 등록된 Bean애서 Mapping된 bean을 사용해야 하는데 그 클래스 안에서 호출하면 @Async 어노테이션을
     * 붙이지 않은 메소드 처럼 동작
     */
    public void asyncCall_3(){
        System.out.println("{asyncCall_3} :: " + Thread.currentThread().getName());
        sendMail();
    }

    @Async
    public void sendMail(){
        System.out.println("[ sendMail ] :: " + Thread.currentThread().getName());
    }


}
