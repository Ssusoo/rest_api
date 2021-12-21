package me.ssu.springrestapi.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.ssu.springrestapi.accounts.AccountRepository;
import me.ssu.springrestapi.accounts.AccountService;
import me.ssu.springrestapi.events.EventRepository;
import org.junit.jupiter.api.Disabled;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@Disabled
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfigurationTest.class)
@ActiveProfiles("test")
public class BaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    protected EventRepository eventRepository;

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected AccountService accountService;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected AppProperties appProperties;
}