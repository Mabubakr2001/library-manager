package dev.bakr.library_manager.controller;

import dev.bakr.library_manager.requestDtos.LoginReaderDtoRequest;
import dev.bakr.library_manager.requestDtos.RegisterReaderDtoRequest;
import dev.bakr.library_manager.requestDtos.VerifyReaderDtoRequest;
import dev.bakr.library_manager.responses.LoginReaderDtoResponse;
import dev.bakr.library_manager.responses.RegisterReaderDtoResponse;
import dev.bakr.library_manager.service.ReaderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public class ReaderController {
    private final ReaderService readerService;

    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @PostMapping("/signup")
    public ResponseEntity<RegisterReaderDtoResponse> registerReader(@Valid @RequestBody RegisterReaderDtoRequest registerReaderDtoRequest) {
        RegisterReaderDtoResponse registeredReader = readerService.registerReader(registerReaderDtoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredReader);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyReader(@Valid @RequestBody VerifyReaderDtoRequest verifyReaderDtoRequest) {
        String verificationMessage = readerService.verifyReader(verifyReaderDtoRequest);
        return ResponseEntity.ok(verificationMessage);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginReader(@Valid @RequestBody LoginReaderDtoRequest loginReaderDtoRequest) {
        LoginReaderDtoResponse loginReaderDtoResponse = readerService.loginReader(loginReaderDtoRequest);
        return ResponseEntity.ok(loginReaderDtoResponse);
    }

    @PostMapping("/resend")
    public ResponseEntity<?> reSendOTAC(@RequestParam String email) {
        String returnedMessage = readerService.reSendOTAC(email);
        return ResponseEntity.ok(returnedMessage);
    }
}
