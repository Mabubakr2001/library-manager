package dev.bakr.library_manager.service;

import dev.bakr.library_manager.exceptions.ExistsException;
import dev.bakr.library_manager.exceptions.InvalidException;
import dev.bakr.library_manager.exceptions.NotFoundException;
import dev.bakr.library_manager.mappers.ReaderMapper;
import dev.bakr.library_manager.model.Reader;
import dev.bakr.library_manager.repository.ReaderRepository;
import dev.bakr.library_manager.requestDtos.LoginReaderDtoRequest;
import dev.bakr.library_manager.requestDtos.RegisterReaderDtoRequest;
import dev.bakr.library_manager.requestDtos.VerifyReaderDtoRequest;
import dev.bakr.library_manager.responses.LoginReaderDtoResponse;
import dev.bakr.library_manager.responses.RegisterReaderDtoResponse;
import dev.bakr.library_manager.utils.GenerateVerificationCode;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReaderService {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    private static final Logger logger = LoggerFactory.getLogger(ReaderService.class);
    private final AuthenticationManager authenticationManager;
    private final ReaderRepository readerRepository;
    private final ReaderMapper readerMapper;
    private final EmailService emailService;
    private final JwtService jwtService;

    public ReaderService(AuthenticationManager authenticationManager,
            ReaderRepository readerRepository,
            ReaderMapper readerMapper,
            EmailService emailService,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.readerRepository = readerRepository;
        this.readerMapper = readerMapper;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    public RegisterReaderDtoResponse registerReader(RegisterReaderDtoRequest registerReaderDtoRequest) {
        var readerUsername = registerReaderDtoRequest.username();
        Boolean isReaderExists = readerRepository.existsByUsername(readerUsername);

        if (isReaderExists) {
            throw new ExistsException("You are trying to register a user who is already registered");
        }

        var newReaderEntity = readerMapper.toEntity(registerReaderDtoRequest);

        newReaderEntity.setPassword(encoder.encode(registerReaderDtoRequest.password()));
        newReaderEntity.setVerificationCode(GenerateVerificationCode.generateCode());
        newReaderEntity.setVerificationExpiration(LocalDateTime.now().plusMinutes(10));
        newReaderEntity.setIsEnabled(false);

        readerRepository.save(newReaderEntity);

        sendOTAC(newReaderEntity);

        return readerMapper.toDto(newReaderEntity);
    }

    public String verifyReader(VerifyReaderDtoRequest verifyReaderDtoRequest) {
        var readerEmail = verifyReaderDtoRequest.email();
        Reader neededReader = readerRepository.findByEmail(readerEmail);

        if (neededReader == null) {
            throw new NotFoundException("You are trying to verify a user who doesn't even exist!");
        }

        if (neededReader.getIsEnabled()) {
            throw new ExistsException("You are trying to verify a user who is already verified!");
        }

        if (neededReader.getVerificationExpiration().isBefore(LocalDateTime.now())) {
            throw new InvalidException("The verification code has expired!");
        }

        if (!neededReader.getVerificationCode().equals(verifyReaderDtoRequest.verificationCode())) {
            throw new InvalidException("Invalid verification code!");
        }

        neededReader.setIsEnabled(true);
        neededReader.setVerificationCode(null);
        neededReader.setVerificationExpiration(null);

        readerRepository.save(neededReader);

        return "User verified successfully! Login to receive your JWT token...";
    }

    public LoginReaderDtoResponse loginReader(LoginReaderDtoRequest loginReaderDtoRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginReaderDtoRequest.username(),
                loginReaderDtoRequest.password()
        ));

        if (!authentication.isAuthenticated()) {
            return null;
        }

        String jwtToken = jwtService.generateToken(loginReaderDtoRequest);
        Long jwtExpirationMs = jwtService.getExpirationTime();
        String jwtExpirationText = "In " + String.valueOf(jwtService.getExpirationTime() / (1000 * 60 * 60)) + " Hours";


        return new LoginReaderDtoResponse(jwtToken, jwtExpirationMs, jwtExpirationText);
    }

    public void sendOTAC(Reader reader) {
        String verificationCode = reader.getVerificationCode();
        try {
            emailService.sendVerificationEmail(reader.getEmail(), verificationCode);
        } catch (MessagingException e) {
            logger.error("Error sending the email while signing up! {}", e.getMessage());
        }
    }

    public String reSendOTAC(String email) {
        Reader neededReader = readerRepository.findByEmail(email);
        if (neededReader == null) {
            throw new NotFoundException("You are trying to resend the OTAC to a user who doesn't even exist!");
        }
        if (neededReader.getIsEnabled()) {
            throw new InvalidException("You are trying to resend the OTAC to a user who is already verified!");
        }

        String newVerificationOTAC = GenerateVerificationCode.generateCode();

        neededReader.setVerificationCode(newVerificationOTAC);
        neededReader.setVerificationExpiration(LocalDateTime.now().plusMinutes(10));

        readerRepository.save(neededReader);

        try {
            emailService.sendVerificationEmail(neededReader.getEmail(), newVerificationOTAC);
        } catch (MessagingException e) {
            logger.error("Error resending email while verifying! {}", e.getMessage());
        }

        return "The verification code was successfully resent!";
    }
}
