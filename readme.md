자바스크립트 관련 라이브러리 추가를 위해 npm을 사용했습니다.<br>

**JS library list** <br>
npm version - 6.14.4 <br>
1. @yaireo/tagify<br>
2. summernote<br>
3. @fontawesome-free<br>
4. jdention<br>
5. bootstrap<br>
6. jquery & jquery-form<br>
7. moment<br>
8. tooltipster<br>

_NPM 미포함 예정이었지만 배포를 위해 포함시킴_

프론트 설계 및 코드는 bootstrap을 사용했습니다.<br>
백엔드 설계 및 코드는 spring MVC를 기반으로 한 프레임 워크들을 사용했습니다.<br>

사용된 프레임워크<br>
1. Spring Web MVC Ver.5.2.2
2. Spring Boot Ver.2.2.6
3. Spring Data API(JPA)
4. Spring Security

DB 
postgreSQL (dev)<br>
embeded (local)


Video 영상 데이터들은 프로젝트 내 /static/video에 위치하게 작성하였습니다.

**_참고한 사이트_**<br>
https://www.inflearn.com/ 인프런

**발견된 버그들**<br>
영상 업로드 후 바로 재생시 경로 못찾는 버그 (비디오 타입을 mp4로 특정지어 문제 해결)<br>
영상 업로드 후 강의 상세 페이지 접근시 값 변경을 인지 못함(재 로그인시 정상 동작)<br>
테스트용 카카오페이 결제 시스템 결제 오류(카카오페이 API 오류)<br>

**지식의 한계로 미구현 된 기능들**<br>
영상 리스트로 보여주기<br>
영상 제공시 빈공간(스페이스바) 값이 존재시 발생하는 에러<br>
footer 하단 고정(현) div로 강제로 하단에 위치하게끔 하였음, 추후 수정 예정이었으나 지나치게 많이 깨지므로 현상유지)<br>
JPA Projection 미도입(추후 프로젝트에서는 도입)<br>
영상은 mp4만 입력받도록 하였음<br>
모바일은 상정하지 않았으며, 일정 해상도 이하에서 UI 일그러짐 발생

**실행방법**<br>
코드를 IDEA에 import 시켜서 실행시켜도 상관없습니다.<br>
maven에서 pacake를 이용해 jar로 만든 후 실행시켜도 동작합니다.<br>
local로 동작시킬 경우(그냥 실행) SMTP 시스템은 동작하지 않으며 cmd line로 값이 보내집니다.
<pre><code>java -jar choi-0.0.1-SNAPSHOT.jar</code></pre>
본인 PC 환경에서는 동작하는 코드
<pre><code>java -jar choi-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev</code></pre>
