package me.ssu.spring_rest_api.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.ssu.spring_rest_api.common.RestDocsConfiguration;
import me.ssu.spring_rest_api.common.TestDescrption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @DisplayName("정상적으로 이벤트를 생성하는 테스트")
    void createEvent() throws Exception {

        // TODO Event -> EventDto(입력값 제한하기)
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

        mockMvc.perform(post("/api/events")
                    // TODO Location 헤더에 생성된 이벤트 조회할 수 있는 URI 확인
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON_UTF8_VALUE)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                // TODO 201 입력값 상태 확인(isCreated())
                .andExpect(status().isCreated())
                // TODO id가 있는지 확인
                .andExpect(jsonPath("id").exists())
                // TODO Header 정보 Test(Type Safe Version)
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
                // TODO 저장하기 전에 유료인지 무료인지 여부 업데이트(비즈니스 로직 적용)
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                // TODO RestDocs create-event 추가
                // TODO link, Req, Res 필드와 헤더 문서화
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
                // TODO 201 입력값 상태 확인(isCreated())
                // TODO 입력값이 제대로 들어온 경우
                // TODO isCreated -> badRequest 수정
                .andExpect(status().isBadRequest())
       ;
    }

    @Test
    @DisplayName("입력 데이터가 없는 경우 Bad_Request 처리하기")
    void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                // TODO Bad_Request
                .andExpect(status().isBadRequest())
                // TODO 인덱스로 가는 링크 제공
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @DisplayName("입력 값의 날짜 데이터가 이상한 경우 Bad_Request 처리하기")
    void createEvent_Bad_Request_Wrong_Input() throws Exception {
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
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                // TODO Bad_Request
                .andExpect(status().isBadRequest())
                .andExpect(status().isBadRequest())
                // TODO Bad_Request
                //  응답처리(Body Message, $[0](Error)) 담기
                // TODO 인덱스로 가는 링크 제공-2
                //  Unwrap 수정(Json에는 Unwrap가 적용 안 됨)
                //  $[0](Error) -> content[0](ErrorsResource)
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists()) // 기본 메시지
                .andExpect(jsonPath("content[0].code").exists()) // 에러 코드
                // TODO Global Error에는 없기 때문에 에러 발생할 수 있음.
//                .andExpect(jsonPath("$[0].field").exists()) // 어떤 필드에서 발생한 에러인지
//                .andExpect(jsonPath("$[0].rejectValue").exists()) // 입력 거절 받은 값
                // TODO 인덱스로 가는 링크 제공-1
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    // TODO Event 목록 조회 API
    @Test
    @TestDescrption("30개의 이벤트를 10개씩 두 번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        // TODO Given
        IntStream.range(0, 30).forEach(i -> {
            this.generateEvent(i);
        });
        // TODO When & Then
        mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        // TODO 이름 역순으로
                        .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                // TODO 리소스에 하나의 링크 정보가 있는지 확인
                // TODO 완벽한 HATEOAS가 아님(각각의 리소스에 링크가 달려야 함)
                .andExpect(jsonPath("_links").exists())
                // TODO 각각의 리소스에 링크가 정보가 있는지 확인
                .andExpect(jsonPath("_embedded.eventList[0]._links").exists())
                // TODO profile 링크확인
                .andExpect(jsonPath("_links.profile").exists())
                // TODO REST Docs(문서화)
                .andDo(document("query-events"))
        ;
    }
    // TODO 이벤트 30개 만들기
    @Autowired
    EventRepository eventRepository;

    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event" + index)
                .description("test event")
                .build();

        return eventRepository.save(event);
    }

    @Test
    @DisplayName("기존의 이벤트를 하나 조회하기")
    void getEvent() throws Exception {

        // TODO Given
        Event event = generateEvent(100);

        // TODO When & Then
        mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                // TODO REST Docs(문서화)
                .andDo(document("get-an-event"))
        ;
    }

    @Test
    @DisplayName("없는 이벤트를 조회했을 때 404 응답받기")
    void getEvent404() throws Exception {

        // TODO When & Then
        mockMvc.perform(get("/api/events/123123"))
                .andDo(print())
                // TODO 없는 이벤트를 조회할 때
                .andExpect(status().isNotFound())
        ;
    }
}








