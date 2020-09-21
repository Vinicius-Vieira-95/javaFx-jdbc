package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Alerts;
import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class departmentFormController implements Initializable {

	private Department entity;
	
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
	
	@FXML
	public void onBtSaveAction() {
		System.out.println("onBtSaveAction");
	}
	
	@FXML
	public void onBTCancelAction() {
		System.out.println("onBtCancelAction");
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
	
}
