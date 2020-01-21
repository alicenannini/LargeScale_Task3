package task3;

import java.util.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.util.*;

public class SubjectTable extends TableView<Subject> {

	ObservableList<Subject> subjectsList;

	TableColumn idSubjectColumn, nameSubjectColumn, creditSubjectColumn, professorsColumn;

	GraphicInterface graphic;

	SubjectTable(GraphicInterface g) {

		graphic = g;

		subjectsList = FXCollections.observableArrayList();
		subjectsList.addAll(graphic.manager.getSubjects(-1));

		// table = new TableView<>();

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
		
		this.getColumns().addAll(/*idSubjectColumn,*/ nameSubjectColumn, creditSubjectColumn, professorsColumn);
		this.setItems(subjectsList);
		this.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
		this.setEditable(true);
	}

	// update table: set subjects information
	void setSubjectsList(List<Subject> l) {
		this.subjectsList.clear();
		this.subjectsList.addAll(l);


		if (graphic.student != null && graphic.student.getAdmin()) {
			graphic.FieldsAdminBox.setVisible(true);
			graphic.infoLab.setText("Subject Informations:");
			graphic.surnameAndCreditsLab.setText("Credits:");
			if (!graphic.FieldsAdminBox.getChildren().contains(graphic.profIdBox))
				graphic.FieldsAdminBox.getChildren().add(2, graphic.profIdBox);
		} else {
			graphic.FieldsAdminBox.setVisible(false);
		}
	}
}
