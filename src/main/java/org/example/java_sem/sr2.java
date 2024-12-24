
package org.example.java_sem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class sr2 {
    //private ServerSocket serverSocket;
    private static int clientCount = 0;
    private static List<Message> messageHistory = new ArrayList<>();
    private static final String CHAT_HISTORY_FILE = "response.json";
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);
    private static final String DB_URL = "jdbc:mysql://db4free.net:3306/";
    private static final String DB_NAME = "kihiki";  //имя базы данных
    private static final String USER = "pkihiki";  //имя пользователя
    private static final String PASSWORD = "12345678"; //пароль
    private static final String TABLE_NAME = "logins";

    public void start(int port) throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(port)){
            loadChatHistory();
            System.out.println("Сервер запущен. Ожидание подключения...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                //pool.execute();
                System.out.println("Клиент подключен: " + clientSocket.getInetAddress());
                new Thread(new EchoClientHandler(clientSocket)).start();
            }
        }
    }

//    public void stop() throws IOException {
//        serverSocket.close();
//    }

    private static class EchoClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }


        public void run() {
            try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

//            String inputLine;
//            while ((inputLine = in.readLine()) != null) {
//                int currentClientCount;
//                synchronized (sr2.class) {
//                    currentClientCount = clientCount;
//                }
//                if (".".equals(inputLine)) {
//                    out.println("bye");
//                    break;
//                }
//                out.println(inputLine);
//            }
                System.out.println("Клиент подключен1: " + clientSocket.getInetAddress());
                String request;
                String userName = "";
                while ((request = in.readLine()) != null) {
                    if (request.startsWith("input")) {
                        userName=in.readLine();
                        System.out.println("Клиент подключен: " +userName+ clientSocket.getInetAddress());
                        System.out.println(request);
                        DataOutputStream out1 = new DataOutputStream(clientSocket.getOutputStream());
                        File jsonFile = new File("response.json");
                        sendFile(jsonFile, out1);
                        //out.println("55");
                    }else if (request.startsWith("update")){
                        String mes =in.readLine();
                        Message messages = (new Message(userName, mes));
                        messageHistory.add(messages);
                        Gson gson= new Gson();
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CHAT_HISTORY_FILE))) {
                            writer.write(gson.toJson(messageHistory));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        DataOutputStream out1 = new DataOutputStream(clientSocket.getOutputStream());
                        File jsonFile = new File("response.json");
                        sendFile(jsonFile, out1);
                    }
                    else if(request.startsWith("login")){
                        String login=in.readLine();
                        String pass=in.readLine();
                        System.out.println("77777");
                        try {
                            if(login(login,pass)){
                                out.println("1");
                            }
                            else {
                                out.println("пользователя не существует или пароль не правельный");
                                //out.println(false);
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (request.startsWith("reg")) {
//                        System.out.println("22222222222222222");
                        String login=in.readLine();
                        String pass=in.readLine();
//                        System.out.println("1111111111111111111111");
                        try {
                            if(reg(login,pass)){
                                out.println("зарегистрировались");
                            }
                            else {
                                out.println("пользователя уже существует");
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                System.out.println("Клиент отключон:00000000 " + clientSocket.getInetAddress());
            in.close();
            out.close();
            clientSocket.close();
                System.out.println("end");
//            synchronized (sr2.class) {
//                    clientCount--;
//            }
            } catch (IOException e) {
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                } catch (IOException ex) {
                    System.out.println("eror");
                }
                System.out.println("1111111");
            }
        }
    }
    public static void main(String[] args) throws IOException {
        sr2 server = new sr2();
        server.start(12345);
    }
    private static void sendFile(File file, DataOutputStream out) throws IOException {
        // Отправляем размер файла
        System.out.println("11");
        out.writeLong(file.length());
        System.out.println("22");
        // Отправляем файл
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        }
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
    private static boolean login(String login, String pass) throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASSWORD);
        String checkSql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE login = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(checkSql);
        preparedStatement.setString(1, login);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        int rowsAffected=0;
        if (count > 0) {
            String insertSql = "SELECT pass, ch FROM " + TABLE_NAME + " WHERE login = ?";
            preparedStatement = connection.prepareStatement(insertSql);
            preparedStatement.setString(1, login);
            resultSet = preparedStatement.executeQuery();
            String password = "";
            String chValue = "";
            String passH = "";
            if (resultSet.next()) {
                password = resultSet.getString("pass");
                chValue = resultSet.getString("ch");
                System.out.println("Pass: " + password + ", Ch: " + chValue);
                byte[] convertedBytes = chValue.getBytes(StandardCharsets.UTF_8);
                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                md.update(convertedBytes);
                byte[] hashedPassword = md.digest(pass.getBytes());
                passH = Base64.getEncoder().encodeToString(hashedPassword);
                System.out.println(passH);
            }if(passH.equals(password)){
                return true;
            }
            else return false;
        }
        else {
            System.out.println("Логин '" + login + "' не существует.");
        return false;}
    }
    private static boolean reg(String login,String pass) throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASSWORD);
        String checkSql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE login = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(checkSql);
        preparedStatement.setString(1, login);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        int rowsAffected=0;
        if (count > 0) {
            System.out.println("Логин '" + login + "' уже существует.");
            return false;
        }
        else {
            byte[] salt = new byte[16];
            SecureRandom sr = new SecureRandom();
            sr.nextBytes(salt);
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            String ch=new String(salt, StandardCharsets.UTF_8);
            System.out.println("----------");
            System.out.println(ch);
            byte[] convertedBytes = ch.getBytes(StandardCharsets.UTF_8);
            md.update(convertedBytes); // Добавляем соль к хешу
            byte[] hashedPassword = md.digest(pass.getBytes());
            String passCH = Base64.getEncoder().encodeToString(hashedPassword);

            String insertSql = "INSERT INTO " + TABLE_NAME + " (login, pass, ch) VALUES (?, ?,?);";
            preparedStatement = connection.prepareStatement(insertSql);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, passCH);
            preparedStatement.setString(3, ch);
            System.out.println(preparedStatement.toString());
            rowsAffected = preparedStatement.executeUpdate();
        }
        if (rowsAffected > 0) {
            System.out.println("Логин '" + login + "' успешно добавлен.");
        } else {
            System.out.println("Не удалось добавить логин '" + login + "'.");
        }
        //Закрытие ресурсов
        try { if (resultSet != null) resultSet.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
        try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        return true;
    }
}