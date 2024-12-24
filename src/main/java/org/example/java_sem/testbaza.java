package org.example.java_sem;

import java.sql.*;

public class testbaza {
    static String driver = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://db4free.net:3306/";
    private static final String DB_NAME = "kihiki";  // Замените на имя вашей базы данных
    private static final String USER = "pkihiki";  // Замените на ваше имя пользователя
    private static final String PASSWORD = "12345678"; // Замените на ваш пароль
    private static final String TABLE_NAME = "logins";
    private static final String NEW_LOGIN = "mol";  // Новое значение логина
    private static final String NEW_PASS = "123321";
    public static void main(String[] args) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

            // 1. Загрузка драйвера
            //Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Установка соединения
            connection = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASSWORD);

            // 3. Создание запроса
            statement = connection.createStatement();
            String sql = "SELECT pass FROM " + TABLE_NAME + ";"; // SQL запрос

            // 4. Выполнение запроса и обработка результата
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String login = resultSet.getString("pass"); // Получение значения столбца 'login'
                System.out.println("Login: " + login);
            }
            System.out.println("--------------");
            String checkSql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE login = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(checkSql);
            preparedStatement.setString(1, NEW_LOGIN);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
        int rowsAffected=0;
            if (count > 0) {
            System.out.println("Логин '" + NEW_LOGIN + "' уже существует.");
        }
        else {
            String insertSql = "INSERT INTO " + TABLE_NAME + " (login, pass, ch) VALUES (?, ?,'');";
            preparedStatement = connection.prepareStatement(insertSql);
            preparedStatement.setString(1, NEW_LOGIN);
            preparedStatement.setString(2, NEW_PASS);
                System.out.println(preparedStatement.toString());
            rowsAffected = preparedStatement.executeUpdate();
        }
        if (rowsAffected > 0) {
            System.out.println("Логин '" + NEW_LOGIN + "' успешно добавлен.");
        } else {
            System.out.println("Не удалось добавить логин '" + NEW_LOGIN + "'.");
        }
        sql = "SELECT pass, ch FROM " + TABLE_NAME + " WHERE login = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "mol");
        resultSet = preparedStatement.executeQuery();
        String password = "";
        String chValue = "";
        if (resultSet.next()) {
            password = resultSet.getString("pass");
            chValue = resultSet.getString("ch");
        }
        System.out.println("Pass: " + password + ", Ch: " + chValue);
            // 5. Закрытие ресурсов
            try { if (resultSet != null) resultSet.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }



    }
}
