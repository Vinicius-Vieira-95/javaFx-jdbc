package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentdDao;
import model.entities.Department;

public class DepartmentService {

	private DepartmentdDao dao = DaoFactory.creatDepartmentDao();
	
	public List<Department>findAll(){
		return dao.findAll();
	}
	
	public void saveOrUpdate(Department obj) {
		
		if(obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
		
	}
	
	
	public void removeDepartment(Department obj) {
		dao.deleteById(obj.getId());
	}
	
	
}
