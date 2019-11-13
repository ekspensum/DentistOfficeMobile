package pl.dentistoffice.mobile.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter @Setter
public class WorkingWeekMapWrapper {

	Map<LocalDate, Map<LocalTime, Boolean>> workingWeekFreeTimeMap;
	
}
