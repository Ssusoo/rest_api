package me.ssu.spring_rest_api.events;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor @AllArgsConstructor
@Setter @Getter @EqualsAndHashCode(of = "id")
@Entity
public class Event {

    @Id @GeneratedValue
    private Integer id;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;

    // TODO 입력제한 Dto
    private String name; // 이벤트 이름
    private String description; // 설명
    private LocalDateTime beginEnrollmentDateTime; // 등록 시작일시
    private LocalDateTime closeEnrollmentDateTime; // 종료일시
    private LocalDateTime beginEventDateTime; // 이벤트 시작일시
    private LocalDateTime endEventDateTime; // 이벤트 종료일시
    private String location; // 이벤트 위치(optional) 이게 없으면 온라인 모임
    private int basePrice; // 기본 금액(optional)
    private int maxPrice; // 최고 금액(optional)
    private int limitOfEnrollment; // 등록 한도

    private boolean offline; // 온/오프라인
    private boolean free; // 무료/유료

    public void update() {
        // TODO freeOrNotFree
        if (this.basePrice == 0 && this.maxPrice == 0) {
            this.free = true;
        } else {
            this.free = false;
        }
        // TODO offlineOrOnline
        if (this.location == null ||
                this.location.isBlank()) {
            this.offline = false;
        } else {
            this.offline = true;
        }
    }
}