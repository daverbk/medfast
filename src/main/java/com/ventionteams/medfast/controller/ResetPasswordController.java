package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.request.ResetPasswordRequest;
import com.ventionteams.medfast.service.password.ResetPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reset password")
@RequestMapping("/auth/password")
public class ResetPasswordController {
    private final ResetPasswordService resetPasswordService;

    @Operation(summary = "Reset password")
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        resetPasswordService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok("Password has been reset");
    }
}
