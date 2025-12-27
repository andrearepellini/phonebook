package andrearepellini.phonebook.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Hello world", description = "Hello world test API")
public class HelloWorldController {

    @GetMapping("/helloworld")
    public String helloWorld() {
        return "Hello world!";
    }

}
