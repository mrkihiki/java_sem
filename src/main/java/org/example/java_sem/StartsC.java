package org.example.java_sem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class StartsC {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    public PrintWriter out;
    public BufferedReader in;
    @FXML
    private Text logMess;
    @FXML
    private TextFlow regMess;
    private StartsC.EchoClient client2;
    @FXML
    private Label welcomeText;
    @FXML
    private TextField  loginLogin;
    @FXML
    protected TextField  loginPass;
    @FXML
    private TextField regLogin;
    @FXML
    protected TextField  regPass;
    String name="eror";
    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    @FXML
    void onLogin(ActionEvent ae) throws IOException {
//        FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("chat.fxml"));
//        Scene scene = new Scene(fxmlLoader1.load(), 600, 400);
//        Node source = (Node) ae.getSource();
//        Stage theStage = (Stage) source.getScene().getWindow();
//        theStage.setScene(scene);
        //////////
        String login=loginLogin.getText();
        String pass=loginPass.getText();
        if(login.isEmpty() || pass.isEmpty()){
            logMess.setText("заполните все поля");
        }else {
            client2.out.println("login");
            client2.out.println(login);
            client2.out.println(pass);
            String a = client2.in.readLine();
            if(a.equals("пользователя не существует или пароль не правельный")) {
                logMess.setText(a);
            }else if(a.equals("1")) {
                System.out.println("555555");
                name=loginLogin.getText();
                login(ae);
            }
        }
    }
    @FXML
    void onReg() throws IOException {
        String login=regLogin.getText();
        String pass=regPass.getText();
        if(login.isEmpty() || pass.isEmpty()){
            regMess.getChildren().clear();
            regMess.getChildren().add(new Text("заполните все поля"));
        }else {
            client2.out.println("reg");
            client2.out.println(login);
            client2.out.println(pass);
            String a = client2.in.readLine();
            regMess.getChildren().clear();
            regMess.getChildren().add(new Text(a));
        }
    }

    public static class EchoClient {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private DataInputStream inD;

        public void startConnection(String ip, int port) throws IOException {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            inD = new DataInputStream(clientSocket.getInputStream());
        }

//        public String sendMessage(String msg) throws IOException {
//            out.println(msg);
//            String resp = in.readLine();
//            return resp;
//        }

        public void stopConnection() throws IOException {
            in.close();
            out.close();
            inD.close();
            clientSocket.close();
        }
    }
    @FXML
    public void initialize() throws IOException {
        client2 = new EchoClient();
        try {
            client2.startConnection(SERVER_ADDRESS,SERVER_PORT);
        } catch (IOException e) {
            System.out.println("start server!!!");
        }
        //System.out.println("111111111111111111111111111111111111111111");
    }
    void login(ActionEvent ae)throws IOException{
        //System.out.println("77777777");
        FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("chat.fxml"));
        //System.out.println("77777777");
        //
        //System.out.println("22222222222");
        Parent root = fxmlLoader1.load();
        Chat controller2 = fxmlLoader1.getController();
        controller2.receiveData(name);
        client2.stopConnection();
        Scene scene = new Scene(root, 600, 400);
        //
        //System.out.println("33333333");
        Node source = (Node) ae.getSource();
        Stage theStage = (Stage) source.getScene().getWindow();
        theStage.setScene(scene);
    }
}