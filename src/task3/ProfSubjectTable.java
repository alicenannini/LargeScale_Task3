package task3;

import java.util.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.util.*;

public class ProfSubjectTable extends TableView<Object> {

	ObservableList<Object> subjectsList;
	ObservableList<Object> professorsList;

	TableColumn idSubjectColumn, nameSubjectColumn, creditSubjectColumn, professorsColumn;
	TableColumn idProfColumn, nameProfColumn, surnameProfColumn;

	GraphicInterface graphic;

	ProfSubjectTable(GraphicInterface g) {

		graphic = g;

		subjectsList = FXCollections.observableArrayList();
		//subjectsList.addAll(graphic.manager.getSubjects(-1));
		
		professorsList = FXCollections.observableArrayList();
		//professorsList.addAll(graphic.manager.getProfessors(-1));

		idSubjectColumn = new TableColumn<Subject, Integer>("ID");
		idSubjectColumn.setCellValueFactory(new PropertyValueFactory("id"));

		nameSubjectColumn = new TableColumn<Subject, String>("NAME");
		nameSubjectColumn.setCellValueFactory(new PropertyValueFactory("name"));

		creditSubjectColumn = new TableColumn<Subject, Integer>("CFU");
		creditSubjectColumn.setCellValueFactory(new PropertyValueFactory("credits"));
		
		professorsColumn = new TableColumn<Subject, List<Professor>>("PROFESSORS");
		professorsColumn.setCellValueFactory(new PropertyValueFactory("professors"));
		professorsColumn.setCellFactory(new Callback<TableColumn, TableCell>(){ //4
            @Override
            public TableCell call(TableColumn param){
                return new TableCell<Subject, List<Professor>>(){
                    @Override
                    public void updateItem(List value, boolean empty){
                        super.updateItem(value, empty);
                        if(!isEmpty()){
                        	List<Professor> list = value;
                        	String s = "";
                    		for(Professor p : list) {
                    			s += p.toString()+"; ";
                    		}
                    		setText(s);
                        }else{
                            setText("");
                        }
                    }
                };
            }
        });
		
		idProfColumn = new TableColumn("ID");
		idProfColumn.setCellValueFactory(new PropertyValueFactory<Professor, Integer>("id"));

		nameProfColumn = new TableColumn("NAME");
		nameProfColumn.setCellValueFactory(new PropertyValueFactory<Professor, String>("name"));

		surnameProfColumn = new TableColumn("SURNAME");
		surnameProfColumn.setCellValueFactory(new PropertyValueFactory<Professor, String>("surname"));
		
		this.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
		this.setEditable(true);
	}

	// update table: set subjects information
	void setSubjectsList(List<Subject> l) {
		this.subjectsList.clear();
		this.subjectsList.addAll(l);

		this.getColumns().clear();
		this.getColumns().addAll(/*idSubjectColumn,*/ nameSubjectColumn, creditSubjectColumn, professorsColumn);
		this.setItems(subjectsList);

		if (graphic.student != null && graphic.student.getAdmin()) {
			graphic.commentBtn.setDisable(false);
			graphic.updateBtn.setDisable(false);
			graphic.deleteBtn.setDisable(false);
			graphic.FieldsAdminBox.setVisible(true);
			graphic.infoLab.setVisible(true);
			graphic.addInfo.setVisible(true);
			graphic.surnameAndCreditsLab.setText("Credits:");
			if (!graphic.FieldsAdminBox.getChildren().contains(graphic.profIdBox))
				graphic.FieldsAdminBox.getChildren().add(2, graphic.profIdBox);
		} else {
			graphic.FieldsAdminBox.setVisible(false);
		}
	}
	
	void setProfessorsList(List<Professor> l) {
		this.professorsList.clear();
		this.professorsList.addAll(l);

		this.getColumns().clear();
		this.getColumns().addAll(idProfColumn, nameProfColumn, surnameProfColumn);
		this.setItems(professorsList);

		if (graphic.student != null && graphic.student.getAdmin()) {
			graphic.commentBtn.setDisable(true);
			graphic.updateBtn.setDisable(true);
			graphic.deleteBtn.setDisable(true);
			graphic.FieldsAdminBox.setVisible(true);
			graphic.infoLab.setVisible(false);
			graphic.addInfo.setVisible(false);
			graphic.surnameAndCreditsLab.setText("Surname:");
			graphic.FieldsAdminBox.getChildren().remove(graphic.profIdBox);

		} else {
			graphic.FieldsAdminBox.setVisible(false);
		}
	}
}
