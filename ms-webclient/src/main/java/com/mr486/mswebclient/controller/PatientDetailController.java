package com.mr486.mswebclient.controller;

import com.mr486.mswebclient.dto.Patient;
import com.mr486.mswebclient.dto.PatientUpdateForm;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/app")
@Slf4j
public class PatientDetailController {

  private final RestTemplate restTemplate;
  private final String gatewayBase;

  public PatientDetailController(RestTemplate restTemplate,
                                 @Value("${app.gateway.base-url}") String gatewayBase) {
    this.restTemplate = restTemplate;
    this.gatewayBase = gatewayBase;
  }

  @GetMapping("/dashboard/{id}")
  public String dashboard(@PathVariable Long id, Model model) {
    ResponseEntity<Patient> response = restTemplate.exchange(
            gatewayBase + "/ms-patients/patients/" + id,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {
            }
    );
    Patient patient = response.getBody();
    model.addAttribute("patient", patient);
    return "patient-detail";
  }

  @GetMapping("/dashboard/{id}/update")
  public String updatePatientForm(@PathVariable Long id, Model model) {
    ResponseEntity<Patient> response = restTemplate.exchange(
            gatewayBase + "/ms-patients/patients/" + id,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {
            }
    );
    Patient patient = response.getBody();
    model.addAttribute("patient", patient);
    return "patient-update";
  }

  @PostMapping("/dashboard/{id}/update")
  public String updatePatient(@PathVariable Long id, @Valid @ModelAttribute PatientUpdateForm patient) {
    HttpEntity<PatientUpdateForm> requestEntity = new HttpEntity<>(patient);

    ResponseEntity<Patient> scoreResponse = restTemplate.exchange(
            gatewayBase + "/ms-patients/patients/" + id,
            HttpMethod.PUT,
            requestEntity,
            new ParameterizedTypeReference<>() {
            }
    );
    return "redirect:/app/dashboard/" + id;
  }
}
