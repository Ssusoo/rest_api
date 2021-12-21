package me.ssu.springrestapi.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class EventDto {

    // TODO 입력제한 Dto
    // TODO Filed Error
    @NotEmpty
    private String name; // 이벤트 이름

    @NotEmpty
    private String description; // 설명

    @NotNull
    private LocalDateTime beginEnrollmentDateTime; // 등록 시작일시

    @NotNull
    private LocalDateTime closeEnrollmentDateTime; // 종료일시

    @NotNull
    private LocalDateTime beginEventDateTime; // 이벤트 시작일시

    @NotNull
    private LocalDateTime endEventDateTime; // 이벤트 종료일시

    private String location; // 이벤트 위치(optional) 이게 없으면 온라인 모임

    @Min(0)
    private int basePrice; // 기본 금액(optional)

    @Min(0)
    private int maxPrice; // 최고 금액(optional)

    @Min(0)
    private int limitOfEnrollment; // 등록 한도
}