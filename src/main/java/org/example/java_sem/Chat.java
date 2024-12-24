package org.example.java_sem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Chat {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static List<Message> messageHistory = new ArrayList<>();
    private static final String CHAT_HISTORY_FILE = "chat_history.json";
    @FXML
    private Text onlneP;
    private Message mess;
    private String USER = "er1111";
    private String cUSER;
    private void updateLabel() {
        System.out.println("es");
        client1.out.println("up");
        try {
            cUSER=client1.in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        onlneP.setText(cUSER);
        // Получаем размер файла
        long fileSize = 0;
        try {
            fileSize = client1.inD.readLong();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Получаем файл
        try {
            receiveFile(fileSize, client1.inD);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //
        loadChatHistory();
        //
        updateChatHistory();
    }
    @FXML
    public void initialize() throws IOException {
        //System.out.println("--------------------");
        client1 = new EchoClient();
        client1.startConnection(SERVER_ADDRESS,SERVER_PORT);
        System.out.println(USER);
        // Этот метод будет вызван при загрузке FXML
//        try {
//            vrem();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        loadChatHistory();
//        updateChatHistory();
        //System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbb");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> updateLabel()));
        timeline.setCycleCount(Timeline.INDEFINITE); // Устанавливаем бесконечный цикл
        timeline.play();
    }
    private EchoClient client1;
    @FXML
    private TextArea addMes;
    @FXML
    public TextFlow chatM;
    @FXML
    void onSend() throws IOException {
        Gson gson= new Gson();
        client1.out.println("update");
        client1.out.println(addMes.getText());
        //Message mes = (new Message("kihiki", addMes.getText()));
        //messageHistory.add(mes);
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CHAT_HISTORY_FILE))) {
//            writer.write(gson.toJson(messageHistory));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        // Получаем размер файла
        long fileSize = client1.inD.readLong();
        // Получаем файл
        receiveFile(fileSize, client1.inD);
        //
        loadChatHistory();
        //
        updateChatHistory();
        //chatM.getChildren().addAll((Collection<? extends Node>) mes);
    }

    public static void loadChatHistory(){
        Gson gson= new Gson();
        try (BufferedReader reader = new BufferedReader(new FileReader(CHAT_HISTORY_FILE))) {
            messageHistory = gson.fromJson(reader, new TypeToken<List<Message>>() {}.getType());
        } catch (FileNotFoundException e) {
            // Файл не найден, создаем новый
            messageHistory = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateChatHistory(){
        chatM.getChildren().clear();
        for (Message message:messageHistory){
            Text text1 =new Text(message.getUsername());
            text1.setStyle("-fx-font-weight: bold; -fx-fill: blue;");
            Text text2 = new Text(message.getText()+"\n");
            chatM.getChildren().addAll(text1, text2);
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

        public String sendMessage(String msg) throws IOException {
            out.println(msg);
            String resp = in.readLine();
            return resp;
        }

        public void stopConnection() throws IOException {
            in.close();
            out.close();
            inD.close();
            clientSocket.close();
        }
    }
//    public void vrem1() throws IOException {
//        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
//             DataInputStream in = new DataInputStream(socket.getInputStream())) {
//            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
////
////            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
////            System.out.print("Введите два числа через пробел: ");
////            String input = userInput.readLine();
////
////            // Отправляем данные на сервер
//            out.println("input");
//            out.println(USER);
//            // Получаем размер файла
//            long fileSize = in.readLong();
//            // Получаем файл
//            receiveFile(fileSize, in);
//            System.out.println("111111");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void vrem()throws IOException{
//        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             DataInputStream in = new DataInputStream(socket.getInputStream())){
//            DataInputStream inD = new DataInputStream(socket.getInputStream());
//            out.println("input");
//            // Получаем размер файла
//            long fileSize = inD.readLong();
//            // Получаем файл
//            receiveFile(fileSize, in);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        client1.out.println("input");
        client1.out.println(USER);
        // Получаем размер файла
        long fileSize = client1.inD.readLong();
        // Получаем файл
        receiveFile(fileSize, client1.inD);
        System.out.println("111111");
    }

    private static void receiveFile(long fileSize, DataInputStream in) throws IOException {
        FileOutputStream fos = new FileOutputStream(CHAT_HISTORY_FILE);
        byte[] buffer = new byte[4096];
        long totalRead = 0;
        int bytesRead;

        while (totalRead < fileSize && (bytesRead = in.read(buffer, 0, (int)Math.min(buffer.length, fileSize - totalRead))) != -1) {
            fos.write(buffer, 0, bytesRead);
            totalRead += bytesRead;
        }
        fos.close();
        System.out.println("Файл получен: received_response.json");
    }
//    @FXML
//    public void handleClose(){
//
//    }
    public void receiveData(String name){
        USER=name;
        //System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        System.out.println(name);
        try {
            vrem();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        updateLabel();
        loadChatHistory();
        updateChatHistory();
        //System.out.println("111");
    }
}
