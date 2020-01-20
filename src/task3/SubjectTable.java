package task3;

import java.util.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;

public class SubjectTable extends TableView<Subject> {

	ObservableList<Subject> subjectsList;

	TableColumn<Subject, String> idSubjectColumn, nameSubjectColumn, creditSubjectColumn, professorsColumn;

	GraphicInterface graphic;

	SubjectTable(GraphicInterface g) {

		graphic = g;

		subjectsList = FXCollections.observableArrayList();
		subjectsList.addAll(graphic.manager.getSubjects(-1));

		// table = new TableView<>();

		idSubjectColumn = new TableColumn("ID");
		idSubjectColumn.setCellValueFactory(new PropertyValueFactory("id"));

		nameSubjectColumn = new TableColumn("NAME");
		nameSubjectColumn.setCellValueFactory(new PropertyValueFactory("name"));

		creditSubjectColumn = new TableColumn("CREDITS");
		creditSubjectColumn.setCellValueFactory(new PropertyValueFactory("credits"));
		
		professorsColumn = new TableColumn("PROFESSORS");
		professorsColumn.setCellValueFactory(new PropertyValueFactory("professors"));
		
		// setProfessorsList();
		this.getColumns().addAll(idSubjectColumn, nameSubjectColumn, creditSubjectColumn, professorsColumn);
		this.setItems(subjectsList);
		this.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
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
