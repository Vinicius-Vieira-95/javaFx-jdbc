package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationExceptions;
import model.services.DepartmentService;

public class departmentFormController implements Initializable {

	private Department entity;
	
	private DepartmentService service;
	
	private List<DataChangeListener> dataChangeListener = new ArrayList<DataChangeListener>();
	
	@FXML
	private TextField textId;
	
	@FXML
	private TextField textName;
	
	@FXML
	private Label labelErroName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setDepartment( Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	public void subcribeDataChangeListener(DataChangeListener listener) {
		dataChangeListener.add(listener);
	}
	
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if(entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
		entity = getFormData();
		service.saveOrUpdate(entity);
		
		notifyDataChangeListeners();
		
		Utils.currentStage(event).close();
		}
		catch(ValidationExceptions e) {
			setErrosMessages(e.getErros());
		}
		catch(DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
		}
	}
	
	private void notifyDataChangeListeners() {
		
		for(DataChangeListener listener : this.dataChangeListener) {
			listener.onDataChanged();
		}
		
	}

	private Department getFormData(){
		Department obj = new Department();
		
		ValidationExceptions exception = new ValidationExceptions("Validation error");
		
		obj.setId(Utils.tryPArsetoInt( textId.getText()));
		
		if(textName.getText() == null || textName.getText().trim().equals("")) {
			exception.addErro("name", "Field can't be empty");
		}
		
		obj.setName(textName.getText());
		
		if(exception.getErros().size() > 0) {
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
		Constraints.setTextFieldMaxLength(textName, 30);
	}
	
	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("entity was null");
		}
		textId.setText(String.valueOf(entity.getId()));
		textName.setText(entity.getName());
	}
	
	private void setErrosMessages(Map<String , String> error) {
		Set<String>fields = error.keySet();
		
		if(fields.contains("name")) {
			labelErroName.setText(error.get("name"));
		}
		
		
	}
	
	
}
