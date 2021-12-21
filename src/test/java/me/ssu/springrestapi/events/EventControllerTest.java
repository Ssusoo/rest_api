package me.ssu.springrestapi.events;

import me.ssu.springrestapi.accounts.Account;
import me.ssu.springrestapi.accounts.AccountRole;
import me.ssu.springrestapi.common.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

public class EventControllerTest extends BaseTest {

    // TODO 엑세스 토큰-1
    private String getAccessToken(boolean needToCreateAccount) throws Exception {
        // TODO Given, 이벤트 생성
        if (needToCreateAccount) {
            createAccount();
        }

        // TODO When & Then
        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password"));

        var responseBody = perform.andReturn().getResponse().getContentAsString();

        Jackson2JsonParser parser = new Jackson2JsonParser();

        return parser.parseMap(responseBody).get("access_token").toString();
    }

    // TODO 엑세스 토큰-2
    private Account createAccount() {
        Account ssu = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        return this.accountService.saveAccount(ssu);
    }

    // TODO 엑세스 토큰-2
    private String getBearerToken(boolean needToCreateAccount) throws Exception {
        return "Bearer " + getAccessToken(needToCreateAccount);
    }

    // TODO 엑세스 토큰-3
    private String getBearerToken() throws Exception {
        return "Bearer " + getAccessToken(true);
    }

    // TODO 엑세스 토큰-4
    @BeforeEach
    public void setUp() {
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    // TODO 이벤트 생성 API
    @Test
    @DisplayName("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        // TODO Given, 이벤트 생성
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        // TODO Location 헤더 정보
        mockMvc.perform(post("/api/events/")
                        // TODO 토큰 인증 값 넘겨주기
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                // TODO 201 응답 확인(새 리소스를 성공적으로 생성함)
                .andExpect(status().isCreated())
                // TODO ID가 있는지 확인(DB에도)
                .andExpect(jsonPath("id").exists())
                // TODO 헤더 조회
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                // TODO 비즈니스 로직
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                // TODO HATEOAS
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                // TODO Rest Docs
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                // TODO _links.profile
                                linkWithRel("profile").description("link to update an existing event")
                        ),
                        // TODO 요청 헤더
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        // TODO 요청 필터
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrolmment")
                        ),
                        // TODO 응답 헤더
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        // TODO 응답 필드
                        // TODO 일부분만 하고 싶을 때 'relaxedResponseFields'
                        relaxedResponseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrolmment"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                // TODO _links.profile
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }

