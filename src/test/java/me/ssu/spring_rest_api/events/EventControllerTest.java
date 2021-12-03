package me.ssu.spring_rest_api.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.ssu.spring_rest_api.common.RestDocsConfiguration;
import me.ssu.spring_rest_api.common.TestDescrption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    // TODO Spring Boot는 자동으로 가능함.
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("입력값이 제대로인 경우")
    void createEvent() throws Exception {

        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .location("강남역 D2 스타일 펙토리")
                .beginEnrollmentDateTime(LocalDateTime
                        .of(2021,12, 23, 21,50))
                .closeEnrollmentDateTime(LocalDateTime
                        .of(2021,12, 24,22,30))
                .beginEventDateTime(LocalDateTime
                        .of(2021,12,25,22,55))
                .endEventDateTime(LocalDateTime
                        .of(2021,12,26,20,00))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .build();

        // TODO Location 헤더 정보 조회
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                // TODO 201 응답 확인(새 리소스를 성공적으로 생성함)
                .andExpect(status().isCreated())
                // TODO HATEOAS 적용
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                // TODO REST Docs
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                // TODO profile 추가
                                linkWithRel("profile").description("link to profile an existing event")
                        ),
                        // TODO 요청 헤더
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        // TODO 요청 필터
                        requestFields(
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                        ),
                        // TODO 응답 헤더
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        // TODO 응답 필드
                        // TODO 일부분만 하고 싶을 때 'relaxedResponseFields'
                        responseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                // TODO Error(Success, self, query-events, update-event
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update event list"),
                                // TODO profile 추가
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }
    @Test
    @DisplayName("입력값 이외에 에러 발생")
    void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .free(true)
                .location("강남역 D2 스타일 펙토리")
                .offline(false)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime
                        .of(2021,12, 23, 21,50))
                .closeEnrollmentDateTime(LocalDateTime
                        .of(2021,12, 24,22,30))
                .beginEventDateTime(LocalDateTime
                        .of(2021,12,25,22,55))
                .endEventDateTime(LocalDateTime
                        .of(2021,12,26,20,00))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .build();

        mockMvc.perform(post("/api/events")
                        // TODO Location 헤더에 생성된 이벤트 조회할 수 있는 URI 확인
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                // TODO 400 응답확인(잘못된 요청을 보낸 경우)
                .andExpect(status().isBadRequest())
       ;
    }
    // TODO Field Error
    @Test
    @DisplayName("입력값이 아예 없는 경우")
    void createBadRequestEmpty() throws Exception {
        // TODO Given
        // TODO 이벤트 생성(입력값이 아예 없는 경우)
        EventDto eventDto = EventDto.builder().build();

        // TODO When & Then
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                // TODO 400 응답확인(잘못된 요청을 보낸 경우)
                .andExpect(status().isBadRequest())
                // TODO Field Error($[0])
                // TODO 인덱스 핸들러($[0] -> content[0])-2
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].code").exists())              // 에러 코드
                .andExpect(jsonPath("content[0].defaultMessage").exists())    // 기본 메시지
                .andExpect(jsonPath("content[0].field").exists())             // 어떤 필드에서 발생한 에러인지
