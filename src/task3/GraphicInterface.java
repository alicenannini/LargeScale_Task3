package task3;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GraphicInterface extends Application {
	Label usernameLab, passwordLab, nameLab, surnameAndCreditsLab, infoLab, studentLab, profIdLab;
    TextField username, name, surnameAndCredits, profId;
    PasswordField password;
    TextArea info, comment, addInfo;
    Button loginBtn, logoutBtn, commentBtn, deleteBtn, updateBtn, addBtn, editBtn, deleteBtnProfSubj;
    ChoiceBox<String> choosePS; //PS -> ProfessorSubject
    ChoiceBox<Degree> chooseDegree;
    HBox loginBox, logoutBox, commentBox, adminButtonsBox, FieldsAdminBox, chooseBox;
    VBox leftBox, rightBox, nameSurnameBox, InfoVBox, profIdBox;
    Group root;
    DbManager manager;
    ObservableList<Degree> degreeList;
    Student student;
    CommentTable comments;
    ProfSubjectTable table;	

	@Override
	public void start(Stage primaryStage) throws Exception {
		manager = new DbManager();
    	table = new ProfSubjectTable(this);
    	comments = new CommentTable(this);
    	 
        //login box on the top
    	usernameLab = new Label("Username:");
        username = new TextField();
        username.setPromptText("Your Username");
        passwordLab = new Label("Password:");
        password = new PasswordField();
        password.setPromptText("Your Password");
        loginBtn = new Button("Login");
        
        loginBox = new HBox(10);
        loginBox.getChildren().addAll(usernameLab,username,passwordLab,password,loginBtn);
       
        //logout box on the top
        studentLab = new Label("");
        logoutBtn = new Button("Logout");
        
        logoutBox = new HBox(10);
        logoutBox.getChildren().addAll(studentLab, logoutBtn);
        logoutBox.setVisible(false); //user not logged, hide logoutbox
        
        //box on the left
        info = new TextArea("");
        info.setPromptText("Information about subjects are visualized here");
        info.setWrapText(true);
        info.setEditable(false);
        
        leftBox = new VBox(10);
        leftBox.getChildren().addAll(info,comments);
          
        //info Vbox creation for the admin
        infoLab = new Label("Subject Information:");
        addInfo = new TextArea("");
        addInfo.setWrapText(true);
        
        InfoVBox = new VBox(10);
        InfoVBox.getChildren().addAll(infoLab,addInfo);
        
        //name and surname/credits about professor/subject VBox for the admin
        nameLab = new Label("Name:");
        name = new TextField();
        surnameAndCreditsLab = new Label("Surame:");
        surnameAndCredits = new TextField();       
        
        nameSurnameBox = new VBox(10);
        nameSurnameBox.getChildren().addAll(nameLab,name, surnameAndCreditsLab, surnameAndCredits ); 
        
        //id prof vbox for admin
        profIdLab = new Label("Professor Id:");
        profId = new TextField();
        
        profIdBox = new VBox(10);
        profIdBox.getChildren().addAll(profIdLab, profId);
        
        //Hbox with the 2 vbox created before and the add button.
        addBtn = new Button("Add");
        FieldsAdminBox = new HBox(10);
        FieldsAdminBox.getChildren().addAll(nameSurnameBox, InfoVBox, addBtn);
        FieldsAdminBox.setVisible(false);
        
        //box on the right
        choosePS = new ChoiceBox<>(); //Professor/subject choice box
        choosePS.getItems().addAll("Subjects","Professors");
        choosePS.getSelectionModel().selectFirst();
        choosePS.setVisible(false);
        
        chooseDegree = new ChoiceBox<>(); //degree choice box
        degreeList = FXCollections.observableArrayList();
        degreeList.addAll(manager.getDegreeCourses());
        chooseDegree.setItems(degreeList);
        chooseDegree.getItems().add(0, new Degree(-1,"All"));
        chooseDegree.getSelectionModel().selectFirst();
        
        table.setSubjectsList(manager.getSubjects(-1));
        
     // add event to chooseProfSubject changing selection value
        choosePS.getSelectionModel().selectedIndexProperty().addListener((ChangeListener<? super Number>) new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                
                int degreeId = chooseDegree.getValue().getId();
                if("Subjects".equals(choosePS.getItems().get((Integer) newValue))) {
                	table.setSubjectsList(manager.getSubjects(degreeId));
                }
                else if("Professors".equals(choosePS.getItems().get((Integer) newValue))) {
                	table.setProfessorsList(manager.getProfessorsByDegree(degreeId));
                }
            }
        });
       
        // add event to chooseDegree changing selection value 
        chooseDegree.getSelectionModel().selectedIndexProperty().addListener((ChangeListener<? super Number>) new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {      	
            	  
            	if( "All".equals(chooseDegree.getItems().get((int) newValue).getName())){
            		setSubjProfList(-1);
            	} else {
                    int degreeId = chooseDegree.getItems().get((int) newValue).getId();
                    setSubjProfList(degreeId);
            	}
            }
        });     
        
        //admin buttons
    	editBtn = new Button("Edit");
    	deleteBtnProfSubj = new Button("Delete");
    	
    	adminButtonsBox = new HBox(10);
        adminButtonsBox.getChildren().addAll(editBtn, deleteBtnProfSubj);
        adminButtonsBox.setVisible(false);
        
        loginBtn.setOnAction((ActionEvent e) -> loginAction());       
        logoutBtn.setOnAction((ActionEvent e) -> logoutAction());
        
        table.lookup(".scroll-bar:vertical"); 
        
        chooseBox = new HBox(10);
        chooseBox.getChildren().addAll(chooseDegree,choosePS);
        
        rightBox = new VBox(10);
        rightBox.getChildren().addAll(chooseBox,table);
        
        //box on the bottom
        comment = new TextArea("");
        comment.setPromptText("Leave a comment here...");
        commentBtn = new Button("Comment");
        deleteBtn = new Button("Delete");
        updateBtn = new Button("Update");
        
        commentBox = new HBox(10);
        commentBox.getChildren().addAll(comment,commentBtn, deleteBtn, updateBtn);
        
        root = new Group();
        root.getChildren().addAll(loginBox, logoutBox, leftBox, rightBox, adminButtonsBox, commentBox, FieldsAdminBox);
        
        Scene scene = new Scene(root, 1210, 700);
        primaryStage.setTitle("Students Evaluations");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        setPositions();
        setSizes();
        setCssStyle();
        
        //choice box selection 		
        table.setOnMouseClicked((MouseEvent ev)-> profSubjectSelectionButton());
             
        //add comment 
        commentBtn.setOnAction((ActionEvent ev)-> addCommentButton());
        
        //select comment
        comments.setOnMouseClicked((MouseEvent ev)-> commentSelectionButton());
        
        //update comment
        updateBtn.setOnAction((ActionEvent ev)->updateCommentButton());
        
        //delete comment
        deleteBtn.setOnAction((ActionEvent ev)-> deleteCommentButton());
        
        //Add professor/course button for admin
        addBtn.setOnAction((ActionEvent ev)-> addProfSubjectButton()); 
        
        deleteBtnProfSubj.setOnAction((ActionEvent e) -> adminDeleteAction());
        
        editBtn.setOnAction((ActionEvent e) -> adminEditAction());
    }
   
    // STYLE
    private void setCssStyle(){
        root.setStyle("-fx-color:orange; -fx-font-size: 14; -fx-font-family: Arial");
        studentLab.setStyle("-fx-font-size: 20");
    }
    
    // STYLE
    private void setSizes(){
        logoutBtn.setMinWidth(80);//logout button
        loginBtn.setMinWidth(80);//login button
        comment.setMaxHeight(50); 

        //right box
        choosePS.setMinSize(150,30);
        chooseDegree.setMinSize(350,30); chooseDegree.setMaxWidth(350);
        table.setMinHeight(500);
        rightBox.setMinWidth(500);

        //Left Box
        leftBox.setMaxSize(540, 530);
        comments.setMaxSize(540, 430); comments.setMinHeight(430);	//tabella dei commenti
        info.setMaxSize(540,100); info.setMinSize(540, 100);		 

        //admin fields
        FieldsAdminBox.setMinSize(540, 100); FieldsAdminBox.setMaxWidth(640);
        addInfo.setMaxSize(285, 90);

        //admin buttons
        addBtn.setMinSize(60, 120);
        editBtn.setMinWidth(80);
        deleteBtnProfSubj.setMinWidth(80);
    }
        
    // STYLE
    private void setPositions(){
        loginBox.setLayoutX(10); loginBox.setLayoutY(10);
        logoutBox.setLayoutX(450); logoutBox.setLayoutY(10);
        studentLab.setLayoutY(20);
        leftBox.setLayoutX(20); leftBox.setLayoutY(70);
        FieldsAdminBox.setLayoutX(20); FieldsAdminBox.setLayoutY(515);
        rightBox.setLayoutX(leftBox.getWidth()+100); rightBox.setLayoutY(70);
        commentBox.setLayoutX(20); commentBox.setLayoutY(640);  
        adminButtonsBox.setLayoutX(900); adminButtonsBox.setLayoutY(530);
    }
    
    // STYLE updates tables size based on the actual user (admin or not)
  	private void updateSizes(){
  		if( student != null && student.getAdmin()) {
                    table.setMinHeight(400);
                    comments.setMaxSize(540, 330); comments.setMinHeight(330);
  		} else {
                    table.setMinHeight(500);
                    comments.setMaxSize(540, 430); comments.setMinHeight(430);	
  		}
  	}
    
    // function for click on loginButton
    private void loginAction() {
    	student = manager.checkUser(username.getText(), password.getText());
    	
    	if(student != null) {
            loginBox.setVisible(false);
            logoutBox.setVisible(true);
            studentLab.setText("User: " + student.getUsername());
            table.setSubjectsList(manager.getSubjects(student.getDegree().getId()));    
            
            //update the degree course choice box based on the student degree course.
            for(Degree d : degreeList) {
            	if(d.getId() == student.getDegree().getId())
            		chooseDegree.setValue(d);
            }
            
            //show/hide admin buttons 
            adminButtonsBox.setVisible(student.getAdmin());
            choosePS.setVisible(student.getAdmin());
            
            //updates STYLE sizes based on the logged user (admin or not)
            updateSizes();
    	} else {
            username.setText("");
            password.setText("");
            System.err.println("Invalid username or password, please retry!");
    	}
    }
       
    private void logoutAction(){
    	chooseDegree.getSelectionModel().selectFirst();
    	logoutBox.setVisible(false);
    	loginBox.setVisible(true);
    	adminButtonsBox.setVisible(false); 
    	FieldsAdminBox.setVisible(false);
    	choosePS.setVisible(false);
    	student = null;
    	table.setSubjectsList(manager.getSubjects(-1));
    	updateSizes();
        this.username.setText("");
    	this.password.setText("");
    }
    
    private void setSubjProfList(int degree){
        if("Subjects".equals((String)choosePS.getValue())){
            table.setSubjectsList(manager.getSubjects(degree));
    	} else {
            table.setProfessorsList(manager.getProfessorsByDegree(degree));
    	}
    }
        
    private void profSubjectSelectionButton() {
    	String type = (String) choosePS.getValue();
        Object o = table.getSelectionModel().getSelectedItem();
        if(o != null){
            if("Professors".equals(type)){
                Professor p = (Professor)o;
                if( student != null && student.getAdmin()){ // if admin is operating
                    name.setText(p.getName());
                    surnameAndCredits.setText(p.getSurname());
                }
            }else{
            	Subject s = (Subject)o;
                info.setText(s.getInfo());
                comments.setCommentsList(manager.getComments(s.getId()));
                if( student != null && student.getAdmin()) { // if admin is operating
                    name.setText(s.getName());
                    surnameAndCredits.setText(Integer.toString(s.getCredits()));
                    addInfo.setText(s.getInfo());
                }
            }
        }
    }
    
    private void commentSelectionButton(){
        Comment o = comments.getSelectionModel().getSelectedItem();
        if(o != null){
            comment.setText(o.getText());
        }
    }
    
    private void addCommentButton() {
    	if(student != null) {
            Subject s = (Subject) table.getSelectionModel().getSelectedItem();
            
            manager.createComment(comment.getText(), student, s.getId());
            comments.setCommentsList(manager.getComments(s.getId()));
            
            this.comment.setText("");
        }else 
            System.err.println("You have to login!\n");
    }
    
    private void updateCommentButton() {
    	if(student != null) {
            Subject s = (Subject) table.getSelectionModel().getSelectedItem();
            Comment sc = (Comment) comments.getSelectionModel().getSelectedItem();
            if(s == null || sc == null) {
                System.err.println("Select a subject and a comment in order to update it");
                return;
            }

            if(manager.updateComment(sc.getId(), comment.getText(), student.getId()))
                comments.setCommentsList(manager.getComments(s.getId()));
    	}else 
            System.err.println("You have to login!\n");
    }
    
    private void deleteCommentButton() {
    	if(student != null) {
            Subject s = (Subject) table.getSelectionModel().getSelectedItem();
            Comment c = (Comment) comments.getSelectionModel().getSelectedItem();
            
            if(manager.deleteComment(c.getId(),student.getId(), student.getAdmin()))
                comments.setCommentsList(manager.getComments(s.getId()));
        }else 
            System.err.println("You have to login!\n");
    }
  
    private void adminEditAction() {
    	
    	if(student != null && student.getAdmin()) {
            if(name.getText().isEmpty() || surnameAndCredits.getText().isEmpty()) {
                System.err.println("Insert name and surname(for professor) or credits(for subject)");
                return;
            }
            int degreeId = chooseDegree.getValue().getId();
            
            String type = (String) choosePS.getValue();
            if("Professors".equals(type)) {
                Professor professor = (Professor) table.getSelectionModel().getSelectedItem();
                if(professor == null) {
                    System.err.println("Select a professor");
                    return;
                }
                manager.updateProfessor(professor.getId(), name.getText(), surnameAndCredits.getText());
                name.clear();
                surnameAndCredits.clear();
                addInfo.clear();
                table.setProfessorsList(manager.getProfessorsByDegree(degreeId));
            }else{
	            Subject subject = (Subject) table.getSelectionModel().getSelectedItem();
	            if(subject == null) {
	                System.err.println("Select a subject");
	                return;
	            }
	            
	            int credits = 0;
	            try{
	                if(!surnameAndCredits.getText().isEmpty())
	                    credits = Integer.valueOf(surnameAndCredits.getText());
	            } catch(NumberFormatException e) {
	                System.err.println("Invalid input in numeric fields");
	                return;
	            }
	            if(manager.updateSubject(subject.getId(), name.getText(), credits, addInfo.getText(), profId.getText())) {
	                info.setText(addInfo.getText());
	                name.clear();
	                surnameAndCredits.clear();
	                addInfo.clear();
	                profId.clear();
	                table.setSubjectsList(manager.getSubjects(degreeId));
	            }
    		}
        }else{
            System.err.println("You have to login as an admin!\n");
    	}
    }
    
    private void adminDeleteAction() {
    	if(student != null) {
    		String type = (String) choosePS.getValue();
            if("Professors".equals(type)){
                Professor professor = (Professor) table.getSelectionModel().getSelectedItem();
                if(professor == null) {
                    System.err.println("Select a professor");
                    return;
                }
                manager.deleteProfessor(professor.getId());
                info.clear();
                name.clear();
                surnameAndCredits.clear();
                table.setProfessorsList(manager.getProfessorsByDegree(chooseDegree.getValue().getId()));
            }else {
	            Subject subject = (Subject) table.getSelectionModel().getSelectedItem();
	            if(subject == null) {
	                System.err.println("Select a subject");
	                return;
	            }
	            manager.deleteSubject(subject.getId());
	            info.clear();
	            name.clear();
	            surnameAndCredits.clear();
	            addInfo.clear();
	            comments.setCommentsList(manager.getComments(-1));
	            table.setSubjectsList(manager.getSubjects(chooseDegree.getValue().getId()));
            }
        }else{
            System.err.println("You have to login as an admin!\n");
    	}
    }
    
    private void addProfSubjectButton() {
    	int degreeId;
    	if("Subjects".equals((String)choosePS.getValue())){
		    if(name.getText().isEmpty() || surnameAndCredits.getText().isEmpty() || profId.getText().isEmpty()) {
		        System.err.println("Insert name, credits, info(optional) and the id of a professor for the new subject.");
		        return;
		    }
		    
		    if(!"All".equals(chooseDegree.getValue().getName())) {
		        degreeId = chooseDegree.getValue().getId();
		        try {
		            int credits = Integer.parseInt(surnameAndCredits.getText());
		            if(credits > 0 && credits < 50 )
		                manager.createSubject(name.getText(), credits, addInfo.getText(), profId.getText(), degreeId);
		            else {
		                System.err.println("invalid credits value, insert a number between 1 and 50");
		                return;
		            }
		        }catch(NumberFormatException e) {
		            System.err.println("Please, insert a numeric value in \"Credits\" and \"Professor Id\" fields");
		            return;
		        }
		        name.setText("");
		        surnameAndCredits.setText("");
		        addInfo.setText("");	
		        profId.setText("");
		        table.setSubjectsList(manager.getSubjects(degreeId));
		    }else 
		        System.err.println("Select a Degree program");
    	}else{
            if(name.getText().isEmpty() || surnameAndCredits.getText().isEmpty()) {
                System.err.println("Insert name, surname and info(optional) for the new professor.");
                return;
            }
            manager.createProfessor(name.getText(),surnameAndCredits.getText());
            name.setText("");
            surnameAndCredits.setText("");
            addInfo.setText(""); 
            table.setProfessorsList(manager.getProfessorsByDegree(chooseDegree.getValue().getId()));
        }
    }
	
	public static void main(String[] args) {
		launch(args);
		System.out.println("\nEND");
	}

}
