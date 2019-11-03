package pl.dentistoffice.mobile.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

import java.util.Map;

public class WorkingWeek implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	
	byte [] workingWeekMapByte;
	
//	private Map<DayOfWeek, Map<LocalTime, Boolean>> workingWeekMap;
	
	private User userLogged;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte[] getWorkingWeekMapByte() {
		return workingWeekMapByte;
	}

	public void setWorkingWeekMapByte(byte[] workingWeekMapByte) {
		this.workingWeekMapByte = workingWeekMapByte;
	}

	@SuppressWarnings("unchecked")
	public Map<DayOfWeek, Map<LocalTime, Boolean>> getWorkingWeekMap() {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(this.workingWeekMapByte);
		Map<DayOfWeek, Map<LocalTime, Boolean>> workingWeekMap = null;
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			workingWeekMap = (Map<DayOfWeek, Map<LocalTime, Boolean>>) objectInputStream.readObject();
			inputStream.close();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return workingWeekMap;
	}

	public void setWorkingWeekMap(Map<DayOfWeek, Map<LocalTime, Boolean>> workingWeekMap) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream() ;
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
			outputStream.writeObject(workingWeekMap);
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.workingWeekMapByte = byteArrayOutputStream.toByteArray();
	}

	public User getUserLogged() {
		return userLogged;
	}

	public void setUserLogged(User userLogged) {
		this.userLogged = userLogged;
	}
	
}
