package edu.ncsu.csc.itrust2.controllers.api;
	
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.services.UserService;
import edu.ncsu.csc.itrust2.forms.EHRForm;
import edu.ncsu.csc.itrust2.models.User;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.Diagnosis;
import edu.ncsu.csc.itrust2.models.EHR;
import edu.ncsu.csc.itrust2.models.Prescription;
import edu.ncsu.csc.itrust2.models.Patient;
import edu.ncsu.csc.itrust2.services.EHRService;
import edu.ncsu.csc.itrust2.services.PrescriptionService;
import edu.ncsu.csc.itrust2.services.DiagnosisService;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APIEHRController extends APIController {
	
	@Autowired
    private LoggerUtil loggerUtil;
	
	@Autowired
	private EHRService service;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	public APIEHRController(EHRService EHRService) {
		this.service = EHRService;
	}
	
	/**
	 * Retrieves Emergency Health Records for a patient based on the information provided in the EHRForm.
	 * The EHR includes the patient's name, age, date of birth, gender, blood type, a list of short-term diagnoses
	 * within the last 60 days (sorted by most recent first), and a list of prescriptions received in the last
	 * 90 days (sorted by most recent first).
	 *
	 * @param EHRForm The form containing the information to identify the patient (name, last name, or id)
	 * @return A list of Emergency Health Records for the selected patient, or null if no matching records are found.
	 */
	@GetMapping ( BASE_PATH + "/viewHCP" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
	public List<EHR> findByNameid(@RequestBody EHRForm EHRForm) {
		if ( null != service.findByFirstName(EHRForm) ) {
			return service.findByFirstName(EHRForm);
		}
		else if ( null != service.findByLastName(EHRForm) ) {
			return service.findByLastName(EHRForm);
		}
		else if ( null != service.findByid(EHRForm) ) {
			return service.findByid(EHRForm);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Retrieves Emergency Health Records for a specific patient identified by their unique ID.
	 * The Emergency Health Records include the patient's name, age, date of birth, gender, blood type
	 *
	 * @param id The unique identifier of the patient
	 * @return Emergency Health Records for the specified patient
	 */
	@GetMapping ( BASE_PATH + "/viewHCP/{id}")
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
	public EHR getPatient(@PathVariable Long id) {
		return service.getPatient(id);
	}
	
	/**
	 * Retrieves a list of short-term diagnoses for a specific patient identified by their unique ID.
	 * Diagnoses are sorted by most recent first.
	 *
	 * @param id The unique identifier of the patient
	 * @return A list of short-term diagnoses for the specified patient
	 */
	@GetMapping ( BASE_PATH + "/viewHCP/{id}/diagnoses")
	@PreAuthorize ( "hasRole('ROLE_HCP')" )
	public List<Diagnosis> getPatientDiagnosis(@PathVariable Long id) {
		return service.getPatientDiagnosis(id);
	}
	
	/**
	 * Retrieves a list of prescriptions for a specific patient identified by their unique ID.
	 * Prescriptions are sorted by most recent first.
	 *
	 * @param id The unique identifier of the patient
	 * @return A list of prescriptions for the specified patient
	 */
	@GetMapping ( BASE_PATH + "/viewHCP/{id}/prescriptions")
	@PreAuthorize ( "hasRole('ROLE_HCP')" )
	public List<Prescription> getPatientPrescriptions(@PathVariable Long id) {
		return service.getPatientPrescriptions(id);
	}

	/**
	 * HCP Retrieves Emergency Health Records for a patient based on the information provided in the EHRForm.
	 * The EHR includes the patient's name, age, date of birth, gender, blood type, a list of short-term diagnoses
	 * within the last 60 days (sorted by most recent first), and a list of prescriptions received in the last
	 * 90 days (sorted by most recent first).
	 *
	 * @param Patient containing the information to identify the patient (name, last name, or id)
	 * @return Response which include Emergency Health Records for the selected patient.
	 */
	@GetMapping ( BASE_PATH + "/viewHCP/{id}/ehr" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
	public ResponseEntity HCPViewEHR(@RequestBody Patient patient) {
		DiagnosisService diagnosisService = new DiagnosisService();
		PrescriptionService prescriptionService = new PrescriptionService();

		List <Diagnosis> PatientDiagnosis = (List<Diagnosis>) diagnosisService.findByPatient(patient);
		List <Prescription> PatientPrescriptions = (List<Prescription>) prescriptionService.findByPatient(patient);

		List EHR = new ArrayList();
		EHR.add(patient.getFirstName());
		EHR.add(patient.getLastName());
		int Age = Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears();
		EHR.add(Age);
		EHR.add(patient.getDateOfBirth());
		EHR.add(patient.getGender());
		EHR.add(patient.getBloodType());
		EHR.add(PatientDiagnosis);
		EHR.add(PatientPrescriptions);

		loggerUtil.log( TransactionType.HCP_VIEW_ER, LoggerUtil.currentUser(), patient.getFirstName() + patient.getLastName(),
            "HCP views EHR of patient " + patient.getFirstName() + patient.getLastName() );
		return new ResponseEntity( EHR, HttpStatus.OK );
	}

	/**
	 * ER Retrieves Emergency Health Records for a patient based on the information provided in the EHRForm.
	 * The EHR includes the patient's name, age, date of birth, gender, blood type, a list of short-term diagnoses
	 * within the last 60 days (sorted by most recent first), and a list of prescriptions received in the last
	 * 90 days (sorted by most recent first).
	 *
	 * @param Patient containing the information to identify the patient (name, last name, or id)
	 * @return Response which include Emergency Health Records for the selected patient.
	 */
	@GetMapping ( BASE_PATH + "/viewER/{id}/ehr" )
    @PreAuthorize ( "hasAnyRole('ROLE_ER')" )
	public ResponseEntity ERViewEHR(@RequestBody Patient patient) {
		DiagnosisService diagnosisService = new DiagnosisService();
		PrescriptionService prescriptionService = new PrescriptionService();

		List <Diagnosis> PatientDiagnosis = (List<Diagnosis>) diagnosisService.findByPatientForEHR(patient);
		List <Prescription> PatientPrescriptions = (List<Prescription>) prescriptionService.findByPatientForEHR(patient);

		List EHR = new ArrayList();
		EHR.add(patient.getFirstName());
		EHR.add(patient.getLastName());
		int Age = Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears();
		EHR.add(Age);
		EHR.add(patient.getDateOfBirth());
		EHR.add(patient.getGender());
		EHR.add(patient.getBloodType());
		EHR.add(PatientDiagnosis);
		EHR.add(PatientPrescriptions);

		loggerUtil.log( TransactionType.ER_VIEW_ER, LoggerUtil.currentUser(), patient.getFirstName() + patient.getLastName(),
            "HCP views EHR of patient " + patient.getFirstName() + patient.getLastName() );
		return new ResponseEntity( EHR, HttpStatus.OK );
	}
	
}
