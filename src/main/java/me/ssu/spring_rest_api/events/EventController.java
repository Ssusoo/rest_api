package me.ssu.spring_rest_api.events;

import me.ssu.spring_rest_api.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    // TODO Repository 등록
    private final EventRepository eventRepository;

    // TODO Model Mapper 입력값 제한하기
    private final ModelMapper modelMapper;

    // TODO Global Error
    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper,
                           EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    // TODO new ErrorsResource 리팩토링
    private ResponseEntity<ErrorsResource> badRequests(Errors errors) {
        // TODO 에러를 그냥 error로 던지는 게 아니라 ErrorsResource로 바꿔서 던짐.
        // TODO errors -> new ErrorsResource(errors)
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    @PostMapping
    // TODO Event -> EventDto
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto,
                                      Errors errors) {

        // TODO Field Error
        // TODO .build - > .body(errors), JavaBean 준수 객체가 아님.
        if (errors.hasErrors()) {
            // return ResponseEntity.badRequest().body(errors);
            // TODO Bad_Request 리팩토링
            return badRequests(errors);
        }

        // TODO Global Error
        // TODO .build - > .body(errors), JavaBean 준수 객체가 아님.
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            // return ResponseEntity.badRequest().body(errors);
            // TODO Bad_Request 리팩토링
            return badRequests(errors);
        }

        // TODO 입력값 제한하기 Dto
        // TODO Event Dto에 있는 것을 Event 타입의 인스턴스로 만들어 달라
        Event event = modelMapper.map(eventDto, Event.class);

        // TODO ModelMapper 사용 전, 코드
//        Event event = Event.builder()
//                .name(eventDto.getName())
//                .build();

        // TODO 저장하기 전에 유료인지 무료인지 여부 업데이트(비즈니스 로직 적용)
        event.update();

        // TODO Repository
        // TODO Event -> EventDto
        Event newEvent = this.eventRepository.save(event);

        // TODO HATEOAS 적용-1
        // TODO ControllerLinkBuilder(2.1.0.RELEASE) -> WebMvcLinkBuilder(2.2.5.RELEASE)
        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class)
                .slash(newEvent.getId());
        URI createUri = selfLinkBuilder
                .toUri();

        // TODO HATEOAS 적용-2
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        // TODO _links.self, EventResource로 이동
//        eventResource.add(selfLinkBuilder.withSelfRel());

        // TODO REST Docs(profile) 추가
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));

        return ResponseEntity.created(createUri).body(eventResource);
    }

    // TODO Event 전체 조회 API
    @GetMapping
    public ResponseEntity queryEvent(Pageable pageable,
                                     PagedResourcesAssembler<Event> assembler) {
        // TODO 리소스로 변경하기 전(Event Data)
        Page<Event> page = eventRepository.findAll(pageable);

        // TODO Repository에서 받아온 Event를 리소스로 변경하기 위해 PagedResourcesAssembler<T> 사용함.
        // TODO PagedResources<Resource<Event>>(2.1.0.RELEASE) -> PagedModel<EntityModel<Event>>(2.2.5.RELEASE)
        // TODO toResource(2.1.0.RELEASE) -> toModel(2.2.5.RELEASE)
//        PagedResources<Resource<Event>> pagedResources = assembler.toResource(page);

        // TODO 리소스(toResource(page) -> 각각의 리소스로 변경(toResource(page, e -> new EventResource(e))
        var pagedResources = assembler
                .toResource(page, entity -> new EventResource(entity));

        // TODO profile 추가
        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));

        return ResponseEntity.ok(pagedResources);
    }

    // TODO 이벤트 개별 조회 API
    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id) {

        // TODO Event Data
        Optional<Event> optionalEvent = eventRepository.findById(id);

        // TODO 조회한 데이터가 없는 경우
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // TODO 하나의 데이터 조회
        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);

        // TODO 문서화
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }

    // TODO Events 수정 API
    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors) {

        Optional<Event> optionalEvent = eventRepository.findById(id);

        // TODO 입력값이 비어있는 경우
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // TODO 일반적인 값 Bad_Request
        if (errors.hasErrors()) {
            return badRequests(errors);
        }

        // TODO 특정한 값 검증 Bad_Request
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequests(errors);
        }

        // TODO Events 수정 API
        Event existingEvent = optionalEvent.get();
        modelMapper.map(eventDto, existingEvent);
        Event savedEvent = eventRepository.save(existingEvent);

        // TODO 리소스
        EventResource eventResource = new EventResource(savedEvent);

        // TODO profile 링크 추가
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }
}
