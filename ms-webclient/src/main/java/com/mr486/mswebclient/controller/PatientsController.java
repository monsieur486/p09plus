package com.mr486.mswebclient.controller;

import com.mr486.mswebclient.dto.Patient;
import com.mr486.mswebclient.dto.PatientAddForm;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
@RequestMapping("/app")
public class PatientsController {

  private final RestTemplate restTemplate;
  private final String gatewayBase;

  public PatientsController(RestTemplate restTemplate,
                            @Value("${app.gateway.base-url}") String gatewayBase) {
    this.restTemplate = restTemplate;
    this.gatewayBase = gatewayBase;
  }

  @GetMapping("/dashboard")
  public String dashboard(Model model) {
    List<Patient> patients;
    ResponseEntity<List<Patient>> response = restTemplate.exchange(
            gatewayBase + "/ms-patients/patients",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {
            }
    );
    patients = response.getBody();
    model.addAttribute("patients", patients);
    return "patients";
  }

  @GetMapping("/dashboard/ajout")
  public String showCreatePatientForm(Model model) {
    model.addAttribute("patient", new PatientAddForm());
    return "patient-ajout";
  }

  @PostMapping("/dashboard/ajout")
  public String ajoutPatientPost(@Valid @ModelAttribute PatientAddForm patient) {
    HttpEntity<PatientAddForm> requestEntity = new HttpEntity<>(patient);

    ResponseEntity<Patient> scoreResponse = restTemplate.exchange(
            gatewayBase + "/ms-patients/patients",
            HttpMethod.POST,
            requestEntity,
            new ParameterizedTypeReference<>() {
            }
    );
    return "redirect:/app/dashboard";
  }
}
