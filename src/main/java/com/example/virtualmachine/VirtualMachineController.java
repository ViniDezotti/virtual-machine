package com.example.virtualmachine;

import com.example.virtualmachine.handlers.CodeRunner;
import com.example.virtualmachine.model.Assembly;
import com.example.virtualmachine.model.Memory;
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
    private ObservableList<Memory> memoryStack = FXCollections.observableArrayList();

    @FXML
    private Button btnStart, btnStop, btnSubmit;

    @FXML
    private TextField tfInput;

    @FXML
    private TextArea taOutput;

    @FXML
    private ToggleGroup selectMode;

    @FXML
    private RadioButton rbNormalMode, rbPassThrough;

    @FXML
    private TableView<Memory> tvMemory;

    @FXML
    private TableColumn <Memory, Integer> tcAddress, tcValue;

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
        tcAddress.setCellValueFactory(new PropertyValueFactory<>("Address"));
        tcValue.setCellValueFactory(new PropertyValueFactory<>("Value"));
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

    private void missingInputValue() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Entrada de dados vazia!");
        alert.setContentText("Insira um valor numérico");
        alert.setOnCloseRequest(dialogEvent -> enableInput(true));
        alert.show();
    }

    private void printOutput(String value) {
        codeRunner.setStackPointer(codeRunner.getStackPointer() - 1);
        taOutput.setText(taOutput.getText() + "\n" + value);
        codeRunner.setProgramCounter(codeRunner.getProgramCounter() + 1);
        taOutput.requestFocus();
        memoryStackStatus();
        paintActualRow(codeRunner.getProgramCounter(), codeRunner.getStackPointer());
    }

    private boolean needPrint() {
        return assemblyList.get(codeRunner.getProgramCounter()).getInstruction().equals("PRN");
    }

    @FXML
    private void startProgram() {
        if (codeRunner.getProgramCounter() == assemblyList.size() - 1) {
            btnStart.setDisable(true);
            return;
        }
        if (normalMode) {
            if (!codeRunner.executeAll()) {
                if (needPrint()) {
                    printOutput(codeRunner.getMemory().get(codeRunner.getStackPointer()));
                } else {
                    enableInput(true);
                }
                return;
            }
        } else {
            if (!codeRunner.executeStepByStep()) {
                if (needPrint()) {
                    printOutput(codeRunner.getMemory().get(codeRunner.getStackPointer()));
                } else {
                    enableInput(true);
                }
                return;
            }
        }
        memoryStackStatus();
        paintActualRow(codeRunner.getProgramCounter(), codeRunner.getStackPointer());
    }

    @FXML
    private void stopProgram() {
        taOutput.clear();
        tfInput.clear();
        btnStart.setDisable(true);
        btnStop.setDisable(true);
        tvAssembly.getItems().clear();
        tvMemory.getItems().clear();
        assemblyList.clear();
        memoryStack.clear();
    }

    private void enableInput(boolean status) {
        // TODO textField aceitar somente 1 numero
        tvAssembly.getSelectionModel().select(codeRunner.getProgramCounter());
        btnStart.setDisable(status);
        tfInput.requestFocus();
        tfInput.setEditable(status);
        btnSubmit.setDisable(!status);
    }

    @FXML
    private void submitValue() {
        if (!tfInput.getText().isEmpty() && !tfInput.getText().isBlank()) {
            codeRunner.setStackPointer(codeRunner.getStackPointer() + 1);
            codeRunner.getMemory().set(codeRunner.getStackPointer(), tfInput.getText());
            codeRunner.setProgramCounter(codeRunner.getProgramCounter() + 1);
            enableInput(false);
            memoryStackStatus();
            paintActualRow(codeRunner.getProgramCounter(), codeRunner.getStackPointer());
            startProgram();
        } else {
            missingInputValue();
        }
    }

    private void paintActualRow(int programCounter, int stackPointer) {
        System.out.println(programCounter + "    " + stackPointer);
        tvAssembly.getSelectionModel().select(programCounter);
        tvAssembly.scrollTo(programCounter);
        tvMemory.getSelectionModel().select(stackPointer);
        tvMemory.scrollTo(stackPointer);
    }

    private void memoryStackStatus() {
        memoryStack.clear();
        for (int i = 0; i <= codeRunner.getStackPointer(); i++) {
            memoryStack.add(new Memory(i, Integer.parseInt(codeRunner.getMemory().get(i))));
        }
        tvMemory.setItems(memoryStack);
    }
}