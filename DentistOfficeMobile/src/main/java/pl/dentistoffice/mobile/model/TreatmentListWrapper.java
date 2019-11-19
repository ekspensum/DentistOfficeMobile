package pl.dentistoffice.mobile.model;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter @Setter
public class TreatmentListWrapper {

	private List<TreatmentCategory> treatmentCategoriesList;
	private List<DentalTreatment> dentalTreatmentsList;
}
