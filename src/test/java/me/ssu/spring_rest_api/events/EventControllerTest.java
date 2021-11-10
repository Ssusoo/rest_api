package me.ssu.spring_rest_api.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    // Spring Boot는 자동으로 가능함.
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createEvent() throws Exception {
        Event event = Event.builder()
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
                .location("강남역 D2 스타일 펙토리")
                .build();
        mockMvc.perform(post("/api/events")
                    // TODO Location 헤더에 생성된 이벤트 조회할 수 있는 URI 확인
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                // TODO id가 있는지 확인
                .andExpect(jsonPath("id").exists());
    }
}





