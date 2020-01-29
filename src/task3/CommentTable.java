package task3;

import java.util.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;

public class CommentTable extends TableView<Comment> {

	ObservableList<Comment> commentsList;
	TableColumn textColumn, dateColumn, studentColumn;

	GraphicInterface graphic;

	CommentTable(GraphicInterface g) {
		graphic = g;

		commentsList = FXCollections.observableArrayList();

		textColumn = new TableColumn("Text");
		textColumn.setCellValueFactory(new PropertyValueFactory<Comment, String>("text"));
		dateColumn = new TableColumn("Date");
		dateColumn.setCellValueFactory(new PropertyValueFactory<Comment, Date>("date"));
		studentColumn = new TableColumn("Student");
		studentColumn.setCellValueFactory(new PropertyValueFactory<Comment, Integer>("student"));

		this.getColumns().addAll(textColumn, dateColumn, studentColumn);
		this.setItems(commentsList);
		this.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
		this.setEditable(true);
	}

	void setCommentsList(List<Comment> l) {
		commentsList.clear();
		if(l != null)
			commentsList.addAll(l);
	}
}