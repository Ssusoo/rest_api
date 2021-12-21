package me.ssu.springrestapi.events;

import me.ssu.springrestapi.accounts.Account;
import me.ssu.springrestapi.accounts.CurrentUser;
import me.ssu.springrestapi.errors.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
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

    // TODO Index Handler
    public ResponseEntity badRequests(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    // TODO 이벤트 생성 API
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser) {

        // TODO 입력값 제한하기
        Event event = modelMapper.map(eventDto, Event.class);

        // TODO Field Error
        if (errors.hasErrors()) {
            // TODO 인덱스 핸들러
            return badRequests(errors);
//            return ResponseEntity.badRequest().body(errors);
        }

        // TODO Global Error
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            // TODO 인덱스 핸들러
            return badRequests(errors);
//            return ResponseEntity.badRequest().body(errors);
        }

        // TODO 비즈니스 로직 적용
        event.update();

        // TODO 현재 사용자(Manager를 현재 사용자 유저)
        event.setManager(currentUser);

        // TODO Repository
        Event newEvent = eventRepository.save(event);

        // TODO HATEOAS 적용-1
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class)
                .slash(newEvent.getId());

        // TODO Location 헤더 정보
        URI createUri = selfLinkBuilder
                .toUri();

        // TODO HATEOAS 적용-2
        EventResource eventResource = new EventResource(newEvent);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        // TODO _links.self
//        eventResource.add(selfLinkBuilder.withSelfRel());

        // TODO 문서화
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));

        return ResponseEntity.created(createUri).body(eventResource);
    }

    // TODO 이벤트 전체 목록 API
    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable,
                                      PagedResourcesAssembler<Event> assembler,
                                      // TODO 현재 사용자(Spring Security를 바로 주입받을 수 있음)-3
                                      // TODO 현재 사용자-4
                                      // TODO User -> AccountAdapter로 변경
                                      // TODO @CurrentUser(커스텀 어노테이션)
                                      @CurrentUser Account currentUser) {

        // TODO 현재 사용자(인증정보 꺼내기(자바 ThreadLocal 기반))-1
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // TODO 현재 사용자(AccountService에 있는 User)-2
//        User principal = (User) authentication.getPrincipal();

        // TODO 페이징 정렬
        Page<Event> page = eventRepository.findAll(pageable);

        // TODO 전체 리소스화
//        PagedModel<EntityModel<Event>> pagedResources = assembler.toModel(page);

        // TODO 개별 리스소화
        PagedModel<EventResource> pagedResources = assembler
                .toModel(page, entity -> new EventResource(entity));

        // TODO 문서화하기
        pagedResources.add(new Link("/docs/index.html#resources-events-list")
                .withRel("profile"));

        // TODO 현재 사용자(인증한 경우, UserDetails에 접근이 가능함)
        // TODO 있는지 없는지 유무이기 때문에 이런 경우 딱히 유저 정보를 참조할 필요가 없음.
        // TODO Spring Security의 인증정보를 받아도 됨.
        if (currentUser != null) {
            pagedResources.add(linkTo(EventController.class).withRel("create-event"));
        }

        return ResponseEntity.ok(pagedResources);
    }

    // TODO 이벤트 개별 조회 API
    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id,
                                   @CurrentUser Account currentUser) {


        // TODO Optional 검증
        Optional<Event> optionalEvent = eventRepository.findById(id);

        // TODO 데이터가 존재하지 않은 경우
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // TODO 객체에 담기
        Event event = optionalEvent.get();

        // TODO 리소스화 하기
        EventResource eventResource = new EventResource(event);

        // TODO 현재 사용자
        if (event.getManager().equals(currentUser)) {
            eventResource.add(linkTo(EventController.class)
                    .slash(event.getId()).withRel("update-event"));
        }

        // TODO 문서화
        eventResource.add(new Link("/docs/index.html#resources-events-get")
                .withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }

    // TODO 이벤트 수정 조회 API
    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser) {

        // TODO Optional 검증
        Optional<Event> optionalEvent = eventRepository.findById(id);

        // TODO NotFound Error
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // TODO Field Error
        if (errors.hasErrors()) {
            return badRequests(errors);
        }

        // TODO Global Error
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequests(errors);
        }

        // TODO 수정 전
        Event existingEvent = optionalEvent.get();

        // TODO 현재 이벤트의 매니저가 현재 사용자가 아닌 경우
        if (!existingEvent.getManager().equals(currentUser)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        modelMapper.map(eventDto, existingEvent);

        // TODO 수정 후
        Event savedEvent = eventRepository.save(existingEvent);

        // TODO 리소스화하기
        EventResource eventResource = new EventResource(savedEvent);

        // TODO 문서화(profile)
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }
}

