package me.ssu.spring_rest_api.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors) {
        // TODO 무제한경매 (basePrice(100)이고 maxPrice(0)일 때 적용
        // TODO 아래의 경우가 잘못된 거임.
        if (eventDto.getBasePrice() > eventDto.getMaxPrice()
            && eventDto.getMaxPrice() != 0) {
            errors.rejectValue("basePrice", "wrongValue",
                    "BasePrice is wrong.");
            errors.rejectValue("maxPrice", "wrongValue",
                    "MaxPrice is wrong.");
        }

        // TODO 날짜 검증
        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
        endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
        endEventDateTime.isBefore(eventDto.getBeginEventDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue",
                    "EndEventDateTime is wrong.");
        }

        // TODO BeginEventDateTime
        // TODO CloseEnrollmentDateTime
    }
}
