package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationExceptions;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;

	private SellerService service;

	private DepartmentService departmentService;

	private List<DataChangeListener> dataChangeListener = new ArrayList<DataChangeListener>();

	@FXML
	private TextField textId;

	@FXML
	private TextField textName;
	@FXML
	private TextField textEmail;
	@FXML
	private DatePicker dpBirthDate;
	@FXML
	private TextField textBaseSalary;

	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErroName;
	@FXML
	private Label labelErrorEmail;
	@FXML
	private Label labelErroBirthDate;
	@FXML
	private Label labelErrorBasesalary;

	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;

	private ObservableList<Department> objList;

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	public void subcribeDataChangeListener(DataChangeListener listener) {
		dataChangeListener.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);

			notifyDataChangeListeners();

			Utils.currentStage(event).close();
		} catch (ValidationExceptions e) {
			setErrosMessages(e.getErros());
		} catch (DbException e) {
			e.printStackTrace();
			Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {

		for (DataChangeListener listener : this.dataChangeListener) {
			listener.onDataChanged();
		}

	}

	private Seller getFormData() {
		Seller obj = new Seller();

		ValidationExceptions exception = new ValidationExceptions("Validation error");

		obj.setId(Utils.tryPArsetoInt(textId.getText()));

		if (textName.getText() == null || textName.getText().trim().equals("")) {
			exception.addErro("name", "Field can't be empty");
		}

		obj.setName(textName.getText());

		if (textEmail.getText() == null || textEmail.getText().trim().equals("")) {
			exception.addErro("email", "Field can't be empty");
		}

		obj.setEmail(textEmail.getText());

		if (dpBirthDate.getValue() == null) {
			exception.addErro("birthDate", "Field can't be empty");
		}
		else {
			// pegando o valor em date picker de acordo com o lugar.
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDay(Date.from(instant));
		}
		
		if (textBaseSalary.getText() == null || textBaseSalary.getText().trim().equals("")) {
			exception.addErro("baseSalary", "Field can't be empty");
		}

		obj.setSalary(Utils.tryPArsetoDouble(textBaseSalary.getText()));
		
		obj.setDepartment(comboBoxDepartment.getValue());

		if (exception.getErros().size() > 0) {
			throw exception;
		}

		return obj;
	}

	@FXML
	public void onBTCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(textId);
		Constraints.setTextFieldMaxLength(textName, 70);
		Constraints.setTextFieldDouble(textBaseSalary);
		Constraints.setTextFieldMaxLength(textEmail, 70);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/YYYY");

		initializeComboBoxDepartment();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("entity was null");
		}
		// pegando os objetos e joga nas caixs do formulario.
		textId.setText(String.valueOf(entity.getId()));
		textName.setText(entity.getName());
		textEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		textBaseSalary.setText(String.format("%.2f", entity.getSalary()));
		if (entity.getBirthDay() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDay().toInstant(), ZoneId.systemDefault()));
		}

		if (entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		}
		comboBoxDepartment.setValue(entity.getDepartment());
	}

	public void loadAssociatobjects() {
		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService was null");
		}
		List<Department> list = departmentService.findAll();
		objList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(objList);

	}

	private void setErrosMessages(Map<String, String> error) {
		Set<String> fields = error.keySet();

		labelErroName.setText(fields.contains("name") ? error.get("name") : "");
		labelErrorEmail.setText(fields.contains("email") ? error.get("email") : "");
		labelErrorBasesalary.setText(fields.contains("baseSalary") ? error.get("baseSalary") : "");
		labelErroBirthDate.setText(fields.contains("birthDate") ? error.get("birthDate") : "");
		
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

}
