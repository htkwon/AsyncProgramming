# AsyncProgramming
비동기적 프로그래밍

## 비동기 프로그램이란?

: 비동기적 통신이며 실시간 응답을 필요로 하지 않는 상황에서 사용 (Notification, Email 전송, Push 알림)

-> Main Thread가 task를 처리하는 것이 아닌 Sub Thread에게 task를 위임하는 행위.

이를 위해, spring boot에서는 ThreadPool을 정의할 필요가 있다. (TheadPool을 생성하여 Async 적업을 처리.)

----


### ThreadPool 생성 옵션 

1. CorePoolSize : 쓰레드를 최소한 몇 개 가지고 있을 것인지.
2. MaxPoolSize : 쓰레드를 최대 몇 개 까지 할당할 것인지.
3. WorkQueue : 먼저 들어온 작업을 먼저 처리하도록 하는 자료구조인 Queue를 사용하여 순서 처리.
4. KeepAliveTime : 개발자가 지정한 시간만큼 쓰레드가 일을 하지 않으면 쓰레드를 반납하는 시간을 정해주는 옵션.
5. TimeUnit : 시간, 분, 초를 정해주는 시간 단위 설정.

-> 처음에 CorePoolSize 만큼 쓰레드를 생성하고 요청이 들어오면 해당 작업의 개수만큼 WorkQueue에 담겨지게 되고

(만약 CorePoolSize를 3개로 설정했는데 요청이 4개가 들어온다면 4번째 쓰레드를 생성하는 것이 아니라 WorkQueue에 담고 WorkQueue의 처음에 지정한 사이즈만큼 요청이 다 쌓였으면 MaxPoolSize 만큼 쓰레드 생성)

### ThreadPool 생성 시, 주의(고려) 사항

1. CorePoolSize 값을 너무 크게 설정할 경우, SideEffect 고려

-> 너무 큰 값을 설정하면 ThreadPool은 잘 사용되자 않고, 사용되더라도 그 만큼의 쓰레드가 필요가 없음에도 많은 쓰레드를 가지고있음.

2. IllegalArgumentException , NullpointerException 고려

-> CorePoolSize < 0 , KeepAliveTime < 0 , MaxPoolSize <= 0 , MaxPoolSize < CorePoolSize 중 하나라도 해당 시 IllegalArgumentException 발생.

-> WorkQueue 가 null 이면 NullPointerException 발생. 




#### 정리
1. if ( Thread 수 < CorePoolSize ) : 새로운 쓰레드 생성.
2. if ( Thread 수 > CorePoolSize ) : WorkQueue에 요청 추가.
3. if ( Queue FUll && Thread 수 < MaxPoolSize ) : 새로운 쓰레드 생성.
4. if ( Queue Full && Thread 수 > MaxPoolSize ) : 요청 거절 (무시)

----


### Spring에서 비동기 동작 원리

![KakaoTalk_20230818_112906686](https://github.com/htkwon/AsyncProgramming/assets/117131575/0fa2de78-7e68-40e4-bacf-7da1e9103f35)

<해당 프로젝트 기준>

Caller (AsyncService)가 EmailService를 호출 하는데 Spring이 개입하여 순수한 EmailService bean이 아니라 해당 EmailService bean을 매핑(Proxy 객체로)한 EmailService를 사용하게 하여 비동기적으로 동작할 수 있게 하는 메커니즘 




- 해당 프로젝트에서 asyncCall_2 와 asyncCall_3 의 문제점

![KakaoTalk_20230818_113522095](https://github.com/htkwon/AsyncProgramming/assets/117131575/a7886060-88c9-4ac0-a3af-41dd0e879b8d)


-> 기본적으로 비동기적으로 동작하기위해서는 스프링 프레임워크의 도움이 필요한데,

만약 비동기적인 동작을 희망하는 요청이 있다면 그 요청을 처리할 수 있는 bean(해당 프로젝트에서는 EmailService)을 스프링이 Proxy 객체로 매핑하여 비동기적으로 동작할 수 있게 도와줘야 하는데

스프링 컨테이너에 있는 bean을 사용하 않거나 인스턴를 생성하여 사용하는 방법, 직접적으로 접근하는 방법(내부 메소드)은 스프링의 도움을 받을 수 없으므로 비동기적인 동작을 할 수 없다



