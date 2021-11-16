package me.ssu.spring_rest_api.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.ssu.spring_rest_api.common.TestDescrption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    // TODO Spring Boot는 자동으로 가능함.
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("정상적으로 이벤트를 생성하는 테스트")
    void createEvent() throws Exception {
        EventDto event = EventDto.builder()
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
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                // TODO 201 상태인지 확인
                .andExpect(status().isCreated())
                // TODO id가 있는지 확인
                .andExpect(jsonPath("id").exists())
                // TODO Header 정보 Test(Type Safe Version)
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                // TODO 입력값 제한하기
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                // TODO HATEOAS 적용하기
                .andExpect(jsonPath("_link.self").exists())
//                .andExpect(jsonPath("_link.profile").exists())
                .andExpect(jsonPath("_link.query-events").exists())
                .andExpect(jsonPath("_link.update-event").exists())
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
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
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
                .andExpect(status().isBadRequest())
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("$[0].rejectValue").exists())
        ;
    }
}