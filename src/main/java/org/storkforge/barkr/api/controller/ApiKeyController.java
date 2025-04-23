package org.storkforge.barkr.api.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.storkforge.barkr.domain.IssuedApiKeyService;
import org.storkforge.barkr.dto.apiKeyDto.GenerateApiKeyRequest;
import org.storkforge.barkr.dto.apiKeyDto.ResponseApiKeyOnce;
import org.storkforge.barkr.dto.apiKeyDto.UpdateApiKey;
import org.storkforge.barkr.mapper.ApiKeyMapper;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


@Controller
@RequestMapping("/apikeys")
public class ApiKeyController {
    private final IssuedApiKeyService issuedApiKeyService;

    public ApiKeyController(IssuedApiKeyService issuedApiKeyService) {
        this.issuedApiKeyService = issuedApiKeyService;
    }

    @GetMapping("/apikeyform")
    @PreAuthorize("hasRole('USER')")
    public String showApiKeyForm(Model model) {
        String now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        model.addAttribute("now", now);
        return "apikeys";
    }


    @GetMapping("/result")
    @PreAuthorize("hasRole('USER')")
    public String showGeneratedApiKey(@ModelAttribute("response") ResponseApiKeyOnce response, Model model) {
        if (response == null || response.value() == null) {
            return "redirect:/apikeys/apikeyform";
        }

        model.addAttribute("response", response);
        return "apikeys/result";
    }


    @PostMapping("/generate")
    @PreAuthorize("hasRole('USER')")
    public String generateApiKey(
            @RequestParam String apiKeyName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME
            ) LocalDateTime expiresAt,
            RedirectAttributes redirectAttributes
            ) throws NoSuchAlgorithmException, InvalidKeyException {
        GenerateApiKeyRequest inputRequest = new GenerateApiKeyRequest(apiKeyName, expiresAt);
        GenerateApiKeyRequest request = ApiKeyMapper.normalizeExpiresAt(inputRequest);

        String apiKey;
        String hashedApiKey;

        do{
        apiKey = issuedApiKeyService.generateRawApiKey();
        hashedApiKey = issuedApiKeyService.hashedApiKey(apiKey);
        } while (issuedApiKeyService.apiKeyExists(hashedApiKey));
        issuedApiKeyService.apiKeyGenerate(request, hashedApiKey);

        ResponseApiKeyOnce response = new ResponseApiKeyOnce(
                "API-KEY", apiKey, "Please store these credentials safely");

        redirectAttributes.addFlashAttribute("response", response);

        return "redirect:/apikeys/result";


    }

    @GetMapping("/mykeys")
    public String myKeys(Model model , @AuthenticationPrincipal OidcUser user) {
        var currentUser = issuedApiKeyService.findByGoogleOidc2Id(user.getName());
        var keys = issuedApiKeyService.allApiKeys(user.getName());
        model.addAttribute("account", currentUser.get().getAccount());
        model.addAttribute("keys", keys.apiKeys());
        return "apikeys/mykeys";

    }

    @PostMapping("/mykeys/revoke")
    public String revokeKey(@RequestParam String referenceId, @AuthenticationPrincipal OidcUser user) {
        if (issuedApiKeyService.isValidUuid(UUID.fromString(referenceId), user.getName())) {
        var update = new UpdateApiKey(UUID.fromString(referenceId),null, true, null);
        issuedApiKeyService.updateApiKey(update);
        }
        return "redirect:/apikeys/mykeys";
    }

    @PostMapping("mykeys/nameupdate")
    public String nameUpdate(@RequestParam String apiKeyName, @RequestParam String referenceId, @AuthenticationPrincipal OidcUser user) {
        if(issuedApiKeyService.isValidUuid(UUID.fromString(referenceId), user.getName())) {
        var update = new UpdateApiKey(UUID.fromString(referenceId), apiKeyName, false, null);
        issuedApiKeyService.updateApiKey(update);
        }
        return "redirect:/apikeys/mykeys";
    }

}
