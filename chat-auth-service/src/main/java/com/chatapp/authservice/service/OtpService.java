package com.chatapp.authservice.service;

import com.chatapp.authservice.model.Otp;
import com.chatapp.authservice.model.User;
import com.chatapp.authservice.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Value("${chatapp.app.otpExpirationMs}")
    private int otpExpirationMs;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void createAndSendOtp(User user) {
        otpRepository.findByUser(user).ifPresent(otpRepository::delete);

        String otp = generateOtp();

        Otp otpEntity = new Otp();
        otpEntity.setOtp(otp);
        otpEntity.setExpirationTime(Instant.now().plusMillis(otpExpirationMs));
        otpEntity.setUser(user);

        otpRepository.save(otpEntity);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    public boolean verifyOtp(User user, String otp) {
        Optional<Otp> otpOptional = otpRepository.findByUser(user);
        if (otpOptional.isPresent()) {
            Otp otpEntity = otpOptional.get();
            if (otpEntity.getOtp().equals(otp) && otpEntity.getExpirationTime().isAfter(Instant.now())) {
                otpRepository.delete(otpEntity);
                return true;
            }
        }
        return false;
    }
}
