package com.example.demo.controller;

import com.example.demo.entity.Patient;
import com.example.demo.service.DoctorService;
import com.example.demo.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/patients")
public class PatientController {
    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @PostMapping
    public Patient addPatient(@RequestBody Patient patient) {
        return patientService.addPatient(patient);
    }

    @DeleteMapping("/{id}")
    public void removePatient(@PathVariable Long id) {
        patientService.removePatient(id);
    }

    @GetMapping("/suggest-doctor/{id}")
    public String suggestDoctor(@PathVariable Long id) {
        Optional<Patient> patientOpt = patientService.getPatientById(id);
        if (patientOpt.isEmpty()) {
            return "Patient not found";
        }
        Patient patient = patientOpt.get();
        String city = patient.getCity();
        String symptom = patient.getSymptom();
        String speciality = getSpecialityBySymptom(symptom);

        if (!city.equals("Delhi") && !city.equals("Noida") && !city.equals("Faridabad")) {
            return "We are still waiting to expand to your location";
        }

        List<Doctor> doctors = doctorService.suggestDoctors(city, speciality);
        if (doctors.isEmpty()) {
            return "There isn’t any doctor present at your location for your symptom";
        }

        return doctors.toString();
    }

    private String getSpecialityBySymptom(String symptom) {
        switch (symptom) {
            case "Arthritis":
            case "Back Pain":
            case "Tissue injuries":
                return "Orthopaedic";
            case "Dysmenorrhea":
                return "Gynecology";
            case "Skin infection":
            case "skin burn":
                return "Dermatology";
            case "Ear pain":
                return "ENT";
            default:
                throw new IllegalArgumentException("Invalid symptom: " + symptom);
        }
    }
        }
