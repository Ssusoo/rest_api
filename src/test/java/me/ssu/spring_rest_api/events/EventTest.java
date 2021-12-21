package me.ssu.spring_rest_api.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EventTest {

    private static Stream<Arguments> isFree() {
        return Stream.of(
                Arguments.of(0, 0, true),
                Arguments.of(100, 0, false),
                Arguments.of(0, 100, false),
                Arguments.of(100, 200, false)
        );
    }

    @ParameterizedTest
    @DisplayName("무료/유료 검증")
    @MethodSource("isFree")
    void offlineOrOnline(int basePrice, int maxPrice, boolean isFree) {

        // TODO Given, 이벤트 생성
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        // TODO When, 수정
        event.update();

        // TODO When
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    private static Stream<Arguments> isOffline() {
        return Stream.of(
                Arguments.of("강남역", true),
                Arguments.of(null, false),
                Arguments.of(" ", false)
        );
    }

    @ParameterizedTest
    @DisplayName("오프라인/온라인")
    @MethodSource("isOffline")
    void isOfflineOrOnline(String location, boolean isOffline) {

        // TODO Given, 이벤트 생성
        Event event = Event.builder()
                .location(location)
                .build();

        // TODO When, 수정
        event.update();

        // TODO Then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }
}