//                .andExpect(jsonPath("$[0].rejectedValue").exists())             // 입력 거절받은 값
                // TODO 인덱스 핸들러(_links.index)-1
                .andExpect(jsonPath("_links.index").exists())
        ;

    }
    // TODO Global Error
    @Test
    @DisplayName("입력 값의 날짜 데이터가 이상한 경우")
    void createEventBadRequestWrong() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                // TODO 날짜 검증
                .beginEnrollmentDateTime(LocalDateTime
                        .of(2021,12, 26, 21,50))
                .closeEnrollmentDateTime(LocalDateTime
                        .of(2021,12, 25,22,30))
                .beginEventDateTime(LocalDateTime
                        .of(2021,12,24,22,55))
                .endEventDateTime(LocalDateTime
                        .of(2021,12,23,20,00))
                // TODO Max가 Base보다 커야 함.
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .build();
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                // TODO 400 응답 처리(잘못된 요청을 보낸 경우)
                .andExpect(status().isBadRequest())
                // TODO Global Error($[0])
                // TODO 인덱스 핸들러($[0] -> content[0])-2
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                // TODO _links.index(content[0])
//                .andExpect(jsonPath("content[0].objectName").exists())
//                .andExpect(jsonPath("content[0].defaultMessage").exists()) // 기본 메시지
//                .andExpect(jsonPath("content[0].code").exists()) // 에러 코드
                // TODO _links.index(content[0])
                .andExpect(jsonPath("_links.index").exists())
        ;
    }
    // TODO 이벤트 전체 목록 조회 API
    //  이벤트 생성
    @Autowired
    EventRepository eventRepository;
    private Event generate(int index) {
        Event event = Event.builder()
                .name("event " + index)
                .description("event test")
                .build();

        return eventRepository.save(event);
    }
    @Test
    @DisplayName("30개의 데이터를 10개씩 두 번째 페이지 조회")
    void queryEvents() throws Exception {
        // TODO Given(이벤트 생성)
        IntStream.range(0, 30).forEach(i -> {
            generate(i);
        });
        // TODO Then(페이지 정렬)
        mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        // TODO 이름의 역순으로
                        .param("sort", "name,desc"))
                .andDo(print())
                .andExpect(status().isOk())
                // TODO 전체 리소스화(PageResourceAssembler<Event>로 Page 안에 있는 Data, 리소스화하기)
                .andExpect(jsonPath("_links").exists())
                // TODO 개별 리소스화
                .andExpect(jsonPath("_embedded.eventList[0]._links").exists())
                // TODO 문서화(profile)
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }
    // TODO 이벤트 개별 조회 API-1
    // TODO 정상적인 값 세팅하기
    private Event generatesEvent(int index) {
        Event event = Event.builder()
                .name("event " + index)
                .description("test event")
                .location("강남역 D2 스타일 펙토리")
                .beginEnrollmentDateTime(LocalDateTime.of(2021,12, 23, 21,50))
                .closeEnrollmentDateTime(LocalDateTime.of(2021,12, 24,22,30))
                .beginEventDateTime(LocalDateTime.of(2021,12,25,22,55))
                .endEventDateTime(LocalDateTime.of(2021,12,26,20,00))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        return eventRepository.save(event);
    }
    // TODO Event 개별 조회 API-2
    @Test
    @DisplayName("기본 이벤트에서 하나 조회하기")
    void getEvent() throws Exception {
        // TODO Given
        Event event = generatesEvent(100);

        mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                // TODO 200 응답 확인(응답을 성공적으로 처리함)
                .andExpect(status().isOk())
                // TODO Event Data에 _links.self 담기
                .andExpect(jsonPath("_links.self").exists())
                // TODO 문서화
                .andExpect(jsonPath("_links.profile").exists())
        ;
    }
    // TODO Event 개별 조회 API-3
    @Test
    @DisplayName("조회 데이터가 없는 경우")
    void getEvent_404() throws Exception {
        // TODO When & Then
        mockMvc.perform(get("/api/events/1231234"))
                .andDo(print())
                // TODO 404 응답확인(요청한 리소스가 없음)
                .andExpect(status().isNotFound())
        ;
    }
    // TODO 이벤트 수정 API-1
    @Autowired
    ModelMapper modelMapper;
    @Test
    @DisplayName("정상적으로 수정한 경우")
    void updateEvent() throws Exception {
        // TODO Given(이벤트 생성)
        Event event = generatesEvent(100);

        // TODO 이벤트 수정
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                // TODO 200 응답 받기(요청을 성공적으로 처리함)
                .andExpect(status().isOk())
                // TODO 수정한 이름이 존재하는지
                .andExpect(jsonPath("name").value(eventName))
                // TODO 리소스화(_Links.self)
                .andExpect(jsonPath("_links.self").exists())
                // TODO 문서화
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("events-update"))
        ;
    }
    // TODO 이벤트 수정 API-2
    @Test
    @DisplayName("수정한 입력값이 없을 때")
    void updateEventEmptyFieldError() throws Exception {
        // TODO Given(이벤트 생성)
        Event event = generatesEvent(100);

        // TODO 수정한 입력값이 없을 때
        EventDto eventDto = new EventDto();

        // TODO When & Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                // TODO 400 응답확인(잘못된 요청을 보낸 경우)
                .andExpect(status().isBadRequest())
        ;
    }
    // TODO 이벤트 수정 API-3
    @Test
    @DisplayName("수정한 이벤트 값이 이상한 경우")
    void updateEventWrongGlobalError() throws Exception {
        // TODO Given(이벤트 생성)
        Event event = generatesEvent(200); // setName = event + index

        // TODO 이벤트를 수정할 DTO
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        // TODO When & Then
        mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                // TODO 400 응답확인(잘못된 요청을 보낸 경우)
                .andExpect(status().isBadRequest())
        ;
    }
    // TODO 이벤트 수정 API-4
    @Test
    @DisplayName("존재하지 않을 이벤트일 경우")
    void updateEventWrong_404() throws Exception {
        // TODO Given(이벤트 생성)
        Event event = generatesEvent(100);

        // TODO 존재하지 않을 이벤트
        EventDto eventDto = modelMapper.map(event, EventDto.class);

        // TODO When & Then
        mockMvc.perform(put("/api/events/12312412")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                // TODO 404 응답확인(요청한 리소스가 없음)
                .andExpect(status().isNotFound())
        ;
    }
}