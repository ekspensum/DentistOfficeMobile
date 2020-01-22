/**
 * 
 */

function sentEmailConfirm(text) {
    alert(text);
    document.getElementById("subject").value = "";
    document.getElementById("message").value = "";
    document.getElementById("replyMail").value = "";
}

function dateTime(text) {
    var today = new Date();
    var day = today.getDate();
    if(day < 10)
    	day = "0" + day;
    var month = today.getMonth() + 1;
    if (month < 10)
        month = "0" + month;
    var year = today.getFullYear();
    var hours = today.getHours();
    if (hours < 10)
        hours = "0" + hours;
    var minute = today.getMinutes();
    if (minute < 10)
        minute = "0" + minute;
    var seconde = today.getSeconds();
    if (seconde < 10)
        seconde = "0" + seconde;
    document.getElementById("clock").innerHTML = text+ " " + day +
        "/" + month + "/" + year + " " + " " + hours + ":" + minute + ":" +
        seconde;
    setTimeout(function() {
    	dateTime(text);
    	}, 1000);
}

function goBack() {
    window.history.back();
}

function validateLoginForm() {
    if (document.loginForm.username.value == "" && document.loginForm.password.value == "") {
        alert("Login i hasło są wymagane!");
        document.loginForm.username.focus();
        return false;
    }
    if (document.loginForm.username.value == "") {
        alert("Proszę podać login!");
        document.loginForm.username.focus();
        return false;
    }
    if (document.loginForm.password.value == "") {
        alert("Proszę podać hasło");
        document.loginForm.password.focus();
        return false;
    }
    return true;
}

function validateSelectChbxPatient() {
    let idChbx = document.forms.selectPatientForm.elements['patientId'];
    if (idChbx.length > 1) {
        let j = 0;
        for (i = 0; i < idChbx.length; i++) {
            if (idChbx[i].checked) {
                j++;
            }
        }
        if (j != 1) {
            alert("Proszę wybrać jeden rekord!");
            return false;
        } else {
            return true;
        }
    } else {
        if (document.selectPatientForm.patientId.checked) {
            return true;
        } else {
            alert("Proszę zaznaczyć wybór pacjenta!");
            return false;
        }
    }
}

function validateSelectChbxTreatment() {
    let idChbx = document.forms.selectTreatmentForm.elements['treatmentId'];
    if (idChbx.length > 1) {
        let j = 0;
        for (i = 0; i < idChbx.length; i++) {
            if (idChbx[i].checked) {
                j++;
            }
        }
        if (j != 1) {
            alert("Proszę wybrać jeden rekord!");
            return false;
        } else {
            return true;
        }
    } else {
        if (document.selectTreatmentForm.treatmentId.checked) {
            return true;
        } else {
            alert("Proszę zaznaczyć wybór pacjenta!");
            return false;
        }
    }
}

function validateSelectChbxVisit() {
    let idChbx = document.forms.selectVisitForm.elements['visitId'];
    if (idChbx.length > 1) {
        let j = 0;
        for (i = 0; i < idChbx.length; i++) {
            if (idChbx[i].checked) {
                j++;
            }
        }
        if (j != 1) {
            alert("Proszę wybrać jeden rekord!");
            return false;
        } else {
            return true;
        }
    } else {
        if (document.selectVisitForm.visitId.checked) {
            return true;
        } else {
            alert("Proszę zaznaczyć wybór wizyty!");
            return false;
        }
    }
}

function validateSelectChbxVisitAndConfirmRemove() {
    let idChbx = document.forms.selectVisitForm.elements['visitId'];
    if (idChbx.length > 1) {
        let j = 0;
        for (i = 0; i < idChbx.length; i++) {
            if (idChbx[i].checked) {
                j++;
            }
        }
        if (j != 1) {
            alert("Proszę wybrać jeden rekord!");
            return false;
        } else {
            if (confirm("Czy na pewno odwołać wybraną wizytę!?")) {
                return true;
            } else {
                return false;
            }
        }
    } else {
        if (document.selectVisitForm.visitId.checked) {
            if (confirm("Czy na pewno odwołać wybraną wizytę!?")) {
                return true;
            } else {
                return false;
            }
        } else {
            alert("Proszę zaznaczyć wybór wizyty!");
            return false;
        }
    }
}

function confirmRemoveVisit() {
    if (confirm("Czy na pewno odwołać wybraną wizytę!?")) {
        return true;
    } else {
        return false;
    }
}

function validateSelectChbxVisitDate() {
    let idChbx = document.forms.selectVisitDateForm.elements['dateTime'];
    if (idChbx == undefined) {
        alert("Brak wolnych terminów wizyt w tym tgodniu!")
        return false;
    }
    if (idChbx.length > 1) {
        let j = 0;
        for (i = 0; i < idChbx.length; i++) {
            if (idChbx[i].checked) {
                j++;
            }
        }
        if (j != 1) {
            alert("Proszę wybrać jeden termin wizyty!");
            return false;
        } else {
            return true;
        }
    } else {
        if (document.selectVisitDateForm.dateTime.checked) {
            return true;
        } else {
            alert("Proszę zaznaczyć wybór terminu wizyty!");
            return false;
        }
    }
}

function validateInputFieldPatientData() {
    if (document.searchDataPatientForm.patientData.value == "") {
        alert("Proszę wprowadzić dane do wyszukania!");
        document.searchDataPatientForm.patientData.focus();
        return false;
    } else {
        return true;
    }
}

function validateInputFieldTreatmentData() {
    if (document.searchTreatmentForm.treatmentData.value == "") {
        alert("Proszę wprowadzić dane do wyszukania!");
        document.searchTreatmentForm.treatmentData.focus();
        return false;
    } else {
        return true;
    }
}

function setValueOnInputFromChbx(chbx_id) {
    if (document.getElementById(chbx_id).checked == true) {
        document.getElementById("in" + chbx_id).value = true;
    } else {
        document.getElementById("in" + chbx_id).value = false;
    }
}

function validateInputFieldsDateSearchVisits() {
    if (document.selectVisitForm.dateFrom.value == "" || document.selectVisitForm.dateTo.value == "") {
        alert("Proszę wprowadzić daty wizyt do wyszukania!");
        return false;
    } else {
        return true;
    }
}

//slider
var number = Math.ceil(Math.random() * 6);

function collapse() {
    $("#slider").fadeOut(500);
}

function changeSlide() {

    number++;
    if (number == 7) {
        number = 1;
    }
    let file = "<img src=\"images/slides/slide" + number + ".jpg\" />";
    document.getElementById("slider").innerHTML = file;
    $("#slider").fadeIn(500);
    setTimeout("changeSlide();", 4000);
    setTimeout("collapse();", 3500);
}

function checkCorrectPassword(){
	let password = document.getElementById("password").value;
	let password2 = document.getElementById("password2").value;
	if(password == password2){
		return true;
	} else {
		alert("Hasła nie są zgodne!");
		return false;
	}
}