    // TODO 이벤트 생성 API(실패)-2
    @Test
    @DisplayName("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                // TODO 입력 받을 수 없는 값
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        // TODO Location 헤더 정보
        mockMvc.perform(post("/api/events/")
                        // TODO 토큰 인증 값 넘겨주기
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                // TODO 400 응답확인(잘못된 요청을 보낸 경우)
                .andExpect(status().isBadRequest())
        ;
    }

    // TODO 이벤트 생성 API(실패, Field Error)-3
    @Test
    @DisplayName("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEventFieldError() throws Exception {
        // TODO Given, 이벤트 생성
        EventDto eventDto = EventDto.builder().build();

        // TODO Location 헤더 정보
        mockMvc.perform(post("/api/events")
                        // TODO 토큰 인증 값 넘겨주기
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                // TODO 400 응답확인(잘못된 요청을 보낸 경우)
                .andExpect(status().isBadRequest())
                // TODO Field Error($[0])
                // TODO 인덱스 핸들러($[0] -> content[0])-2
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].code").exists())              // 에러 코드
                .andExpect(jsonPath("content[0].defaultMessage").exists())    // 기본 메시지
                .andExpect(jsonPath("content[0].field").exists())             // 어떤 필드에서 발생한 에러인지
//                .andExpect(jsonPath("content[0].rejectedValue").exists())            // 입력 거절받은 값
                // TODO 인덱스 핸들러(_links.index)-1
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    // TODO 이벤트 생성 API(실패, Global Error)-4
    @Test
    @DisplayName("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEventGlobalError() throws Exception {
        // TODO Given, 이벤트 생성
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                // TODO 날짜 검증
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        // TODO Location 헤더 정보
        mockMvc.perform(post("/api/events")
                        // TODO 토큰 인증 값 넘겨주기
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                // TODO 400 응답확인(잘못된 요청을 보낸 경우)
                // TODO Field Error($[0])
                // TODO 인덱스 핸들러($[0] -> content[0])-2
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].code").exists())              // 에러 코드
                .andExpect(jsonPath("content[0].defaultMessage").exists())    // 기본 메시지
//                .andExpect(jsonPath("content[0].field").exists())                     // 어떤 필드에서 발생한 에러인지
//                .andExpect(jsonPath("$[0].rejectedValue").exists())                   // 입력 거절받은 값
                // TODO 인덱스 핸들러(_links.index)-1
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    // TODO 이벤트 데이터-1
    private Event buildEvent(int index) {
        return Event.builder()
                .name("event " + index)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();
    }

    // TODO 이벤트 데이터-2
    private Event generateEvent(int index) {
        Event event = buildEvent(index);
        return this.eventRepository.save(event);
    }

    // TODO 이벤트 데이터-3
    private Event generateEvent(int index, Account account) {
        Event event = buildEvent(index);
        event.setManager(account);
        return this.eventRepository.save(event);
    }

    // TODO 이벤트 전체 목록 조회(성공, 인증 정보가 없는 경우)-1
    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        // TODO Given(이벤트 생성)
        IntStream.range(0, 30).forEach(i -> {
            generateEvent(i);
        });

        // TODO When & Then(페이지 정렬)
        this.mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        // TODO 이름의 역순으로
                        .param("sort", "name,DESC"))
                .andDo(print())
                // TODO 200 응답 확인(요청을 성공적으로 처리함)
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                // TODO 전체 리소스화(PageResourceAssembler<Event>로 Page 안에 있는 Data, 리소스화하기)
                .andExpect(jsonPath("_links").exists())
                // TODO 개별 리소스화
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                // TODO 문서화(profile)
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }

    // TODO 이벤트 전체 목록 조회(성공, 인증 정보가 있는 경우)-2
    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEventsWithAuthentication() throws Exception {
        // TODO Given(이벤트 생성)
        IntStream.range(0, 30).forEach(i -> {
            generateEvent(i);
        });

        // TODO When & Then
        mockMvc.perform(get("/api/events")
                        // TODO 토큰 인증 값 넘겨주기
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,DESC"))
                .andDo(print())
                // TODO 200 응답 확인(요청을 성공적으로 처리함)
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                // TODO 전체 리소스화(PageResourceAssembler<Event>로 Page 안에 있는 Data, 리소스화하기)
                .andExpect(jsonPath("_links").exists())
                // TODO 개별 리소스화
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                // TODO 인증 정보를 준 경우
                .andExpect(jsonPath("_links.create-event").exists())
                // TODO 문서화(profile)
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }

    // TODO 이벤트 개별 조회 API(성공)-1
    @Test
    @DisplayName("기존의 이벤트를 하나 조죄하기")
    public void getEvent() throws Exception {
        // TODO Given, 이벤트 생성과 유저 생성
        Account account = this.createAccount();
        Event event = this.generateEvent(100, account);

        // TODO When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                // TODO 문서화(profile)
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
        ;
    }

    // TODO 이벤트 개별 조회 API(실패, NotFound)-2
    @Test
    @DisplayName("없는 이벤트는 조회했을 때 404 응답받기")
    public void getEvent404() throws Exception {
        // TODO When & Then
        this.mockMvc.perform(get("/api/events/11883"))
                // TODO 404 응답확인(요청한 리소스가 없음)
                .andExpect(status().isNotFound());
    }

    // TODO 이벤트 수정 API-1(성공)
    @Test
    @DisplayName("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception {
        // TODO Given, 이벤트와 유저 생성
        Account account = this.createAccount();
        Event event = generateEvent(200, account);

        // TODO 이벤트 수정
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        // TODO When & Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                        // TODO 토큰 인증 값 넘겨주기
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                // TODO 200 응답 받기(요청을 성공적으로 처리함)
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                // TODO 리소스화(_Links.self)
                .andExpect(jsonPath("_links.self").exists())
                // TODO 문서화(profile)
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("update-event"))
        ;
    }

    // TODO 이벤트 수정 API(실패, Field Error)-2
    @Test
    @DisplayName("입력값이 비어있는 경우에 이벤트 수정 실패")
    public void updateEventFieldError() throws Exception {
        // TODO Given, 이벤트 생성
        Event event = this.generateEvent(200);
        EventDto eventDto = new EventDto();

        // TODO When & Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                        // TODO 토큰 인증 값 넘겨주기
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                // TODO 400 응답확인(잘못된 요청을 보낸 경우)
                .andExpect(status().isBadRequest());
    }

    // TODO 이벤트 수정 API(실패, Global Error)-2
    @Test
    @DisplayName("입력값이 잘못된 경우에 이벤트 수정 실패")
    public void updateEventGlobalError() throws Exception {
        // TODO Given, 이벤트 생성
        Event event = generateEvent(200);

        // TODO 이벤트를 수정할 DTO
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        // TODO When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        // TODO 토큰 인증 값 넘겨주기
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                // TODO 400 응답확인(잘못된 요청을 보낸 경우)
                .andExpect(status().isBadRequest());
    }

    // TODO 이벤트 수정 API(실패, NotFound)-4
    @Test
    @DisplayName("존재하지 않는 이벤트 수정 실패")
    public void updateEventNotFound() throws Exception {
        // TODO Given(이벤트 생성)
        Event event = generateEvent(200);

        // TODO 존재하지 않을 이벤트
        EventDto eventDto = modelMapper.map(event, EventDto.class);

        // TODO When & Then
        mockMvc.perform(put("/api/events/123123")
                        // TODO 토큰 인증 값 넘겨주기
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                // TODO 404 응답확인(요청한 리소스가 없음)
                .andExpect(status().isNotFound());
    }
}