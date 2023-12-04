package com.example.virtualmachine;

import com.example.virtualmachine.handlers.CodeRunner;
import com.example.virtualmachine.model.Assembly;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class VirtualMachineController implements Initializable {
    private boolean normalMode = true;
    private Stage stage;
    private CodeRunner codeRunner;
    private ObservableList<Assembly> assemblyList = FXCollections.observableArrayList();

    @FXML
    private Button btnStart, btnStop;

    @FXML
    private MenuItem btnOpenFile;

    @FXML
    private ToggleGroup selectMode;

    @FXML
    private RadioButton rbNormalMode, rbPassThrough;

    @FXML
    private TableView<Assembly> tvAssembly;

    @FXML
    private TableColumn <Assembly, Integer> tcLine;

    @FXML
    private TableColumn <Assembly, String> tcLabel, tcInstruction, tcArg1, tcArg2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stage = VirtualMachine.getMainStage();
        tcLine.setCellValueFactory(new PropertyValueFactory<>("Line"));
        tcLabel.setCellValueFactory(new PropertyValueFactory<>("Label"));
        tcInstruction.setCellValueFactory(new PropertyValueFactory<>("Instruction"));
        tcArg1.setCellValueFactory(new PropertyValueFactory<>("Arg1"));
        tcArg2.setCellValueFactory(new PropertyValueFactory<>("Arg2"));
        tvAssembly.setItems(assemblyList);
        rbNormalMode.setToggleGroup(selectMode);
        rbPassThrough.setToggleGroup(selectMode);
        selectMode.selectedToggleProperty().addListener((observableValue, toggle, t1) ->
                normalMode = rbNormalMode.isSelected());
    }


    @FXML
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("obj", "*.obj"));
        fileChooser.setInitialDirectory(new File("c:\\"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            codeRunner = new CodeRunner(selectedFile.toString());
            enableComponents();
        } else {
            wrongFileAlert();
        }
    }

    private void enableComponents() {
        btnStart.setDisable(false);
        btnStop.setDisable(false);
        loadAssemblyList();
    }


    @FXML
    private void loadAssemblyList() {
        String instruction, arg1, arg2, line, label = "";
        for (int i = 0; i < codeRunner.getCode().size(); i++) {
            line = codeRunner.getCode().get(i);
            label = line.substring(0, 3).strip();
            instruction = line.substring(4, 11).strip();
            arg1 = line.substring(12, 15).strip();
            arg2 = line.substring(16, 19).strip();
            assemblyList.add(new Assembly(i, label, instruction, arg1, arg2));
        }
    }

    private void wrongFileAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Arquivo inválido");
        alert.setContentText("Escolha um arquivo do tipo objeto (.obj)");
        alert.show();
    }

    private void startProgram() {
        if (normalMode) {
            codeRunner.executeStepByStep();
        } else {
            codeRunner.executeAll();
        }
    }
}